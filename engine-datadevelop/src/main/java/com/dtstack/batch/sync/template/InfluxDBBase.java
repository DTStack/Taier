package com.dtstack.batch.sync.template;

import lombok.Data;

import java.util.List;

/**
 * shixi
 * 2021-06-09 11:33
 * 用于 InfluxDB 数据同步
 */
@Data
public class InfluxDBBase extends BaseSource{

    private String username = "admin";

    private String password;

    private String url;

    private String table;

    private String schema;

    protected List column;


}
