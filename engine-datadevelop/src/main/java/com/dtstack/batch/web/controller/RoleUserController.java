package com.dtstack.batch.web.controller;

import com.dtstack.batch.domain.RoleUser;
import com.dtstack.batch.mapstruct.vo.RoleUserMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.RoleUserService;
import com.dtstack.batch.web.security.vo.result.SecurityResultRoleUserAdd;
import com.dtstack.batch.web.security.vo.result.SecurityResultRoleUserRemove;
import com.dtstack.batch.web.security.vo.result.SecurityResultRoleUserUpdate;
import com.dtstack.batch.web.user.vo.query.*;
import com.dtstack.batch.web.user.vo.result.BatchUserRolePermissionResultVO;
import com.dtstack.dtcenter.common.annotation.SecurityAudit;
import com.dtstack.dtcenter.common.console.SecurityResult;
import com.dtstack.dtcenter.common.enums.ActionType;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
StringUtils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(value = "用户权限管理", tags = {"用户权限管理"})
@RestController
@RequestMapping(value = "/api/rdos/common/roleUser")
public class RoleUserController {

    @Autowired
    private RoleUserService roleUserService;

    @ApiOperation(value = "添加用户权限")
    @PostMapping(value = "addRoleUserNew")
    @Security(code = AuthCode.PROJECT_MEMBER_EDIT)
    public R<SecurityResultRoleUserAdd> addRoleUserNew(@RequestBody BatchRoleUserAddNewVO vo) {
        return new APITemplate<SecurityResultRoleUserAdd>() {
            @Override
            protected SecurityResultRoleUserAdd process() {
                SecurityResult<Map<Long, List<RoleUser>>> mapSecurityResult = roleUserService.addRoleUserNew(vo.getRoleIds(), vo.getUserId(), vo.getTenantId(), vo.getProjectId(), vo.getTargetUsers(), vo.getIsRoot());
                return RoleUserMapstructTransfer.INSTANCE.mapSecurityResultTOBatchRoleUserResultVO(mapSecurityResult);
            }
        }.execute();
    }

    @ApiOperation(value = "SDK调用: 添加用户权限")
    @PostMapping(value = "addRoleUserNewFromSdk")
    @Security(code = AuthCode.PROJECT_MEMBER_EDIT)
    public R<SecurityResultRoleUserAdd> addRoleUserNewFromSdk(@RequestBody BatchRoleUserAddRoleUserNewFromSdk vo) {
        return new APITemplate<SecurityResultRoleUserAdd>() {
            @Override
            protected SecurityResultRoleUserAdd process() {
                SecurityResult<Map<Long, List<RoleUser>>> mapSecurityResult = roleUserService.addRoleUserNewFromSdk(vo.getRoleValues(), vo.getUserId(), vo.getTenantId(), vo.getProjectId(), vo.getTargetUsers(), vo.getIsRoot());
                return RoleUserMapstructTransfer.INSTANCE.mapSecurityResultTOBatchRoleUserResultVO(mapSecurityResult);
            }
        }.execute();
    }

    @ApiOperation(value = "修改成员角色")
    @PostMapping(value = "updateUserRole")
    @Security(code = AuthCode.PROJECT_MEMBER_EDIT)
    public R<SecurityResultRoleUserUpdate> updateUserRole(@RequestBody BatchRoleUserUpdateUserRoleVO vo) {
        return new APITemplate<SecurityResultRoleUserUpdate>() {
            @Override
            protected SecurityResultRoleUserUpdate process() {
                SecurityResult<List<RoleUser>> listSecurityResult = roleUserService.updateUserRole(vo.getUserId(), vo.getTargetUserId(), vo.getRoleIds(), vo.getTenantId(), vo.getProjectId(), vo.getIsRoot());
                return RoleUserMapstructTransfer.INSTANCE.listSecurityResultTOMySecurityResult(listSecurityResult);
            }
        }.execute();
    }

    @ApiOperation(value = "SDK调用: 修改成员角色")
    @PostMapping(value = "updateUserRoleFromSdk")
    @Security(code = AuthCode.PROJECT_MEMBER_EDIT)
    public R<SecurityResultRoleUserUpdate> updateUserRoleFromSdk(@RequestBody(required = false) BatchRoleUserUpdateUserRoleSDK vo) {
        return new APITemplate<SecurityResultRoleUserUpdate>() {
            @Override
            protected SecurityResultRoleUserUpdate process() {
                SecurityResult<List<RoleUser>> listSecurityResult = roleUserService.updateUserRoleFromSdk(vo.getUserId(), vo.getTargetUserId(), vo.getRoleValues(), vo.getTenantId(), vo.getProjectId(), vo.getIsRoot());
                return RoleUserMapstructTransfer.INSTANCE.listSecurityResultTOMySecurityResult(listSecurityResult);
            }
        }.execute();
    }

