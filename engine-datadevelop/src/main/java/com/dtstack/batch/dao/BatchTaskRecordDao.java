package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskRecord;
import com.dtstack.batch.dto.BatchTaskRecordDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BatchTaskRecordDao {

    Integer insert(BatchTaskRecord record);

    Integer insertAll(@Param("records") List<BatchTaskRecord> records);

    BatchTaskRecord getOne(@Param("id") Long id);

    Integer deleteById(@Param("taskId") Long id, @Param("projectId") Long projectId, @Param("operatorId") Long userId);

    List<BatchTaskRecord> generalQuery(PageQuery<BatchTaskRecordDTO> pageQuery);

    Integer generalCount(PageQuery<BatchTaskRecordDTO> pageQuery);

    Integer deleteByProjectId(@Param("projectId") Long projectId);
}
