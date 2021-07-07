package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchTableSyncLog;
import com.dtstack.batch.web.datasource.vo.query.BatchDataSourceMigrationTaskVO;
import com.dtstack.batch.web.datasource.vo.query.BatchDataSourceMigrationTransformFieldVO;
import com.dtstack.batch.web.datasource.vo.query.BatchDataSourceMigrationVO;
import com.dtstack.batch.web.datasource.vo.result.BatchDataSourceMigrationResultVO;
import com.dtstack.batch.web.datasource.vo.result.BatchDataSourceMigrationTableListResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DataSourceMigrationMapstructTransfer {
    DataSourceMigrationMapstructTransfer INSTANCE = Mappers.getMapper(DataSourceMigrationMapstructTransfer.class);

    /**
     * IdeDataSourceMigrationVO -> BatchDataSourceMigrationVO
     *
     * @param vo
     * @return
     */
    com.dtstack.batch.vo.BatchDataSourceMigrationVO newDataSourceMigrationVoToVo(BatchDataSourceMigrationVO vo);

    /**
     * IdeDataSourceMigrationTaskVO -> BatchDataSourceMigrationVO
     *
     * @param vo
     * @return
     */
    com.dtstack.batch.vo.BatchDataSourceMigrationVO newDataSourceMigrationTaskVoToVo(BatchDataSourceMigrationTaskVO vo);

    /**
     * List<IdeDataSourceMigrationTransformFieldVO> -> List<BatchDataSourceMigrationVO.TransformField>
     *
     * @param vo
     * @return
     */
    List<com.dtstack.batch.vo.BatchDataSourceMigrationVO.TransformField> newTransformFieldVoToVo(List<BatchDataSourceMigrationTransformFieldVO> vo);

    /**
     * com.dtstack.batch.vo.BatchDataSourceMigrationVO  ->  BatchDataSourceMigrationResultVO
     *
     * @param vo
     * @return
     */
    BatchDataSourceMigrationResultVO newMigrationVoToMigrationResultVo(com.dtstack.batch.vo.BatchDataSourceMigrationVO vo);

    /**
     * PageResult<List<BatchTableSyncLog>>  ->  PageResult<List<BatchDataSourceMigrationTableListResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchDataSourceMigrationTableListResultVO>> newTableSyncLogToMigrationTableListResultVo(PageResult<List<BatchTableSyncLog>> result);

    /**
     * PageResult<List<com.dtstack.batch.vo.BatchDataSourceMigrationVO>>  ->  PageResult<List<BatchDataSourceMigrationResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchDataSourceMigrationResultVO>> newPageMigrationVoToMigrationResultVo(PageResult<List<com.dtstack.batch.vo.BatchDataSourceMigrationVO>> result);
}
