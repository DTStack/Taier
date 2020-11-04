package com.dtstack.lineage.dao;

/**
 * @author chener
 * @Classname LineageTableTableAppRefDao
 * @Description
 * @Date 2020/10/28 18:01
 * @Created chener@dtstack.com
 */
public interface LineageTableTableUniqueKeyDao {
    void deleteByUniqueKey(Integer appType,String uniqueKey);
}
