package com.dtstack.batch.mapstruct.vo;


import com.dtstack.engine.api.domain.BatchDataSource;
import com.dtstack.batch.domain.BatchEngineSqlTemplate;
import com.dtstack.batch.domain.BatchTableInfo;
import com.dtstack.engine.api.domain.po.StorageSizePO;
import com.dtstack.batch.engine.rdbms.common.dto.ColumnDTO;
import com.dtstack.batch.engine.rdbms.common.dto.TableDTO;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.table.vo.query.BatchTableDataSourceVO;
import com.dtstack.batch.web.table.vo.result.*;
import com.dtstack.batch.web.task.vo.query.BatchTableSearchInfoVO;
import com.dtstack.batch.web.task.vo.query.BatchTableVO;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface TableTransfer {

    TableTransfer INSTANCE = Mappers.getMapper(TableTransfer.class);

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
     * BatchTableDataResultDTO -> BatchTableInfoGetDataResultVO
     * @param batchTableDataResultDTO
     * @return
     */
    BatchTableInfoGetDataResultVO DataResultDTOToDataResultVO(BatchTableDataResultDTO batchTableDataResultDTO);

    /**
     * BatchTableDataSourceVO -> BatchDataSource
      * @param batchTableDataSourceVO
     * @return
     */
    BatchDataSource BatchTableDataSourceVOToBatchDataSource(BatchTableDataSourceVO batchTableDataSourceVO);

    /**
     * PageResult<List<BatchTableInfoVO>> -> PageResult<List<BatchTableInfoResultVO>>
     * @param batchTableInfo
     * @return
     */
    PageResult<List<BatchTableInfoResultVO>>  BatchTableInfoPageListVOToResultPageListVO(PageResult<List<BatchTableInfoVO>> batchTableInfo);

    /**
     * PageResult<List<BatchTableInfo>> -> PageResult<List<BatchSimpleTableResultVO>>
     * @param batchTableInfo
     * @return
     */
    PageResult<List<BatchSimpleTableResultVO>>  BatchTableInfoPageListVOToBatchSimpleTableResultPageListVO(PageResult<List<BatchTableInfo>> batchTableInfo);

    /**
     * BatchEngineSqlTemplate -> BatchEngineSqlTemplateResultVO
     * @param batchEngineSqlTemplate
     * @return
     */
    BatchEngineSqlTemplateResultVO BatchEngineSqlTemplateToResultVO(BatchEngineSqlTemplate batchEngineSqlTemplate);

    /**
     * List<BatchTableInfo>  -> List<BatchTableInfoPOResultVO>
     * @param batchTableInfoList
     * @return
     */
    List<BatchTableInfoPOResultVO> BatchTableInfoListToResultListVO(List<BatchTableInfo> batchTableInfoList);

    /**
     * BatchTableInfo -> BatchTableInfoPOResultVO
     * @param batchTableInfo
     * @return
     */
    BatchTableInfoPOResultVO BatchTableInfoToResultVO(BatchTableInfo batchTableInfo);

    /**
     * PageResult<List<Map<String, Object>>> -> com.dtstack.batch.web.pager.PageResult<List<Map<String, Object>>>
     * @param pageResult
     * @return
     */
    PageResult<List<Map<String, Object>>> PageListToPageList(PageResult<List<Map<String, Object>>> pageResult);

    /**
     * BatchTableBloodVO -> BatchTableBloodResultVO
     * @param bloodVO
     * @return
     */
    BatchTableBloodResultVO BatchTableBloodVOToResultVO(BatchTableBloodVO bloodVO);

    /**
     * BatchTableBloodInfoVO -> BatchTableBloodInfoResultVO
     * @param infoVO
     * @return
     */
    BatchTableBloodInfoResultVO BatchTableBloodInfoVOToResultVO(BatchTableBloodInfoVO infoVO);

    /**
     * BatchTableRelationVO -> BatchTableRelationResultVO
     * @param batchTableRelationVO
     * @return
     */
    BatchTableRelationResultVO BatchTableRelationVOToResultVO(BatchTableRelationVO batchTableRelationVO);

    /**
     * PageResult<List<BatchTableRelationVO>> -> PageResult<List<BatchTableRelationResultVO>
     * @param pageResult
     * @return
     */
    PageResult<List<BatchTableRelationResultVO>> BatchTableRelationPageListVOToResultPageListVO(PageResult<List<BatchTableRelationVO>> pageResult);

    /**
     * PageResult<List<BatchTableInfoVO>> -> PageResult<List<BatchTableInfoResultVO>>
     * @param batchTableInfo
     * @return
     */
    PageResult<List<BatchTableInfoResultVO>> BatchTableInfoPageListTOResultVOPageList(PageResult<List<BatchTableInfoVO>> batchTableInfo);

    /**
     * List<StorageSizePO> -> List<BatchTableStorageSizeResultVO>
     * @param storageSizePOList
     * @return
     */
    List<BatchTableStorageSizeResultVO> StorageSizePOListToResultVOList(List<StorageSizePO> storageSizePOList);

    /**
     * column -> BatchTableColumnMetaInfoDTO
     * @param column
     * @return
     */
    BatchTableColumnMetaInfoDTO columnMetaDTOToBatchTableColumnMetaInfoDTO(ColumnMetaDTO column);

    /**
     * table -> BatchTableMetaInfoDTO
     *
     * @param table
     * @return
     */
    BatchTableMetaInfoDTO tableToBatchTableMetaInfoDTO(com.dtstack.dtcenter.loader.dto.Table table);
}
