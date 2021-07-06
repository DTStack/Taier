package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchDataSourceCenter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description 离线和数据源中心的关联表
 * @date 2021/6/15 4:46 下午
 */
public interface BatchDataSourceCenterDao {

    /**
     * 根据离线id查询离线和数据源中心的关联信息
     * @param id
     * @return
     */
    BatchDataSourceCenter getSourceCenterByCenterId(@Param("id") Long id);

    /**
     * 根据项目id删除数据源
     * @param projectId
     * @param userId
     * @return
     */
    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    /**
     * 根据id删除数据源
     * @param sourceId
     * @param userId
     * @return
     */
    Integer deleteById(@Param("sourceId") Long sourceId, @Param("userId") Long userId);

    /**
     * 引入数据源
     * @param batchDataSourceCenter
     * @return
     */
    Integer insertDataSource(BatchDataSourceCenter batchDataSourceCenter);

    /**
     * 根据离线数据源id列表 查询 数据源中心id列表
     * @param sourceIds
     * @return
     */
    List<Long> getInfoIdsByCenterIds(@Param("sourceIds") List<Long> sourceIds);

    /**
     * 根据离线数据源id 查询 数据源中心的id
     * @param sourceId
     * @return
     */
    Long getInfoIdByCenterId(@Param("sourceId") Long sourceId);

    /**
     * 根据项目id获取引入的数据源中心id列表
     * @param projectId
     * @return
     */
    List<BatchDataSourceCenter> getInfoIdsByProject(@Param("projectId") Long projectId);

    /**
     * 根据数据源中心id 查询离线数据源信息
     * @param infoId
     * @return
     */
    BatchDataSourceCenter getDataSourceCenterByInfoId(@Param("projectId") Long projectId, @Param("infoId") Long infoId);

    /**
     * 获取所有的数据源中心的id
     * @return
     */
    List<BatchDataSourceCenter> getAllDataSourceCenterList();

    /**
     * 根据tenantId 获取本租户下所有的数据源信息
     *
     * @param tenantId
     * @return
     */
    List<BatchDataSourceCenter> getListByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 根据数据源中心的id 获取数据源id
     * @param projectId
     * @param infoIdList
     */
    List<BatchDataSourceCenter> getDataSourceCenterByInfoIds(@Param("projectId") Long projectId, @Param("infoIdList") List<Long> infoIdList);

    /**
     * 获取项目下的默认数据源
     * @param projectId
     * @return
     */
    List<BatchDataSourceCenter> getDefaultDataSourceCenterByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据数据源中心id获取默认数据源对应的projectId
     * @param dataInfoId
     * @return
     */
    BatchDataSourceCenter getDefaultDataSourceCenterByDataInfoId(@Param("dataInfoId") Long dataInfoId);

    /**
     * 根据id查询数据源管理信息
     * @param sourceId
     * @return
     */
    BatchDataSourceCenter getDataSourceCenterById(@Param("sourceId") Long sourceId);
}
