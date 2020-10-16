package com.dtstack.engine.base.util;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author mowen
 * @ProjectName engine-all
 * @ClassName YarnClientUtils.java
 * @Description TODO
 * @createTime 2020年10月15日 18:10:00
 */
public class YarnClientUtils {

    private static final Logger LOG = LoggerFactory.getLogger(YarnClientUtils.class);

    public YarnClient getYarnClient(YarnClient yarnClient, BaseConfig baseConfig, Configuration yarnConf) {
        try {
            if (yarnClient == null) {
                return buildYarnClient(baseConfig, yarnConf);
            } else {
                //判断下是否可用
                CompletableFuture.supplyAsync(() -> {
                    try {
                        EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
                        enumSet.add(YarnApplicationState.ACCEPTED);
                        List<ApplicationReport> acceptedApps = yarnClient.getApplications(enumSet);
                    } catch (YarnException | IOException e) {
                        LOG.error("YarnClient is unavailable.");
                        throw new RdosDefineException("", e);
                    }
                    return null;
                }).get(1, TimeUnit.MINUTES);
                return yarnClient;
            }
        } catch (Throwable e) {
            LOG.error("getYarnClient error:", e);
        }
        return buildYarnClient(baseConfig, yarnConf);
    }

    public YarnClient buildYarnClient(BaseConfig baseConfig, Configuration yarnConf) {
        try {
            LOG.debug("build yarn client.");
            return KerberosUtils.login(baseConfig, () -> {
                YarnClient yarnClient1 = YarnClient.createYarnClient();
                yarnClient1.init(yarnConf);
                yarnClient1.start();
                return yarnClient1;
            }, yarnConf);
        } catch (Exception e) {
            throw new RdosDefineException("build yarn client error", e);
        }
    }

}
