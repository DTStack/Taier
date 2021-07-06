package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchDataSourceTaskRef;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.dto.BatchDataSourceTaskDto;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Reason:
 * Date: 2017/8/22
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
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
