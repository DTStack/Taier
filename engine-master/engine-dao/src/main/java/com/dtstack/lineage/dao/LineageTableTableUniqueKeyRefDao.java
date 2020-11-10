package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageTableTableUniqueKeyRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableAppRefDao
 * @Description
 * @Date 2020/10/28 18:01
 * @Created chener@dtstack.com
 */
public interface LineageTableTableUniqueKeyRefDao {
    Integer deleteByUniqueKey(@Param("appType") Integer appType, @Param("uniqueKey")String uniqueKey);

    Integer deleteByLineageTableIdAndUniqueKey(@Param("appType")Integer appType, @Param("uniqueKey")String uniqueKey, @Param("lineageTableId")Long lineageTableId);

    Integer batchInsert(List<LineageTableTableUniqueKeyRef> resList);
}
