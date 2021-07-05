package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author sanyue
 * @date 2018/11/26
 */
@Data
public class CarbonDataBase extends BaseSource{


    private String path;

    private JSONObject hadoopConfig;

    private String defaultFS;

    private String table;

    private String database;

}
