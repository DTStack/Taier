package com.dtstack.batch.sync.template;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author jingzhen
 */
@Data
public abstract class HBaseBase extends BaseSource {
    protected Map<String, Object> hbaseConfig;
    protected List<JSONObject> column;
    protected String encoding = "utf-8";
    protected String mode = "normal";
    protected String table;
    protected String remoteDir;
    protected Map<String, Object> sftpConf;
}