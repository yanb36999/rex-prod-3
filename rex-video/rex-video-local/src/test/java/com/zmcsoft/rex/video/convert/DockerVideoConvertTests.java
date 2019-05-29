package com.zmcsoft.rex.video.convert;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.zmcsoft.rex.video.local.LocalVideoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
public class DockerVideoConvertTests {


    private LocalVideoService localVideoService;

    @Before
    public void init() throws DockerCertificateException {
        localVideoService = new LocalVideoService();
        localVideoService.setInputDir(new File("./").getAbsolutePath() + "/");
        localVideoService.setOutputDir(new File("target").getAbsolutePath() + "/");
        localVideoService.setBaseUrl("http://file.rex.hsweb.me:8088/");
        localVideoService.init();
    }

    @Test
    public void testScreenshot() {
        localVideoService.screenshot("src/test/resources/15301803733088.mov",
                0, 1229);

    }

    @Test
    public void testM3u8() throws Exception {

        String url = localVideoService.convertToM3u8("src/test/resources/15096055516634.mov");
        System.err.println(url);
    }
}
