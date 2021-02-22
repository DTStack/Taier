package com.dtstack.engine.alert.serivce;

/**
 * @Auther: dazhi
 * @Date: 2020/10/12 9:36 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ISenderService {

    long addSenderRecord(long contentId, long notifyRecordId, long userId, int senderType, int appType, long projectId, long tenantId);

    void updateSenderRecord(long id, int status);
}
