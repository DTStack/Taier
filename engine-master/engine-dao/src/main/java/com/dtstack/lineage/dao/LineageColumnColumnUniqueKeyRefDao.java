package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageColumnColumnUniqueKeyRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageColumnColumnAppRefDao
 * @Description
 * @Date 2020/10/28 18:02
 * @Created chener@dtstack.com
 */
public interface LineageColumnColumnUniqueKeyRefDao {

    Integer batchInsert(List<LineageColumnColumnUniqueKeyRef> columnColumns);

    Integer deleteByUniqueKey(@Param("uniqueKey")String uniqueKey);

    Integer deleteByLineageIdAndUniqueKey(@Param("appType") Integer appType, @Param("uniqueKey")String uniqueKey, @Param("columnLineageId")Long columnLineageId);

}
