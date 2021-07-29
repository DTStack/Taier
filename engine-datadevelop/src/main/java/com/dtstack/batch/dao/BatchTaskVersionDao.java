package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskVersion;
import com.dtstack.batch.domain.BatchTaskVersionDetail;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author: toutian
 */
public interface BatchTaskVersionDao {

    List<BatchTaskVersionDetail> listByTaskId(@Param("taskId") Long taskId, @Param("pageQuery") PageQuery pageQuery);

    Integer insert(BatchTaskVersion batchTaskVersion);

    BatchTaskVersionDetail getByVersionId(@Param("versionId") Long versionId);

    List<BatchTaskVersionDetail> getByVersionIds(@Param("versionIds") List<Integer> versionId);

    List<BatchTaskVersionDetail> getByTaskIds(@Param("taskIds") List<Long> taskIds);

    List<BatchTaskVersionDetail> getWithoutSqlByTaskIds(@Param("taskIds") List<Long> taskIds);

    List<BatchTaskVersionDetail> getLatestTaskVersionByTaskIds(@Param("taskIds") List<Long> taskIds);

    Integer getMaxVersionId(@Param("taskId") Long taskId);

    BatchTaskVersionDetail getBytaskIdAndVersionId(@Param("taskId") Long taskId, @Param("versionId") Long versionId);

    BatchTaskVersion getByTaskIdAndVersion(@Param("taskId") Long taskId, @Param("version") Integer version);

    Integer deleteByProjectId(@Param("projectId") Long projectId);
}
