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
 * @author chener
 * @Classname GreenplumEngineInfo
 * @Description TODO
 * @Date 2020/5/21 19:24
 * @Created chener@dtstack.com
 */
public class GreenplumEngineInfo extends EngineInfo {
    private static final Logger LOG = LoggerFactory.getLogger(GreenplumEngineInfo.class);

    private static final String JDBC_URL_KEY = "jdbcUrl";

    private static final String USER_NAME_KEY = "username";

    private String jdbcURL;

    private String userName;
    @Override
    public void init(Map<String, String> conf){
        String jsonString = conf.get(EComponentType.GREENPLUM_SQL.getTypeCode() + "");
        Map<String,Object> greenplumConf = JSONObject.parseObject(jsonString, HashMap.class);
        String jdbcUrlTmp = MathUtil.getString(greenplumConf.get(JDBC_URL_KEY));
        Preconditions.checkNotNull(jdbcUrlTmp, String.format("greenplum引擎类型必须配置: %s", JDBC_URL_KEY));
        this.jdbcURL = jdbcUrlTmp;

        String userNameTmp = MathUtil.getString(greenplumConf.get(USER_NAME_KEY));
        this.userName = userNameTmp;
    }

    public GreenplumEngineInfo() {
        super(MultiEngineType.GREENPLUM);
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
