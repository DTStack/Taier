package com.dtstack.taier.develop.service.template.es;


/**
 * @author daojin
 */
public class EsWriterParam extends EsBaseParam {

    private int bulkAction = 100;
    private String username;
    private String password;
    private SslConfig sslConfig;

    public int getBulkAction() {
        return bulkAction;
    }

    public void setBulkAction(int bulkAction) {
        this.bulkAction = bulkAction;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }
}
