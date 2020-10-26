package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageColumnColumnDao
 * @Description TODO
 * @Date 2020/10/22 20:05
 * @Created chener@dtstack.com
 */
public interface LineageColumnColumnDao {

    Integer batchInsertColumnColumn(List<LineageColumnColumn> columnColumns);

    Integer deleteByuniqueKey(@Param("uniqueKey")String uniqueKey);

    List<LineageColumnColumn> queryColumnInputList(@Param("appType")Integer appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);

    List<LineageColumnColumn> queryColumnResultList(@Param("appType")Integer appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);
}
