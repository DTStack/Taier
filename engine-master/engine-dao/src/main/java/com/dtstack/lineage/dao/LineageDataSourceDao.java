package com.dtstack.lineage.dao;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * @author zyd
     * @Description 查找符合条件的数据源数量
     * @Date 2020/10/30 2:01 下午
     * @param dataSource:
     * @return: java.lang.Integer
     **/
    Integer generalCount(@Param("model") LineageDataSource dataSource);

    /**
     * @author zyd
     * @Description 根据条件查找数据源列表
     * @Date 2020/10/30 2:13 下午
     * @param pageQuery:
     * @return: java.util.List<com.dtstack.engine.api.domain.LineageDataSource>
     **/
    List<LineageDataSource> generalQuery(PageQuery<LineageDataSource> pageQuery);

    /**
     * @author zyd
     * @Description 根据appType和sourceId查询数据源信息
     * @Date 2020/10/30 4:06 下午
     * @param sourceId:
     * @param appType:
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    LineageDataSource getOneByAppTypeAndId(@Param("sourceId") Long sourceId,@Param("appType") Integer appType);


    /**
     * @author zyd
     * @Description 根据dt租户id和数据源类型查询数据源
     * @Date 2020/11/11 4:35 下午
     @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    LineageDataSource getDataSourceByParams(LineageDataSource lineageDataSource);




    /**
     * @author zyd
     * @Description 根据id列表批量查询逻辑数据源信息
     * @Date 2020/10/30 2:25 下午
     * @param ids:
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    List<LineageDataSource> getDataSourcesByIdList(@Param("ids") List<Long> ids);
}
