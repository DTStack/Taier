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

public class JupyterType extends AbstractAppType {

    private final static String JUPYTER_NOTEBOOK_CONFIG_PREFIX = "c.";
    private final static String JUPYTER_NOTEBOOK_CONFIG_SIGN = "=";

    private final static String JUPYTER_NOTEBOOK_CONFIG_PORT_TMP = "#{port}";

    private final static String LINEFEED = "\n";
    private final static String JUPYTER_PROJECT_ROOT = "jupyter.project.root";
    private final static String JUPYTER_PROJECT_DIR_KEY = "jupyter.project.dir";
    private final static String JUPYTER_CONFIG_DIR_KEY = "jupyter.conf.dir";
    private final static String JUPYTER_CONFIG_DIR_SUFFIX = "/conf";
    private final static String JUPYTER_CONFIG_FILENAME = "jupyter_notebook_config.py";
    private final static String JUPYTER_BIN_PATH = "jupyter.path";
    private final static String JUPYTER_WORKSPACE_DIR_KEY = "jupyter.workspace.dir";
    private final static String JUPYTER_WORKSPACE_DIR_SUFFIX = "/workspace";
    private final static String JUPYTER_ALLOW_ROOT = "jupyter.allow.root";
    private final static String JUPYTER_BASE_URL_PREFIX = "/aiworks/jupyter/";
    private final static String[] DEFAULT_PORT_RANGE = new String[]{"8888", "65535"};


    @Override
    public String buildCmd(ClientArguments clientArguments, YarnConfiguration conf) {

        String jupyterBinPath = conf.get(JUPYTER_BIN_PATH, "");
        if (StringUtils.isBlank(jupyterBinPath)) {
            throw new IllegalArgumentException(JUPYTER_BIN_PATH + " must be set");
        }
        boolean allowRoot = conf.getBoolean(JUPYTER_ALLOW_ROOT, true);

        String jupyterProjectRoot = conf.get(JUPYTER_PROJECT_ROOT, "");
        if (StringUtils.isBlank(jupyterProjectRoot)) {
            throw new IllegalArgumentException(JUPYTER_PROJECT_ROOT + " must be set");
        }
        if (!jupyterProjectRoot.endsWith("/")) {
            jupyterProjectRoot += "/";
        }
        String jupyterProject = jupyterProjectRoot + clientArguments.getAppName();
        String jupyterWorkspace = jupyterProject + JUPYTER_WORKSPACE_DIR_SUFFIX;
        String jupyterConfDir = jupyterProject + JUPYTER_CONFIG_DIR_SUFFIX;
        String jupyterBaseUrl = JUPYTER_BASE_URL_PREFIX + clientArguments.getAppName();
        conf.set(JUPYTER_PROJECT_DIR_KEY, jupyterProject);
        conf.set(JUPYTER_WORKSPACE_DIR_KEY, jupyterWorkspace);
        conf.set(JUPYTER_CONFIG_DIR_KEY, jupyterConfDir);

        StringBuilder bashScript = new StringBuilder(400);
        bashScript.append(cmdPrefix(conf)).append(" <<JUPYTER").append(LINEFEED);
        bashScript.append("if [ ! -d \"").append(jupyterWorkspace).append("\" ]; then").append(LINEFEED);
        bashScript.append("  mkdir \"").append(jupyterWorkspace).append("\"").append(LINEFEED);
        bashScript.append("fi").append(LINEFEED);
        bashScript.append("if [ ! -d \"").append(jupyterConfDir).append("\" ]; then").append(LINEFEED);
        bashScript.append("  mkdir \"").append(jupyterConfDir).append("\"").append(LINEFEED);
        bashScript.append("fi").append(LINEFEED);
        bashScript.append("cd \"").append(jupyterConfDir).append("\"").append(LINEFEED);
        bashScript.append(generateJupyterNotebookConfig(conf, jupyterWorkspace, jupyterBaseUrl));
        bashScript.append(jupyterBinPath).append(" --config ").append(jupyterConfDir).append("/").append(JUPYTER_CONFIG_FILENAME);
        if (allowRoot) {
            bashScript.append(" --allow-root");
        }
        bashScript.append(LINEFEED);
        bashScript.append("JUPYTER");
        return bashScript.toString();
    }

    @Override
    public String cmdPrefix(YarnConfiguration config) {
        return "bash";
    }

    @Override
    public String cmdContainerExtra(String cmd, DtYarnConfiguration conf, Map<String, Object> containerInfo) {
        String jupyterProject = conf.get(JUPYTER_PROJECT_DIR_KEY);
        File jupyterProjectDir = new File(jupyterProject);
        if (!jupyterProjectDir.exists()) {
            if (!jupyterProjectDir.mkdirs()) {
                throw new RuntimeException("create the jupyterWorkspaceRootDir of " + jupyterProject + " failed");
            }
        }

        String[] configPort = conf.getStrings(DtYarnConfiguration.APP_CONTAINER_PORT_RANGE, DEFAULT_PORT_RANGE);
        int portStart = Integer.valueOf(DEFAULT_PORT_RANGE[0]);
        int portEnd = Integer.valueOf(DEFAULT_PORT_RANGE[1]);
        if (configPort.length == 1) {
            portStart = Integer.valueOf(configPort[0]);
        } else if (configPort.length >= DEFAULT_PORT_RANGE.length) {
            portStart = Integer.valueOf(configPort[0]);
            portEnd = Integer.valueOf(configPort[1]);
        }
        int port = NetUtils.getAvailablePortRange(portStart, portEnd);
        containerInfo.put("port", port);
        return cmd.replace(JUPYTER_NOTEBOOK_CONFIG_PORT_TMP, String.valueOf(port));
    }

    private String generateJupyterNotebookConfig(YarnConfiguration conf, String workspace, String baseUrl) {
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
        jncsb.append("c.NotebookApp.base_url").append(JUPYTER_NOTEBOOK_CONFIG_SIGN).append("'").append(baseUrl).append("'").append(LINEFEED);
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