package com.zmcsoft.rex.workflow.illegal.api.service;

import java.util.Map;

/**
 * 字典服务，获取字典配置的值
 *
 * @author zhouhao
 * @since 1.0
 */
public interface DictService {
    /**
     * 获取字典值，如果不存在字典或者值则返回指定的默认值
     *
     * @param dictId       字典id，如：car-type
     * @param key          字典的键： 02
     * @param defaultValue 默认值
     * @return 字典id中 key对应的值，如果不存在则返回指定的defaultValue
     */
    String getString(String dictId, String key, String defaultValue);

    /**
     * @return 获取全部字典数据
     */
    Map<String, Map<String, Object>> getAll();

    Map<String, Object> getAll(String id);
}
