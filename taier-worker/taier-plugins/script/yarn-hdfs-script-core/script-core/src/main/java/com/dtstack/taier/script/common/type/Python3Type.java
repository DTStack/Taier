package com.dtstack.taier.script.common.type;

import com.dtstack.taier.script.ScriptConfiguration;
import org.apache.commons.lang.StringUtils;

public class Python3Type extends AbstractPythonType {

    @Override
    public String cmdPrefix(ScriptConfiguration dtconf) {
        String python = dtconf.get(ScriptConfiguration.SCRIPT_PYTHON3_PATH);
        return StringUtils.isNotBlank(python) ? python : "python3";
    }

    @Override
    public String name() {
        return "PYTHON3";
    }
}