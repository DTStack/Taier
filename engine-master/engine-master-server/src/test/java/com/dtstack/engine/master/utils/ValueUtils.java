package com.dtstack.engine.master.utils;

import com.dtstack.engine.dao.TestCommonDao;
import com.dtstack.engine.master.anno.DataSource;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

@Component
public class ValueUtils {

    private static final String packages = DataCollection.class.getPackage().getName();
    private static final String strValue = "test__";

    @Autowired
    private TestCommonDao testCommonDao;

    @Autowired
    private ApplicationContext context;

    private static TestCommonDao runTestCommonDao;
    public static ApplicationContext runContext;

    @PostConstruct
    public void runSetBean() {
        runTestCommonDao = testCommonDao;
        runContext = context;
    }

    public static void initData() throws Exception {
        runTestCommonDao.truncate();
        Set<Class<? extends Class<?>>> classes = find(packages);
        for (Class<? extends Class<?>> clazz : classes) {
            if (!Modifier.isPublic(clazz.getModifiers()) || clazz.isMemberClass()) {
                continue;
            }
            Object ins = null;
            for (Method method : clazz.getDeclaredMethods()) {
                method.setAccessible(true);
                if (method.isAnnotationPresent(DataSource.class)) {
                    ins = method.invoke(clazz);
                    break;
                }
            }
            if (ins == null) {
                throw new RuntimeException("init Data error, Data Singleton miss.");
            }
            for (Method method : clazz.getDeclaredMethods()) {
                method.setAccessible(true);
                DatabaseInsertOperation databaseOperation = method.getAnnotation(DatabaseInsertOperation.class);
                if (databaseOperation != null) {
                    if (method.getParameterCount() == 0) {
                        method.invoke(ins);
                    }
                }
            }
        }
    }

    private static Set<Class<? extends Class<?>>> find(String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
        resolverUtil.find(new ResolverUtil.IsA(Object.class), packageName);
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        return typeSet;
    }


    public static String getChangedStr() {
        return strValue + AutoChangedNumbers.INCR.getAndIncrement();
    }

    public static Long getChangedLong() {return (long)AutoChangedNumbers.INCR.getAndIncrement();}

}