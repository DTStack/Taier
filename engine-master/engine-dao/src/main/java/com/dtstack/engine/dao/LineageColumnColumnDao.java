package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author chener
 * @Classname LineageColumnColumnDao
 * @Description TODO
 * @Date 2020/10/22 20:05
 * @Created chener@dtstack.com
 */
public interface LineageColumnColumnDao {

    Integer batchInsertColumnColumn(List<LineageColumnColumn> columnColumns);

//    Integer deleteByUniqueKey(@Param("uniqueKey")String uniqueKey);

    List<LineageColumnColumn> queryColumnInputList(@Param("appType")Integer appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);

    List<LineageColumnColumn> queryColumnResultList(@Param("appType")Integer appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);

    LineageColumnColumn queryByLineageKey(@Param("appType")Integer appType,@Param("lineageKey")String lineageKey);

    List<LineageColumnColumn> queryByLineageKeys(@Param("appType")Integer appType, @Param("keys") Set<String> columnLineageKeys);

    List<String> queryTableLineageInputColumns(@Param("tableId")Long tableId);

    List<String> queryTableLineageResultColumns(@Param("tableId")Long tableId);

    /**
     * 根据taskId和appType查询字段血缘
     * @param taskId
     * @param appType
     * @return
     */
    List<LineageColumnColumn> queryColumnLineageByTaskIdAndAppType(@Param("taskId") Long taskId,@Param("appType") Integer appType);
}
