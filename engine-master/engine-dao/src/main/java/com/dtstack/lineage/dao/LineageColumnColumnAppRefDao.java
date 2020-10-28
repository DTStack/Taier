package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageColumnColumnAppRef;

import java.util.List;

/**
 * @author chener
 * @Classname LineageColumnColumnAppRefDao
 * @Description
 * @Date 2020/10/28 18:02
 * @Created chener@dtstack.com
 */
public interface LineageColumnColumnAppRefDao {

    Integer batchInsert(List<LineageColumnColumnAppRef> columnColumns);


}
