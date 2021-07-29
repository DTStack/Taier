package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchTableActionRecord;
import com.dtstack.batch.vo.HiveActionRecordSerchVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.table.vo.query.BatchHiveActionRecordSerchInfoVO;
import com.dtstack.batch.web.table.vo.result.BatchTableActionRecordResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HiveActionMapstructTransfer {

    HiveActionMapstructTransfer INSTANCE = Mappers.getMapper(HiveActionMapstructTransfer.class);

    /**
     * HiveActionRecordSerchInfoVO -> HiveActionRecordSerchVO
     * @param infoVO
     * @return
     */
    HiveActionRecordSerchVO hiveActionRecordSerchInfoVOToHiveActionRecordSerchVO(BatchHiveActionRecordSerchInfoVO infoVO);

    /**
     * PageResult<List<BatchTableActionRecord>> -> PageResult<List<BatchTableActionRecordResultVO>>
     * @param page
     * @return
     */
    PageResult<List<BatchTableActionRecordResultVO>> tableActionAPageRecordToTableActionRecordResultVO(PageResult<List<BatchTableActionRecord>> page);
}
