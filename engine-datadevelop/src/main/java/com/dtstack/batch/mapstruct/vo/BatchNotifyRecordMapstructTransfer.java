package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.NotifyRecordVO;
import com.dtstack.batch.web.notify.vo.result.NotifyRecordResultPageQueryVO;
import com.dtstack.batch.web.notify.vo.result.NotifyRecordResultVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.engine.api.dto.NotifyRecordReadDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BatchNotifyRecordMapstructTransfer {

    BatchNotifyRecordMapstructTransfer INSTANCE = Mappers.getMapper(BatchNotifyRecordMapstructTransfer.class);

    /**
     * NotifyRecordVO -> NotifyRecordResultVO
     *
     * @param notifyRecordVO
     * @return
     */
    NotifyRecordResultVO notifyRecordVOToNotifyRecordResultVO(NotifyRecordVO notifyRecordVO);

    /**
     * NotifyRecordVO -> NotifyRecordResultVO
     *
     * @param pageResult
     * @return
     */
    PageResult<List<NotifyRecordResultPageQueryVO>> myPageResultToPageResult(com.dtstack.engine.api.pager.PageResult<List<NotifyRecordReadDTO>> pageResult);
}
