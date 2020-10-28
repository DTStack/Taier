package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageTableTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableDao
 * @Description 表级血缘表
 * @Date 2020/10/22 20:05
 * @Created chener@dtstack.com
 */
public interface LineageTableTableDao {

    Integer batchInsertTableTable(List<LineageTableTable> lineageTableTable);

    Integer deleteByUniqueKey(@Param("uniqueKey")String uniqueKey);

    List<LineageTableTable> queryTableInputList(@Param("appType")Integer appType,@Param("tableId")Long tableId);

    List<LineageTableTable> queryTableResultList(@Param("appType")Integer appType,@Param("tableId")Long tableId);

}
