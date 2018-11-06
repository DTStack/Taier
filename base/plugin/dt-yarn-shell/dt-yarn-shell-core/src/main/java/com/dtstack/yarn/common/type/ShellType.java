package com.dtstack.yarn.common.type;


import org.apache.hadoop.yarn.conf.YarnConfiguration;

public class ShellType extends AppType {

    @Override
    public String cmdPrefix(YarnConfiguration config) {
        return "bash";
    }

    @Override
    public String name() {
        return "SHELL";
    }

}
