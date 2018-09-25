package com.dtstack.yarn.common.type;


import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.client.ClientArguments;
import org.apache.commons.lang.StringUtils;

public class Python2Type extends AppType {

    @Override
    public String cmdPrefix(DtYarnConfiguration config) {
        String python = config.get(DtYarnConfiguration.PYTHON2_PATH);
        return StringUtils.isNotBlank(python) ? python : "python";
    }

    @Override
    public String name() {
        return "PYTHON2";
    }

}
