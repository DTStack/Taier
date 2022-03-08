package com.dtstack.taier.develop.dto.devlop;


import java.util.List;

/**
 * Date: 2020/1/8
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class ConnectionDTO {
    private List<String> jdbcUrl;
    private List<String> table;
    private String password;
    private String schema;
    private String username;
    private Long sourceId;
    private Integer type;

    public List<String> getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(List<String> jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public List<String> getTable() {
        return table;
    }

    public void setTable(List<String> table) {
        this.table = table;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
