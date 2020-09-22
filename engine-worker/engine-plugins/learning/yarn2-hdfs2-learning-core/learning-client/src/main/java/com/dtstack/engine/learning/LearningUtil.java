package com.dtstack.engine.learning;


import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.PluginInfoUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class LearningUtil {

    private static final BASE64Decoder DECODER = new BASE64Decoder();

    public static String[] buildPythonArgs(JobClient jobClient) throws IOException {
        String exeArgs = jobClient.getClassArgs();
        List<String> args = DtStringUtil.splitIngoreBlank(exeArgs);
        for(int i = 0; i < args.size() - 1; ++i) {
            if("--launch-cmd".equals(args.get(i)) || "--cmd-opts".equals(args.get(i)) || "--remote-dfs-config".equals(args.get(i))) {
                args.set(i + 1, new String(DECODER.decodeBuffer(args.get(i+1)), "UTF-8"));
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

        //pluginInfo --> --remote-dfs-config
        if(StringUtils.isNotEmpty(jobClient.getPluginInfo())){
            args.add("--remote-dfs-config");
            Object hdfsConf = PluginInfoUtil.getSpecKeyConf(jobClient.getPluginInfo(), PluginInfoUtil.HADOOP_CONF_KEY);
            args.add(new Gson().toJson(hdfsConf));
        }

        return args.toArray(new String[args.size()]);
    }

}
