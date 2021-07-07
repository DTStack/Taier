package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.RoleVO;
import com.dtstack.batch.web.permission.vo.result.BatchTreeNodeResultVO;
import com.dtstack.batch.web.role.vo.result.BatchRoleResultVO;
import com.dtstack.dtcenter.common.tree.TreeNode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PermissionMapstructTransfer {

    PermissionMapstructTransfer INSTANCE = Mappers.getMapper(PermissionMapstructTransfer.class);


    /**
     * RoleVO -> BatchRoleResultVO
     *
     * @param roleVO
     * @return
     */
    BatchRoleResultVO RoleVOToBatchRoleResultVO(RoleVO roleVO);


    /**
     * treeNode -> BatchTreeNodeResultVO
     *
     * @param treeNode
     * @return
     */
    BatchTreeNodeResultVO treeNodeToBatchTreeNodeResultVO(TreeNode treeNode);
}
