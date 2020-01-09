package com.dtstack.engine.dtscript.common.type;


import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.client.ClientArguments;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AppType {

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    protected static final Logger buildCmdLog = LoggerFactory.getLogger(AppType.class);

    public static AppType fromString(String type) {
        if (StringUtils.isBlank(type)) {
            return new DummyType();
        } else if (type.equalsIgnoreCase(AppTypeEnum.SHELL.name())) {
            return new ShellType();
        } else if (type.equalsIgnoreCase(AppTypeEnum.PYTHON.name()) || type.equalsIgnoreCase(AppTypeEnum.PYTHON2.name())) {
            return new Python2Type();
        } else if (type.equalsIgnoreCase(AppTypeEnum.PYTHON3.name())) {
            return new Python3Type();
        } else if (type.equalsIgnoreCase(AppTypeEnum.JLOGSTASH.name())) {
            return new JLogstashType();
        } else if (type.equalsIgnoreCase(AppTypeEnum.JUPYTER.name())) {
            return new JupyterType();
        }
        throw new IllegalArgumentException("Unsupported appType: " + type);
    }

    public String cmdPrefix(YarnConfiguration config) {
        return "";
    }

    public String buildCmd(ClientArguments clientArguments, YarnConfiguration conf) {
        if (StringUtils.isNotBlank(clientArguments.getLaunchCmd())) {
            return clientArguments.getLaunchCmd();
        } else {
            String fullPath = clientArguments.getFiles()[0];
            String[] parts = fullPath.split("/");
            String encodedOpts = "";

            return cmdPrefix(conf) + " " + parts[parts.length - 1] + " " + encodedOpts;
        }
    }

    /**
     * 每个类型，可能对执行时的命令有额外的参数处理
     */
    public String cmdContainerExtra(String cmd, DtYarnConfiguration conf, Map<String, Object> containerInfo) {
        return cmd;
    }

    abstract public String name();

    public void env(List<String> envList) {
        envList.add("LANG=zh_CN.UTF-8");
        envList.add("CLASSPATH=" + "./:" + System.getenv("CLASSPATH"));
        envList.add("PATH=" + "./:" + System.getenv("PATH"));
    }

}