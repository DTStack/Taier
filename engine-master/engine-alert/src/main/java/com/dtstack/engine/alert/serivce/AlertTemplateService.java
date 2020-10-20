package com.dtstack.engine.alert.serivce;

import com.dtstack.engine.api.domain.po.AlertTemplatePO;
import com.dtstack.engine.api.dto.AlertTemplateDTO;
import com.dtstack.engine.dao.AlertTemplateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AlertTemplateService {


    @Autowired
    private AlertTemplateDao alertTemplateDao;

    public AlertTemplatePO editAlertTemplate(AlertTemplateDTO alertTemplateDTO) {
        AlertTemplatePO alertTemplatePO = alertTemplateDao.getByTemplateTypeAndSource(alertTemplateDTO.getAlertTemplateType(), alertTemplateDTO.getAlertGateSource());

        alertTemplatePO = alertTemplatePO == null ? new AlertTemplatePO() : alertTemplatePO;
        alertTemplatePO.setAlertTemplateName(alertTemplateDTO.getAlertTemplateName());
        alertTemplatePO.setAlertTemplateType(alertTemplateDTO.getAlertTemplateType());
        alertTemplatePO.setAlertTemplateStatus(alertTemplateDTO.getAlertTemplateStatus());
        alertTemplatePO.setAlertTemplate(alertTemplateDTO.getAlertTemplate());
        alertTemplatePO.setAlertGateSource(alertTemplateDTO.getAlertGateSource());
        alertTemplatePO.setGmtModified(new Date());

        if (alertTemplatePO.getId() == null) {
            alertTemplateDao.insert(alertTemplatePO);
        } else {
            alertTemplateDao.update(alertTemplatePO);
        }
        return alertTemplatePO;
    }

    /**
     * 根据alertGateType和 alertGateSource 删除
     *
     * @param alertGateType
     * @param alertGateSource
     */
    public boolean deleteTemplateByTypeAndSource(int alertGateType, String alertGateSource) {
        int updateRows = alertTemplateDao.deleteByTemplateTypeAndSource(alertGateType, alertGateSource);
        return updateRows > 0;
    }


	public AlertTemplatePO getAlertTemplateByTypeAndSource(int templateType, String sourceName) {
		return alertTemplateDao.getByTemplateTypeAndSource(templateType, sourceName);
	}
}
