package com.dtstack.engine.master;

import com.dtstack.engine.common.log.LogbackComponent;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EngineApplication {

    public static void main(String[] args) throws Exception {
        SystemPropertyUtil.setSystemUserDir();
        LogbackComponent.setupLogger();
        SpringApplication.run(EngineApplication.class, args);
    }
}