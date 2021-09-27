package com.dtstack.engine.dto;


import com.dtstack.engine.domain.BatchDataSource;

public class BatchDataSourceDTO extends BatchDataSource {
    private String fuzzName;

    private String jdbcUrl;

    private String password;

    private String userName;

    public String getFuzzName() {
        return fuzzName;
    }

    public void setFuzzName(String fuzzName) {
        this.fuzzName = fuzzName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
