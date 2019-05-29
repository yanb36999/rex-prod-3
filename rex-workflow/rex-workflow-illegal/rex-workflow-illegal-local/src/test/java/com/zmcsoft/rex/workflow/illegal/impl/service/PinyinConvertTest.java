package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.zmcsoft.rex.commons.district.api.utils.NullUtils;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import org.apache.commons.beanutils.BeanUtils;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by zhouhao on 2017/11/3.
 */
public class PinyinConvertTest {

    @Test
    public void testPinyinField() throws InvocationTargetException, IllegalAccessException {
        JsonReportIllegalInfo json = new JsonReportIllegalInfo();

        ReportIllegalInfo illegalInfo = new ReportIllegalInfo();

        illegalInfo.setAdminDivision("test");

        BeanUtils.copyProperties(json, illegalInfo);

        System.out.println(json.getAdminDivision());
        System.out.println(JSON.toJSONString(json));

    }

    public static void main(String[] args) {
        RspReportIllegalCar json = new RspReportIllegalCar();
        json.setWf_info(Arrays.asList(new JsonReportIllegalInfo()));

        Object object = NullUtils.transNullToEmpty(json, obj -> (Map<String, Object>) JSON.toJSON(obj), key -> "");

        System.out.println(((Map) object).get("c_wxgzsj"));
        System.out.println(object);

        ResponseMessage<RspReportIllegalCar> message=new ResponseMessage<>();


        message=(ResponseMessage)ResponseMessage.ok("123");

       String t =  JSON.toJSONString(message, new ValueFilter() {
            @Override
            public Object process(Object object, String name, Object value) {
                if(value==null){
                    return "";
                }
                return value;
            }
        });

        System.out.println(t);
    }
}
