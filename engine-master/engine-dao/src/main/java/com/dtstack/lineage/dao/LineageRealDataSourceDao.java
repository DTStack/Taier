package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageRealDataSource;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname LineageRealDataSourceDao
 * @Description 真实数据源
 * @Date 2020/10/22 20:03
 * @Created chener@dtstack.com
 */
public interface LineageRealDataSourceDao {


    Integer addRealDataSource(LineageRealDataSource lineageRealDataSource);

    Integer updateRealDataSource(LineageRealDataSource lineageRealDataSource);

    Integer deleteRealDataSource(@Param("id")Long id);

    LineageRealDataSource getOne(@Param("id")Long id);

    LineageRealDataSource getOneBySourceKey(@Param("sourceKey") String sourceKey);
}
