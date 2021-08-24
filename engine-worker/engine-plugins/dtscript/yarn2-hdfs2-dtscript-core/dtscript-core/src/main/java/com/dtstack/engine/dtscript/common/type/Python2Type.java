package com.dtstack.engine.dtscript.common.type;


import com.dtstack.engine.dtscript.DtYarnConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;


public class Python2Type extends AbstractPythonType {

    @Override
    public String cmdPrefix(YarnConfiguration config) {
        String python = config.get(DtYarnConfiguration.PYTHON2_PATH);
        return StringUtils.isNotBlank(python) ? python : "python";
    }

    @Override
    public String name() {
        return "PYTHON2";
    }


}