package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.Role;
import com.dtstack.batch.vo.RoleVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.role.vo.query.BatchRoleAddVO;
import com.dtstack.batch.web.role.vo.result.BatchRoleUpdateResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RoleMapstructTransfer {
    RoleMapstructTransfer INSTANCE = Mappers.getMapper(RoleMapstructTransfer.class);

    /**
     * RoleAddVO -> RoleVO
     *
     * @param addVO
     * @return
     */
    RoleVO roleAddVOToRoleVO(BatchRoleAddVO addVO);

    /**
     * Role -> BatchRoleUpdateResultVO
     *
     * @param role
     * @return
     */
    BatchRoleUpdateResultVO roleToBatchRoleUpdateResultVO(Role role);

    /**
     * PageResult<List<RoleVO>> -> PageResult<List<BatchRoleUpdateResultVO>>
     *
     * @param role
     * @return
     */
    PageResult<List<BatchRoleUpdateResultVO>> batchRoleUpdateResultVOToRoleVO(PageResult<List<RoleVO>> role);
}
