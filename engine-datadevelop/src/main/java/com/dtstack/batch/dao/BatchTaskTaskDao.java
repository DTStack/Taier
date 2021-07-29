package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Reason:
 * Date: 2017/5/5
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public interface BatchTaskTaskDao {

    List<BatchTaskTask> listByTaskId(@Param("taskId") long taskId);

    List<BatchTaskTask> listByParentTaskId(@Param("parentTaskId") long parentTaskId);

    Integer deleteByTaskId(@Param("taskId") long taskId);

    Integer delete(@Param("id") long id);

    Integer insert(BatchTaskTask batchTaskTask);

    Integer update(BatchTaskTask batchTaskTask);

    Integer deleteByParentId(@Param("parentId") long parentId, @Param("parentAppType") Integer parentAppType);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("parentAppType") Integer parentAppType);

    List<BatchTaskTask> listTaskTaskByTaskIds(@Param("taskIds") List<Long> taskIds);
}
