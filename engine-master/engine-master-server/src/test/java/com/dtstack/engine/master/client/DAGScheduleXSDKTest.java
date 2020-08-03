package com.dtstack.engine.master.client;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Auther: dazhi
 * @Date: 2020/7/31 1:34 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DAGScheduleXSDKTest {

    private final String SUFFIX_CONTROLLER = "Controller";
    private final String SUFFIX_POINT = ".";
    private final String SUFFIX_SERVICE = "Service";
    private final List<String> ignoreMethod = Lists.newArrayList("wait","equals","toString","hashCode","getClass","notify","notifyAll");

    @Test
    public void testStart() throws Exception {
        Set<ScannedGenericBeanDefinition> beanDefinition = getBeanDefinition("classpath*:com/dtstack/engine/master/controller/*.class");
        Set<ScannedGenericBeanDefinition> beanDefinitionApi = getBeanDefinition("classpath*:com/dtstack/engine/api/service/*.class");
        Map<String, BeanDefinition> apiMap = beanDefinitionApi.stream().collect(Collectors.toMap(bean -> cutMeaningfulName(Objects.requireNonNull(bean.getBeanClassName()),SUFFIX_SERVICE), g -> (g)));

        for (BeanDefinition controllerBeanDefinition : beanDefinition) {
            String beanClassName = controllerBeanDefinition.getBeanClassName();

            if (StringUtils.isNotBlank(beanClassName) && beanClassName.endsWith(SUFFIX_CONTROLLER)) {
                beanClassName = cutMeaningfulName(beanClassName,SUFFIX_CONTROLLER);
                BeanDefinition serviceBeanDefinition = apiMap.get(beanClassName);

                if (serviceBeanDefinition == null) {
                    // 开始比对
                    System.out.println(beanClassName+": 未扫描到service类，请检查!");
                    continue;
                }
                comparisonClass(serviceBeanDefinition,controllerBeanDefinition);
            }
        }

    }

    private void comparisonClass(BeanDefinition serviceBeanDefinition, BeanDefinition controllerBeanDefinition) throws ClassNotFoundException {
        String controllerClassName = controllerBeanDefinition.getBeanClassName();
        String serviceClassName = serviceBeanDefinition.getBeanClassName();
        System.out.println("开始比较:"+ controllerClassName +"----"+ serviceClassName);
        Class<?> serviceClass = ClassLoader.getSystemClassLoader().loadClass(serviceBeanDefinition.getBeanClassName());
        Class<?> controllerClass = ClassLoader.getSystemClassLoader().loadClass(controllerBeanDefinition.getBeanClassName());

        Method[] controllerMethods = controllerClass.getMethods();
        Method[] serviceMethods = serviceClass.getDeclaredMethods();

        if (ArrayUtils.isEmpty(serviceMethods)) {
            System.out.println("类:"+serviceClassName+":没有方法，比较失败!");
            return;
        }

        if (ArrayUtils.isEmpty(controllerMethods)) {
            System.out.println("类:"+controllerClassName+":没有方法，比较失败!");
            return;
        }

        Map<String, List<Method>> serviceMethodMap = Arrays.stream(serviceMethods).collect(Collectors.groupingBy(Method::getName));

        for (Method controllerMethod : controllerMethods) {
            String name = controllerMethod.getName();
            List<Method> serviceMethodList = serviceMethodMap.get(name);

            if (ignoreMethod.contains(name)) {
                continue;
            }

            if (CollectionUtils.isEmpty(serviceMethodList)) {
                System.out.println(controllerClassName+": service中方法缺少,方法名: "+name);
                continue;
            }


            comparisonMethod(controllerMethod,serviceMethodList,name);
        }


    }

    private void comparisonMethod(Method controllerMethod, List<Method> serviceMethod,String name) {
//        Class<?> controllerReturnType = controllerMethod.getReturnType();
//        Class<?> serviceReturnType = serviceMethod.getReturnType();
        String controllerMethodName = controllerMethod.getName();
//        if (!controllerReturnType.equals(serviceReturnType)) {
//            System.out.println(name+":  方法"+controllerMethodName+"返回值不一致");
//        }

        Parameter[] controllerParameters = controllerMethod.getParameters();
        Parameter[] serviceParameters = null;
        if (serviceMethod.size() == 1) {
            serviceParameters = serviceMethod.get(0).getParameters();
            if (serviceParameters != null) {
                String result = comparisonFiled(name, controllerMethodName, controllerParameters, serviceParameters);
                if (StringUtils.isNotBlank(result)) {
                    System.out.println(result);
                }
            }
        } else {
            System.out.println("出现重载方法");
            for (Method method : serviceMethod) {
                serviceParameters = method.getParameters();
                String result = comparisonFiled(name, controllerMethodName, controllerParameters, serviceParameters);
                if(StringUtils.isNotBlank(result)) {
                    System.out.println("重载方法method:"+method.getName()+"出现参数不一致,"+result);
                }
            }
        }
    }

    private String comparisonFiled(String name, String controllerMethodName, Parameter[] controllerParameters, Parameter[] serviceParameters) {
        Map<String, Parameter> serviceMethodMap = Arrays.stream(serviceParameters).collect(Collectors.toMap(Parameter::getName, parameter -> (parameter)));

        for (Parameter controllerParameter : controllerParameters) {
            Parameter parameter = serviceMethodMap.get(controllerParameter.getName());
            if (parameter == null) {
                return name + ":  方法" + controllerMethodName + "中的属性" + controllerParameter.getName() + "不存在！";
            }
            Class<?> controllerParameterType = controllerParameter.getType();
            Class<?> serviceParameterType = parameter.getType();

            if (!controllerParameterType.equals(serviceParameterType)) {
                return name + ":  方法" + controllerMethodName + "中的属性" + controllerParameter.getName() + "类型不相等";
            }
        }
        return "";
    }

    private String cutMeaningfulName(String beanClassName,String cut) {
        int beginIndex = beanClassName.lastIndexOf(SUFFIX_POINT);
        int endIndex = beanClassName.lastIndexOf(cut);

        if (beginIndex==-1|| endIndex==-1) {
            return "";
        }

        return beanClassName.substring(beginIndex+1, endIndex);
    }

    private Set<ScannedGenericBeanDefinition> getBeanDefinition(String path) {
        Set<ScannedGenericBeanDefinition> candidates = new LinkedHashSet<>();
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = patternResolver.getResources(path);
            MetadataReaderFactory metadata=new SimpleMetadataReaderFactory();
            for(Resource resource:resources) {
                MetadataReader metadataReader=metadata.getMetadataReader(resource);
                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                sbd.setResource(resource);
                sbd.setSource(resource);
                candidates.add(sbd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return candidates;
    }


}