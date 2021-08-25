package com.dtstack.batch.service.impl;

import com.dtstack.batch.dao.TenantDao;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.batch.service.uic.impl.UIcUserTenantRelApiClient;
import com.dtstack.engine.api.vo.tenant.TenantUsersVO;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author sishu.yss
 */
@Service("batchTenantService")
public class TenantService {

    public static Logger LOG = LoggerFactory.getLogger(TenantService.class);

    @Resource(name = "batchTenantDao")
    private TenantDao tenantDao;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @Autowired
    private ClusterService clusterService;

    public Tenant getTenantByDtUicTenantId(long dtUicTenantId) {
        return tenantDao.getByDtUicTenantId(dtUicTenantId);
    }

    public Tenant getTenantById(long tenantId) {
        return tenantDao.getOne(tenantId);
    }

    public Long getDtuicTenantId(Long tenantId) {
        Tenant tenant = tenantDao.getOne(tenantId);
        if (tenant != null) {
            return tenant.getDtuicTenantId();
        }
        return null;
    }


    /**
     *  根据tenantId集合获取对应租户集合
     *
     * @param tenantIds
     * @return
     */
    public List<Tenant> listDtuicTenantIdByTenantId(List<Long> tenantIds) {
        if (CollectionUtils.isNotEmpty(tenantIds)) {
            return tenantDao.listDtuicTenantIdByTenantId(tenantIds);
        }
        return Lists.newArrayList();
    }

    /**
     * 根据TenantIds获取到DtUicTenantIds
     * @param tenantIds
     * @return
     */
    public List<Long> getDtUicTenantListByTenantIds(List<Long> tenantIds) {
        return tenantDao.getDtUicTenantIdListByIds(tenantIds);
    }


    /**
     * 获取该租户下拥有管理员角色（超级管理员、租户所有者、租户管理员）以上的用户
     *
     * @TODO 该方法暂时获取不到超级管理员、租户所有者（待uic接口完善进行补齐）
     *
     * @param dtuicTenantId
     * @return
     */
    public List<TenantUsersVO> findUicAdminRoleUserByDtuicTenantId(Long dtuicTenantId){
        // 获取该租户下所有的用户
        List<TenantUsersVO> tenantUsersVOList = uIcUserTenantRelApiClient.findUsersByTenantId(dtuicTenantId);

        List<TenantUsersVO> adminRoleUserList = new ArrayList<>();
        for (TenantUsersVO tenantUsersVO : tenantUsersVOList) {
            // 判断是否是 超级管理员 、 租户管理员 、租户所有者（待补充）
            if (BooleanUtils.isTrue(tenantUsersVO.getRoot()) || BooleanUtils.isTrue(tenantUsersVO.getAdmin())) {
                adminRoleUserList.add(tenantUsersVO);
            }
        }
        return adminRoleUserList;
    }


    /**
     * 调用console接口 返回当前租户是否支持standeAlone模式
     *
     * @param dtuicTenantId
     * @return
     */
    public Boolean hasStandAlone(Long dtuicTenantId){
        return clusterService.hasStandalone(dtuicTenantId, EComponentType.FLINK.getTypeCode());
    }

}
