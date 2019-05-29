package com.zmcsoft.rex.workflow.illegal.controller.my;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.*;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by zhouhao on 2017/10/30.
 */
@RestController
@Api(tags = "违法照片信息")
@RequestMapping("/illegal/image")
public class IllegalImageController {

    private String api = "http://178.16.13.73:8090/WeChat/system/query!getIllegalImg.action?xh=%s";

    private RequestBuilder requestBuilder = new SimpleRequestBuilder();


    @GetMapping("/{caseId}")
    @ApiOperation("获取违法照片信息")
    public ResponseMessage<Object> illegalImage(@ApiParam("案件id")@PathVariable String caseId) throws IOException {
       JSONObject object= JSON.parseObject(requestBuilder.http(String.format(api,caseId))
                .get().asString());
       if(object.getBoolean("success")){
           return ResponseMessage.ok(object.get("data"));
       }

       return ResponseMessage.ok();
    }

}
