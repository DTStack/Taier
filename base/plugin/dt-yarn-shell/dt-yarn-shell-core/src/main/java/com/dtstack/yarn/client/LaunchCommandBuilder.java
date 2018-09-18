package com.dtstack.yarn.client;

import com.dtstack.yarn.DtYarnConfiguration;


public class LaunchCommandBuilder {

    private ClientArguments clientArguments;

    private DtYarnConfiguration conf;

    public LaunchCommandBuilder(ClientArguments clientArguments, DtYarnConfiguration conf) {
        this.clientArguments = clientArguments;
        this.conf = conf;
    }

    private String cmdPrefix() {
        return clientArguments.appType.cmdPrefix(conf);
    }


    public String buildCmd() {
        return clientArguments.appType.buildCmd(clientArguments, conf);
    }

}
