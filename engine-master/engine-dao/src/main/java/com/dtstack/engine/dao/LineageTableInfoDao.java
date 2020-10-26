package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageTableInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname LineageTableInfo
 * @Description TODO
 * @Date 2020/10/22 20:04
 * @Created chener@dtstack.com
 */
public interface LineageTableInfoDao {
    Integer insertTableInfo(LineageTableInfo lineageTableInfo);

    LineageTableInfo getTableInfo(@Param("sourceId")Long sourceId,@Param("db")String db,@Param("tableName")String tableName);

    Integer deleteTableInfo(@Param("id")Long id);
}
