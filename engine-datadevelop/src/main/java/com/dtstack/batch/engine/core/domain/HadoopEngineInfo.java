package com.dtstack.batch.engine.core.domain;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Hadoop引擎
 * Date: 2019/4/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class HadoopEngineInfo extends EngineInfo {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopEngineInfo.class);

    private static final String JDBC_URL_KEY = "jdbcUrl";

    private static final String DEFAULT_FS_KEY = "fs.defaultFS";

    private String jdbcURL;

    private String defaultFS;

    public HadoopEngineInfo(){
        super(MultiEngineType.HADOOP);
    }

    @Override
    public void init(Map<String, String> conf){
        String jdbcJsonString = conf.get("metePluginInfo");
        Map<String, Object> sparkThriftConf = JSONObject.parseObject(jdbcJsonString, HashMap.class);
        String jdbcURL = MathUtil.getString(sparkThriftConf.get(JDBC_URL_KEY));
        Preconditions.checkNotNull(jdbcURL, "HADOOP 引擎类型必须配置" + JDBC_URL_KEY);
        this.jdbcURL = jdbcURL;

        String hdfsJsonString = conf.get(EComponentType.HDFS.getTypeCode() + "");
        Map<String, Object> hdfsConf = JSONObject.parseObject(hdfsJsonString, HashMap.class);
        String defaultFS = MathUtil.getString(hdfsConf.get(DEFAULT_FS_KEY));
        Preconditions.checkNotNull(jdbcURL, "HADOOP 引擎类型必须配置" + DEFAULT_FS_KEY);
        this.defaultFS = defaultFS;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getDefaultFS() {
        return defaultFS;
    }

    public void setDefaultFS(String defaultFS) {
        this.defaultFS = defaultFS;
    }
}
