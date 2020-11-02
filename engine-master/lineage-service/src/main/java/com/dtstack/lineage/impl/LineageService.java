package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.lineage.dao.LineageColumnColumnDao;
import com.dtstack.lineage.dao.LineageTableTableDao;
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
    public SqlParseInfo parseSql(String sql,Integer dataSourceType){
        //TODO
        //解析sql基本信息
        return null;
    }

    /**
     * 解析表血缘
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @return
     */
    public LineageTableTable parseTableLineage(String sql, String defaultDb,Integer dataSourceType){
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
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        //3.根据表名和数dbName，schemaName查询表,sourceId。表不存在则需要插入表
        //4.获取表中的字段列表
        //5.解析字段级血缘关系
        //6.存储字段级血缘关系
        //TODO
    }

    /**
     * 解析字段级血缘
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @param tableColumnsMap 表字段map
     * @return
     */
    public ColumnLineageParseInfo parseColumnLineage(String sql, Integer dataSourceType,String defaultDb, Map<String, List<Column>> tableColumnsMap){
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
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        //3.根据表名和数据库名，数据库id查询表。表不存在则需要插入表
        //4.获取表中的字段列表
        //5.解析字段级血缘关系
        //6.存储字段级血缘关系
        //TODO
    }

    /**
     * 查询表上游表血缘
     * @param appType
     * @param tableId
     * @return
     */
    public List<LineageTableTable> queryTableInputLineage(Long appType,Long tableId){
        //TODO
        return null;
    }

    /**
     * 查询表下游表血缘
     * @param appType
     * @param tableId
     * @return
     */
    public List<LineageTableTable> queryTableResultLineage(Long appType,Long tableId){
        //TODO
        return null;
    }

    /**
     * 查询表级血缘关系
     * @param appType
     * @param tableId
     * @return
     */
    public List<LineageTableTable> queryTableLineages(Long appType,Long tableId){
        //TODO
        return null;
    }

    /**
     * 手动添加表级血缘
     * @param appType
     * @param lineageTableTable
     */
    public void manualAddTableLineage(Long appType,LineageTableTable lineageTableTable){
        //TODO
    }

    /**
     * 手动删除表级血缘
     * @param appType
     * @param lineageTableTable
     */
    public void manualDeleteTableLineage(Long appType,LineageTableTable lineageTableTable){
        //TODO
    }

    /**
     * 查询字段上游字段血缘
     * @return
     */
    public List<LineageColumnColumn> queryColumnInoutLineage(Long appType,Long tableId,String columnName){
        //TODO
        return null;
    }

    /**
     * 查询字段下游字段血缘
     * @return
     */
    public List<LineageColumnColumn> queryColumnResultLineage(Long appType,Long tableId,String columnName){
        //TODO
        return null;
    }

    /**
     * 查询字段级血缘关系
     * @param appType
     * @param tableId
     * @param columnName
     * @return
     */
    public List<LineageColumnColumn> queryColumnLineages(Long appType,Long tableId,String columnName){
        //TODO
        return null;
    }

    /**
     * 手动添加表级血缘
     * @param appType
     * @param lineageColumnColumn
     */
    public void manualAddColumnLineage(Long appType,LineageColumnColumn lineageColumnColumn){
        //TODO
    }

    /**
     * 手动删除字段级级血缘
     * @param appType
     * @param lineageColumnColumn
     */
    public void manualDeleteColumnLineage(Long appType,LineageColumnColumn lineageColumnColumn){
        //TODO
    }
}
