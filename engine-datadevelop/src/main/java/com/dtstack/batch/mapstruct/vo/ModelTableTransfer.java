package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchModelTable;
import com.dtstack.batch.domain.BatchModelTableWithUserName;
import com.dtstack.batch.engine.rdbms.common.dto.ColumnDTO;
import com.dtstack.batch.engine.rdbms.common.dto.TableDTO;
import com.dtstack.batch.vo.BatchColumnVO;
import com.dtstack.batch.vo.BatchTableSearchVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.table.vo.result.BatchModelTableResultVO;
import com.dtstack.batch.web.table.vo.result.BatchModelTableWithUserNameResultVO;
import com.dtstack.batch.web.task.vo.query.BatchModelTableVO;
import com.dtstack.batch.web.task.vo.query.BatchTableSearchInfoVO;
import com.dtstack.batch.web.task.vo.query.BatchTableVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ModelTableTransfer {

    ModelTableTransfer INSTANCE = Mappers.getMapper(ModelTableTransfer.class);

    /**
     * BatchModelTableVO -> BatchModelTable
     * @param batchModelTableVO
     * @return
     */
    BatchModelTable BatchModelTableVOToPO(BatchModelTableVO batchModelTableVO);

    /**
     * BatchTableSearchInfoVO -> BatchTableSearchVO
     * @param batchTableSearchInfoVO
     * @return
     */
    BatchTableSearchVO BatchTableSearchInfoVOToBatchTableSearchVO(BatchTableSearchInfoVO batchTableSearchInfoVO);

    /**
     * List<ColumnVO> -> List<ColumnDTO>
     * @param batchColumnVOList
     * @return
     */
    List<ColumnDTO> ColumnVOListToColumnDTOList(List<BatchColumnVO> batchColumnVOList);

    /**
     * TableVO -> TableDTO
     * @param batchTableVO
     * @return
     */
    TableDTO TableVOToTableDTO(BatchTableVO batchTableVO);


    /**
     * PageResult<List<BatchModelTableWithUserName>> listPageResult -> PageResult<List<BatchModelTableWithUserNameResultVO>>
     * @param listPageResult
     * @return
     */
    com.dtstack.batch.web.pager.PageResult<List<BatchModelTableWithUserNameResultVO>> BatchModelTableWithUserNameToResultVO(PageResult<List<BatchModelTableWithUserName>> listPageResult);

    /**
     * BatchModelTable -> BatchModelTableResultVO
     * @param modelTable
     * @return
     */
    BatchModelTableResultVO BatchModelTableToResultVO(BatchModelTable modelTable);

}
