package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.dao.LineageColumnColumnDao;
import com.dtstack.engine.dao.LineageTableTableDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname LineageService
 * @Description 血缘解析、存储service
 * @Date 2020/10/23 14:43
 * @Created chener@dtstack.com
 */
@Service
public class LineageService {
    private static final Logger logger = LoggerFactory.getLogger(LineageService.class);

    @Autowired
    private LineageTableTableDao lineageTableTableDao;

    @Autowired
    private LineageColumnColumnDao lineageColumnColumnDao;

    @Autowired
    private LineageDataSourceService lineageDataSourceService;

    /**
     * 解析sql基本信息
     * @param sql 单条sql
     * @return
     */
    public SqlParseInfo parseSql(String sql){
        //TODO
        return null;
    }

    /**
     * 解析表血缘
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @return
     */
    public LineageTableTable parseTableLineage(String sql, String defaultDb){
        //TODO
        return null;
    }

    /**
     * 解析并存储表血缘
     * @param appType 应用类型
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @param engineSourceId 数据源id
     */
    public void parseAndSaveTableLineage(Integer appType,String sql, String defaultDb, Long engineSourceId){
        //TODO
    }

    /**
     * 解析字段级血缘
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @param tableColumnsMap 表字段map
     * @return
     */
    public ColumnLineageParseInfo parseColumnLineage(String sql, String defaultDb, Map<String, List<Column>> tableColumnsMap){
        //TODO
        return null;
    }

    /**
     * 解析并存储字段级血缘
     * @param appType 应用类型
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @param engineSourceId 数据源id
     */
    public void parseAndSaveColumnLineage(Integer appType,String sql, String defaultDb, Long engineSourceId){
        //TODO
    }
}
