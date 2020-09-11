package com.dtstack.engine.master.impl;


import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.enums.PlatformEventType;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.vo.PlatformEventVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author yuebai
 * @date 2020-03-11
 */
@Service
public class PlatformService {

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private TenantDao tenantDao;

    private void registerEvent(PlatformEventType eventType, boolean active) {
        DtUicUserConnect.registerEvent(environmentContext.getDtUicUrl(), eventType, "/node/platform/callBack", active);
    }

    private static final Logger logger = LoggerFactory.getLogger(PlatformService.class);

    /**
     * 由于路由填充参数时失去了动态可能性，参数只能定义成map,或者注册多个callback
     */
    public void callback(PlatformEventVO eventVO) {
        try {
            logger.info("call back parameter {}", eventVO);
            if (null == eventVO) {
                return;
            }
            PlatformEventType eventType = PlatformEventType.getByCode(eventVO.getEventCode());
            if (null == eventType) {
                throw new RdosDefineException("不支持的事件类型");
            }
            if (null == eventVO.getTenantId()) {
                logger.info("callback {} tenantId is null ", eventVO);
                return;
            }
            switch (eventType) {
                case DELETE_TENANT:
                    Long consoleTenantId = tenantDao.getIdByDtUicTenantId(eventVO.getTenantId());
                    if (null != consoleTenantId) {
                        logger.info("delete console tenant id {} by callback {}", consoleTenantId, eventVO.getTenantId());
                        tenantDao.delete(consoleTenantId);
                    }
                    break;
                case EDIT_TENANT:
                    Tenant tenant = new Tenant();
                    tenant.setDtUicTenantId(eventVO.getTenantId());
                    tenant.setTenantName(eventVO.getTenantName());
                    tenant.setTenantDesc(eventVO.getTenantDesc());
                    tenantDao.updateByDtUicTenantId(tenant);
                default:
                    break;
            }
        } catch (Exception e) {
            logger.info("call back parameter error", e);
        }
    }

    @PostConstruct
    public void init() {
        registerEvent(PlatformEventType.DELETE_TENANT, true);
        registerEvent(PlatformEventType.EDIT_TENANT, true);
    }
}
