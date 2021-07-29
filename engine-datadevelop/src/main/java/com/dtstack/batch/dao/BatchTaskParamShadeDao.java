package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskParamShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Reason:
 * Date: 2017/6/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public interface BatchTaskParamShadeDao {

    BatchTaskParamShade getOne(@Param("id") Long id);

    BatchTaskParamShade getByTypeAndName(@Param("taskId") long taskId, @Param("type") Integer type, @Param("paramName") String paramName);

    List<BatchTaskParamShade> listByTaskId(@Param("taskId") long taskId);

    Integer deleteByTaskId(@Param("taskId") long taskId);

    Integer insert(BatchTaskParamShade batchTaskParamShade);

    Integer update(BatchTaskParamShade batchTaskParamShade);

}
