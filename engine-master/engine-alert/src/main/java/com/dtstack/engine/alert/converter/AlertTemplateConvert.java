package com.dtstack.engine.alert.converter;


import com.dtstack.engine.api.dto.AlertTemplateDTO;
import com.dtstack.engine.api.vo.alert.AlertGateVO;

public class AlertTemplateConvert {
	
	public static AlertTemplateDTO toDTO(AlertGateVO alertGateVO){
		AlertTemplateDTO alertTemplateDTO = new AlertTemplateDTO();
		alertTemplateDTO.setId(alertGateVO.getAlertTemplateId());
		alertTemplateDTO.setAlertTemplateName(alertGateVO.getAlertGateName());
		alertTemplateDTO.setAlertTemplateType(alertGateVO.getAlertGateType());
		alertTemplateDTO.setAlertTemplateStatus(1);
		alertTemplateDTO.setAlertTemplate(alertGateVO.getAlertTemplate());
		alertTemplateDTO.setAlertGateSource(alertGateVO.getAlertGateSource());
		return alertTemplateDTO;
	} 
}
