package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageDataSource;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname LineageDataSource
 * @Description TODO
 * @Date 2020/10/22 20:04
 * @Created chener@dtstack.com
 */
public interface LineageDataSourceDao {

    /**
     * 插入数据源
     * @param lineageDataSource
     * @return
     */
    Integer insertDataSource(LineageDataSource lineageDataSource);

    /**
     * 更新数据源
     * @param lineageDataSource
     * @return
     */
    Integer updateDataSource(LineageDataSource lineageDataSource);

    /**
     * 删除数据源
     * @param id
     * @return
     */
    Integer deleteDataSource(@Param("id")Long id);

    /**
     * 根据id查找数据源
     * @param id
     * @return
     */
    LineageDataSource getOne(@Param("id")Long id);
}
