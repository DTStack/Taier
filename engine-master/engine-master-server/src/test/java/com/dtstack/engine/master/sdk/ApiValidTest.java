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


    @Test
    public void testApiSame() {
        Set<Class<? extends Class<?>>> controllerClasses = find(CONTROLLER_PACKSGE);
        List<String> controllerMethods = new ArrayList<>();
        for (Class<? extends Class<?>> clazz: controllerClasses) {
            RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
            if (requestMapping == null) {
                continue;
            }
            String path = getFormatString(requestMapping.value(), false);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method: methods) {
                StringBuilder sb = new StringBuilder();
                sb.append(path);
                RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                if (methodRequestMapping == null) {
                    continue;
                }
                sb.append(getFormatString(methodRequestMapping.value(), false));

                for (Parameter p: method.getParameters()) {
                    sb.append(getFormatString(p.getType().getSimpleName(), true));
                    if (p.isAnnotationPresent(DtRequestParam.class)) {
                        DtRequestParam dtRequestParam = p.getAnnotation(DtRequestParam.class);
                        sb.append(getFormatString(dtRequestParam.value(),true));
                    } else if (p.isAnnotationPresent(RequestParam.class)) {
                        RequestParam requestParam = p.getAnnotation(RequestParam.class);
                        sb.append(getFormatString(requestParam.value(), true));
                    } else {
                        sb.append(getFormatString(p.getName(), true));
                    }
                }

                controllerMethods.add(sb.toString());
            }
        }
        Collections.sort(controllerMethods);

        List<String> serviceMethods = new ArrayList<>();
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
                String path = getFormatString(splitValues[splitValues.length - 1], false);
                sb.append(path);
                for (Parameter p: method.getParameters()) {
                    sb.append(getFormatString(p.getType().getSimpleName(), true));
                    if (p.isAnnotationPresent(Param.class)) {
                        Param param = p.getAnnotation(Param.class);
                        sb.append(getFormatString(param.value(), true));
                    } else {
                        sb.append(getFormatString(p.getName(), true));
                    }
                }
                serviceMethods.add(sb.toString());
            }
        }
        Collections.sort(serviceMethods);

        List<String> tempControllerMethods = new ArrayList<>(controllerMethods);
        controllerMethods.removeAll(serviceMethods);
        serviceMethods.removeAll(tempControllerMethods);

        List<String> diffMethods = new ArrayList<>();
        diffMethods.addAll(controllerMethods);
        diffMethods.addAll(serviceMethods);

        Collections.sort(diffMethods);

        if (!diffMethods.isEmpty()) {
            StringBuilder diffSb = new StringBuilder();
            for (String method: diffMethods) {
                diffSb.append("\n" + method);
            }
            fail("have different methods: " + diffSb.toString());
        }
    }

    private static Set<Class<? extends Class<?>>> find(String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
        resolverUtil.find(new ResolverUtil.IsA(Object.class), packageName);
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        return typeSet;
    }

    private static String getFormatString(String[] str, boolean needDownLine) {
        StringBuilder sb = new StringBuilder();
        for (String s : str) {
            if (needDownLine) {
                sb.append("_");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private static String getFormatString(String str, boolean needDownLine) {
        if (needDownLine) {
            return "_" + str;
        }
        return str;
    }

}
