package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchModelColumn;
import com.dtstack.batch.domain.BatchModelColumnWithUserInfo;
import com.dtstack.batch.web.model.vo.query.BatchModelColumnAddVO;
import com.dtstack.batch.web.model.vo.query.BatchModelColumnPageQueryVO;
import com.dtstack.batch.web.model.vo.result.BatchModelColumnResultVO;
import com.dtstack.batch.web.model.vo.result.BatchModelColumnWithUserInfoResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BatchModelColumnMapstrustTransfer {
    BatchModelColumnMapstrustTransfer INSTANCE = Mappers.getMapper(BatchModelColumnMapstrustTransfer.class);

    /**
     * BatchModelColumnAddVO -> BatchModelColumn
     *
     * @param addVO
     * @return
     */
    BatchModelColumn columnAddVOToColumn(BatchModelColumnAddVO addVO);

    /**
     * BatchModelColumnPageQueryVO -> BatchModelColumnWithUserInfo
     *
     * @param pageQueryVO
     * @return
     */
    BatchModelColumnWithUserInfo pageQueryVOToColumnWithUserInfo(BatchModelColumnPageQueryVO pageQueryVO);

    /**
     * BatchModelColumn -> BatchModelColumnResultVO
     *
     * @param batchModelColumn
     * @return
     */
    BatchModelColumnResultVO batchModelColumnToBatchModelColumnResultVO(BatchModelColumn batchModelColumn);

    /**
     * BatchModelColumn -> BatchModelColumnResultVO
     *
     * @param batchModelColumn
     * @return
     */
    List<BatchModelColumnResultVO> listBatchModelColumnToListBatchModelColumnResultVO(List<BatchModelColumn> batchModelColumn);

    /**
     * PageResult<List<BatchModelColumnWithUserInfo>> -> PageResult<List<BatchModelColumnWithUserInfoResultVO>>
     *
     * @param pageResult
     * @return
     */
    PageResult<List<BatchModelColumnWithUserInfoResultVO>> pageResultWithUserInfoToPageResultWithUserInfoResultVO(PageResult<List<BatchModelColumnWithUserInfo>> pageResult);

}
