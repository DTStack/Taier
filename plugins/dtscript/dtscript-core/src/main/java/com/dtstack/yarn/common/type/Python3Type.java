package com.dtstack.yarn.common.type;


import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.client.ClientArguments;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.net.URLEncoder;
import java.util.List;

public class Python3Type extends AppType {

    @Override
    public String cmdPrefix(YarnConfiguration config) {
        String python = config.get(DtYarnConfiguration.PYTHON3_PATH);
        return StringUtils.isNotBlank(python) ? python : "python3";
    }

    @Override
    public String buildCmd(ClientArguments clientArguments, YarnConfiguration conf) {

        String encodedOpts = "";
        if (StringUtils.isNotBlank(clientArguments.getCmdOpts())) {
            try {
                String cmdOpts = clientArguments.getCmdOpts();
                encodedOpts = URLEncoder.encode(cmdOpts, "UTF-8");
                if (clientArguments.getLocalFile()){
                    encodedOpts += " " + clientArguments.getApplicationId();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (StringUtils.isNotBlank(clientArguments.getLaunchCmd())) {
            return clientArguments.getLaunchCmd();
        } else {
            String execFile =clientArguments.getFiles()[0];
            if (!clientArguments.getLocalFile()){
                String fullPath = clientArguments.getFiles()[0];
                String[] parts = fullPath.split("/");
                execFile = parts[parts.length - 1];
            }
            return cmdPrefix(conf) + " " + execFile + " " + encodedOpts;
        }
    }

    @Override
    public String name() {
        return "PYTHON3";
    }

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }
}