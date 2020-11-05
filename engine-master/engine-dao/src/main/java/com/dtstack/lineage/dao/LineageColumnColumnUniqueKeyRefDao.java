package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageColumnColumnUniqueKeyRef;

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

    Integer deleteByUniqueKey(String uniqueKey);

}
