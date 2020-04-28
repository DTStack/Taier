package com.dtstack.engine.learning;


import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.util.PluginInfoUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.util.Strings;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LearningUtil {

    private static final BASE64Decoder DECODER = new BASE64Decoder();

    public static String[] buildPythonArgs(JobClient jobClient) throws IOException {
        String exeArgs = jobClient.getClassArgs();
        String[] args = exeArgs.split("\\s+");
        for(int i = 0; i < args.length - 1; ++i) {
            if("--launch-cmd".equals(args[i]) || "--cmd-opts".equals(args[i]) || "--remote-dfs-config".equals(args[i])) {
                args[i+1] = new String(DECODER.decodeBuffer(args[i+1]), "UTF-8");
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

        //pluginInfo --> --remote-dfs-config
        if(Strings.isNotEmpty(jobClient.getPluginInfo())){
            argList.add("--remote-dfs-config");
            Object hdfsConf = PluginInfoUtil.getSpecKeyConf(jobClient.getPluginInfo(), PluginInfoUtil.HADOOP_CONF_KEY);
            argList.add(new Gson().toJson(hdfsConf));
        }

        return argList.toArray(new String[argList.size()]);
    }

}
