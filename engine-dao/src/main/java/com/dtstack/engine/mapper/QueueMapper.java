package com.dtstack.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.engine.domain.Queue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QueueMapper extends BaseMapper<Queue> {

    List<Queue> listByClusterId(@Param("clusterId") Long clusterId);

    List<Queue> listByClusterWithLeaf(@Param("clusterId") Long clusterId);

    Integer deleteByIds(@Param("ids") List<Long> collect, @Param("clusterId") Long clusterId);

    Integer countByParentQueueId(@Param("parentQueueId") Long parentQueueId);

    List<Queue> listByIds(@Param("ids") List<Long> ids);
}

