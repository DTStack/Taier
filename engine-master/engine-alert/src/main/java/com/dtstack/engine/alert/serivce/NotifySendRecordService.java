package com.dtstack.engine.alert.serivce;

import com.dtstack.engine.api.domain.NotifySendRecord;
import com.dtstack.engine.api.enums.SendStatus;
import com.dtstack.engine.dao.NotifySendRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2020/10/12 9:37 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class NotifySendRecordService implements ISenderService {

    @Autowired
    private NotifySendRecordDao notifySendRecordDao;

    @Override
    public long addSenderRecord(long contentId, long notifyRecordId, long userId, int senderType, int appType, long projectId, long tenantId) {
        NotifySendRecord sendRecord = new NotifySendRecord();
        sendRecord.setContentId(contentId);
        sendRecord.setNotifyRecordId(notifyRecordId);
        sendRecord.setUserId(userId);
        sendRecord.setSendType(senderType);
        sendRecord.setAppType(appType);
        sendRecord.setSendStatus(SendStatus.PREPARED.getStatus());
        sendRecord.setProjectId(projectId);
        sendRecord.setTenantId(tenantId);
        notifySendRecordDao.insert(sendRecord);
        return sendRecord.getId();
    }

    @Override
    public void updateSenderRecord(long id, int status) {
        notifySendRecordDao.updateByIdAndStatus(id, status);
    }
}