    @ApiOperation(value = "从项目中移除用户角色")
    @PostMapping(value = "removeRoleUserFromProject")
    @Security(code = AuthCode.PROJECT_MEMBER_EDIT)
    public R<SecurityResultRoleUserRemove> removeRoleUserFromProject(@RequestBody(required = false) BatchRoleUserRemoveRoleUserVO vo) {
        return new APITemplate<SecurityResultRoleUserRemove>() {
            @Override
            protected SecurityResultRoleUserRemove process() {
                SecurityResult<Integer> integerSecurityResult = roleUserService.removeRoleUserFromProject(vo.getUserId(), vo.getTargetUserId(), vo.getProjectId(), vo.getTenantId(), vo.getIsRoot());
                return RoleUserMapstructTransfer.INSTANCE.integerSecurityResultTOMySecurityResult(integerSecurityResult);
            }
        }.execute();
    }

    @ApiOperation(value = "SDK调用: 从项目中移除用户角色")
    @PostMapping(value = "removeRoleUserFromSdk")
    @Security(code = AuthCode.PROJECT_MEMBER_EDIT)
    public R<SecurityResultRoleUserRemove> removeRoleUserFromSdk(@RequestBody(required = false) BatchRoleUserRemoveRoleUserVO vo) {
        return new APITemplate<SecurityResultRoleUserRemove>() {
            @Override
            protected SecurityResultRoleUserRemove process() {
                SecurityResult<Integer> integerSecurityResult = roleUserService.removeRoleUserFromSdk(vo.getUserId(), vo.getTargetUserId(), vo.getProjectId(), vo.getTenantId(), vo.getIsRoot());
                return RoleUserMapstructTransfer.INSTANCE.integerSecurityResultTOMySecurityResult(integerSecurityResult);
            }
        }.execute();
    }

    @ApiOperation(value = "获取此项目下是否还有任务、已提交任务、表责任人,告警接受人为被删除用户的")
    @PostMapping(value = "checkUserHaveOwner")
    @Security(code = AuthCode.PROJECT_MEMBER_EDIT)
    @SecurityAudit(actionType = ActionType.REMOVE_USER,orderedKeys = "removedUser")
    public R<Boolean> checkUserHaveOwner(@RequestBody BatchRoleUserCheckUserHaveOwnerVO vo) {
        return new APITemplate<Boolean>() {
            @Override
            protected Boolean process() {
                return roleUserService.checkUserHaveOwner(vo.getUserId(), vo.getTargetUserId(), vo.getProjectId(), vo.getIsRoot());
            }
        }.execute();
    }

    @ApiOperation(value = "模糊查询出用户以及在该项目下的权限")
    @PostMapping(value = "getUsersAndPermission")
    public R<List<BatchUserRolePermissionResultVO>> getUsersAndPermission(@RequestBody BatchRoleUserGetUsersAndPermissionVO vo){
        return new APITemplate<List<BatchUserRolePermissionResultVO>>() {
            @Override
            protected List<BatchUserRolePermissionResultVO> process() {
                return RoleUserMapstructTransfer.INSTANCE.userRolePermissionVOListToBatchUserRolePermissionResultVOList(roleUserService.getUsersAndPermission(vo.getProjectId(),
                        vo.getName(), vo.getOldOwnerUserId()));
            }
        }.execute();
    }


    @ApiOperation(value = "资源交接")
    @PostMapping(value = "handoverOwner")
    @Security(code = AuthCode.PROJECT_MEMBER_EDIT)
    @SecurityAudit(actionType = ActionType.REMOVE_USER,orderedKeys = "removedUser")
    public R<Void> handoverOwner(@RequestBody BatchRoleUserHandoverOwnerVO vo){
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                 roleUserService.handoverOwner(vo.getOldOwnerUserId(), vo.getNewOwnerUserId(), vo.getProjectId(),
                         vo.getTenantId(), vo.getUserId(), vo.getIsRoot());
                 return null;
            }
        }.execute();
    }

    @ApiOperation(value = "恢复已移除的角色信息")
    @PostMapping(value = "replyDeleOwnerUserRole")
    public R<Void> replyDeleOwnerUserRole() {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                roleUserService.replyDeleOwnerUserRole();
                return null;
            }
        }.execute();
    }
}
