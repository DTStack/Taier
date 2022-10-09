package com.dtstack.taier.develop.service.template.rdbms;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author: gengxin
 * @Date: 2021/10/30 8:00 下午
 * 对接flink的字段
 */
public class RDBReader extends BaseReaderPlugin implements Reader {
    private String session;
    private String preSql;
    private String postSql;
    private String writeMode;
    protected List<Long> sourceIds;
    /**
     * 连接信息
     */
    private List<ConnectionDTO> connection;
    /**
     * rdb中 需要schema key的
     */
    private static List<DataBaseType> needSchema = Lists.newArrayList(DataBaseType.Oracle, DataBaseType.SQLServer, DataBaseType.Greenplum6, DataBaseType.PostgreSQL, DataBaseType.LIBRA, DataBaseType.ADB_FOR_PG);

    /**
     * 密码
     */
    private String password;
    /**
     * 用户名
     */
    private String username;
    /**
     * jdbcurl
     */
    private String jdbcUrl;
    /**
     * sourceId 做替换数据源信息用
     */
    private Long sourceId;

    /**
     * 表名
     */
    private List<String> table;
    /**
     * 字段dtoList
     */
    private List<ColumnDTO> column;
    /**
     * schema
     */
    private String schema;

    /**
     * 数据库类型,mysql 、oracle、 hive
     */
    private DataBaseType type;

    /**
     *增量标识字段
     */
    private String increColumn;

    /**
     * 采集起点
     */
    private String startLocation;



    /**
     * 是否是间隔轮询
     */
    private Boolean polling = true;

    /**
     * 轮询间隔时间
     */
    private Long pollingInterval;

    @Override
    public String pluginName() {
        return null;
    }

    @Override
    public void checkFormat(JSONObject jsonObject) {

    }

}
