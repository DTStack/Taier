package com.dtstack.yarn.common.type;


import com.dtstack.yarn.DtYarnConfiguration;

public class ShellType extends AppType {

    @Override
    public String cmdPrefix(DtYarnConfiguration config) {
        return "bash";
    }

    @Override
    public String name() {
        return "SHELL";
    }

}
