package com.dtstack.yarn.client;

import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.common.AppType;
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
        AppType appType = clientArguments.appType;
        if(appType == AppType.SHELL) {
            return "bash";
        }

        String pythonVersion = clientArguments.pythonVersion;
        if(pythonVersion.equalsIgnoreCase(PYTHON2)) {
            String python = conf.get(DtYarnConfiguration.PYTHON2_PATH);
            return StringUtils.isNotBlank(python) ? python : "python";
        }

        if(pythonVersion.equalsIgnoreCase(PYTHON3)) {
            String python = conf.get(DtYarnConfiguration.PYTHON3_PATH);
            return StringUtils.isNotBlank(python) ? python : "python3";
        }

        throw new IllegalArgumentException("Illegal python version: " + pythonVersion);
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
