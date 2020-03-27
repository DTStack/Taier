package com.dtstack.engine.dao;

import com.dtstack.engine.domain.BatchTaskTaskShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface BatchTaskTaskShadeDao {

    BatchTaskTaskShade getOne(@Param("id") long id);

    BatchTaskTaskShade getOneByTaskId(@Param("taskId") Long taskId, @Param("parentTaskId") Long parentTaskId);

    List<BatchTaskTaskShade> listChildTask(@Param("parentTaskId") long parentTaskId);

    List<BatchTaskTaskShade> listParentTask(@Param("childTaskId") long childTaskId);

    Integer deleteByTaskId(@Param("taskId") long taskId,@Param("appType")Integer appType);

    Integer insert(BatchTaskTaskShade batchTaskTaskShade);

    Integer update(BatchTaskTaskShade batchTaskTaskShade);


}
