package com.dtstack.taier.script.common.type;


import java.util.List;

public class DummyType extends AbstractAppType {

    @Override
    public String name() {
        return "DUMMPY";
    }

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }

}