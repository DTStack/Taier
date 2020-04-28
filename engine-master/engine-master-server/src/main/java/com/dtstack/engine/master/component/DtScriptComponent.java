package com.dtstack.engine.master.component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DtScriptComponent extends BaseComponent {

    private static List<String> YARN_SHELL_KERBEROS_CONF = Arrays.asList(
            "hdfsPrincipal",
            "hdfsKeytabPath",
            "hdfsKrb5ConfPath"
    );


    public DtScriptComponent(Map<String, Object> allConfig) {
        super(allConfig);
    }

    @Override
    protected List<String> getKerberosKey() {
        return YARN_SHELL_KERBEROS_CONF;
    }

    @Override
    public void testConnection() throws Exception {

    }

}