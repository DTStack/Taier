package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.datamask.domain.DataMaskRule;
import com.dtstack.batch.datamask.dto.DataMaskColumnsInfoDto;
import com.dtstack.batch.datamask.dto.DataMaskConfigDto;
import com.dtstack.batch.datamask.dto.DataMaskRuleDto;
import com.dtstack.batch.datamask.dto.LineageDto;
import com.dtstack.batch.datamask.dto.TableTableDto;
import com.dtstack.batch.datamask.vo.DataMaskConfigVO;
import com.dtstack.batch.datamask.vo.DataMaskRuleVO;
import com.dtstack.batch.service.datamask.impl.DataMaskConfigService;
import com.dtstack.batch.web.datamask.vo.query.BatchDataMaskConfigVO;
import com.dtstack.batch.web.datamask.vo.query.BatchDataMaskRuleVO;
import com.dtstack.batch.web.datamask.vo.result.BatchDataMaskConfigCheckColumnsResultVO;
import com.dtstack.batch.web.datamask.vo.result.BatchDataMaskConfigLineageResultVO;
import com.dtstack.batch.web.datamask.vo.result.BatchDataMaskConfigListResultVO;
import com.dtstack.batch.web.datamask.vo.result.BatchDataMaskConfigProjectResultVO;
import com.dtstack.batch.web.datamask.vo.result.BatchDataMaskConfigRelatedTableResultVO;
import com.dtstack.batch.web.datamask.vo.result.BatchDataMaskConfigTableListResultVO;
import com.dtstack.batch.web.datamask.vo.result.BatchDataMaskRuleListResultVO;
import com.dtstack.batch.web.datamask.vo.result.BatchDataMaskRuleResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DataMaskMapstructTransfer {
    DataMaskMapstructTransfer INSTANCE = Mappers.getMapper(DataMaskMapstructTransfer.class);

    /**
     * BatchDataMaskConfigVO -> DataMaskConfigVO
     * @param vo
     * @return
     */
    DataMaskConfigVO newDataMaskConfigVoToConfigVo(BatchDataMaskConfigVO vo);

    /**
     * BatchDataMaskRuleVO  ->  DataMaskRuleVO
     *
     * @param vo
     * @return
     */
    DataMaskRuleVO newDataMaskRuleVoToRuleVo(BatchDataMaskRuleVO vo);

    /**
     * List<TableTableDto>  ->  List<BatchDataMaskConfigCheckColumnsResultVO>
     *
     * @param list
     * @return
     */
    List<BatchDataMaskConfigCheckColumnsResultVO> newTableTableDtoToDataMaskConfigCheckColumnsResultVo(List<TableTableDto> list);

    /**
     * PageResult<List<DataMaskConfigDto>>  ->  PageResult<List<BatchDataMaskConfigListResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchDataMaskConfigListResultVO>> newDataMaskConfigDtoToDataMaskConfigListResultVo(PageResult<List<DataMaskConfigDto>> result);

    /**
     * List<DataMaskConfigService.TableInfoDto>  ->  List<BatchDataMaskConfigTableListResultVO>
     *
     * @param list
     * @return
     */
    List<BatchDataMaskConfigTableListResultVO> newTableInfoDtoToDataMaskConfigTableListResultVo(List<DataMaskConfigService.TableInfoDto> list);

    /**
     * PageResult<List<DataMaskColumnsInfoDto>>  ->  PageResult<List<BatchDataMaskConfigRelatedTableResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchDataMaskConfigRelatedTableResultVO>> newDataMaskColumnsInfoDtoToDataMaskConfigRelatedTableResultVo(PageResult<List<DataMaskColumnsInfoDto>> result);

    /**
     * List<DataMaskConfigService.ProjectinfoDto>  ->  List<BatchDataMaskConfigProjectResultVO>
     *
     * @param list
     * @return
     */
    List<BatchDataMaskConfigProjectResultVO> newProjectInfoDtoToDataMaskConfigProjectResultVo(List<DataMaskConfigService.ProjectinfoDto> list);

    /**
     * LineageDto  ->  BatchDataMaskConfigLineageResultVO
     *
     * @param vo
     * @return
     */
    BatchDataMaskConfigLineageResultVO newLineageDtoToDataMaskConfigLineageResultVo(LineageDto vo);

    /**
     * PageResult<List<DataMaskRuleDto>>  ->  PageResult<List<BatchDataMaskRuleListResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchDataMaskRuleListResultVO>> newPageDataMaskRuleDtoToDataMaskRuleListResultVo(PageResult<List<DataMaskRuleDto>> result);

    /**
     * DataMaskRuleDto  ->  BatchDataMaskRuleListResultVO
     *
     * @param result
     * @return
     */
    BatchDataMaskRuleListResultVO newDataMaskRuleDtoToDataMaskRuleListResultVo(DataMaskRuleDto result);

    /**
     * List<DataMaskRule>  ->  List<BatchDataMaskRuleResultVO>
     *
     * @param list
     * @return
     */
    List<BatchDataMaskRuleResultVO> newDataMaskRuleToDataMaskRuleResultVo(List<DataMaskRule> list);
}
