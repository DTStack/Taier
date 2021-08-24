package com.dtstack.engine.dtscript.common.type;


import com.dtstack.engine.dtscript.DtYarnConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;


public class Python3Type extends AbstractPythonType {

    @Override
    public String cmdPrefix(YarnConfiguration config) {
        String python = config.get(DtYarnConfiguration.PYTHON3_PATH);
        return StringUtils.isNotBlank(python) ? python : "python3";
    }

    @Override
    public String name() {
        return "PYTHON3";
    }
}