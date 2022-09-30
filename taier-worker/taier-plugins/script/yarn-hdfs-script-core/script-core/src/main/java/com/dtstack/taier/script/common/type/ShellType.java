package com.dtstack.taier.script.common.type;


import com.dtstack.taier.script.ScriptConfiguration;

import java.util.List;

public class ShellType extends AbstractAppType {

    @Override
    public String cmdPrefix(ScriptConfiguration dtconf) {
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