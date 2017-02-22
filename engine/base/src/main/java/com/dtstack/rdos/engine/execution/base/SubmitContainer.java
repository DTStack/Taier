package com.dtstack.rdos.engine.execution.base;

/**
 * Reason:
 * Date: 2017/2/22
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SubmitContainer {

    private ClientType clientType;

    private String zkNamespace;

    private String host;

    private String jobManagerPort;

    private String restPort;

    /**
     * 设置上传jar文件临时目录,如果未设置默认是/tmp/flinkjar
     */
    private String jarFileTmpPath;

    public void start(){

    }


    /**********************************************/
    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getZkNamespace() {
        return zkNamespace;
    }

    public void setZkNamespace(String zkNamespace) {
        this.zkNamespace = zkNamespace;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getJobManagerPort() {
        return jobManagerPort;
    }

    public void setJobManagerPort(String jobManagerPort) {
        this.jobManagerPort = jobManagerPort;
    }

    public String getRestPort() {
        return restPort;
    }

    public void setRestPort(String restPort) {
        this.restPort = restPort;
    }

    public String getJarFileTmpPath() {
        return jarFileTmpPath;
    }

    public void setJarFileTmpPath(String jarFileTmpPath) {
        this.jarFileTmpPath = jarFileTmpPath;
    }
}
