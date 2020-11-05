package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageTableTableUniqueKeyRef;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableAppRefDao
 * @Description
 * @Date 2020/10/28 18:01
 * @Created chener@dtstack.com
 */
public interface LineageTableTableUniqueKeyDao {
    Integer deleteByUniqueKey(Integer appType,String uniqueKey);

    Integer batchInsert(List<LineageTableTableUniqueKeyRef> resList);
}
