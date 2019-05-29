package com.zmcsoft.rex.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video")
@Slf4j
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/convert/m3u8")
    public ResponseMessage<String> convertM3u8(@RequestParam String location) {
        log.info("video convert/m3u8 start,location:{}", location);
        ResponseMessage<String> responseMessage = ResponseMessage.ok(videoService.convertToM3u8(location));
        log.info("video convert/m3u8 end,responseMessage:{}", JSONObject.toJSONString(responseMessage));
        return responseMessage;
    }

    @GetMapping("/test")
    public ResponseMessage<Boolean> test(@RequestParam String file) {
        return ResponseMessage.ok(videoService.testFile(file));
    }

    @GetMapping("/screenshot")
    public ResponseMessage<List<String>> screenshot(@RequestParam String location,
                                                    @RequestParam(defaultValue = "0") int rotate,
                                                    @RequestParam String secondsJson) {
        log.info("video screenshot start,location:{},rotate:{},secondsJson:{}",location,rotate,secondsJson);
        List<Integer> secondsList = JSON.parseArray(secondsJson).toJavaList(Integer.class);

        int[] seconds = new int[secondsList.size()];

        for (int i = 0; i < secondsList.size(); i++) {
            seconds[i] = secondsList.get(i);
        }
        ResponseMessage<List<String>> responseMessage = ResponseMessage.ok(videoService.screenshot(location, rotate, seconds));
        log.info("video screenshot end,responseMessage:{}", JSONObject.toJSONString(responseMessage));
        return responseMessage;
    }

}
