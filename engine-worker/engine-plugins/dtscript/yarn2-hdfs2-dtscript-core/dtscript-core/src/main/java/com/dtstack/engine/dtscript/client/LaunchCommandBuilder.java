package com.dtstack.engine.dtscript.client;

import org.apache.hadoop.yarn.conf.YarnConfiguration;


public class LaunchCommandBuilder {

    private ClientArguments clientArguments;

    private YarnConfiguration conf;

    public LaunchCommandBuilder(ClientArguments clientArguments, YarnConfiguration conf) {
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
