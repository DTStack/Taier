package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.dto.BatchFunctionDTO;
import com.dtstack.batch.vo.TaskCatalogueVO;
import com.dtstack.batch.web.function.vo.query.BatchFunctionAddVO;
import com.dtstack.batch.web.function.vo.query.BatchFunctionQueryVO;
import com.dtstack.batch.web.function.vo.query.BatchFunctionVO;
import com.dtstack.batch.web.function.vo.result.BatchFunctionAddResultVO;
import com.dtstack.batch.web.function.vo.result.BatchFunctionQueryResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FunctionMapstructTransfer {
    FunctionMapstructTransfer INSTANCE = Mappers.getMapper(FunctionMapstructTransfer.class);

    /**
     * BatchFunctionVO  ->  BatchFunction
     *
     * @param vo
     * @return
     */
    BatchFunction newFunctionVoToFunctionVo(BatchFunctionVO vo);

    /**
     * BatchFunctionAddVO  ->  BatchFunction
     *
     * @param vo
     * @return
     */
    BatchFunction newFunctionAddVoToFunctionVo(BatchFunctionAddVO vo);

    /**
     * BatchFunctionQueryVO  ->  BatchFunctionDTO
     *
     * @param vo
     * @return
     */
    BatchFunctionDTO newFunctionQueryVoToDTO(BatchFunctionQueryVO vo);

    /**
     * com.dtstack.batch.vo.BatchFunctionVO  ->  BatchFunctionQueryResultVO
     *
     * @param vo
     * @return
     */
    BatchFunctionQueryResultVO newFunctionToFunctionResultVo(com.dtstack.batch.vo.BatchFunctionVO vo);

    /**
     * TaskCatalogueVO  ->  BatchFunctionAddResultVO
     *
     * @param vo
     * @return
     */
    BatchFunctionAddResultVO newTaskCatalogueVoToFunctionAddResultVo(TaskCatalogueVO vo);

    /**
     * PageResult<List<com.dtstack.batch.vo.BatchFunctionVO>>  ->  PageResult<List<BatchFunctionQueryResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchFunctionQueryResultVO>> newFunctionVoToFunctionQueryResultVo(PageResult<List<com.dtstack.batch.vo.BatchFunctionVO>> result);
}
