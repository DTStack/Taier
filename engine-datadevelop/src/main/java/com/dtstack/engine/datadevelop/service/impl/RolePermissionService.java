package com.dtstack.engine.datadevelop.service.impl;

import com.dtstack.batch.dao.RolePermissionDao;
import com.dtstack.batch.domain.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionService {

    @Autowired
    private RolePermissionDao rolePermissionDao;

    /**
     * 根据 roleId 拿出 关系列表
     * @param roleId
     * @return
     */
    public List<RolePermission> listByRoleId(Long roleId) {
        return rolePermissionDao.listByRoleId(roleId);
    }

    /**
     * 根据roleId  拿出 关联的permissionId
     * @param roleId
     * @return
     */
    public List<Long> listPermissionIdsByRoleId(Long roleId) {
        return rolePermissionDao.listPermissionIdsByRoleId(roleId);
    }

    /**
     * 根据roleId permissionids 删除对应记录
     * @param roleId
     * @param existPermissionIds
     * @param userId
     * @return
     */
    public Integer deleteByRoleIdAndPermissionIds(Long roleId, List<Long> existPermissionIds, Long userId) {
        return rolePermissionDao.deleteByRoleIdAndPermissionIds(roleId, existPermissionIds, userId);
    }

    /**
     * 插入
     * @param rolePermission
     * @return
     */
    public Integer insert(RolePermission rolePermission){
        return rolePermissionDao.insert(rolePermission);
    }
}
