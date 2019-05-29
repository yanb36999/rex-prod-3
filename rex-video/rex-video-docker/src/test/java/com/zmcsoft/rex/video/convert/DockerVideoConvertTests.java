package com.zmcsoft.rex.video.convert;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.zmcsoft.rex.video.local.DockerVideoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
public class DockerVideoConvertTests {


    private DockerVideoService dockerVideoService;

    @Before
    public void init() throws DockerCertificateException {
        dockerVideoService = new DockerVideoService();
        dockerVideoService.setInputDir(new File("./").getAbsolutePath() + "/");
        dockerVideoService.setOutputDir(new File("target").getAbsolutePath() + "/");
        dockerVideoService.setBaseUrl("http://file.rex.cdjg.gov.cn:8090/");
        dockerVideoService.init();
    }

    @Test
    public void testScreenshot() {
        dockerVideoService.screenshot("src/test/resources/15096055516634.mov",
                0, 10_1000);
        dockerVideoService.screenshot("src/test/resources/15096055516634.mov",
                90, 10_1000);
        dockerVideoService.screenshot("src/test/resources/15096055516634.mov",
                180, 10_1000);
        dockerVideoService.screenshot("src/test/resources/15096055516634.mov",
                270, 10_1000);

    }

    @Test
    public void testM3u8() throws Exception {

        String url = dockerVideoService.convertToM3u8("src/test/resources/15096055516634.mov");
        System.err.println(url);
    }
}
