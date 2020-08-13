package com.dtstack.engine.master.sdk;

import com.dtstack.engine.master.AbstractCommonTest;
import com.dtstack.engine.master.router.DtRequestParam;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;
import org.apache.ibatis.io.ResolverUtil;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static org.junit.Assert.fail;

public class ApiValidTest extends AbstractCommonTest {
    private final static String SERVICE_PACKAGE = "com.dtstack.engine.api.service";
    private final static String CONTROLLER_PACKSGE = "com.dtstack.engine.master.controller";
    private final static String[] ignorePath = {"/node/component/cluster", "/node"};

    @Test
    public void testApiSame() {
        Set<Class<? extends Class<?>>> controllerClasses = find(CONTROLLER_PACKSGE);
        Map<String, String> controllerMethods = new HashMap<>();
        for (Class<? extends Class<?>> clazz: controllerClasses) {
            RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
            if (requestMapping == null) {
                continue;
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (String clazzPath: requestMapping.value()) {
                if (Arrays.asList(ignorePath).contains(clazzPath)) {
                    continue;
                }
                for (Method method: methods) {
                    StringBuilder sb = new StringBuilder();
                    RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                    if (methodRequestMapping == null) {
                        continue;
                    }
                    for (String methodPath: methodRequestMapping.value()) {
                        for (Parameter p: method.getParameters()) {
                            sb.append(getFormatString(p.getType().getSimpleName()));
                            if (p.isAnnotationPresent(DtRequestParam.class)) {
                                DtRequestParam dtRequestParam = p.getAnnotation(DtRequestParam.class);
                                sb.append(getFormatString(dtRequestParam.value()));
                            } else if (p.isAnnotationPresent(RequestParam.class)) {
                                RequestParam requestParam = p.getAnnotation(RequestParam.class);
                                sb.append(getFormatString(requestParam.value()));
                            } else {
                                sb.append(getFormatString(p.getName()));
                            }
                        }
                        controllerMethods.put(clazzPath + methodPath, sb.toString());
                    }

                }
            }
        }

        Map<String, String> serviceMethods = new HashMap<>();
        Set<Class<? extends Class<?>>> serviceClasses = find(SERVICE_PACKAGE);
        for (Class<? extends Class<?>> clazz: serviceClasses) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method: methods) {
                StringBuilder sb = new StringBuilder();
                RequestLine requestLine = method.getAnnotation(RequestLine.class);
                if (requestLine == null) {
                    continue;
                }
                String lineValue = requestLine.value();
                String[] splitValues = lineValue.split(" ");
                String path = splitValues[splitValues.length - 1];
                for (Parameter p: method.getParameters()) {
                    sb.append(getFormatString(p.getType().getSimpleName()));
                    if (p.isAnnotationPresent(Param.class)) {
                        Param param = p.getAnnotation(Param.class);
                        sb.append(getFormatString(param.value()));
                    } else {
                        sb.append(getFormatString(p.getName()));
                    }
                }
                serviceMethods.put(path, sb.toString());
            }
        }

        String result = formatOutput(controllerMethods, serviceMethods);
        if (result.length() != 0) {
            fail("\n" + result);
        }
    }

    private static Set<Class<? extends Class<?>>> find(String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
        resolverUtil.find(new ResolverUtil.IsA(Object.class), packageName);
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        return typeSet;
    }

    private static String getFormatString(String str) {
        return "_" + str;
    }

    private static String formatOutput(Map<String, String> controllerMethods, Map<String, String> serviceMethods) {
        StringBuilder sb = new StringBuilder();
        for (String controllerKey: controllerMethods.keySet()) {
            String controllerParams = controllerMethods.get(controllerKey);
            if (serviceMethods.containsKey(controllerKey)) {
                String serviceParams = serviceMethods.get(controllerKey);
                if (!(controllerParams != null && controllerParams.equals(serviceParams))) {
                    sb.append(controllerKey + "两者都有，但是参数不一致" + "\n" + "controller参数: " + controllerParams + "\n" +
                            "service参数: " + serviceParams + "\n\n");
                }
                serviceMethods.remove(controllerKey);
            } else {
                sb.append(controllerKey + "在controller存在，service中不存在" + "\n\n");
            }
        }
        for (String serviceKey: serviceMethods.keySet()) {
            sb.append(serviceKey + "在service中存在，controller中不存在" + "\n\n");
        }
        return sb.toString();
    }

}
