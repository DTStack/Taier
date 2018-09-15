package com.dtstack.yarn.client;

import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.common.type.AppType;
import org.apache.commons.lang.StringUtils;


public class LaunchCommandBuilder {

    private ClientArguments clientArguments;

    private DtYarnConfiguration conf;

    private static final String PYTHON2 = "2.x";

    private static final String PYTHON3 = "3.x";

    public LaunchCommandBuilder(ClientArguments clientArguments, DtYarnConfiguration conf) {
        this.clientArguments = clientArguments;
        this.conf = conf;
    }

    private String cmdPrefix() {
        return clientArguments.appType.cmdPrefix(conf);
    }


    public String buildCmd() {
        if (StringUtils.isNotBlank(clientArguments.launchCmd)) {
            return clientArguments.launchCmd;
        } else {
            String fullPath = clientArguments.files[0];
            String[] parts = fullPath.split("/");
            return cmdPrefix() + " " + parts[parts.length - 1] + " " + clientArguments.cmdOpts;
        }
    }

}
