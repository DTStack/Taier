package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.dto.AlarmSendDTO;
import com.dtstack.engine.dto.AlertContentDTO;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.dao.AlertContentDao;
import com.dtstack.engine.domain.AlertContent;
import com.dtstack.engine.master.enums.AlertMessageStatusEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 9:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class AlertContentService {

    @Autowired
    private AlertContentDao alertContentDao;

    public Long insertContent(AlertContentDTO alertContentDTO) {
        alertContentDTO.setAlertMessageStatus(AlertMessageStatusEnum.NO_ALTER.getType());
        alertContentDTO.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertContentDTO.setSendInfo("");
        AlertContent alertContent = new AlertContent();
        BeanUtils.copyProperties(alertContentDTO, alertContent);

        if (alertContent.getProjectId() == null) {
            alertContent.setProjectId(-1L);
        }
        alertContentDao.insert(alertContent);
        return alertContent.getId();
    }


    public AlertContent findContentById(Long contentId) {
        return alertContentDao.selectById(contentId);
    }

    public void updateContent(Long contentId, AlarmSendDTO alarmSendDTO, Integer type) {
        AlertContent alertContent = new AlertContent();
        alertContent.setSendInfo(JSON.toJSONString(alarmSendDTO));
        alertContent.setAlertMessageStatus(type);
        alertContent.setId(contentId);
        alertContentDao.updateById(alertContent);

    }
}
