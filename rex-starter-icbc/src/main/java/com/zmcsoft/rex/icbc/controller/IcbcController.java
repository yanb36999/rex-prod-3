package com.zmcsoft.rex.icbc.controller;


import com.zmcsoft.rex.icbc.service.IcbcApiService;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/icbc")
@Slf4j(topic = "business.icbc")
public class IcbcController {

    @Autowired
    private IcbcApiService apiService;

    @GetMapping("/query/{decisionId}")
    //input 01|51XXXXXXXXXXX|^^
    //output ﻿00|510132100048232|1|A|510132196802014214|杨际祥|192.98|200.00|5101320006成都市新津县公安局交通警察大队城区巡逻一中队|5101320006成都市新津县公安局交通警察大队城区巡逻一中队|5101320006|1|0|2010-09-07 20:22:00|^^
    @Authorize //需要授权
    public ResponseMessage<String> queryByDecisionId(@PathVariable String decisionId) {
        //test ip  192.168.56.97

        if (StringUtils.isEmpty(decisionId)) {
            return ResponseMessage.ok("0");
        }

        return ResponseMessage.ok(callIcbc("01|" + decisionId + "|^^"));
    }


    @PostMapping("/pay/{decisionId}")
    @Authorize //需要授权
    public ResponseMessage<String> pay(@PathVariable String decisionId, @RequestParam(required = false) BigDecimal amount) {

        if (StringUtils.isEmpty(decisionId)) {
            return ResponseMessage.ok("0");
        }

        String old = callIcbc("01|" + decisionId + "|^^");

        if (old == null) {
            return ResponseMessage.error("获取缴费信息失败");
        }
        //0-------1----------2-3---------4------------5-----6------7--------------8---------------------------------------------9-------------------------------------------10------11-12---13
        //00|510132100048232|1|A|510132196802014214|杨际祥|192.98|200.00|5101320006成都市新津县公安局交通警察大队城区巡逻一中队|5101320006成都市新津县公安局交通警察大队城区巡逻一中队|5101320006|1|0|2010-09-07 20:22:00|^^

        String[] arr = old.split("[|]");
        if (!"00".equals(arr[0])) {

            return ResponseMessage.error(old);
        }

        String data = apiService.enrich(arr, amount);

        return ResponseMessage.ok(callIcbc(data));
    }

    @GetMapping("/books/{bookDate}")
    public ResponseMessage<String> bookPay(@PathVariable String bookDate) {
        apiService.sendBook(bookDate);
        return ResponseMessage.ok();
    }

    public String callIcbc(String input) {
        return apiService.callIcbc(input);
    }

}


