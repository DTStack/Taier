package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.enums.EComponentApiType;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.client.ClientCache;
import com.dtstack.engine.common.client.IClient;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.lineage.dao.LineageDataSetDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 4:18 下午 2020/10/30
 */
@Service
public class LineageDataSetInfoService {


    @Autowired
    private LineageDataSourceService sourceService;

    @Autowired
    private LineageDataSetDao lineageDataSetDao;

    /**
     * @author zyd
     * @Description 根据条件查询表信息，如果没有则新增
     * @Date 2020/10/30 4:20 下午
     * @param sourceId:
     * @param dbName:
     * @param tableName:
     * @param schemaName:
     * @return: com.dtstack.lineage.impl.LineageTableInfoService
     **/
    public LineageDataSetInfo getOneBySourceIdAndDbNameAndTableName(Integer sourceId, String dbName, String tableName, String schemaName){

        LineageDataSetInfo lineageDataSetInfo = lineageDataSetDao.getOneBySourceIdAndDbNameAndTableName(sourceId,dbName,tableName,schemaName);
        if(null != lineageDataSetInfo){
            return lineageDataSetInfo;
        }
        //如果没有查到，则新增表信息
        //根据sourceId查询数据源信息
        LineageDataSource dataSource = sourceService.getDataSourceById(sourceId);
        if(null == dataSource){
            throw new RdosDefineException("该数据源不存在");
        }
        lineageDataSetInfo = generateDataSet(sourceId, tableName, schemaName, dataSource, dbName);
        lineageDataSetDao.insertTableInfo(lineageDataSetInfo);
        return lineageDataSetInfo;
    }

    private LineageDataSetInfo generateDataSet(Integer sourceId, String tableName, String schemaName, LineageDataSource dataSource, String dbName) {
        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        BeanUtils.copyProperties(dataSource,dataSetInfo);
        dataSetInfo.setSourceId(sourceId);
        dataSetInfo.setDbName(dbName);
        dataSetInfo.setIsManual(0);
        dataSetInfo.setSchemaName(schemaName);
        dataSetInfo.setSetType(0);
        dataSetInfo.setTableName(tableName);
        //生成tableKey
        String tableKey = generateTableKey(sourceId, dbName, tableName);
        dataSetInfo.setTableKey(tableKey);
        return dataSetInfo;
    }

    private String generateTableKey(Integer sourceId, String dbName, String tableName) {

        return sourceId+dbName+tableName;
    }

    public List<Column> getTableColumns(LineageDataSetInfo dataSetInfo,String sql){

        //获取数据源信息
        LineageDataSource dataSource = sourceService.getDataSourceById(dataSetInfo.getSourceId());
        if(null == dataSource){
            throw new RdosDefineException("找不到对应的数据源");
        }
        ClientCache clientCache = ClientCache.getInstance();
        IClient iClient ;
        try {
            iClient =  clientCache.getClient(EComponentType.getByCode(dataSetInfo.getSourceType()).getName(),dataSource.getDataJason());
        } catch (ClientAccessException e) {
            throw new RdosDefineException("获取client异常",e);
        }
           return iClient.getAllColumns(dataSetInfo.getTableName(),dataSetInfo.getDbName());
    }
}
