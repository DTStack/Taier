package com.dtstack.engine.dtscript;

import com.dtstack.engine.common.DtStringUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.yarn.common.type.AppTypeEnum;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;
import java.io.IOException;
import java.util.List;


public class DtYarnShellUtil {
    private static final BASE64Decoder decoder = new BASE64Decoder();

    public static String[] buildPythonArgs(JobClient jobClient) throws IOException {
        String exeArgs = jobClient.getClassArgs();
        List<String> args = DtStringUtil.splitIngoreBlank(exeArgs);

        String appType = null;
        for (int i = 0; i < args.size() - 1; ++i) {
            if (args.get(i).equals("--app-type")) {
                appType = args.get(i + 1);
            }
        }
        for (int i = 0; i < args.size() - 1; ++i) {
            if (args.get(i).equals("--launch-cmd") || args.get(i).equals("--cmd-opts")) {
                if (!AppTypeEnum.JLOGSTASH.name().equalsIgnoreCase(appType)) {
                    args.set(i + 1, new String(decoder.decodeBuffer(args.get(i + 1)), "UTF-8"));
                }
            }
        }

        String taskParams = jobClient.getTaskParams();

        if (StringUtils.isNotBlank(taskParams)) {
            taskParams = taskParams.trim();
            List<String> taskParam = DtStringUtil.splitIngoreBlank(taskParams);
            for (int i = 0; i < taskParam.size(); ++i) {
                String[] pair = taskParam.get(i).split("=", 2);
                if (pair.length == 2) {
                    pair[0] = pair[0].replaceAll("\\.", "-");
                    if (pair[0].contains("priority")) {
                        pair[0] = "priority";
                        pair[1] = String.valueOf(jobClient.getPriority());
                    }
                    args.add("--" + pair[0]);
                    args.add(pair[1]);
                }
            }
        }
        return args.toArray(new String[args.size()]);
    }

}