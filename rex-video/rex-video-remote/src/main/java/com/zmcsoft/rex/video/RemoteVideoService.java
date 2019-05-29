package com.zmcsoft.rex.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class RemoteVideoService implements VideoService {

    @Value("${com.zmcsoft.video.server.url:http://178.25.1.49:8898/}")
    private String videoServerUrl = "http://178.25.1.49:8898/";

    private RequestBuilder requestBuilder = new SimpleRequestBuilder();

    @Autowired(required = false)
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);
        }
    }

    @Override
    public void convertToM3u8(String fileLocation, Consumer<String> consumer) {
        executorService.execute(() -> {
            try {
                String jsonResult = requestBuilder.http(videoServerUrl + "video/convert/m3u8")
                        .param("location", fileLocation)
                        .get()
                        .asString();
                consumer.accept(getResult(jsonResult).getString("result"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private JSONObject getResult(String json) {
        JSONObject response = JSON.parseObject(json);

        if (200 == response.getInteger("status")) {
            return response;
        } else {
            throw new RuntimeException(response.getString("message"));
        }
    }

    @Override
    public List<String> screenshot(String fileLocation, int rotate, int... seconds) {
        try {
            String jsonResult = requestBuilder.http(videoServerUrl + "video/screenshot")
                    .param("location", fileLocation)
                    .param("rotate", String.valueOf(rotate))
                    .param("secondsJson", JSON.toJSONString(seconds))
                    .get().asString();

            return getResult(jsonResult).getJSONArray("result").toJavaList(String.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean testFile(String file) {
        try {
            String jsonResult = requestBuilder.http(videoServerUrl + "video/test")
                    .param("file", file)
                    .get().asString();
            return getResult(jsonResult).getBoolean("result");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setVideoServerUrl(String videoServerUrl) {
        this.videoServerUrl = videoServerUrl;
    }
}
