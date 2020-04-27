package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Queue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QueueDao {

    Integer insert(Queue queue);

    List<Queue> listByEngineId(@Param("engineId") Long engineId);

    List<Queue> listByEngineIdWithLeaf(@Param("engineId") Long engineId);

    Integer update(Queue oldQueue);

    Queue getOne(@Param("id") Long id);

    Integer deleteByIds(@Param("ids") List<Long> collect, @Param("engineId") Long engineId);

    Integer countByParentQueueId(@Param("parentQueueId") Long parentQueueId);

    List<Queue> listByIds(@Param("ids") List<Long> ids);
}

