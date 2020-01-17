package com.dtstack.task.dao;

import com.dtstack.task.domain.BatchTaskTaskShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface BatchTaskTaskShadeDao {

    BatchTaskTaskShade getOne(@Param("id") long id);

    List<BatchTaskTaskShade> listChildTask(@Param("parentTaskId") long parentTaskId);

    List<BatchTaskTaskShade> listParentTask(@Param("childTaskId") long childTaskId);

    Integer deleteByTaskId(@Param("taskId") long taskId);

    Integer insert(BatchTaskTaskShade batchTaskTaskShade);

    Integer update(BatchTaskTaskShade batchTaskTaskShade);


}
