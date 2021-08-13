package com.dtstack.engine.master.impl;


import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.enums.PlatformEventType;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.vo.PlatformEventVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author yuebai
 * @date 2020-03-11
 */
@Service
public class PlatformService {

    @Autowired
    private EnvironmentContext environmentContext;

    @Resource(name = "engineTenantService")
    private TenantService tenantService;

    @Autowired
    private DtUicUserConnect dtUicUserConnect;

    private void registerEvent(PlatformEventType eventType, boolean active) {
        dtUicUserConnect.registerEvent(environmentContext.getDtUicUrl(), eventType, "/node/platform/callBack", active);
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
                throw new RdosDefineException("Unsupported event type");
            }
            if (null == eventVO.getTenantId()) {
                logger.info("callback {} tenantId is null ", eventVO);
                return;
            }
            switch (eventType) {
                case DELETE_TENANT:
                    tenantService.deleteTenantId(eventVO.getTenantId());
                    break;
                case EDIT_TENANT:
                    tenantService.updateTenantInfo(eventVO.getTenantId(), eventVO.getTenantName(), eventVO.getTenantDesc());
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
