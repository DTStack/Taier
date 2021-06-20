package com.dtstack.engine.datadevelop.dao.mapper.ide;

import com.dtstack.engine.api.domain.BatchDataSourceTaskRef;
import com.dtstack.engine.api.domain.BatchTask;
import com.dtstack.engine.api.dto.BatchDataSourceTaskDto;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/4/16
 */
public interface BatchDataSourceTaskRefDao {

    Integer countBySourceId(@Param("dataSourceId") Long dataSourceId, @Param("taskName") String taskName);

    BatchDataSourceTaskRef getBySourceIdAndTaskId(@Param("dataSourceId") Long dataSourceId, @Param("taskId") Long taskId);

    Integer deleteByTaskId(@Param("taskId") Long taskId);

    Integer insert(BatchDataSourceTaskRef batchDataSourceTaskRef);

    Integer update(BatchDataSourceTaskRef batchDataSourceTaskRef);

    List<BatchTask> pageQueryBySourceId(PageQuery<BatchDataSourceTaskDto> pageQuery);

    List<Long> listSourceIdByTaskId(@Param("taskId") Long taskId);

    Integer deleteByProjectId(@Param("projectId") Long projectId);
}
