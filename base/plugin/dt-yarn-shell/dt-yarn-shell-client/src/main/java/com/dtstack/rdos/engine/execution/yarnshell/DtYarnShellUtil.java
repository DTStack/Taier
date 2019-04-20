package com.dtstack.rdos.engine.execution.yarnshell;

import com.dtstack.rdos.engine.execution.base.JobClient;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DtYarnShellUtil {
    private static final BASE64Decoder decoder = new BASE64Decoder();

    public static String[] buildPythonArgs(JobClient jobClient) throws IOException {
        String exeArgs = jobClient.getClassArgs();
        String[] args = exeArgs.split("\\s+");
        for(int i = 0; i < args.length - 1; ++i) {
            if(args[i].equals("--launch-cmd") || args[i].equals("--cmd-opts")) {//|| args[i].equals("--remote-dfs-config")
                args[i+1] = new String(decoder.decodeBuffer(args[i+1]), "UTF-8");
            }
        }

        List<String> argList = new ArrayList<>();
        argList.addAll(Arrays.asList(args));
        String taskParams = jobClient.getTaskParams();

        if(StringUtils.isNotBlank(taskParams)) {
            taskParams = taskParams.trim();
            String[] taskParam = taskParams.split("\\s+");
            for(int i = 0; i < taskParam.length; ++i) {
                String[] pair = taskParam[i].split("=");
                pair[0] = pair[0].replaceAll("\\.", "-");
                if (pair[0].contains("priority")){
                    pair[0] = "priority";
                    pair[1] = String.valueOf(jobClient.getPriority());
                }
                argList.add("--" + pair[0]);
                argList.add(pair[1]);
            }
        }
        return argList.toArray(new String[argList.size()]);
    }
}
