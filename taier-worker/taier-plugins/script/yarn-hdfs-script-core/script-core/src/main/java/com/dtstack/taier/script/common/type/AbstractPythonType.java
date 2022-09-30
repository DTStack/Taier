package com.dtstack.taier.script.common.type;

import com.dtstack.taier.script.ScriptConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.Path;

import java.util.List;

public abstract class AbstractPythonType extends AbstractAppType {

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }

    @Override
    public String buildCmd(ScriptConfiguration dtconf) {
        if (StringUtils.isNotBlank(dtconf.get(ScriptConfiguration.SCRIPT_LAUNCH_CMD))) {
            return StringUtils.replaceOnce(dtconf.get(ScriptConfiguration.SCRIPT_LAUNCH_CMD), "python", cmdPrefix(dtconf));
        }
        String cmdOpts = "";
        if (StringUtils.isNotBlank(dtconf.get(ScriptConfiguration.SCRIPT_CMD_OPTS))) {
            try {
                cmdOpts = dtconf.get(ScriptConfiguration.SCRIPT_CMD_OPTS);
                if (dtconf.getBoolean(ScriptConfiguration.SCRIPT_LOCALFILE,false)){
                    cmdOpts += " " + dtconf.get(ScriptConfiguration.SCRIPT_RUNNING_APPLICATIONID);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String execFile = new Path(dtconf.get(ScriptConfiguration.SCRIPT_FILES)).getName();
        return cmdPrefix(dtconf) + " " + execFile + " " + cmdOpts;
    }
}
