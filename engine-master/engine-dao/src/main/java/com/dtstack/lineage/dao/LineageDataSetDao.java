package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname LineageTableInfo
 * @Description dataSet：数据集，一般为表；也可能是文件或者kafka数据流等形式
 * @Date 2020/10/22 20:04
 * @Created chener@dtstack.com
 */
public interface LineageDataSetDao {

    Integer insertTableInfo(LineageDataSetInfo lineageDataSetInfo);

    LineageDataSetInfo getTableInfo(@Param("sourceId")Long sourceId, @Param("db")String db, @Param("tableName")String tableName);

    Integer deleteTableInfo(@Param("id")Long id);

    LineageDataSetInfo getOneBySourceIdAndDbNameAndTableName(@Param("sourceId") Integer sourceId, @Param("dbName") String dbName,
                                                             @Param("tableName") String tableName,@Param("schemaName") String schemaName);
}
