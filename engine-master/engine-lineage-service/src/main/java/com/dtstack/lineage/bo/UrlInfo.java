package com.dtstack.lineage.bo;

/**
 * @author jiangbo
 * @date 2018/6/28 19:26
 */
public class UrlInfo {

    private String host;

    private Integer port;

    private String db;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    @Override
    public String toString() {
        return "UrlInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", db='" + db + '\'' +
                '}';
    }
}
