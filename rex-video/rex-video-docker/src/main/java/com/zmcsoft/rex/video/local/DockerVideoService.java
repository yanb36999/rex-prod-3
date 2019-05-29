package com.zmcsoft.rex.video.local;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.zmcsoft.rex.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.utils.time.DateFormatter;
import org.hswebframework.web.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
@Slf4j
public class DockerVideoService implements VideoService {

    @Value("${com.zmcsoft.video.input-dir:'/opt/file/input'}")
    @Deprecated
    private String inputDir;

    @Value("${com.zmcsoft.video.output-dir:'/opt/file/output'}")
    private String outputDir;

    @Value("${com.zmcsoft.video.base-url:'http://localhost:8898/'}")
    private String baseUrl;

    @Value("${com.zmcsoft.video.file-source-url:'http://localhost:8898/'}")
    private String fileSourceUrl;

    private RequestBuilder builder = new SimpleRequestBuilder();

    public DockerClient docker;

    private ExecutorService executorService;

    private Queue<Runnable> jobQueue = new ConcurrentLinkedDeque<>();

    private boolean shutdown = false;

    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    @PostConstruct
    public void init() throws DockerCertificateException {
        docker = DefaultDockerClient.fromEnv().build();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        executorService.execute(() -> {
            while (!shutdown) {
                Runnable runnable = jobQueue.poll();
                if (null != runnable) {
                    executorService.execute(runnable);
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (@SuppressWarnings("all") InterruptedException ignore) {
                    }
                }
            }
            shutdownLatch.countDown();
        });
    }

    @PreDestroy
    public void shutdown() {
        shutdown = true;
        docker.close();
        try {
            shutdownLatch.await();
        } catch (@SuppressWarnings("all") InterruptedException ignore) {
        }
    }

    public String tryDownload(String location) {
        String downloadUrl = location;

        if (downloadUrl.startsWith("http")) {
            try {
                location = new URL(location).getPath();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            downloadUrl = fileSourceUrl + location;
        }
        String newLocation = outputDir + location;
        File file = new File(newLocation);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                builder.http(downloadUrl)
                        .download()
                        .get()
                        .write(file);
            } catch (IOException e) {
                throw new BusinessException("下载文件失败:" + e.getMessage(), e);
            }
        }
        return location;

    }

    @Override
    public void convertToM3u8(String location, Consumer<String> consumer) {
        location = tryDownload(location);
        String outputPath = "video/" + DateFormatter.toString(new Date(), "yyyyMMdd");
        new File(outputDir + outputPath).mkdirs();

        String output = outputPath + "/" + System.nanoTime();

        String[] ffmegCommand = {
                "-i", "/input/" + location,
                "-y", "-vcodec", "copy",
                "-map", "0", "-f", "segment",
                "-segment_list", "/output/" + output + ".m3u8",
                "-segment_time", "20",
                "/output/" + output + "-%03d.ts"};

        executeDocker(ffmegCommand, (success) -> {
            if (success) {
                consumer.accept(baseUrl + output + ".m3u8");
            } else {
                consumer.accept(null);
            }
        });

    }

    private void executeDocker(String[] command, Consumer<Boolean> callback) {
        jobQueue.add(() -> {
            boolean success = false;
            try {
                HostConfig hostConfig =
                        HostConfig.builder()
                                .appendBinds(outputDir + ":/output/")
                                .appendBinds(outputDir + ":/input/")
                                .autoRemove(true)
                                .build();

                ContainerConfig containerConfig = ContainerConfig.builder()
                        .hostConfig(hostConfig)
                        .image("jrottenberg/ffmpeg")
                        .cmd(command)
                        .build();
                System.out.println(containerConfig.cmd());
                ContainerCreation creation = docker.createContainer(containerConfig);
                String id = creation.id();
                try {
                    docker.startContainer(id);
                    while ((docker.inspectContainer(id)).state().running()) {
                        Thread.sleep(100);
                    }
                    System.out.println(containerConfig.cmd() + ",ok");
                    success = true;
                } finally {
                    try {
                        docker.removeContainer(id);
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {
                log.error("执行ffmepeg命令{}失败", Arrays.asList(command));
                e.printStackTrace();
            } finally {
                callback.accept(success);
            }
        });
    }

    private static String convertSeconds(int s) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(s);
    }

    @Override
    public boolean testFile(String file) {
        if (file.startsWith("http")) {
            try {
                file = new URL(file).getPath();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        File realFile = new File(outputDir + file);

        return realFile.exists() && realFile.length() > 0;
    }

    @Override
    public List<String> screenshot(String fileLocation, int rotate, int... ms) {
        fileLocation = tryDownload(fileLocation);
        String outputPath = "images/" + DateFormatter.toString(new Date(), "yyyyMMdd");

        new File(outputDir + outputPath).mkdirs();

        List<String> paths = new ArrayList<>(ms.length);
        List<CountDownLatch> latches = new ArrayList<>(ms.length);

        if (rotate != 0) {
            if (rotate < 0) {
                rotate = 360 - rotate;
            }
            rotate = rotate / 90;
        }
        String[] transposes = new String[rotate];

        for (int i = 0; i < rotate; i++) {
            transposes[i] = "transpose=1";
        }

        for (int i : ms) {
            String output = outputPath + "/" + System.nanoTime();
            String commandString = "-ss " + convertSeconds(i) +
                    " -t 0.001 -i /input/" + fileLocation +
//                    (rotate != 0 ? " -vf rotate=\"" + rotate + "\"" : "") +
                    (rotate != 0 ? " -filter_complex " + (String.join(",", transposes)) : "") +
                    " -f image2" +
                    " -y /output/" + output + ".jpg";

            String[] ffmegCommand = commandString.split(" ");

            CountDownLatch latch = new CountDownLatch(1);
            latches.add(latch);

            executeDocker(ffmegCommand, success -> {
                try {
                    if (success) {
                        paths.add(baseUrl + output + ".jpg");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        for (CountDownLatch latch : latches) {
            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        latches.clear();

        return new ArrayList<>(paths);
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setFileSourceUrl(String fileSourceUrl) {
        this.fileSourceUrl = fileSourceUrl;
    }
}
