package com.zmcsoft.rex.video;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public interface VideoService {

    default String convertToM3u8(String fileLocation) {
        String[] result = new String[1];
        CountDownLatch latch = new CountDownLatch(1);
        convertToM3u8(fileLocation, url -> {
            result[0] = url;
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result[0];
    }

    void convertToM3u8(String fileLocation, Consumer<String> consumer);


    List<String> screenshot(String fileLocation,int rotate,int... seconds);

    boolean testFile(String file);
}
