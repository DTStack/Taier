package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.BatchApplySearchVO;
import com.dtstack.batch.vo.BatchApplyVO;
import com.dtstack.batch.web.apply.vo.query.BatchApplyQueryVO;
import com.dtstack.batch.web.apply.vo.query.BatchApplyTableVO;
import com.dtstack.batch.web.apply.vo.result.BatchApplyQueryResultVO;
import com.dtstack.batch.web.apply.vo.result.BatchApplyTableResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ApplyMapstructTransfer {
    ApplyMapstructTransfer INSTANCE = Mappers.getMapper(ApplyMapstructTransfer.class);

    /**
     * BatchApplyTableVO  ->  com.dtstack.batch.vo.BatchApplyTableVO
     *
     * @param vo
     * @return
     */
    com.dtstack.batch.vo.BatchApplyTableVO newApplyTableVoToApplyTableVo(BatchApplyTableVO vo);

    /**
     * BatchApplyQueryVO  ->  BatchApplySearchVO
     *
     * @param vo
     * @return
     */
    BatchApplySearchVO newAppQueryVoToApplySearchVo(BatchApplyQueryVO vo);

    /**
     * PageResult<List<BatchApplyVO>>  ->  PageResult<List<BatchApplyQueryResultVO>>
     *
     * @param vo
     * @return
     */
    PageResult<List<BatchApplyQueryResultVO>> newApplyQueryVoToApplyQueryResultVo(PageResult<List<BatchApplyVO>> vo);

    /**
     * com.dtstack.batch.vo.BatchApplyTableVO  ->  BatchApplyTableResultVO
     *
     * @param vo
     * @return
     */
    BatchApplyTableResultVO newApplyTableVoToApplyTableResultVo(com.dtstack.batch.vo.BatchApplyTableVO vo);

}
