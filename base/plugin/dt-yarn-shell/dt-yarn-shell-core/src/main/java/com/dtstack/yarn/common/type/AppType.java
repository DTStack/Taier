package com.dtstack.yarn.common.type;


import com.dtstack.yarn.client.ClientArguments;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;
import java.util.Map;

public abstract class AppType {

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    public static AppType fromString(String type) {
        if (StringUtils.isBlank(type)) {
            return new DummyType();
        } else if (type.equalsIgnoreCase("shell")) {
            return new ShellType();
        } else if (type.equalsIgnoreCase("python") || type.equalsIgnoreCase("python2")) {
            return new Python2Type();
        } else if (type.equalsIgnoreCase("python3")) {
            return new Python3Type();
        } else if (type.equalsIgnoreCase("jlogstash")) {
            return new JLogstashType();
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
    public String cmdContainerExtra(String cmd, Map<String, Object> containerInfo) {
        return cmd;
    }

    abstract public String name();

    public void env(List<String> envList) {
        envList.add("LANG=zh_CN.UTF-8");
        envList.add("CLASSPATH=" + "./:" + System.getenv("CLASSPATH"));
        envList.add("PATH=" + "./:" + System.getenv("PATH"));
    }

}