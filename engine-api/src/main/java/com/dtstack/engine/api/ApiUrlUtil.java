package com.dtstack.engine.api;

import com.dtstack.engine.api.service.BatchJobJobService;
import com.dtstack.engine.api.service.BatchTaskShadeService;
import com.dtstack.engine.api.service.BatchTaskTaskShadeService;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuemo
 * @company www.dtstack.com
 * @Date 2020-03-30
 */
public class ApiUrlUtil {
    private static Map<String, String> apiUrlMap;

    public static String getApiUrl(String urlKey) {
        return apiUrlMap.get(urlKey);
    }

    static {
        apiUrlMap = new ConcurrentHashMap<>();
        List<Class> classes = new ArrayList<Class>(){{
           add(BatchJobJobService.class);
           add(BatchJobJobService.class);
           add(BatchTaskShadeService.class);
           add(BatchTaskTaskShadeService.class);
        }};
        final String root = "/node";
        for (Class serviceClass : classes) {
            String serviceName = serviceClass.getSimpleName();
            serviceName = serviceName.substring(0, serviceName.length() - 7);
            Method[] methods = serviceClass.getMethods();
            for (Method method : methods) {
                String urlKey = String.format("%s_%s", serviceName, method.getName());
                String urlValue = String.format("%s/%s/%s", root, firstStrLowerCase(serviceName), method.getName());
                apiUrlMap.put(urlKey, urlValue);
            }
        }
    }

    private static String firstStrLowerCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'A' && ch[0] <= 'Z') {
            ch[0] = (char) (ch[0] + 32);
        }
        return new String(ch);
    }

}
