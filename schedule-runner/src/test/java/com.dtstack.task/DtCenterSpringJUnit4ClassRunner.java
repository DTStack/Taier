package com.dtstack.task;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class DtCenterSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {
    /**
     * 设置 user.dir,使用项目根目录下的配置文件
     */
    public DtCenterSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        String userDir = System.getProperty("user.dir");
        userDir = userDir.substring(0,userDir.lastIndexOf("/"));
        System.setProperty("user.dir",userDir);
    }
}
