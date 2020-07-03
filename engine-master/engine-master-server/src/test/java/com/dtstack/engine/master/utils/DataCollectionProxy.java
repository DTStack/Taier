package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.annotation.Unique;
import com.dtstack.engine.api.domain.DataObject;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.anno.IgnoreUniqueRandomSet;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataCollectionProxy implements InvocationHandler, DataCollection {
    public static DataCollectionProxy instance = new DataCollectionProxy();
    private static final Logger LOG = LoggerFactory.getLogger(DataCollectionProxy.class);
    private static final Map<String, Object> ENTITY = new ConcurrentHashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String key = getEntityKey(method.getName(), method.getReturnType().getSimpleName());
        if (ENTITY.containsKey(key)) {
            return ENTITY.get(key);
        }
        try {
            Object ins = method.invoke(instance);
            if (method.getReturnType().getName().startsWith("com.dtstack.engine.service.db.dataobject")) {
                LOG.info("invoke create entity:{}", method.getReturnType());
            }
            if (ins == null) {
                return null;
            }
            method.setAccessible(true);
            DatabaseInsertOperation databaseOperation = method.getAnnotation(DatabaseInsertOperation.class);
            if (databaseOperation != null) {
                Field[] fields = ins.getClass().getDeclaredFields();
                boolean hasIgnoreUnique = method.isAnnotationPresent(IgnoreUniqueRandomSet.class);
                for (Field field : fields) {
                    field.setAccessible(true);
                    Unique unique = field.getAnnotation(Unique.class);
                    if (unique != null) {
                        Object fieldValue = field.get(ins);
                        if (fieldValue == null) {
                            throw new RuntimeException("The Unique Field cannot be null");
                        }
                        if (hasIgnoreUnique) {
                            continue;
                        }
                        if (field.getType().isAssignableFrom(String.class)) {
                            String actualValue = (String) fieldValue + AutoChangedNumbers.INCR.getAndIncrement();
                            field.set(ins, actualValue);
                        }
                        if (field.getType().isAssignableFrom(Long.class)) {
                            Long actualValue = (long)AutoChangedNumbers.INCR.getAndIncrement();
                            field.set(ins, actualValue);
                        }
                    }
                }
                if (ins instanceof DataObject) {
                    ((DataObject) ins).setId(AutoChangedNumbers.ID.getAndDecrement());
                }
                Class<?> daoType = databaseOperation.dao();
                Method daoMethod = daoType.getDeclaredMethod(databaseOperation.method(), method.getReturnType());
                Object returnValue = daoMethod.invoke(ValueUtils.runContext.getBean(daoType), ins);
                ENTITY.put(key, ins);
                return ins;
            }
            return ins;
        } catch (Throwable e) {
            LOG.error("", e);
            throw new RuntimeException(e);
        }
    }

    private static String getEntityKey(String methodName, String classSimpleName) {
        return methodName + "_" + classSimpleName;
    }
}
