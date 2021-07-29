package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.BatchFileMergePartitionVO;
import com.dtstack.batch.vo.BatchFileMergeRecordVO;
import com.dtstack.batch.vo.BatchFileMergeRuleVO;
import com.dtstack.batch.vo.FileMergeRuleVO;
import com.dtstack.batch.web.filemanager.vo.query.BatchFileMergeRuleAddVO;
import com.dtstack.batch.web.filemanager.vo.result.BatchFileMergePartitionResultVO;
import com.dtstack.batch.web.filemanager.vo.result.BatchFileMergeRecordQueryResultVO;
import com.dtstack.batch.web.filemanager.vo.result.BatchFileMergeRuleResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 小文件合并 VO 转换工具
 *
 * @author ：wangchuan
 * date：Created in 1:39 下午 2021/1/12
 * company: www.dtstack.com
 */
@Mapper
public interface BatchFileMergeTransfer {

    BatchFileMergeTransfer INSTANCE = Mappers.getMapper(BatchFileMergeTransfer.class);

    /**
     * BatchFileMergeRuleAddVO -> BatchFileMergeRuleVO
     *
     * @param batchFileMergeRuleAddVO controller层封装对象
     * @return service中使用对象
     */
    FileMergeRuleVO newFileMergeRuleAddVOToFileMergeRuleVO (BatchFileMergeRuleAddVO batchFileMergeRuleAddVO);

    /**
     * BatchFileMergeRuleVO -> BatchFileMergeRuleResultVO
     *
     * @param batchFileMergeRuleVO service中使用对象
     * @return controller中封装前端展示对象
     */
    BatchFileMergeRuleResultVO RuleVOToNewRuleResultVO (BatchFileMergeRuleVO batchFileMergeRuleVO);

    /**
     * PageResult<List<BatchFileMergeRuleVO>> -> PageResult<List<BatchFileMergeRuleResultVO>>
     *
     * @param pageResult service中使用对象
     * @return controller 中封装前端展示对象
     */
    PageResult<List<BatchFileMergeRuleResultVO>> PageRuleVOToNewPageRuleResultVO (PageResult<List<BatchFileMergeRuleVO>> pageResult);

    /**
     * PageResult<List<BatchFileMergeRecordVO>> pageResult -> PageResult<List<BatchFileMergeRecordQueryResultVO>>
     *
     * @param pageResult
     * @return
     */
    PageResult<List<BatchFileMergeRecordQueryResultVO>> BatchFileMergeRecordVOListToBatchFileMergeRecordQueryResultVOList (PageResult<List<BatchFileMergeRecordVO>> pageResult);

    /**
     * List<BatchFileMergeRecordVO> pageResult -> List<BatchFileMergeRecordQueryResultVO>
     * @param pageResult
     * @return
     */
    List<BatchFileMergeRecordQueryResultVO> BatchFileMergeRecordVOListToBatchFileMergeRecordQueryResultVOList (List<BatchFileMergeRecordVO> pageResult);

    /**
     * BatchFileMergeRecordVO pageResult -> BatchFileMergeRecordQueryResultVO
     * @param recordVO
     * @return
     */
    BatchFileMergeRecordQueryResultVO BatchFileMergeRecordVOListToBatchFileMergeRecordQueryResultVO (BatchFileMergeRecordVO recordVO);

    /**
     * PageResult<List<BatchFileMergePartitionVO>> -> PageResult<List<BatchFileMergePartitionResultVO>>
     *
     * @param pageResult service中使用对象
     * @return controller 中封装前端展示对象
     */
    PageResult<List<BatchFileMergePartitionResultVO>> PagePartitionVOToNewPagePartitionResultVO (PageResult<List<BatchFileMergePartitionVO>> pageResult);


    /**
     * List<BatchFileMergeRuleVO> -> List<BatchFileMergeRuleResultVO>
     *
     * @param ruleVOS service中使用对象
     * @return controller 中封装前端展示对象
     */
    List<BatchFileMergeRuleResultVO> BatchFileMergeRuleVOToNewBatchFileMergeRuleResultVO (List<BatchFileMergeRuleVO> ruleVOS);
}
