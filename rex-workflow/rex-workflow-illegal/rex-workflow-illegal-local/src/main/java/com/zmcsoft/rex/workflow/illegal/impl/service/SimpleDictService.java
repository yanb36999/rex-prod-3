package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.workflow.illegal.api.service.DictService;
import org.hswebframework.web.Maps;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author zhouhao
 * @since 1.0
 */
@Service
public class SimpleDictService implements DictService {

    private static Map<String, Map<String, Object>> data = new HashMap<>();

    static {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:/dict/*");
            for (Resource resource : resources) {
                String name = resource.getFilename();

                String text = StreamUtils.copyToString(resource.getInputStream(), Charset.forName("utf-8"));

                Map<String, Object> result = Arrays.stream(text.split("\n"))
                        .map(line -> line.split("\t|[ ]|[=]"))
                        .filter(arr -> arr.length >= 2)
                        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
                data.put(name, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String dictId, String key, String defaultValue) {
        Map<String, Object> dict = data.get(dictId);
        return dict == null ? defaultValue : String.valueOf(dict.getOrDefault(key, defaultValue));
    }
    @Override
    public Map<String, Map<String, Object>> getAll() {
        return data;
    }

    @Override
    public Map<String, Object> getAll(String id) {
        return data.getOrDefault(id,new HashMap<>());
    }
}
