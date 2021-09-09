package com.dtstack.batch.sync.template;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.pluginapi.pojo.Column;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2019/12/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Data
public class ImpalaHdfsBase extends HDFSBase {
    /**
     * remoteDir
     * sftpConf
     * 用于kerberosConfig  如果hdfs没有开启 kerberos 无需配置
     * impala 暂时不支持这两个参数
     */

    protected String partition;
    protected String writeMode;
    protected String password;
    protected String username;
    protected String jdbcUrl;
    private String table;
    protected JSONObject kerberosConfig;

    protected List<Column> allColumns = new ArrayList<>();
    protected List<Column> partitionColumns = new ArrayList<>();
    protected List<String> fullColumnNames = new ArrayList<>();
    protected List<String> fullColumnTypes = new ArrayList<>();

    protected String fileName = "";

}
