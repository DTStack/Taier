package com.dtstack.task.server.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.NotifyStatus;
import com.dtstack.dtcenter.common.enums.SenderType;
import com.dtstack.dtcenter.common.util.BitUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.NotifyAlarmDao;
import com.dtstack.engine.dao.NotifyDao;
import com.dtstack.engine.dao.NotifyUserDao;
import com.dtstack.engine.domain.Notify;
import com.dtstack.engine.domain.NotifyAlarm;
import com.dtstack.engine.domain.NotifyUser;
import com.dtstack.engine.dto.UserDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sanyue
 */
@Service
public class NotifyService {

    @Autowired
    private NotifyDao notifyDao;

    @Autowired
    private NotifyUserDao notifyUserDao;

    @Autowired
    private NotifyAlarmDao notifyAlarmDao;


//    /**
//     * 入参均不能为empty
//     *
//     * @param type       biz业务类型
//     * @param relationId 关联id
//     * @return
//     */
//    @Forbidden
//    public NotifyVO getNotify(Integer type, Long relationId, String name, Long tenantId, Long projectId) {
//        Notify notify = this.getByBizTypeAndRelationIdAndName(type, relationId, name, tenantId, projectId);
//        if (notify == null) {
//            return null;
//        }
//        NotifyVO notifyVO = NotifyVO.toVO(notify);
//        notifyVO.setSendTypes(BitUtil.getValues(notify.getSendWay()));
//
//        List<Long> userIds = this.getReceiversByNotifyId(notify.getId(), tenantId, projectId);
//        notifyVO.setReceivers(userIds);
//        return notifyVO;
//    }
//
//    public Notify getNotifyByAlaramId(Long alarmId, Integer bizType, Long tenantId, Long projectId) {
//        Notify notify = notifyAlarmDao.getByAlarmId(alarmId, bizType, tenantId, projectId);
//        return notify;
//    }

    /**
     * 入参均不能为empty
     *
     * @param tenantId   租户id
     * @param type       biz业务类型
     * @param relationId 关联id
     * @param sendTypes  通知方式
     * @param receivers  接收人
     */
    @Forbidden
    public void addNotify(Long alarmId, Long tenantId, Long projectId, Integer type, Integer triggerType, Long relationId,
                          String startTime, String endTime, String uncompleteTime,
                          List<Integer> sendTypes, String receivers, Long createUserId, String name, String webhook,Integer appType) {
        Notify notify = this.getByBizTypeAndRelationIdAndName(type, relationId, name, tenantId, projectId);
        Timestamp modify = Timestamp.valueOf(LocalDateTime.now());
        if (notify != null) {
            throw new RdosDefineException("该任务对应通知对象已存在");
        }
        notify = new Notify();
        notify.setStatus(0);
        notify.setBizType(type);
        notify.setRelationId(relationId);
        notify.setTenantId(tenantId);
        notify.setGmtCreate(modify);
        notify.setSendWay(BitUtil.getBitVector(sendTypes));
        notify.setGmtModified(modify);
        notify.setStartTime(startTime);
        notify.setEndTime(endTime);
        notify.setUncompleteTime(uncompleteTime);
        notify.setProjectId(projectId);
        notify.setTriggerType(triggerType);
        notify.setCreateUserId(createUserId);
        notify.setName(name);
        notify.setWebhook(StringUtils.isNotEmpty(webhook) ? webhook : "");
        notify.setAppType(appType);
        notifyDao.insert(notify);


        List<Long> users = this.convertStringToList(receivers);
        for (Long userId : users) {
            NotifyUser notifyUser = new NotifyUser();
            notifyUser.setTenantId(tenantId);
            notifyUser.setNotifyId(notify.getId());
            notifyUser.setUserId(userId);
            notifyUser.setProjectId(projectId);
            notifyUser.setGmtCreate(modify);
            notifyUser.setGmtModified(modify);
            notifyUser.setAppType(appType);
            this.addNotifyUser(notifyUser);
        }

        this.addNotifyAlarm(notify.getId(), alarmId, type, projectId, tenantId,appType);
    }

