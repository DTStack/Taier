package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageTableTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableDao
 * @Description TODO
 * @Date 2020/10/22 20:05
 * @Created chener@dtstack.com
 */
public interface LineageTableTableDao {

    Integer batchInsertTableTable(LineageTableTable lineageTableTable);

    Integer deleteByUniqueKey(@Param("uniqueKey")String uniqueKey);

    List<LineageTableTable> queryTableInputList(@Param("appType")Integer appType,@Param("tableId")Long tableId);

    List<LineageTableTable> queryTableResultList(@Param("appType")Integer appType,@Param("tableId")Long tableId);

}
