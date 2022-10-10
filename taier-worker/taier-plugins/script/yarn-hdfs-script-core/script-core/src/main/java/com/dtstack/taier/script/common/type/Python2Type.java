package com.dtstack.taier.script.common.type;

import com.dtstack.taier.script.ScriptConfiguration;
import org.apache.commons.lang.StringUtils;


public class Python2Type extends AbstractPythonType {

    @Override
    public String cmdPrefix(ScriptConfiguration dtconf) {
        // 获取控制台配置的 python 路径
        String python = dtconf.get(ScriptConfiguration.SCRIPT_PYTHON2_PATH);
        return StringUtils.isNotBlank(python) ? python : "python";
    }

    @Override
    public String name() {
        return "PYTHON2";
    }
}