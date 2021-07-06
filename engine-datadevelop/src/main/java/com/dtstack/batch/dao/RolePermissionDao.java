package com.dtstack.batch.dao;

import com.dtstack.batch.domain.RolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface RolePermissionDao {

    List<RolePermission> listByRoleId(@Param("roleId") Long roleId);

    List<Long> listPermissionIdsByRoleId(@Param("roleId") Long roleId);

    Integer deleteByRoleIdAndPermissionIds(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> existPermissionIds, @Param("modifyUserId") Long modifyUserId);

    Integer insert(RolePermission rolePermission);
}
