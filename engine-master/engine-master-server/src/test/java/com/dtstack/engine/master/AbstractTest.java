package com.dtstack.engine.master;

import com.alibaba.druid.support.spring.stat.SpringStatUtils;
import com.dtstack.engine.master.anno.DatabaseDeleteOperation;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.config.CacheConfig;
import com.dtstack.engine.master.config.MasterServerBeanConfig;
import com.dtstack.engine.master.config.MybatisConfig;
import com.dtstack.engine.master.config.ThreadPoolConfig;
import com.dtstack.engine.master.data.DataCollection;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.listener.RunnerListener;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.fail;

@Component
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(DtCenterSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentContext.class, MasterServerBeanConfig.class, CacheConfig.class, ThreadPoolConfig.class,
        MybatisConfig.class})
@PowerMockIgnore({"javax.management.*", "javax.security.*", "javax.net.ssl.*", "javax.crypto.*"})
public abstract class AbstractTest implements RunnerListener {

    @Autowired
    public ApplicationContext context;

    @Autowired
    public DataCollection dataCollection;

    @Override
    public void runsBeforeClass() {
        for (Method method: dataCollection.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            DatabaseInsertOperation databaseOperation = method.getAnnotation(DatabaseInsertOperation.class);
            if (databaseOperation != null) {
                try {
                    Class<?> returnClass = Class.forName(method.getGenericReturnType().getTypeName());
                    Object ins = method.invoke(dataCollection);
                    Class<?> daoType = databaseOperation.dao();
                    Method daoMethod = daoType.getDeclaredMethod(databaseOperation.method(), returnClass);
                    daoMethod.invoke(context.getBean(daoType), ins);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getTargetException().getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    @Override
    public void runsAfterClass() {
        for (Method method: dataCollection.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            DatabaseDeleteOperation databaseOperation = method.getAnnotation(DatabaseDeleteOperation.class);
            if (databaseOperation != null) {
                try {
                    Object ins = method.invoke(dataCollection);
                    Class<?> returnClass = method.getReturnType();
                    List<Field> fieldList = new ArrayList<>();
                    while (returnClass != null) {
                        Field[] fields = returnClass.getDeclaredFields();
                        fieldList.addAll(Arrays.stream(fields).collect(Collectors.toList()));
                        returnClass = returnClass.getSuperclass();
                    }
                    for (Field field: fieldList) {
                        if (databaseOperation.field().equals(field.getName())) {
                            field.setAccessible(true);
                            Object val = field.get(ins);
                            Class<?> daoType = databaseOperation.dao();
                            Method daoMethod = daoType.getDeclaredMethod(databaseOperation.method(), field.getType());
                            daoMethod.invoke(context.getBean(daoType), val);
                            break;
                        }
                    }
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getTargetException().getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }
}