    public void updateNotify(String orginalName, Long tenantId, Long projectId, Integer type, Long relationId,
                             String startTime, String endTime, String uncompleteTime,
                             List<Integer> sendTypes, String receivers, String name, String webhook) {
        Notify notify = notifyDao.getNotifyByNameAndProjectId(type, orginalName, projectId, AppType.RDOS.getType());
        Timestamp modify = Timestamp.valueOf(LocalDateTime.now());
        if (notify == null) {
            //throw new RdosDefineException("该任务对应通知对象不存在，请执行新增操作", ErrorCode.UNKNOWN_ERROR);
            //为兼容之前版本，该告警对象无对应的通知对象
            return;
        }

        notify.setName(name);
        notify.setStartTime(startTime);
        notify.setEndTime(endTime);
        notify.setUncompleteTime(uncompleteTime);
        notify.setSendWay(BitUtil.getBitVector(sendTypes));
        notify.setGmtModified(modify);
        notify.setRelationId(relationId);
        if (CollectionUtils.isNotEmpty(sendTypes) && sendTypes.contains(SenderType.DINGDING.getType())) {
            notify.setWebhook(webhook);
        }
        notifyDao.update(notify);

        List<Long> existUserIds = this.getReceiversByNotifyId(notify.getId(), tenantId, projectId);

        List<Long> users = convertStringToList(receivers);
        for (Long userId : users) {
            if (existUserIds.contains(userId)) {
                existUserIds.remove(userId);
                continue;
            }
            NotifyUser notifyUser = new NotifyUser();
            notifyUser.setTenantId(tenantId);
            notifyUser.setNotifyId(notify.getId());
            notifyUser.setUserId(userId);
            notifyUser.setProjectId(projectId);
            notifyUser.setGmtModified(modify);
            notifyUser.setGmtCreate(modify);
            this.addNotifyUser(notifyUser);
        }
        if (CollectionUtils.isNotEmpty(existUserIds)) {
            this.deleteNotifyUser(notify.getId(), existUserIds, tenantId, projectId);
        }
    }

    public void delete(Long alarmId, Integer bizType, Long projectId, Long tenantId) {
        NotifyAlarm notifyAlarm = notifyAlarmDao.getByAlarmIdAndBizType(alarmId, bizType, projectId, tenantId);
        if (notifyAlarm == null) {
            return;
        }
        //为兼容之前，不做notify的非空判断
        Notify notify = new Notify();
        notify.setId(notifyAlarm.getNotifyId());
        notify.setStatus(NotifyStatus.DELETE.getStatus());
        notify.setIsDeleted(Deleted.DELETED.getStatus());
        notify.setProjectId(projectId);
        notify.setTenantId(tenantId);
        notifyDao.update(notify);

        List<Long> existUserIds = this.getReceiversByNotifyId(notify.getId(), tenantId, projectId);
        //删除通知人
        deleteNotifyUser(notifyAlarm.getNotifyId(), existUserIds, tenantId, projectId);

        deleteNotifyAlarm(notifyAlarm.getNotifyId(), notifyAlarm.getAlarmId(), notifyAlarm.getBizType(), tenantId, projectId);
    }

    @Forbidden
    private List<Long> convertStringToList(String receivers) {
        List<UserDTO> userDTOS = JSONObject.parseArray(receivers, UserDTO.class);
        if(CollectionUtils.isNotEmpty(userDTOS)){
            return userDTOS.stream().map(UserDTO::getId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Forbidden
    public Notify getByBizTypeAndRelationIdAndName(Integer bizType, Long relationId, String name, Long tenantId, Long projectId) {
        return notifyDao.getByBizTypeAndRelationIdAndName(bizType, relationId, name, tenantId, projectId,AppType.RDOS.getType());
    }

    @Forbidden
    public NotifyUser addNotifyUser(NotifyUser notifyUser) {
        notifyUserDao.insert(notifyUser);
        return notifyUser;
    }

    @Forbidden
    public List<Long> getReceiversByNotifyId(Long notifyId, Long tenantId, Long projectId) {
        return notifyUserDao.getUserIdByNotifyId(notifyId, tenantId, projectId);
    }

    @Forbidden
    public Integer deleteNotifyUser(Long notifyId, List<Long> userIds, Long tenantId, Long projectId) {
        return notifyUserDao.deleteByNotifyIdAndUserIds(notifyId, userIds, tenantId, projectId);
    }

    @Forbidden
    public Integer deleteNotifyAlarm(Long notifyId, Long alarmId, Integer bizType, Long tenantId, Long projectId) {
        return notifyAlarmDao.deleteByNotifyIdAlarmIdBizType(notifyId, alarmId, bizType, tenantId, projectId);
    }

    @Forbidden
    public NotifyAlarm addNotifyAlarm(Long notifyId, Long alarmId, Integer bizType, Long projectId, Long tenantId,Integer appType) {
        NotifyAlarm notifyAlarm = new NotifyAlarm();
        notifyAlarm.setNotifyId(notifyId);
        notifyAlarm.setAlarmId(alarmId);
        notifyAlarm.setBizType(bizType);
        notifyAlarm.setProjectId(projectId);
        notifyAlarm.setTenantId(tenantId);
        notifyAlarm.setAppType(appType);
        notifyAlarmDao.insert(notifyAlarm);
        return notifyAlarm;
    }

    public void updateStatus(long alarmId, boolean isClose, int bizType, long projectId, long tenantId) {
        NotifyAlarm notifyAlarm = notifyAlarmDao.getByAlarmIdAndBizType(alarmId, bizType, projectId, tenantId);
        if (notifyAlarm == null) {
            return;
        }
        Notify notify = new Notify();
        notify.setId(notifyAlarm.getNotifyId());
        notify.setProjectId(projectId);
        notify.setTenantId(tenantId);
        if (isClose) {
            notify.setStatus(NotifyStatus.CLOSE.getStatus());
        } else {
            notify.setStatus(NotifyStatus.NORMAL.getStatus());
        }
        notifyDao.update(notify);
    }
}
