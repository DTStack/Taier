package com.dtstack.engine.dtscript.common.type;

import com.dtstack.engine.dtscript.client.ClientArguments;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.net.URLEncoder;
import java.util.List;

public abstract class AbstractPythonType extends AbstractAppType {

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }

    @Override
    public String buildCmd(ClientArguments clientArguments, YarnConfiguration conf) {
        if (StringUtils.isNotBlank(clientArguments.getLaunchCmd())) {
            return StringUtils.replaceOnce(clientArguments.getLaunchCmd(), "python", cmdPrefix(conf));
        }

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

        String execFile =clientArguments.getFiles()[0];
        if (!clientArguments.getLocalFile()){
            String fullPath = clientArguments.getFiles()[0];
            String[] parts = fullPath.split("/");
            execFile = parts[parts.length - 1];
        }
        return cmdPrefix(conf) + " " + execFile + " " + encodedOpts;
    }
}
