package com.dtstack.yarn.common.type;


import java.util.List;

public class DummyType extends AppType {

    @Override
    public String name() {
        return "DUMMPY";
    }

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }

}