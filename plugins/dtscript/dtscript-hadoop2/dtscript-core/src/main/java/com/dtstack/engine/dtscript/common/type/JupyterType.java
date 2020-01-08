package com.dtstack.engine.dtscript.common.type;


import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.client.ClientArguments;
import com.dtstack.engine.dtscript.util.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JupyterType extends AppType {

    private final static String JUPYTER_NOTEBOOK_CONFIG_PREFIX = "c.";
    private final static String JUPYTER_NOTEBOOK_CONFIG_SIGN = "=";

    private final static String JUPYTER_NOTEBOOK_CONFIG_PORT_TMP = "#{port}";

    private final static String LINEFEED = "\n";
    private final static String JUPYTER_CONFIG_FILENAME = "jupyter_notebook_config.py";
    private final static String JUPYTER_BIN_PATH = "jupyter.path";
    private final static String JUPYTER_WORKSPACE_ROOT = "jupyter.workspace.root";
    private final static String JUPYTER_WORKSPACE = "jupyter.workspace";

    @Override
    public String buildCmd(ClientArguments clientArguments, YarnConfiguration conf) {

        String jupyterBinPath = conf.get(JUPYTER_BIN_PATH, "");
        if (StringUtils.isBlank(jupyterBinPath)) {
            throw new IllegalArgumentException(JUPYTER_BIN_PATH + " must be set");
        }
        String jupyterWorkspaceRoot = conf.get(JUPYTER_WORKSPACE_ROOT, "");
        if (StringUtils.isBlank(jupyterWorkspaceRoot)) {
            throw new IllegalArgumentException(JUPYTER_WORKSPACE_ROOT + " must be set");
        }
        if (!jupyterWorkspaceRoot.endsWith("/")) {
            jupyterWorkspaceRoot += "/";
        }
        String jupyterWorkspace = jupyterWorkspaceRoot + clientArguments.getAppName();
        conf.set(JUPYTER_WORKSPACE, jupyterWorkspace);

        StringBuilder bashScript = new StringBuilder(400);
        bashScript.append(cmdPrefix(conf)).append(" <<JUPYTER").append(LINEFEED);
        bashScript.append("if [ ! -d \"").append(jupyterWorkspace).append("\" ]; then").append(LINEFEED);
        bashScript.append("  mkdir \"").append(jupyterWorkspace).append("\"").append(LINEFEED);
        bashScript.append("fi").append(LINEFEED);
        bashScript.append("cd \"").append(jupyterWorkspace).append("\"").append(LINEFEED);
        bashScript.append(generateJupyterNotebookConfig(conf, jupyterWorkspace));
        bashScript.append(jupyterBinPath).append(" --config ").append(jupyterWorkspace).append("/").append(JUPYTER_CONFIG_FILENAME).append(" --allow-root").append(LINEFEED);
        bashScript.append("JUPYTER");
        return bashScript.toString();
    }

    @Override
    public String cmdPrefix(YarnConfiguration config) {
        return "bash";
    }

    @Override
    public String cmdContainerExtra(String cmd, DtYarnConfiguration conf, Map<String, Object> containerInfo) {
        String jupyterWorkspace = conf.get(JUPYTER_WORKSPACE_ROOT, "");
        File jupyterWorkspaceDir = new File(jupyterWorkspace);
        if (!jupyterWorkspaceDir.exists()) {
            if (!jupyterWorkspaceDir.mkdirs()) {
                throw new RuntimeException("create dir of " + jupyterWorkspace + "failed");
            }
        }

        String[] configPort = conf.getStrings(DtYarnConfiguration.APP_CONTAINER_PORT_RANGE, "8888", "65535");
        int portStart = 8888;
        int portEnd = 65535;
        if (configPort.length == 1) {
            portStart = Integer.valueOf(configPort[0]);
        } else if (configPort.length == 2) {
            portStart = Integer.valueOf(configPort[0]);
            portEnd = Integer.valueOf(configPort[1]);
        }
        int port = NetUtils.getAvailablePortRange(portStart, portEnd);
        containerInfo.put("port", port);
        return cmd.replace(JUPYTER_NOTEBOOK_CONFIG_PORT_TMP, String.valueOf(port));
    }

    private String generateJupyterNotebookConfig(YarnConfiguration conf, String workspace) {
        StringBuilder jncsb = new StringBuilder(200);
        jncsb.append("cat > ").append(JUPYTER_CONFIG_FILENAME).append(" <<EOF").append(LINEFEED);
        Iterator<Map.Entry<String, String>> confEntryIt = conf.iterator();
        while (confEntryIt.hasNext()) {
            Map.Entry<String, String> confEntry = confEntryIt.next();
            if (confEntry.getKey().startsWith(JUPYTER_NOTEBOOK_CONFIG_PREFIX)) {
                jncsb.append(confEntry.getKey()).append(JUPYTER_NOTEBOOK_CONFIG_SIGN).append(confEntry.getValue()).append(LINEFEED);
            }
        }
        jncsb.append("c.NotebookApp.notebook_dir").append(JUPYTER_NOTEBOOK_CONFIG_SIGN).append("'").append(workspace).append("'").append(LINEFEED);
        jncsb.append("c.NotebookApp.port").append(JUPYTER_NOTEBOOK_CONFIG_SIGN).append(JUPYTER_NOTEBOOK_CONFIG_PORT_TMP).append(LINEFEED);
        jncsb.append("EOF").append(LINEFEED);
        return jncsb.toString();
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