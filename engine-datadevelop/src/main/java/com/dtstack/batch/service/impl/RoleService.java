/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.batch.service.impl;

import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.RoleDao;
import com.dtstack.batch.dao.RoleUserDao;
import com.dtstack.batch.domain.Role;
import com.dtstack.batch.domain.RolePermission;
import com.dtstack.batch.domain.RoleUser;
import com.dtstack.batch.dto.RoleDTO;
import com.dtstack.batch.service.auth.IAuthService;
import com.dtstack.batch.vo.RoleVO;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.dtcenter.common.enums.RoleValue;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.master.impl.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author toutian
 */
@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private IAuthService authService;

    @Autowired
    private RoleUserDao roleUserDao;

    @Autowired
    private UserService userService;

    /**
     * 新建或修改角色
     *
     * @param roleVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Role addOrUpdateRole(RoleVO roleVO, Long userId) {
        if (roleVO.getProjectId() == null || roleVO.getTenantId() == null ||
                roleVO.getRoleName() == null || roleVO.getRoleType() == null) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        Role role = null;
        if (!isRoleNameAvailable(roleVO)) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
        }
        if (roleVO.getId() > 0) {
            role = roleDao.getOne(roleVO.getId());
            if (role == null) {
                throw new RdosDefineException(ErrorCode.ROLE_NOT_FOUND);
            }
            checkRoleValueAccess(role.getRoleValue());
            role = new Role();
            role.setId(roleVO.getId());
            role.setModifyUserId(userId);
            role.setRoleDesc(roleVO.getRoleDesc());
            role.setRoleName(roleVO.getRoleName());
            //执行更新操作
            roleDao.update(role);
        } else {
            role = setRole(roleVO, roleVO.getProjectId(), roleVO.getTenantId(), RoleValue.CUSTOM.getRoleValue(),userId);
            role.setModifyUserId(userId);
            //执行新增操作
            roleDao.insert(role);
        }

        if (CollectionUtils.isNotEmpty(roleVO.getPermissionIds())) {
            this.addOrUpdateRolePermission(role.getId(), roleVO.getPermissionIds(), userId, roleVO.getTenantId(), roleVO.getProjectId());
            removeCache(role.getId(), role.getProjectId(), role.getTenantId());
        }
        return role;
    }

    private void checkRoleValueAccess(Integer roleValue) {
        if (RoleValue.MEMBER.getRoleValue() == roleValue) {
            throw new RdosDefineException("角色为访客不可编辑", ErrorCode.PERMISSION_LIMIT);
        }
        if (RoleValue.PROJECTADMIN.getRoleValue() == roleValue) {
            throw new RdosDefineException("角色为项目管理员不可编辑", ErrorCode.PERMISSION_LIMIT);
        }
        if (RoleValue.PROJECTOWNER.getRoleValue() == roleValue) {
            throw new RdosDefineException("角色为项目所有者不可编辑", ErrorCode.PERMISSION_LIMIT);
        }
        if (RoleValue.TEANTOWNER.getRoleValue() == roleValue) {
            throw new RdosDefineException("角色为租户所有者不可编辑", ErrorCode.PERMISSION_LIMIT);
        }
    }

    private boolean isRoleNameAvailable(Role role) {
        Role result = roleDao.getByName(role.getRoleName(), role.getTenantId(), role.getProjectId());
        if (result != null && !result.getId().equals(role.getId())) {
            return false;
        }
        return true;
    }

    private Role setRole(Role origin, Long projectId, Long tenantId, Integer roleValue,Long userId) {
        Role role = new Role();
        role.setProjectId(projectId);
        role.setTenantId(tenantId);
        role.setRoleValue(roleValue);
        role.setRoleName(origin.getRoleName());
        role.setRoleType(origin.getRoleType());
        role.setRoleDesc(origin.getRoleDesc());
        role.setCreateUserId(userId);
        role.setModifyUserId(userId);
        if (StringUtils.isEmpty(origin.getRoleDesc())) {
            role.setRoleDesc(StringUtils.EMPTY);
        }
        return role;
    }

    /**
     * 修改角色关联的权限点
     *
     * @param roleId
     * @param permissionIds
     * @param userId
     * @param tenantId
     * @param projectId
     * @return
     */
    private Long addOrUpdateRolePermission(Long roleId, List<Long> permissionIds, Long userId, Long tenantId, Long projectId) {
        List<Long> existPermissionIds = rolePermissionService.listPermissionIdsByRoleId(roleId);
        for (Long permissionId : permissionIds) {
            if (existPermissionIds.contains(permissionId)) {
                existPermissionIds.remove(permissionId);
            } else {
                RolePermission rp = new RolePermission();
                rp.setPermissionId(permissionId);
                rp.setRoleId(roleId);
                rp.setTenantId(tenantId);
                rp.setProjectId(projectId);
                rp.setCreateUserId(userId);
                rolePermissionService.insert(rp);
            }
        }
        if (CollectionUtils.isNotEmpty(existPermissionIds)) {
            rolePermissionService.deleteByRoleIdAndPermissionIds(roleId, existPermissionIds, userId);
        }

        return roleId;
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @param projectId
     * @param tenantId
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteRole(Long roleId, Long projectId, Long tenantId, Long userId) {
        Role role = roleDao.getOne(roleId);
        if (role == null) {
            throw new RdosDefineException(ErrorCode.ROLE_NOT_FOUND);
        }
        checkRoleValueAccess(role.getRoleValue());
        int delete = roleDao.deleteById(roleId, userId);
        if (delete > 0) {
            roleUserDao.deleteByRoleIdAndProjectId(roleId, projectId, userId);
            removeCache(roleId, projectId, tenantId);
        }
        return delete;
    }

    private void removeCache(Long roleId, Long projectId, Long tenantId) {
        //清缓存
        List<RoleUser> roleUsers = roleUserDao.listByRoleId(roleId);
        if (CollectionUtils.isNotEmpty(roleUsers)) {
            List<Long> userIds = roleUsers.stream().map(RoleUser::getUserId).collect(Collectors.toList());
            for (Long userId : userIds) {
                authService.clearCache(userId, projectId, tenantId);
            }
        }
    }

    /**
     * 分页查询角色列表
     *
     * @param tenantId
     * @param projectId
     * @param currentPage
     * @param pageSize
     * @param name
     * @return
     */
    public PageResult<List<RoleVO>> pageQuery(Long tenantId, Long projectId, Integer currentPage, Integer pageSize, String name) {
        PageQuery<RoleDTO> pageQuery = new PageQuery<RoleDTO>(currentPage, pageSize, "id", Sort.ASC.name());
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setTenantId(tenantId);
        roleDTO.setProjectId(projectId);
        roleDTO.setRoleNameLike(name);
        pageQuery.setModel(roleDTO);
        int count = roleDao.countByProjectIdAndTenantIdAndRoleName(projectId, tenantId, name);
        List<RoleVO> roleVOS = new ArrayList<>();
        if (count > 0) {
            List<Role> roles = roleDao.listByTenantIdAndProjectIdAndRoleName(pageQuery);
            roleVOS = new ArrayList<>(count);
            for (Role role : roles) {
                RoleVO vo = RoleVO.toVO(role);
                if (vo.getModifyUserId() != null && vo.getModifyUserId() > 0) {
                    vo.setModifyUserName(userService.getUserName(vo.getModifyUserId()));
                }
                roleVOS.add(vo);
            }
        }
        PageResult pageResult = new PageResult(roleVOS, count, pageQuery);
        return pageResult;
    }

    /**
     * 根据项目ID删除角色相关信息
     * @param projectId 项目ID
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProjectId(Long projectId, Long userId) {
        roleDao.deleteByProjectId(projectId, userId);
        roleUserDao.deleteByProjectId(projectId, userId);
    }

    public Role getRoleById(long roleId) {
        return roleDao.getOne(roleId);
    }

    /**
     * 复制系统默认角色
     *
     * @param projectId
     * @param tenantId
     * @return
     */
    public List<Role> copySystemDefaultRole(Long projectId, Long tenantId,Long userId) {
        List<Role> defaultRoles = roleDao.listSystemDefaultRole();
        List<Role> copyRoles = new ArrayList<>(defaultRoles.size());
        for (Role defaultRole : defaultRoles) {
            Role role = setRole(defaultRole, projectId, tenantId, defaultRole.getRoleValue(),userId);
            roleDao.insert(role);
            Role newRole = roleDao.getOne(role.getId());
            copyRoles.add(newRole);
        }
        return copyRoles;
    }

    /**
     * 复制角色对应的权限点
     *
     * @param roles
     */
    public void copyRolePermission(List<Role> roles, Long userId, Long tenantId, Long projectId) {
        for (Role role : roles) {
            Role defaultRole = roleDao.getSystemDefaultRoleByRoleValue(role.getRoleValue());
            List<Long> permissionIds = rolePermissionService.listPermissionIdsByRoleId(defaultRole.getId());
            this.addOrUpdateRolePermission(role.getId(), permissionIds, userId, tenantId, projectId);
        }
    }

    public Role getSystemDefaultRoleByRoleValue(Integer roleValue) {
        return roleDao.getSystemDefaultRoleByRoleValue(roleValue);
    }

    /**
     * 获取租户下和某些角色值的角色
     *
     * @param tenantId
     * @param roleValues
     * @return
     */
    public List<Role> listByTenantIdAndRoleValue(Long tenantId, List<Integer> roleValues) {
        return roleDao.listByTenantIdAndRoleValue(tenantId, roleValues);
    }

    /**
     * 根据项目、角色值获取角色信息
     *
     * @param projectId
     * @param roleValues
     * @return
     */
    public List<Role> getByProjectIdAndRoleValues(Long projectId, List<Integer> roleValues) {
        return roleDao.getByProjectIdAndRoleValues(projectId, roleValues);
    }

}
