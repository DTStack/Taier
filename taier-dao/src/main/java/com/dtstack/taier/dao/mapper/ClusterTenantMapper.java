package com.dtstack.taier.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taier.dao.domain.ClusterTenant;
import com.dtstack.taier.dao.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClusterTenantMapper extends BaseMapper<ClusterTenant> {

    Integer updateQueueId(@Param("tenantId") Long tenantId, @Param("clusterId") Long clusterId, @Param("queueId") Long queueId);

    Integer generalCount(@Param("clusterId") Long clusterId, @Param("tenantName") String tenantName);

    List<ClusterTenant> generalQuery(@Param("query") PageQuery<Object> query, @Param("clusterId") Long clusterId, @Param("tenantName") String tenantName);

    List<Long> listTenantIdByQueueIds(@Param("queueIds") List<Long> queueIds);

    Long getQueueIdByTenantId(@Param("tenantId") Long tenantId);

    List<ClusterTenant> listByClusterId(@Param("clusterId") Long clusterId);

    Long getClusterIdByTenantId(@Param("tenantId") Long tenantId);

}

