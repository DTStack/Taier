package com.dtstack.engine.alert.serivce;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.client.AlertServiceProvider;
import com.dtstack.engine.alert.constant.AGConstant;
import com.dtstack.engine.alert.enums.AGgateType;
import com.dtstack.engine.alert.exception.AlertGateException;
import com.dtstack.engine.api.domain.po.AlertGatePO;
import com.dtstack.engine.api.dto.AlertGateDTO;
import com.dtstack.engine.dao.AlertGateDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AlertGateService {

    private final Logger log = LoggerFactory.getLogger(AlertServiceProvider.class);

    @Autowired
    private AlertGateDao alertGateDao;

    /**
     * <p>
     * 编辑告警通道
     * </p>
     *
     * @return
     */
    public AlertGatePO editAlertGate(AlertGateDTO alertGateDTO) {
        AlertGatePO alertGatePO = new AlertGatePO();
        alertGatePO.setId(alertGateDTO.getId());
        alertGatePO.setAlertGateType(alertGateDTO.getAlertGateType());
        alertGatePO.setAlertGateCode(alertGateDTO.getAlertGateCode());
        alertGatePO.setAlertGateJson(alertGateDTO.getAlertGateJson());
        alertGatePO.setAlertGateName(alertGateDTO.getAlertGateName());
        alertGatePO.setAlertGateStatus(alertGateDTO.getAlertGateStatus());
        alertGatePO.setAlertGateSource(alertGateDTO.getAlertGateSource());
        alertGatePO.setFilePath(alertGateDTO.getFilePath());
        alertGatePO.setGmtModified(new Date());

        if (alertGateDTO.getId() == null) {
            alertGatePO.setIsDeleted(0);
            alertGateDao.insert(alertGatePO);
        } else {
            alertGateDao.update(alertGatePO);
        }
        return alertGatePO;
    }


    /**
     * <p>
     * 激活告警通道
     * </p>
     *
     * @param gateId
     * @return
     */
    public boolean activeAlertGate(Long gateId) {
        AlertGatePO alertGatePO = new AlertGatePO();
        alertGatePO.setId(gateId);
        alertGatePO.setAlertGateStatus(AGConstant.AG_STATUS_ENABLE);
        int update = alertGateDao.update(alertGatePO);
        return update > 0;
    }

    /**
     * <p>
     * 停用告警通道
     * </p>
     *
     * @param gateId
     * @return
     */
    public boolean disableAlertGate(Long gateId) {
        AlertGatePO alertGatePO = new AlertGatePO();
        alertGatePO.setId(gateId);
        alertGatePO.setAlertGateStatus(AGConstant.AG_STATUS_DISABLE);
        int update = alertGateDao.update(alertGatePO);
        return update > 0;
    }

    /**
     * <p>
     * 获取有效的告警通道
     * </p>
     *
     * @return
     */


    public AlertGatePO getSuitGateByTypeAndSource(int gateType, String source) {
        AlertGatePO alertGatePO = new AlertGatePO();
        alertGatePO.setAlertGateType(gateType);
        alertGatePO.setAlertGateSource(source);
        alertGatePO.setAlertGateStatus(AGConstant.AG_STATUS_ENABLE);
        return alertGateDao.get(alertGatePO);
    }


    public AlertGatePO getSuitGateById(Long gateId) {
        AlertGatePO alertGatePO = new AlertGatePO();
        alertGatePO.setId(gateId);
        alertGatePO.setAlertGateStatus(AGConstant.AG_STATUS_ENABLE);
        return alertGateDao.get(alertGatePO);
    }

    /**
     * 获取全部的通道列表个数
     *
     * @return
     */
    @Deprecated
    public Long countAlertGate() {
        //todo
        return 0L;
    }

    /**
     * 获取分页的通道列表
     *
     * @param page
     * @param size
     * @param asc
     * @return
     */
    @Deprecated
    public List<AlertGatePO> getAlertGates(Integer page, Integer size, boolean asc) {
        return null;
    }


    /**
     * 逻辑删除
     *
     * @param gateId
     */
    public boolean deleteAlertGateLogic(Long gateId) {
        return alertGateDao.delete(gateId) > 0;
    }

    public AlertGatePO getGateById(Long gateId) {
        AlertGatePO alertGatePO = new AlertGatePO();
        alertGatePO.setId(gateId);
        return alertGateDao.get(alertGatePO);
    }

    /**
     * 获取有效的告警通道
     *
     * @param alertGateType
     * @param alertGateSource
     * @return
     */
    public AlertGatePO getGateByTypeAndSource(int alertGateType, String alertGateSource) {
        AlertGatePO alertGatePO = new AlertGatePO();
        alertGatePO.setAlertGateType(alertGateType);
        alertGatePO.setAlertGateSource(alertGateSource);
        return alertGateDao.get(alertGatePO);
    }

    //todo 考虑使用缓存
    public AlertGatePO locateAlertGatePO(AGgateType aGgateType, String sourceName) {
        AlertGatePO alertGatePO = getSuitGateByTypeAndSource(aGgateType.type(), sourceName);
        if (alertGatePO == null) {
            log.warn("没有合适的告警通道配置");
            throw new AlertGateException("没有合适的告警通道配置,请检查告警通道配置表(dt_alert_gate)");
        }
        if (log.isDebugEnabled()) {
            log.debug("load alertService, alertGate={}", JSONObject.toJSONString(alertGatePO));
        }
        return alertGatePO;
    }
}
