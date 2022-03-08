package com.dtstack.taier.develop.service.template.es;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: sftp配置
 * @date 2021-11-09 10:45:28
 */
public class SftpConf {
    private String path;
    private String password;
    private String port;
    private String auth;
    private String host;
    private String username;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
