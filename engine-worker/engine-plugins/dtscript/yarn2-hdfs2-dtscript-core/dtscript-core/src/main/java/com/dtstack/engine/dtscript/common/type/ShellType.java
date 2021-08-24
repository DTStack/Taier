package com.dtstack.engine.dtscript.common.type;


import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.List;

public class ShellType extends AbstractAppType {

    @Override
    public String cmdPrefix(YarnConfiguration config) {
        return "bash";
    }

    @Override
    public String name() {
        return "SHELL";
    }

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }
}