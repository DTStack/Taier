package com.dtstack.engine.master.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dtstack.engine.common.enums.AlertGateTypeEnum;
import com.dtstack.engine.common.enums.IsDefaultEnum;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.api.param.ClusterAlertParam;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.dao.AlertChannelDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 9:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class AlertChannelService {

    @Autowired
    private AlertChannelDao alertChannelDao;

    public Boolean addChannelOrEditChannel(AlertGateVO alertGateVO) {
        AlertChannel alertChannel = null;

        if (alertGateVO.getId() != null) {
            alertChannel = alertChannelDao.selectById(alertGateVO.getId());
        }

        int changed = 0;
        if (alertChannel == null) {
            // 插入通道
            alertChannel = new AlertChannel();
            buildBean(alertGateVO, alertChannel);
            changed = alertChannelDao.insert(alertChannel);
        } else {
            // 编辑通道
            alertChannel.setId(alertGateVO.getId());
            buildBean(alertGateVO, alertChannel);
            changed =  alertChannelDao.updateById(alertChannel);
        }

        return changed > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean checkAlertGateSourceExist(String alertGateSource) {
        AlertChannel alertChannel = alertChannelDao.selectOne(new QueryWrapper<AlertChannel>()
                .eq("alertGateSource",alertGateSource)
                .eq("isDeleted",IsDeletedEnum.NOT_DELETE.getType())
        );
        return alertChannel == null ? Boolean.FALSE : Boolean.TRUE;
    }

    private void buildBean(AlertGateVO alertGateVO, AlertChannel alertChannel) {
        if(alertGateVO.getClusterId()!=null) {
            alertChannel.setClusterId(alertGateVO.getClusterId().longValue());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertGateName())) {
            alertChannel.setAlertGateName(alertGateVO.getAlertGateName());
        }

        if(alertGateVO.getAlertGateType()!=null) {
            alertChannel.setAlertGateType(alertGateVO.getAlertGateType());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertGateCode())) {
            alertChannel.setAlertGateCode(alertGateVO.getAlertGateCode());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertGateJson())) {
            alertChannel.setAlertGateJson(alertGateVO.getAlertGateJson());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertGateSource())) {
            alertChannel.setAlertGateSource(alertGateVO.getAlertGateSource());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertTemplate())) {
            alertChannel.setAlertTemplate(alertGateVO.getAlertTemplate());
        }

        if(StringUtils.isNotBlank(alertGateVO.getFilePath())) {
            alertChannel.setFilePath(alertGateVO.getFilePath());
        }

        if(alertGateVO.getIsDefault()!=null) {
            alertChannel.setIsDefault(alertGateVO.getIsDefault());
        }
        alertChannel.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
    }


    public Boolean setDefaultAlert(ClusterAlertParam param) {
        checkDefaultParam(param);
        AlertChannel channel = alertChannelDao.selectById(param.getAlertId());

        if (channel == null) {
            throw new RdosDefineException("通道不存在!");
        }

        // 重设默认通道
        AlertChannel alertChannel = new AlertChannel();
        alertChannel.setIsDefault(IsDefaultEnum.NOT_DEFAULT.getType());
        alertChannelDao.update(alertChannel, new UpdateWrapper<AlertChannel>()
                .eq("isDeleted", IsDeletedEnum.NOT_DELETE.getType())
                .eq("alertGateType", param.getAlertGateType())
        );

//        AlertChannel alertChannel = new AlertChannel();
//        alertChannel.setClusterId(param.getClusterId());
//        alertChannel.setIsDefault(IsDefaultEnum.DEFAULT.getType());
//        alertChannel.setId(param.getAlertId());
//        alertChannel.setAlertGateType(param.getAlertGateType());



        return null;
    }

    private void checkDefaultParam(ClusterAlertParam param) {
        if (param.getClusterId()==null) {
            param.setClusterId(0L);
        }

        if (param.getAlertId() == null) {
            throw new RdosDefineException("通道ID（AlterId）是必传参数");
        }

        if (param.getAlertGateType() == null) {
            throw new RdosDefineException("通道类型（AlertGateType）是必传参数");
        }

        if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(param.getAlertGateType())) {
            throw new RdosDefineException("自定义通道类型（AlertGateType）不能设置默认值");
        }
    }
}
