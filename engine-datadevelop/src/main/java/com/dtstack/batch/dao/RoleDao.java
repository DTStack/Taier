package com.dtstack.batch.dao;

import com.dtstack.batch.domain.Role;
import com.dtstack.batch.dto.RoleDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sishu.yss
 */
public interface RoleDao {
    Role getOne(@Param("roleId") Long roleId);

    List<Role> listSystemDefaultRole();

    Role getSystemDefaultRoleByRoleValue(@Param("roleValue") Integer roleValue);

    Integer deleteById(@Param("id") Long Id, @Param("modifyUserId") Long modifyUserId);

    Role getByName(@Param("name") String name, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    Role getByProjectIdAndRoleValue(@Param("projectId") Long projectId, @Param("roleValue") Integer roleValue);

    List<Role> getByProjectIdAndRoleValues(@Param("projectId") Long projectId, @Param("roleValues") List<Integer> roleValues);

    List<Role> listByTenantIdAndProjectIdAndRoleName(PageQuery<RoleDTO> pageQuery);

    Integer insert(Role role);

    Integer update(Role role);

    Integer countByProjectIdAndTenantIdAndRoleName(@Param("projectId") Long projectId, @Param("tenantId") Long tenantId, @Param("roleName") String roleName);

    Long getDefaultTenantOwner(@Param("tenantId") Long tenantId);

    /**
     * 获取租户下和某些角色值的角色
     *
     * @param tenantId
     * @param roleValues
     * @return
     */
    List<Role> listByTenantIdAndRoleValue(@Param("tenantId") Long tenantId, @Param("roleValues") List<Integer> roleValues);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
