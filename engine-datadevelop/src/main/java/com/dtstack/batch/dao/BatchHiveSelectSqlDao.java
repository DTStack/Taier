package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchHiveSelectSql;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TODO 修改为非特定hive
 * @author jiangbo
 */
public interface BatchHiveSelectSqlDao {

    BatchHiveSelectSql getOne(@Param("id") Long id);

    BatchHiveSelectSql getByJobId(@Param("jobId") String jobId, @Param("tenantId") Long tenantId, @Param("isDeleted") Integer isDeleted);

    Integer insert(BatchHiveSelectSql selectSql);

    Integer updateGmtModify(@Param("jobId") String jobId, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    Integer deleteByJobId(@Param("jobId") String jobId, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    List<BatchHiveSelectSql> listSelectTypeByGmtModified(@Param("timeDiff")Integer timeDiff);

    Integer deleteByIds(@Param("list") List<Long> list);

    Integer deleteByJobIds(@Param("list") List<String> list);

    List<BatchHiveSelectSql> listBySqlType(@Param("type") Integer type);

    Integer deleteByProjectId(@Param("projectId") Long projectId);
}
