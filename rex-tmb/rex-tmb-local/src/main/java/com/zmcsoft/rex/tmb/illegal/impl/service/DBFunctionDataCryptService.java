package com.zmcsoft.rex.tmb.illegal.impl.service;


import com.zmcsoft.rex.tmb.illegal.service.DataCryptService;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.ezorm.rdb.executor.SqlExecutor;
import org.hswebframework.web.Maps;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@UseDataSource("tmbDataSource")
@Service
@Slf4j
public class DBFunctionDataCryptService implements DataCryptService {

    private SqlExecutor sqlExecutor;

    static final String pwd = "cdjgj910seatrend";


    public static final String encryptFunctionSql = "PG_ENCRYPT_DECRYPT.ENCRYPT_3KEY_MODE(?,1,'" + pwd + "')";

    public static final String decryptFunctionSql = "PG_ENCRYPT_DECRYPT.DECRYPT_3KEY_MODE(?,1,'" + pwd + "')";


    private static final String encryptSql = "select PG_ENCRYPT_DECRYPT.ENCRYPT_3KEY_MODE(#{data},1,#{pwd}) as \"res\" from dual";

    private static final String decryptSql = "select PG_ENCRYPT_DECRYPT.DECRYPT_3KEY_MODE(#{data},1,#{pwd}) as \"res\" from dual";

    @Autowired
    public void setSqlExecutor(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }


    @Override
    @Cacheable(cacheNames = "SDojaksdqweczxmnc", key = "#data")
    public String decrypt(String data) {
        if (null == data) {
            return null;
        }
        try {
            return Optional.ofNullable(sqlExecutor
                    .single(decryptSql, Maps.<String, Object>buildMap().put("data", data).put("pwd", pwd).get()))
                    .map(map -> String.valueOf(map.get("res")))
                    .orElse("");
        } catch (Exception e) {
            log.error("解密数据{}失败", data, e);
            return "";
        }
    }

    @Override
    @Cacheable(cacheNames = "asdqweafzxcasdfqwe", key = "#data")
    public String encrypt(String data) {
        if (null == data) {
            return null;
        }
        try {
            return Optional.ofNullable(sqlExecutor
                    .single(encryptSql, Maps.<String, Object>buildMap().put("data", data).put("pwd", pwd).get()))
                    .map(map -> String.valueOf(map.get("res")))
                    .orElse("");
        } catch (Exception e) {
            log.error("加密数据{}失败", data, e);
            return "";
        }
    }
}
