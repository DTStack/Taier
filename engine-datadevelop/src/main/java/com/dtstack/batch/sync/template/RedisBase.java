package com.dtstack.batch.sync.template;

/**
 * @author jiangbo
 * @date 2018/7/3 13:08
 */
public class RedisBase extends BaseSource{

    private String hostPort = "localhost:6379";

    private String password = "";

    private int database = 0;

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}
