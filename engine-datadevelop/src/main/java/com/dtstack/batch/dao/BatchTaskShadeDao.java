package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskShade;
import com.dtstack.batch.domain.po.TaskIdAndVersionIdPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
public interface BatchTaskShadeDao {

    BatchTaskShade getOne(@Param("id") Long id);

    List<BatchTaskShade> listByIdsAndType(@Param("ids") List<Long> ids);

    List<Long> listTaskId();

    List<TaskIdAndVersionIdPO> listTaskIdAndVersionId();

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
