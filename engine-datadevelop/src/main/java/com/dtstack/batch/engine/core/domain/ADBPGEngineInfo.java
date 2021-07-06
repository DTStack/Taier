package com.dtstack.batch.engine.core.domain;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;


/**
 * ADB For PG
 * date: 2021/6/7 2:02 下午
 * author: zhaiyue
 */
public class ADBPGEngineInfo extends EngineInfo {

    private static final String JDBC_URL_KEY = "jdbcUrl";

    private static final String USER_NAME_KEY = "username";

    private String jdbcURL;

    private String userName;

    @Override
    public void init(Map<String, String> conf){
        // @TODO 添加 adb for pg
        String jsonString = conf.get(EComponentType.ANALYTICDB_FOR_PG.getTypeCode() + "");
        Map<String, Object> libraConf = JSONObject.parseObject(jsonString, HashMap.class);
        String jdbcUrlTmp = MathUtil.getString(libraConf.get(JDBC_URL_KEY));
        Preconditions.checkNotNull(jdbcUrlTmp, String.format("AnalyticDb for PostgreSQL 引擎类型必须配置: %s", JDBC_URL_KEY));
        this.jdbcURL = jdbcUrlTmp;

        String userNameTmp = MathUtil.getString(libraConf.get(USER_NAME_KEY));
        this.userName = userNameTmp;
    }

    public ADBPGEngineInfo() {
        super(MultiEngineType.ANALYTICDB_FOR_PG);
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
