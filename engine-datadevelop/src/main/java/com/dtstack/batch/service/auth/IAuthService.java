package com.dtstack.batch.service.auth;

import com.dtstack.batch.common.constant.PublicConstent;
import com.dtstack.dtcenter.common.login.domain.LicenseProductComponent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Set;

/**
 * @author toutian
 */
public interface IAuthService {

    @Cacheable(value = AuthCode.AUTH_EH_CACHE)
    Set<String> getUserCodes(Long userId, Long projectId, Long tenantId);

    boolean clearCache(Long userId, Long project, Long tenantId);

    List<String> getPermissionCodesByRoleId(Long roleId);

    /**
     * 由于使用了Spring cache，所以必须让bean受Spring管理。如果配置了proxyTargetClass = true，使用cglib代理，其代理类，需要在子类上添加@Cacheable注解
     * 否则，使用jdk动态代理，其代理接口，因此需要在接口方法上添加@Cacheable注解。
     * @param uicUrl
     * @param componentCode
     * @return
     */
    @Cacheable(value = AuthCode.AUTH_EH_CACHE)
    LicenseProductComponent fetchLicense(String uicUrl,String componentCode);

    @CacheEvict(value = AuthCode.AUTH_EH_CACHE)
    boolean clearLicenseCache(String uicUrl,String componentCode);

    @Cacheable(value = PublicConstent.AUTH_EH_CACHE)
    Set<Long> getUserAdminProjectIds(Long userId, Long tenantId);


}
