package com.zmcsoft.rex.workflow.illegal.controller;

import com.zmcsoft.rex.workflow.illegal.api.service.DictService;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dict")
public class DictController  {

    @Autowired
    private DictService dictService;

    @GetMapping("/all/{id}")
    public ResponseMessage<Map<String,Object>> all(@PathVariable String id){
        return ResponseMessage.ok(dictService.getAll(id));
    }

}
