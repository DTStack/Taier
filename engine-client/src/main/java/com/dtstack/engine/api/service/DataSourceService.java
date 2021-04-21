package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.lineage.param.DataSourceParam;
import com.dtstack.engine.api.vo.lineage.param.DeleteDataSourceParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @author chener
 * @Classname DataSourceService
 * @Description 数据源接口
 * @Date 2020/10/22 20:43
 * @Created chener@dtstack.com
 */
public interface DataSourceService extends DtInsightServer {

    /**
     * 新增或修改逻辑数据源
     * @param dataSourceDTO 数据源信息
     */
    @RequestLine("POST /node/dataSource/addOrUpdateDataSource")
    ApiResponse<Long> addOrUpdateDataSource(DataSourceDTO dataSourceDTO);

    /**
     * @author zyd
     * @Description 根据appType分页查询逻辑数据源列表
     * @Date 2020/10/30 11:55 上午
     * @param appType:
     * @return: java.util.List<com.dtstack.engine.api.domain.LineageDataSource>
     **/
    @RequestLine("POST /node/dataSource/pageQuery")
    ApiResponse<PageResult<List<LineageDataSource>>> pageQueryDataSourceByAppType(@Param("appType") Integer appType,
                                                                     @Param("currentPage") Integer currentPage,
                                                                     @Param("pageSize") Integer pageSize);

    /**
     * @author zyd
     * @Description 根据id查询逻辑数据源信息
     * @Date 2020/10/30 2:25 下午
     * @param id:
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    @RequestLine("POST /node/dataSource/getDataSourceById")
    ApiResponse<LineageDataSource> getDataSourceById(Integer id);


    /**
     * @author zyd
     * @Description 接收老的推送过来的数据源
     * @Date 2020/11/25 5:27 下午
     * @param dataSourceParam:
     * @return: com.dtstack.sdk.core.common.ApiResponse
     **/
    @RequestLine("POST /node/dataSource/acquireOldDataSourceList")
    ApiResponse acquireOldDataSourceList(@Param("dataSourceParam") DataSourceParam dataSourceParam);



    /**
     * @author zyd
     * @Description 根据指定条件查询数据源
     * @Date 2020/11/30 10:05 下午
     * @param appType: 
     * @param sourceType: 
     * @param sourceName: 
     * @param dtUicTenantId: 
     * @return: void
     **/
    @RequestLine("POST /node/dataSource/getDataSourceByParams")
    ApiResponse getDataSourceByParams(@Param("appType")Integer appType,@Param("sourceType") Integer sourceType,
                                      @Param("sourceName") String sourceName,@Param("dtUicTenantId") Long dtUicTenantId);



    /**
     * @author ZYD
     * @Description 根据平台sourceId和appType修改数据源
     * @Date 2021/4/2 15:45
     * @param dataSourceDTO:
     * @return: com.dtstack.sdk.core.common.ApiResponse<java.lang.Boolean>
     **/
    @RequestLine("POST /node/dataSource/updateBySourceIdAndAppType")
    ApiResponse<Boolean>  updateDataSourceBySourceIdAndAppType(DataSourceDTO dataSourceDTO);


    /**
     * @author ZYD
     * @Description 根据项目id删除数据源
     * @Date 2021/4/21 10:09
     * @param deleteDataSourceParam:
     * @return: void
     **/
    @RequestLine("POST /node/dataSource/deleteDataSourceByProjectId")
    ApiResponse deleteDataSourceByProjectId(DeleteDataSourceParam deleteDataSourceParam);

}
