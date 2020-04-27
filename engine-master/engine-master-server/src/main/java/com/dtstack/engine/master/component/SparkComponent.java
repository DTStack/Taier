package com.dtstack.engine.master.component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/5/31
 */
public class SparkComponent extends BaseComponent {

    private static List<String> SPARK_KERBEROS_CONF = Arrays.asList(
            "sparkPrincipal",
            "sparkKeytabPath",
            "sparkKrb5ConfPath",
            "zkPrincipal",
            "zkKeytabPath",
            "zkLoginName"
    );

    public SparkComponent(Map<String, Object> allConfig) {
        super(allConfig);
    }

    @Override
    protected List<String> getKerberosKey(){
        return SPARK_KERBEROS_CONF;
    }

    @Override
    public void testConnection() throws Exception {

    }
}
