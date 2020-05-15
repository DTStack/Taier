package com.dtstack.engine.dtscript;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.dtscript.common.type.AppTypeEnum;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.List;


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
                    args.set(i + 1, new String(DECODER.decodeBuffer(args.get(i + 1)), "UTF-8"));
                }
            }
        }

        String taskParams = jobClient.getTaskParams();

        if (StringUtils.isNotBlank(taskParams)) {
            taskParams = taskParams.trim();
            String[] ignoreTaskParams = taskParams.split("\n");
            for (String ignoreTaskParam : ignoreTaskParams) {
                if (ignoreTaskParam.trim().startsWith("#")) {
                    continue;
                }
                String[] pair = ignoreTaskParam.split("=", 2);
                if (pair.length == 2) {
                    pair[0] = pair[0].replaceAll("\\.", "-");
                    if (pair[0].contains("priority")) {
                        pair[0] = "priority";
                        pair[1] = String.valueOf(jobClient.getPriority());
                    }
                    args.add("--" + pair[0].trim());
                    args.add(pair[1].trim());
                }
            }
        }
        return args.toArray(new String[args.size()]);
    }

}