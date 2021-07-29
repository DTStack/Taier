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
 * oracle 引擎
 * Date: 2020/4/28
 * Company: www.dtstack.com
 * @author shixi
 */

public class OracleEngineInfo extends EngineInfo {

    private static final Logger LOG = LoggerFactory.getLogger(OracleEngineInfo.class);

    //TODO
    private static final String JDBC_URL_KEY = "jdbcUrl";

    private static final String USER_NAME_KEY = "username";

    private String jdbcURL;

    private String userName;

    @Override
    public void init(Map<String, String> conf){
        String jsonString = conf.get(EComponentType.ORACLE_SQL.getTypeCode() + "");
        Map<String, Object> libraConf = JSONObject.parseObject(jsonString, HashMap.class);
        String jdbcUrlTmp = MathUtil.getString(libraConf.get(JDBC_URL_KEY));
        Preconditions.checkNotNull(jdbcUrlTmp, String.format("Oracle引擎类型必须配置: %s", JDBC_URL_KEY));
        this.jdbcURL = jdbcUrlTmp;

        String userNameTmp = MathUtil.getString(libraConf.get(USER_NAME_KEY));
        this.userName = userNameTmp;
    }

    public OracleEngineInfo() {
        super(MultiEngineType.ORACLE);
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
