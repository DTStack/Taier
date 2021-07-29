package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Reason:
 * Date: 2017/6/7
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface BatchTaskParamDao {

    Integer deleteByTaskId(@Param("taskId") long taskId);

    List<BatchTaskParam> listByTaskId(@Param("taskId") long taskId);

    Integer insert(BatchTaskParam batchTaskParam);

    Integer batchInsert(@Param("list") List<BatchTaskParam> batchTaskParamList);

    Integer update(BatchTaskParam batchTaskParam);
}
