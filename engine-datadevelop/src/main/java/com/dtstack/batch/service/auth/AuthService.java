package com.dtstack.batch.service.auth;

import com.dtstack.batch.common.constant.PublicConstent;
import com.dtstack.batch.dao.PermissionDao;
import com.dtstack.batch.dao.RolePermissionDao;
import com.dtstack.batch.dao.RoleUserDao;
import com.dtstack.batch.domain.Permission;
import com.dtstack.batch.domain.RolePermission;
import com.dtstack.batch.domain.RoleUser;
import com.dtstack.dtcenter.common.login.DtUicUserConnect;
import com.dtstack.dtcenter.common.login.domain.LicenseProductComponent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author toutian
 */
@Service("authService")
public class AuthService implements IAuthService {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Autowired
    private RoleUserDao roleUserDao;

    @Autowired
    private PermissionDao permissionDao;

    /**
     * 根据userId+projectId，判断是否有特定的权限点code
     * //实例内的调用不会走二级缓存（方法不会被拦截）
     *
     * @param userId
     * @param projectId
     * @return
     */
    @Override
    @Cacheable(value = PublicConstent.AUTH_EH_CACHE, key = "'UserRole_' + #userId + '_' + #projectId + '_' + #tenantId")
    public Set<String> getUserCodes(Long userId, Long projectId, Long tenantId) {
        List<RoleUser> roleUsers = roleUserDao.listByUserIdProjectIdTenantId(userId, projectId, tenantId);
        Set<Long> roleIds = roleUsers.stream().map(RoleUser::getRoleId).collect(Collectors.toSet());
        HashSet<String> codes = new HashSet<>();
        for (Long roleId : roleIds) {
            codes.addAll(getPermissionCodesByRoleId(roleId));
        }

        if (CollectionUtils.isEmpty(codes)) {
            RoleUser noProjectRoleUser = roleUserDao.getNoProjectByUserId(userId, tenantId);
            if (noProjectRoleUser != null) {
                codes.addAll(getPermissionCodesByRoleId(noProjectRoleUser.getRoleId()));
            }
        }
        return codes;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = PublicConstent.AUTH_EH_CACHE, key = "'UserRole_' + #userId + '_' + #projectId + '_' + #tenantId"),
            @CacheEvict(value = PublicConstent.AUTH_EH_CACHE, key = "'ProjectAdmin_' + #userId + '_' + #tenantId")
    })
    public boolean clearCache(Long userId, Long project, Long tenantId) {
        return true;
    }

    /**
     * 根据角色id获取角色下所有的权限值
     *
     * @param roleId
     * @return
     */
    @Override
    public List<String> getPermissionCodesByRoleId(Long roleId) {
        List<RolePermission> rolePermissions = rolePermissionDao.listByRoleId(roleId);
        if (CollectionUtils.isEmpty(rolePermissions)) {
            return ListUtils.EMPTY_LIST;
        }
        List<String> codes = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissions) {
            Permission p = permissionDao.getOne(rolePermission.getPermissionId());
            if (p == null) {
                LOGGER.warn("permission_id is null {}", rolePermission.getPermissionId());
                continue;
            }
            codes.add(p.getCode());
        }
        return codes;
    }

    @Override
    @Cacheable(value = PublicConstent.AUTH_EH_CACHE, key = "'lincense_' + #uicUrl + '_' + #componentCode")
    public LicenseProductComponent fetchLicense(String uicUrl, String componentCode) {
        return DtUicUserConnect.getUicComponentLicense(uicUrl, componentCode);
    }

    @Override
    @CacheEvict(value = PublicConstent.AUTH_EH_CACHE, key = "'lincense_' + #uicUrl + '_' + #componentCode")
    public boolean clearLicenseCache(String uicUrl, String componentCode) {
        return true;
    }

    @Override
    @Cacheable(value = PublicConstent.AUTH_EH_CACHE, key = "'ProjectAdmin_' + #userId + '_' + #tenantId")
    public Set<Long> getUserAdminProjectIds(Long userId, Long tenantId) {
        List<RoleUser> roleUserList = roleUserDao.listRoleUserIsAdminByUserId(userId, tenantId);
        Set<Long> projectIdList = roleUserList.stream().map(RoleUser::getProjectId).collect(Collectors.toSet());
        return projectIdList;
    }

}
