package com.dtstack.engine.master.impl;

import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.send.NoticeSender;
import com.dtstack.engine.api.domain.NotifyRecordContent;
import com.dtstack.engine.api.domain.NotifyRecordRead;
import com.dtstack.engine.api.dto.NotifyRecordReadDTO;
import com.dtstack.engine.api.dto.UserMessageDTO;
import com.dtstack.engine.api.enums.MailType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.dao.NotifyRecordContentDao;
import com.dtstack.engine.dao.NotifyRecordReadDao;
import com.dtstack.engine.master.enums.ReadStatus;
import com.dtstack.schedule.common.enums.AppType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 10:53 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class NotifyService {

    @Autowired
    private NotifyRecordContentDao notifyRecordContentDao;

    @Autowired
    private NotifyRecordReadDao notifyRecordReadDao;

    @Autowired
    private NoticeSender noticeSender;

    public NotifyService() {
    }


    public String getByContentId(Long tenantId, Long projectId, AppType appType, Long contentId) {
        return notifyRecordContentDao.getContent(tenantId, projectId, appType.getType(), contentId);
    }


    public NotifyRecordReadDTO getOne(Long tenantId, Long projectId, AppType appType, Long userId, Long readId) {
        return notifyRecordReadDao.getOne(tenantId, projectId, userId, appType.getType(), readId);
    }

    /**
     * 分页查询消息列表
     * mode,1：普通查询，2：未读消息，3：已读消息
     */
    public PageResult<List<NotifyRecordReadDTO>> pageQuery(Long tenantId,
                                                           Long projectId,
                                                           Long userId,
                                                           AppType appType,
                                                           ReadStatus readStatus,
                                                           Integer currentPage,
                                                           Integer pageSize) {
        Integer statusTmp = null;
        if (ReadStatus.ALL != readStatus) {
            statusTmp = readStatus.getStatus();
        }
        final Integer status = statusTmp;
        Page<NotifyRecordReadDTO> page = PageHelper.startPage(currentPage, pageSize, "recordRead.gmt_modified desc")
                .doSelectPage(() -> notifyRecordReadDao.listByUserId(tenantId, projectId, userId, appType.getType(), status));
        return new PageResult(page.getPageNum(), page.getPageSize(),(int) page.getTotal(), page.getPages(), page.getResult());
    }

    /**
     * 标为已读
     */
    public void tabRead(Long tenantId,
                        Long projectId,
                        Long userId,
                        AppType appType,
                        List<Long> readIds) {
        notifyRecordReadDao.updateReadStatus(tenantId, projectId, userId, appType.getType(), readIds, ReadStatus.READ.getStatus());
    }

    /**
     * 全部已读
     */
    public void allRead(Long tenantId,
                        Long projectId,
                        Long userId,
                        AppType appType) {
        notifyRecordReadDao.updateReadStatus(tenantId, projectId, userId, appType.getType(), null, ReadStatus.READ.getStatus());
    }

    /**
     * 删除
     */
    public void delete(Long tenantId,
                       Long projectId,
                       Long userId,
                       AppType appType,
                       List<Long> readIds) {
        notifyRecordReadDao.delete(tenantId, projectId, userId, appType.getType(), readIds);
    }

    public Long generateContent(Long tenantId, Long projectId, AppType appType, String content, Integer status) {
        NotifyRecordContent recordContent = new NotifyRecordContent();
        recordContent.setAppType(appType.getType());
        recordContent.setContent(content);
        recordContent.setStatus(status);
        recordContent.setTenantId(tenantId);
        if (projectId == null) {
            projectId = 0L;
        }
        recordContent.setProjectId(projectId);
        notifyRecordContentDao.insert(recordContent);
        return recordContent.getId();
    }

    public void sendAlarm(Long tenantId, Long projectId, Long notifyRecordId, AppType appType, String title, Long contentId, List<UserMessageDTO> receivers, List<Integer> senderTypes, String webhook, MailType mailType) {
        if (CollectionUtils.isEmpty(receivers)) {
            return;
        }
        if (projectId == null) {
            projectId = 0L;
        }
        String content = notifyRecordContentDao.getContent(tenantId, projectId, appType.getType(), contentId);
        for (UserMessageDTO receiver : receivers) {
            if (CollectionUtils.isNotEmpty(senderTypes)) {
                sendNoticeAsync(tenantId, projectId, notifyRecordId, appType, title, contentId, receiver, senderTypes, null, content, webhook);
            }
            addNotifyRecordRead(tenantId, projectId, appType, notifyRecordId, contentId, receiver);
        }
    }

    private void sendNoticeAsync(Long tenantId, Long projectId, Long notifyRecordId, AppType appType, String title, Long contentId, UserMessageDTO receiver, List<Integer> senderTypes, MailType mailType, String content, String webhook) {
        if (mailType == null) {
            mailType = MailType.SIMPLE;
        }
        for (Integer senderType : senderTypes) {
            Notice notice = new Notice();
            notice.setSenderType(senderType);
            notice.setTitle(title);
            notice.setContentId(contentId);
            notice.setContent(content);
            notice.setNotifyRecordId(notifyRecordId);
            notice.setAppType(appType.getType());
            notice.setUserDTO(receiver);
            notice.setTenantId(tenantId);
            notice.setProjectId(projectId);
            notice.setMailType(mailType);
            notice.setWebhook(webhook);
            noticeSender.sendNoticeAsync(notice);
        }
    }

    private void addNotifyRecordRead(Long tenantId, Long projectId, AppType appType, Long notifyRecordId, Long contentId, UserMessageDTO receiver){
        NotifyRecordRead recordRead = new NotifyRecordRead();
        recordRead.setTenantId(tenantId);
        recordRead.setProjectId(projectId);
        recordRead.setAppType(appType.getType());
        recordRead.setNotifyRecordId(notifyRecordId);
        recordRead.setContentId(contentId);
        recordRead.setReadStatus(ReadStatus.UNREAD.getStatus());
        recordRead.setUserId(receiver.getUserId());
        notifyRecordReadDao.insert(recordRead);
    }
}
