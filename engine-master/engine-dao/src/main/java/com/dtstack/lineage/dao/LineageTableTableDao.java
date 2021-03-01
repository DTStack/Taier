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

    Integer batchInsertTableTable(@Param("list") List<LineageTableTable> lineageTableTable);

    List<LineageTableTable> queryTableInputList(@Param("appType")Integer appType,@Param("tableId")Long tableId);

    List<LineageTableTable> queryTableResultList(@Param("appType")Integer appType,@Param("tableId")Long tableId);

    LineageTableTable queryBTableLineageKey(@Param("appType")Integer appType,@Param("tableLineageKey")String tableLineageKey);

    List<LineageTableTable> queryByTableLineageKeys(@Param("appType")Integer appType,@Param("list") List<String> keys);
}
