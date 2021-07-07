package com.dtstack.batch.sync.template;


import lombok.Data;

import java.util.List;

@Data
public class FtpBase extends BaseSource{

    protected String protocol;
    protected String host;
    protected String username;
    protected String password;
    protected String fieldDelimiter = "\001";
    protected String encoding;
    protected Integer port;
    protected List column;
    protected String connectPattern;
    protected String rsaPath;
    protected String auth;

    /**
     * 写入文件名 可以多个 以逗号分割 离线不处理
     */
    private String fileName;

}

