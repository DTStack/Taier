package com.dtstack.engine.flink.storage;

import com.dtstack.engine.flink.FlinkConfig;
import org.apache.flink.configuration.Configuration;

import java.util.Properties;

public abstract class AbstractStorage {

    public abstract void init(Properties pluginInfo);

    public abstract void fillStorageConfig(Configuration config, FlinkConfig flinkConfig);

    public abstract <T> T getStorageConfig();

}
