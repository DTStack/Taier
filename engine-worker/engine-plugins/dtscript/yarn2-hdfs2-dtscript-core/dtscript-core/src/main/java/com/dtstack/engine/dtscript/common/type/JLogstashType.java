package com.dtstack.engine.dtscript.common.type;

import ch.qos.logback.classic.Level;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.client.ClientArguments;
import com.dtstack.engine.dtscript.util.Base64Util;
import com.dtstack.engine.dtscript.util.GZipUtil;
import com.dtstack.engine.dtscript.util.NetUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class JLogstashType extends AbstractAppType {

    private static final Logger LOG = LoggerFactory.getLogger(JLogstashType.class);

    @Override
    public String buildCmd(ClientArguments clientArguments, YarnConfiguration conf) {

        String root = conf.get("jlogstash.root");
        if (StringUtils.isBlank(root)) {
            throw new IllegalArgumentException("Must specify jlogstash.root");
        }
        String javaHome = conf.get("java.home", "");
        if (StringUtils.isNotBlank(javaHome) && !javaHome.endsWith("/")) {
            javaHome += "/";
        }
        String cmdOpts = clientArguments.getCmdOpts();
        if (StringUtils.isBlank(cmdOpts)) {
            throw new IllegalArgumentException("Must specify cmdOpts");
        }
        String appName = clientArguments.getAppName();
        if (StringUtils.isBlank(appName)) {
            appName = "jlogstashJob";
        }

        if (buildCmdLog.isDebugEnabled()) {
            buildCmdLog.debug("encodedOpts: " + clientArguments.getCmdOpts());
            buildCmdLog.debug("Building jlogstash launch command");
        }

        List<String> jlogstashArgs = new ArrayList<>(20);
        jlogstashArgs.add(javaHome + "java");
        jlogstashArgs.add(clientArguments.getJvmOpts());
        jlogstashArgs.add("-Xms" + (clientArguments.getWorkerMemory() - clientArguments.getWorkerReservedMemory()) + "m");
        jlogstashArgs.add("-Xmx" + (clientArguments.getWorkerMemory() - clientArguments.getWorkerReservedMemory())  + "m");
        jlogstashArgs.add("-cp " + root + "/jlogstash*.jar");
        jlogstashArgs.add("com.dtstack.jlogstash.JlogstashMain");
        jlogstashArgs.add("-l stdout");
        jlogstashArgs.add("-" + getJlogstashLogLevel(clientArguments.getLogLevel().toUpperCase()));
        jlogstashArgs.add("-f " + clientArguments.getCmdOpts());
        jlogstashArgs.add("-p " + root);
        jlogstashArgs.add("-name " + appName);

        StringBuilder command = new StringBuilder();
        for (String arg : jlogstashArgs) {
            command.append(arg).append(" ");
        }
        if (buildCmdLog.isDebugEnabled()) {
            buildCmdLog.debug("jlogstash launch command: " + command.toString());
        }

        return command.toString();

    }

    private String getJlogstashLogLevel(String logLevel) {
        if (Level.TRACE.levelStr.equals(logLevel)) {
            return "vvvvv";
        } else if (Level.DEBUG.levelStr.equals(logLevel)) {
            return "vvvv";
        } else if (Level.INFO.levelStr.equals(logLevel)) {
            return "vvv";
        } else if (Level.WARN.levelStr.equals(logLevel)) {
            return "vv";
        } else if (Level.ERROR.levelStr.equals(logLevel)) {
            return "v";
        } else {
            return "vvv";
        }
    }

    /**
     * logstash 需要对inputs中存在beats的情况，为port进行模式处理
     * 如果端口存在，则使用端口；
     * 不存在，则使用默认端口6767。
     * <p>
     * 端口被占用会进行检测，并递增进行重新设置端口，如6767被占用，则使用6768
     */
    @Override
    public String cmdContainerExtra(String cmd, DtYarnConfiguration conf, Map<String, Object> containerInfo) {
        try {
            String[] args = cmd.split("\\s+");
            String fStr = null;
            int idx = -1;
            for (int i = 0; i < args.length - 1; ++i) {
                if ("-f".equals(args[i])) {
                    fStr = Base64Util.baseDecode(GZipUtil.deCompress(args[i + 1]));
                    idx = i + 1;
                    break;
                }
            }
            boolean isChg = false;
            if (StringUtils.isNotBlank(fStr)) {
                Map configs = OBJECT_MAPPER.readValue(fStr, Map.class);
                List<Map> inputs = (List<Map>) MapUtils.getObject(configs, "inputs", Collections.EMPTY_LIST);
                if (!inputs.isEmpty()) {
                    int i = 1;
                    for (Map input : inputs) {
                        Iterator<Map.Entry<String, Map>> inputIt = input.entrySet().iterator();
                        while (inputIt.hasNext()) {
                            Map.Entry<String, Map> inputEntry = inputIt.next();
                            String inputType = inputEntry.getKey();
                            Map<String, Object> inputConfig = inputEntry.getValue();
                            if ("Beats".equalsIgnoreCase(inputType)) {
                                isChg = true;
                                int configPort = MapUtils.getInteger(inputConfig, "port", 6767);
                                int port = NetUtils.getAvailablePortRange(configPort, 65535);
                                inputConfig.put("port", port);

                                Map<String, Object> beats = new HashMap<>(1);
                                beats.put("port", port);
                                containerInfo.put("beats" + i++, beats);
                            }
                        }
                    }
                }
                fStr = OBJECT_MAPPER.writeValueAsString(configs);
            }
            if (idx != -1 && isChg) {
                args[idx] = GZipUtil.compress(Base64Util.baseEncode(fStr));

                cmd = StringUtils.join(args, " ");
            }
        } catch (Exception e) {
            LOG.error("JLogstashType.cmdContainerExtra error:", e);
        }
        return super.cmdContainerExtra(cmd, conf, containerInfo);
    }

    @Override
    public String name() {
        return "JLOGSTASH";
    }

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }
}