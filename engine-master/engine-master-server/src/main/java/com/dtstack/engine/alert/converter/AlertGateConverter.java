package com.dtstack.engine.alert.converter;

import com.dtstack.engine.api.domain.po.AlertGatePO;
import com.dtstack.engine.api.domain.po.AlertTemplatePO;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.dtstack.engine.api.dto.AlertGateDTO;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * <p>
 *     对象converter
 * </p>
 * @author 青涯
 */
public class AlertGateConverter {

    public static AlertGateVO toVO(AlertGatePO alertGatePO, AlertTemplatePO alertTemplatePO, ClusterAlertPO clusterAlertPO){
    	AlertGateVO alertGateVO = new AlertGateVO();
    	alertGateVO.setId(alertGatePO.getId());

    	alertGateVO.setAlertGateName(alertGatePO.getAlertGateName());
    	alertGateVO.setAlertGateType(alertGatePO.getAlertGateType());
    	alertGateVO.setAlertGateJson(alertGatePO.getAlertGateJson());
    	alertGateVO.setAlertGateCode(alertGatePO.getAlertGateCode());
    	alertGateVO.setAlertGateSource(alertGatePO.getAlertGateSource());
		if (StringUtils.isBlank(alertGatePO.getFilePath())) {
			alertGateVO.setFilePath(alertGatePO.getFilePath());
		} else {
			String[] split = StringUtils.split(alertGatePO.getFilePath(), File.separator);
			alertGateVO.setFilePath(split[split.length - 1]);
		}


		if (alertTemplatePO != null) {
			alertGateVO.setAlertTemplateId(alertTemplatePO.getId());
			alertGateVO.setAlertTemplate(alertTemplatePO.getAlertTemplate());
		}

		if (clusterAlertPO != null) {
			alertGateVO.setClusterId(clusterAlertPO.getClusterId());
			alertGateVO.setIsDefault(clusterAlertPO.getIsDefault());
		}
    	return alertGateVO;
    }
    
    public static AlertGateDTO toDTO(AlertGateVO alertGateVO){
    	AlertGateDTO alertGateDTO = new AlertGateDTO();
		alertGateDTO.setId(alertGateVO.getId());
		alertGateDTO.setAlertGateName(alertGateVO.getAlertGateName());
		alertGateDTO.setAlertGateType(alertGateVO.getAlertGateType());
		alertGateDTO.setAlertGateCode(alertGateVO.getAlertGateCode());
		alertGateDTO.setAlertGateStatus(1);
		alertGateDTO.setAlertGateJson(alertGateVO.getAlertGateJson());
		alertGateDTO.setAlertGateSource(alertGateVO.getAlertGateSource());
		alertGateDTO.setFilePath(alertGateVO.getFilePath()==null? "":alertGateVO.getFilePath());
		return alertGateDTO;
    }
    
    public static AlertGateDTO apply(AlertGatePO source) {
        AlertGateDTO alertGate = new AlertGateDTO();
        alertGate.setAlertGateCode(source.getAlertGateCode());
        alertGate.setAlertGateJson(source.getAlertGateJson());
        alertGate.setAlertGateName(source.getAlertGateName());
        alertGate.setAlertGateStatus(source.getAlertGateStatus());
        alertGate.setAlertGateType(source.getAlertGateType());
        alertGate.setGmtCreated(source.getGmtCreated());
        alertGate.setGmtModified(source.getGmtModified());
        alertGate.setId(source.getId());
        alertGate.setAlertGateSource(source.getAlertGateSource());
        alertGate.setIsDeleted(source.getIsDeleted());
        alertGate.setTenantId(source.getTenantId());
        return alertGate;
    }
}
