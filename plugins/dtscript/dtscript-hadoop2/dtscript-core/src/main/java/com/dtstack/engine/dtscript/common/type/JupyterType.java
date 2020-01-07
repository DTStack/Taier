package com.dtstack.engine.dtscript.common.type;


import com.dtstack.engine.dtscript.client.ClientArguments;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.List;

public class JupyterType extends AppType {

    @Override
    public String buildCmd(ClientArguments clientArguments, YarnConfiguration conf) {
        String bash = "#!/bin/bash\n";
        String config = "cat > jupyter_notebook_config.py <<EOF" +
                "c.NotebookApp.open_browser = False\n" +
                "c.NotebookApp.allow_remote_access = True\n" +
                "c.NotebookApp.ip = '*'\n" +
                "c.NotebookApp.token = ''\n" +
                "c.NotebookApp.default_url = '/lab'\n" +
                "c.NotebookApp.port = 18888\n" +
                "c.NotebookApp.notebook_dir = '/root/engine-aiworks/test'\n" +
                "EOF ";
        return null;
    }

    @Override
    public String name() {
        return "Jupyter";
    }

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }

}