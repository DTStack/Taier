package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.engine.rdbms.common.dto.PartitionDTO;
import com.dtstack.batch.web.common.PartitionResultVO;
import com.dtstack.batch.web.common.TreeNodeResultVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.dtcenter.common.tree.TreeNode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BatchCommonMapstructTransfer {

    BatchCommonMapstructTransfer INSTANCE = Mappers.getMapper(BatchCommonMapstructTransfer.class);

    /**
     * TreeNode -> TreeNodeResultVO
     * @param treeNode
     * @return
     */
    TreeNodeResultVO treeNodeToResultVO(TreeNode treeNode);

    /**
     * PageResult<List<PartitionDTO>>  -> com.dtstack.batch.web.pager.PageResult<List<PartitionResultVO>>
     * @param listPageResult
     * @return
     */
    com.dtstack.batch.web.pager.PageResult<List<PartitionResultVO>> PartitionResultVOPageListTo(PageResult<List<PartitionDTO>> listPageResult);

}
