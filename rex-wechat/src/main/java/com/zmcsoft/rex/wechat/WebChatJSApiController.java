package com.zmcsoft.rex.wechat;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.hswebframework.web.NotFoundException;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/wechat")
@Api(tags = "微信接口", value = "微信")
public class WebChatJSApiController {

    @Autowired
    private WxMpService wxService;

    @Autowired(required = false)
    private FileService fileService;

    @GetMapping("/ticket")
    @ApiOperation("获取ticket")
    public ResponseMessage<String> getJsApiTicket() throws WxErrorException {
        return ResponseMessage.ok(wxService.getJsapiTicket());
    }

    @GetMapping("/signature")
    @ApiOperation("签名")
    public ResponseMessage<WxJsapiSignature> getJsApiSignature(@RequestParam String url) throws WxErrorException {
        return ResponseMessage.ok(wxService.createJsapiSignature(url));
    }

    @GetMapping("/media/download/{id}")
    @ApiOperation("下载文件")
    public ResponseMessage<String> downloadMedia(@PathVariable String id) throws Exception {
        File file = wxService.getMaterialService().mediaDownload(id);
        if (file == null) {
            throw new NotFoundException("media " + id + " not found!");
        }
        String filePath = fileService.saveStaticFile(new FileInputStream(file), file.getName());
        file.delete();
        return ResponseMessage.ok(filePath);
    }

}
