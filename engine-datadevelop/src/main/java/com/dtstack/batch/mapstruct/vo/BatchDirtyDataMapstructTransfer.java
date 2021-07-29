package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.po.BatchDirtyDataTopPO;
import com.dtstack.batch.vo.BatchTableInfoVO;
import com.dtstack.batch.vo.ChartDataVO;
import com.dtstack.batch.web.dirtydata.vo.result.BatchDirtyDataTableResultVO;
import com.dtstack.batch.web.dirtydata.vo.result.BatchDirtyDataTopResultVO;
import com.dtstack.batch.web.dirtydata.vo.result.BatchDirtyDateTrendResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface BatchDirtyDataMapstructTransfer {

    BatchDirtyDataMapstructTransfer INSTANCE = Mappers.getMapper(BatchDirtyDataMapstructTransfer.class);

    /**
     * ChartDataVO  ->  BatchDirtyDateTrendResultVO
     *
     * @param vo
     * @return
     */
    BatchDirtyDateTrendResultVO newChartDataVoToDirtyDateTrendResultVo(ChartDataVO vo);

    /**
     * List<BatchDirtyDataTopPO>  ->  List<BatchDirtyDataTopResultVO>
     *
     * @param list
     * @return
     */
    List<BatchDirtyDataTopResultVO> newDirtyDataTopPoToDirtyDataTopResultVo(List<BatchDirtyDataTopPO> list);

    /**
     * PageResult<List<BatchTableInfoVO>>  ->  PageResult<List<BatchDirtyDataTableResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchDirtyDataTableResultVO>> newTableInfoVoToDirtyDataTableResultVo(PageResult<List<BatchTableInfoVO>> result);
}