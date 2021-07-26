package com.dtstack.engine.worker.env;

import com.dtstack.engine.common.constrant.ConfigConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2020/9/10 10:05 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
@PropertySource(value = "file:${user.dir}/conf/application.properties")
public class WorkerEnvironmentContext implements EnvironmentAware {

    private Environment environment;

    public Long getWorkerTimeout() {
        String keyName = ConfigConstant.WORKER_TIMEOUT;
        return Long.valueOf(environment.getProperty(keyName, "300000"));
    }

    public String getWorkerLogstoreJdbcUrl() {
        String keyName = ConfigConstant.WORKER_LOGSTORE_JDBCURL;
        return environment.getProperty(keyName, StringUtils.EMPTY);
    }

    public String getWorkerLogstoreUsername() {
        String keyName = ConfigConstant.WORKER_LOGSTORE_USERNAME;
        return environment.getProperty(keyName, StringUtils.EMPTY);
    }

    public String getWorkerLogstorePassword() {
        String keyName = ConfigConstant.WORKER_LOGSTORE_PASSWORD;
        return environment.getProperty(keyName, StringUtils.EMPTY);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getWorkerInitialSize() {
        return null;
    }

    public String getWorkerMinActive() {
        return null;
    }

    public String getWorkerMaxActive() {
        return null;
    }
}
