package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    LineageDataSetInfo getOneBySourceIdAndDbNameAndTableName(@Param("sourceId") Long sourceId, @Param("dbName") String dbName,
                                                             @Param("tableName") String tableName,@Param("schemaName") String schemaName);

    /**
     * @author zyd
     * @Description 根据id查询表信息
     * @Date 2020/11/11 5:12 下午
     * @param id:
     * @return: com.dtstack.engine.api.domain.LineageDataSetInfo
     **/
    LineageDataSetInfo getOneById(Long id);

    /**
     * @author zyd
     * @Description 根据ids批量查询表信息
     * @Date 2020/11/11 5:15 下午
     * @param ids:
     * @return: com.dtstack.engine.api.domain.LineageDataSetInfo
     **/
    List<LineageDataSetInfo> getDataSetListByIds(@Param("ids") List<Long> ids);
}
