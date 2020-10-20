package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.dto.NotifyRecordReadDTO;
import com.dtstack.engine.api.dto.UserMessageDTO;
import com.dtstack.engine.api.enums.MailType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.enums.NotifyMode;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.enums.ReadStatus;
import com.dtstack.schedule.common.enums.AppType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author sanyue
 */
@Service
public class NotifyRecordService {

    public static Logger LOG = LoggerFactory.getLogger(NotifyRecordService.class);

    @Autowired
    private NotifyService notifyService;


    public NotifyRecordReadDTO getOne(Long tenantId,
                                      Long projectId,
                                      Long userId,
                                      Long readId,
                                      Integer appType) {
        return notifyService.getOne(tenantId, projectId, AppType.getValue(appType), userId, readId);
    }


    /**
     * 分页查询消息列表
     * mode,1：普通查询，2：未读消息，3：已读消息
     */
    public PageResult<List<NotifyRecordReadDTO>> pageQuery(@Param("tenantId") Long tenantId,
                                                           @Param("projectId") Long projectId,
                                                           @Param("currentPage") Integer currentPage,
                                                           @Param("pageSize") Integer pageSize,
                                                           @Param("userId") Long userId,
                                                           @Param("mode") Integer mode,
                                                           @Param("appType") Integer appType) {
        ReadStatus readStatus = null;
        if (NotifyMode.UNREAD.getMode() == mode) {
            readStatus = ReadStatus.UNREAD;
        } else if (NotifyMode.READ.getMode() == mode) {
            readStatus = ReadStatus.READ;
        } else {
            readStatus = ReadStatus.ALL;
        }
        PageResult<List<NotifyRecordReadDTO>> result = notifyService.pageQuery(tenantId, projectId, userId, AppType.getValue(appType), readStatus, currentPage, pageSize);
        return result;
    }

    /**
     * 标为已读
     */
    public void tabRead(@Param("notifyRecordIds") List<Long> notifyRecordIds,
                        @Param("userId") Long userId,
                        @Param("tenantId") Long tenantId,
                        @Param("projectId") Long projectId,
                        @Param("appType") Integer appType) {
        if (CollectionUtils.isEmpty(notifyRecordIds)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        notifyService.tabRead(tenantId, projectId, userId, AppType.getValue(appType), notifyRecordIds);
    }

    /**
     * 全部已读
     */
    public void allRead(@Param("userId") Long userId, @Param("tenantId") Long tenantId,
                        @Param("projectId") Long projectId, @Param("appType") Integer appType) {
        notifyService.allRead(tenantId, projectId, userId, AppType.getValue(appType));
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(@Param("notifyRecordIds") List<Long> notifyRecordIds, @Param("userId") Long userId,
                       @Param("tenantId") Long tenantId, @Param("projectId") Long projectId,
                       @Param("appType") Integer appType) {
        if (CollectionUtils.isEmpty(notifyRecordIds)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        notifyService.delete(tenantId, projectId, userId, AppType.getValue(appType), notifyRecordIds);
    }

    public Long generateContent(
            @Param("tenantId") Long tenantId, @Param("projectId") Long projectId,
            @Param("appType") Integer appType, @Param("content") String content,
            @Param("status") Integer status) {
        return notifyService.generateContent(tenantId, projectId, AppType.getValue(appType), content, status);
    }


    public void sendAlarm(
            @Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("notifyRecordId") Long notifyRecordId,
            @Param("appType") Integer appType, @Param("title") String title, @Param("contentId") Long contentId,
            @Param("receivers") List<UserMessageDTO> receivers, @Param("senderTypes") List<Integer> senderTypes, @Param("webhook") String webhook,
            @Param("mailType") Integer mailType) {

        notifyService.sendAlarm(tenantId, projectId, notifyRecordId, AppType.getValue(appType), title, contentId, receivers, senderTypes, webhook, convertMailType(mailType));
    }


    private MailType convertMailType(Integer type) {
        if(null == type){
            return MailType.SIMPLE;
        }

        if (MailType.MIME.getType() == type) {
            return MailType.MIME;
        }

        if (MailType.SIMPLE.getType() == type) {
            return MailType.SIMPLE;
        }

        return MailType.SIMPLE;
    }

}
