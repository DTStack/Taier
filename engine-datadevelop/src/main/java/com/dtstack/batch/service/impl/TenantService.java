package com.dtstack.batch.service.impl;

import com.dtstack.batch.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.TenantDao;
import com.dtstack.batch.domain.Tenant;
import com.dtstack.batch.enums.ProductCodeEnum;
import com.dtstack.batch.service.uic.impl.UIcUserTenantRelApiClient;
import com.dtstack.batch.service.uic.impl.UicUserApiClient;
import com.dtstack.batch.service.uic.impl.domain.TenantUsersVO;
import com.dtstack.batch.vo.TenantVO;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.login.DtUicUserConnect;
import com.dtstack.dtcenter.common.login.domain.UserTenant;
import com.dtstack.engine.master.impl.ClusterService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sishu.yss
 */
@Service
public class TenantService {

    public static Logger LOG = LoggerFactory.getLogger(TenantService.class);

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private UicUserApiClient uicUserApiClient;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    public Tenant getTenantByDtUicTenantId(long dtUicTenantId) {
        return tenantDao.getByDtUicTenantId(dtUicTenantId);
    }

    public Tenant getTenantById(long tenantId) {
        return tenantDao.getOne(tenantId);
    }

    public Tenant addOrUpdate(Tenant tenant) {
        if (tenant.getId() > 0) {
            tenantDao.update(tenant);
        } else {
            Integer insert = tenantDao.insert(tenant);
            if (insert != null && insert == 0) {
                return tenantDao.getByDtUicTenantId(tenant.getDtuicTenantId());
            }
        }
        return tenant;
    }

    public Long getDtuicTenantId(Long tenantId) {
        Tenant tenant = tenantDao.getOne(tenantId);
        if (tenant != null) {
            return tenant.getDtuicTenantId();
        }
        return null;
    }

    public List<TenantVO> getUserTenants(String tenantName, String dtToken) {
        if (StringUtils.isBlank(tenantName)){
            tenantName = "";
        }
        try {
            tenantName = URLEncoder.encode(tenantName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("tenantName urlEncode失败 参数为{},原因为{}",tenantName,e);
        }
        List<UserTenant> userTenants = DtUicUserConnect.getUserTenants(environmentContext.getDtUicUrl(), dtToken, tenantName);
        if (CollectionUtils.isEmpty(userTenants)) {
            return Collections.EMPTY_LIST;
        }
        Map<Long, UserTenant> userTenantMap = userTenants.stream().collect(Collectors.toMap(ut -> ut.getTenantId(), ut -> ut));
        List<Tenant> byDtUicTenantIds = tenantDao.getByDtUicTenantIds(userTenantMap.keySet());
        if (CollectionUtils.isEmpty(byDtUicTenantIds)) {
            return Collections.EMPTY_LIST;
        }
        return byDtUicTenantIds.stream().map(t -> {
            TenantVO vo = new TenantVO();
            vo.setTenantId(t.getId());
            vo.setTenantName(t.getTenantName());
            vo.setUicTenantId(t.getDtuicTenantId());
            UserTenant userTenant = userTenantMap.get(t.getDtuicTenantId());
            if(null != userTenant){
                vo.setCurrent(userTenant.getCurrent());
            }
            return vo;
        }).collect(Collectors.toList());
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
     * 获取租户名称
     * @param tenantId
     * @return
     */
    public String getTenantNameById(Long tenantId){
        if(tenantId == null){
            return "";
        }
        Tenant tenant = tenantDao.getOne(tenantId);
        if (tenant == null || tenant.getTenantName() == null) {
            return "";
        }
        return tenant.getTenantName();
    }

    /**
     * 获取任务可依赖的产品
     *
     * @param tenantId
     * @param uicUserId
     * @return
     */
    public List<Integer> getAppList(Long tenantId, Long uicUserId, String token, String productCode) {
        Tenant tenant = tenantDao.getOne(tenantId);
        if (Objects.isNull(tenant)) {
            throw new RdosDefineException(String.format("tenant info is null by tenantId : %s", tenantId));
        }
        List<ProductCodeEnum> productCodeEnumList = Lists.newArrayList(ProductCodeEnum.RDOS, ProductCodeEnum.SCIENCE);
        // 调用uic接口得到哪些产品是有权限的
        List<Integer> appCanLookList = productCodeEnumList.stream().filter(produceCodeEnum -> {
            return BooleanUtils.isTrue(uicUserApiClient.userHasSubProduct(uicUserId, tenant.getDtuicTenantId(), produceCodeEnum.getSubProductCode(),
                    productCode, token));
        }).map(ProductCodeEnum::getType).collect(Collectors.toList());
        return appCanLookList;
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
     * 返回当前租户是否支持 standeAlone模式
     *
     * @param tenantId
     * @return
     */
    public Boolean isStandAlone(Long tenantId) {
        Long dtuicTenantId = getDtuicTenantId(tenantId);
        if (dtuicTenantId == null) {
            throw new RdosDefineException(String.format("传入的tenantId找不到对应的dtUicTenantId，传入的tenantId = %s", tenantId));
        }
        //调用engine接口 返回
        return hasStandAlone(dtuicTenantId);
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

    /**
     * 获取所有的租户
     *
     * @return
     */
    public List<Tenant> getAll() {
        return tenantDao.listAll();
    }
}
