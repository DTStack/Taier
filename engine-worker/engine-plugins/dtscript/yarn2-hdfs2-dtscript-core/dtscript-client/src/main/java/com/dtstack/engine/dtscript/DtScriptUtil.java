package com.dtstack.engine.dtscript;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.dtscript.common.type.AppTypeEnum;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.List;
import java.util.Properties;


public class DtScriptUtil {
    private static final BASE64Decoder DECODER = new BASE64Decoder();

    public static String[] buildPythonArgs(JobClient jobClient) throws IOException {
        String exeArgs = jobClient.getClassArgs();
        List<String> args = DtStringUtil
                .splitIngoreBlank(exeArgs);

        String appType = null;
        for (int i = 0; i < args.size() - 1; ++i) {
            if ("--app-type".equals(args.get(i))) {
                appType = args.get(i + 1);
            }
        }
        for (int i = 0; i < args.size() - 1; ++i) {
            if ("--launch-cmd".equals(args.get(i)) || "--cmd-opts".equals(args.get(i))) {
                if (!AppTypeEnum.JLOGSTASH.name().equalsIgnoreCase(appType)) {
                    String cmd = new String(DECODER.decodeBuffer(args.get(i + 1)), "UTF-8");
                    // FIXME: 3/25/21 hard code, fix in next version
                    if ("--launch-cmd".equals(args.get(i)) && cmd.contains("--app-env")) {
                        String[] tmpStrs = cmd.split("--app-env");

                        if (tmpStrs.length != 2) {
                            throw new RdosDefineException("parse envs which from cmd failed. cmd is " + cmd);
                        }

                        args.set(i + 1, tmpStrs[0]);
                        int length = args.size();
                        args.add(length,"-app-env");
                        args.add(length + 1, tmpStrs[1]);
                    } else {
                        args.set(i + 1, cmd);
                    }
                }
            }
        }

        Properties confProperties = jobClient.getConfProperties();
        confProperties.stringPropertyNames().stream()
                .map(String::trim)
                .forEach(key -> {
                    String value = confProperties.getProperty(key).trim();
                    String newKey = key.replaceAll("\\.", "-");

                    if (key.contains("priority")) {
                        newKey = "priority";
                        value = String.valueOf(jobClient.getPriority()).trim();
                    }
                    args.add("--" + newKey);
                    args.add(value);
                });
        return args.toArray(new String[args.size()]);
    }

}