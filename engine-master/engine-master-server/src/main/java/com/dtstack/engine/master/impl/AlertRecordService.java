package com.dtstack.engine.master.impl;

import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.AlterSender;
import com.dtstack.engine.alert.EventMonitor;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.enums.AlertRecordStatusEnum;
import com.dtstack.engine.api.dto.AlarmSendDTO;
import com.dtstack.engine.api.dto.AlertRecordJoinDTO;
import com.dtstack.engine.api.dto.NotifyRecordReadDTO;
import com.dtstack.engine.api.dto.UserMessageDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.AlertRecordDao;
import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.domain.AlertContent;
import com.dtstack.engine.domain.AlertRecord;
import com.dtstack.engine.master.enums.AlertMessageStatusEnum;
import com.dtstack.engine.master.enums.AlertSendStatusEnum;
import com.dtstack.engine.master.enums.ReadStatus;
import com.dtstack.engine.master.event.StatusUpdateEvent;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 9:45 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class AlertRecordService {

    private final Logger log = LoggerFactory.getLogger(AlertRecordService.class);

    @Autowired
    private AlertRecordDao alertRecordMapper;

    @Autowired
    private AlertChannelService alertChannelService;

    @Autowired
    private AlertContentService alertContentService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private AlterSender alterSender;

    @Autowired
    private List<EventMonitor> eventMonitors;

    public NotifyRecordReadDTO getOne(AlertRecordJoinDTO dto) {
        AlertRecord queryAlertRecord = new AlertRecord();
        queryAlertRecord.setId(dto.getRecordId());
        queryAlertRecord.setAppType(dto.getAppType());
        queryAlertRecord.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        List<AlertRecord> alertRecords = alertRecordMapper.selectQuery(queryAlertRecord);

        if (CollectionUtils.isNotEmpty(alertRecords)) {
            AlertRecord alertRecord = alertRecords.get(0);
            return build(alertRecord);
        }

        return null;
    }

    private NotifyRecordReadDTO build(AlertRecord alertRecord) {
        NotifyRecordReadDTO notifyRecordReadDTO = new NotifyRecordReadDTO();
        Long alertContentId = alertRecord.getAlertContentId();
        AlertContent contentById = alertContentService.findContentById(alertContentId);
        notifyRecordReadDTO.setContent(contentById.getContent());
        notifyRecordReadDTO.setProjectId(contentById.getProjectId());
        notifyRecordReadDTO.setContentId(alertRecord.getAlertContentId());
        notifyRecordReadDTO.setStatus(alertRecord.getStatus());
        notifyRecordReadDTO.setGmtCreateFormat(DateUtil.getDate(alertRecord.getGmtCreate(),DateUtil.STANDARD_DATETIME_FORMAT));
        notifyRecordReadDTO.setAppType(alertRecord.getAppType());
        notifyRecordReadDTO.setUserId(alertRecord.getUserId());
        notifyRecordReadDTO.setReadStatus(alertRecord.getReadStatus());
        notifyRecordReadDTO.setTenantId(alertRecord.getTenantId());
        notifyRecordReadDTO.setId(alertRecord.getId());
        notifyRecordReadDTO.setGmtCreate(alertRecord.getGmtCreate());
        notifyRecordReadDTO.setGmtModified(alertRecord.getGmtModified());
        notifyRecordReadDTO.setNotifyRecordId(alertRecord.getReadId());
        return notifyRecordReadDTO;
    }

    public PageResult<List<NotifyRecordReadDTO>> pageQuery(AlertRecordJoinDTO alertRecordJoinDTO) {
        AlertRecord alertRecord = new AlertRecord();
        alertRecord.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertRecord.setTenantId(alertRecordJoinDTO.getTenantId());
        alertRecord.setUserId( alertRecordJoinDTO.getUserId());
        alertRecord.setAppType(alertRecordJoinDTO.getAppType());
        alertRecord.setReadStatus(alertRecordJoinDTO.getReadStatus());
        Page<AlertRecord> pageData = PageHelper.startPage(alertRecordJoinDTO.getCurrentPage(), alertRecordJoinDTO.getPageSize())
                .doSelectPage(() -> alertRecordMapper.selectQuery(alertRecord));

        List<AlertRecord> result = pageData.getResult();
        List<NotifyRecordReadDTO> notifyRecordReadDTOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(result)) {
            result.forEach(record -> notifyRecordReadDTOS.add(build(record)));
        }

        return new PageResult<>( pageData.getPageNum(), pageData.getPageSize(),
                (int) pageData.getTotal(),  pageData.getPages(), notifyRecordReadDTOS);
    }

    public void tabRead(AlertRecordJoinDTO alertRecordJoinDTO) {
        AlertRecord alertRecord = new AlertRecord();
        alertRecord.setReadStatus(ReadStatus.READ.getStatus());
        Map<String,Object> params = Maps.newHashMap();
        params.put("is_deleted", IsDeletedEnum.NOT_DELETE.getType());
        params.put("tenant_id", alertRecordJoinDTO.getTenantId());
        params.put("user_id", alertRecordJoinDTO.getUserId());
        params.put("app_type", alertRecordJoinDTO.getAppType());
        alertRecordMapper.updateByMapAndIds(alertRecord,params, alertRecordJoinDTO.getRecordIds());
    }

    public void allRead(AlertRecordJoinDTO alertRecordJoinDTO) {
        alertRecordJoinDTO.setRecordIds(null);
        tabRead(alertRecordJoinDTO);
    }

    public void delete(AlertRecordJoinDTO alertRecordJoinDTO) {
        AlertRecord alertRecord = new AlertRecord();
        alertRecord.setIsDeleted(IsDeletedEnum.DELETE.getType());
        Map<String,Object> params = Maps.newHashMap();
        params.put("tenant_id", alertRecordJoinDTO.getTenantId());
        params.put("user_id", alertRecordJoinDTO.getUserId());
        params.put("app_type", alertRecordJoinDTO.getAppType());
        alertRecordMapper.updateByMapAndIds(alertRecord,params, alertRecordJoinDTO.getRecordIds());
    }

    public void sendAlarm(AlarmSendDTO alarmSendDTO) {
        try {
            List<AlertChannel> alertChannels = alertChannelService.selectAlertByIds(alarmSendDTO.getAlertGateSources());

            if (CollectionUtils.isEmpty(alertChannels)) {
                throw new RdosDefineException("发送告警必须设置通道");
            }

            String content = alarmSendDTO.getContent();
            Long contentId = alarmSendDTO.getContentId();
            AlertContent alertContent = alertContentService.findContentById(contentId);

            if (StringUtils.isBlank(content)) {
                if (alertContent == null) {
                    throw new RdosDefineException("发送告警必须设置告警内容");
                }
                content = alertContent.getContent();
            }

            if (alarmSendDTO.getStatus() == null) {
                alarmSendDTO.setStatus(alertContent.getStatus());
            }

            Map<Long,AlertChannel> alertChannelMap = Maps.newHashMap();
            Map<Long,UserMessageDTO> receiversMap = Maps.newHashMap();

            // 生成告警记录
            List<AlertRecord> records = buildRecord(alarmSendDTO, alertChannels,alertChannelMap,receiversMap);

            // 按照告警记录发送告警
            for (AlertRecord record : records) {
                sendAlter(alarmSendDTO, content, alertChannelMap, receiversMap, record);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getErrorMessage(e));
            throw new RdosDefineException(e.getMessage());
        } finally {
            if (alarmSendDTO.getContentId()!=null) {
                alertContentService.updateContent(alarmSendDTO.getContentId(),alarmSendDTO, AlertMessageStatusEnum.ALTER.getType());
            }
        }
    }

    private void sendAlter(AlarmSendDTO alarmSendDTO, String content, Map<Long, AlertChannel> alertChannelMap, Map<Long, UserMessageDTO> receiversMap, AlertRecord record) throws Exception {
        try {
            AlterContext alterContext = new AlterContext();
            AlertChannel alertChannel = alertChannelMap.get(record.getAlertChannelId());
            UserMessageDTO userMessageDTO = receiversMap.get(record.getUserId());

            if (alertChannel == null) {
                throw new RdosDefineException("未查询到通道信息");
            }

            AlertGateCode alertGateCode = AlertGateCode.parse(alertChannel.getAlertGateCode());
            alterContext.setJarPath(alertChannel.getFilePath());
            alterContext.setAlertGateJson(StringUtils.isNotBlank(alertChannel.getAlertGateJson()) ? alertChannel.getAlertGateJson() : "");
            alterContext.setId(record.getId());
            alterContext.setTitle(record.getTitle());
            alterContext.setContent(content);
            alterContext.setAlertGateCode(alertGateCode);
            alterContext.setDing(alarmSendDTO.getWebhook());

            if (userMessageDTO != null) {
                alterContext.setUserName(userMessageDTO.getUsername());
                alterContext.setEmails(Lists.newArrayList(userMessageDTO.getEmail()));
                alterContext.setPhone(userMessageDTO.getTelephone());
            }

            Map<String,Object> extendedPara = Maps.newHashMap();
            extendedPara.put(StatusUpdateEvent.RECORD_PATH,record);
            alterContext.setExtendedParam(extendedPara);
            alterSender.sendAsyncAAlter(alterContext,eventMonitors);
        } catch (Exception e) {
            log.error(ExceptionUtil.getErrorMessage(e));
            AlertRecord alertRecord = new AlertRecord();
            alertRecord.setAlertRecordSendStatus(AlertSendStatusEnum.SEND_FAILURE.getType());
            alertRecord.setFailureReason(ExceptionUtil.getErrorMessage(e));

            Map<String,Object> param = Maps.newHashMap();
            param.put("id",record.getId());
            param.put("is_deleted",IsDeletedEnum.NOT_DELETE.getType());
            alertRecordMapper.updateByMap(alertRecord,param);
        }
    }

    private List<AlertRecord> buildRecord(AlarmSendDTO alarmSendDTO, List<AlertChannel> alertChannels, Map<Long, AlertChannel> alertChannelMap,Map<Long,UserMessageDTO> receiversMap) {
        List<AlertRecord> alertRecords = Lists.newArrayList();
        for (AlertChannel alertChannel : alertChannels) {
            alertChannelMap.put(alertChannel.getId(), alertChannel);
            if (AlertGateTypeEnum.DINGDING.getType().equals(alertChannel.getAlertGateType())) {
                // 钉钉通道 生成一天数据
                alertRecords.add(buildRecord(alarmSendDTO, alertChannel, -1L));
                continue;
            }

            List<UserMessageDTO> receivers = alarmSendDTO.getReceivers();

            if (CollectionUtils.isEmpty(receivers)) {
                receivers = Lists.newArrayList();

                if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(alertChannel.getAlertGateType())) {
                    // 自定义通道支持不选择接收人
                    alertRecords.add(buildRecord(alarmSendDTO, alertChannel, -1L));
                    continue;
                }
            }

            for (UserMessageDTO receiver : receivers) {
                receiversMap.put(receiver.getUserId(),receiver);
                alertRecords.add(buildRecord(alarmSendDTO, alertChannel, receiver.getUserId()));
            }
        }

        if (CollectionUtils.isNotEmpty(alertRecords)) {
            alertRecordMapper.insert(alertRecords);
        }
        return alertRecords;
    }

    private AlertRecord buildRecord(AlarmSendDTO alarmSendDTO, AlertChannel alertChannel,Long userId) {
        AlertRecord alertRecord = new AlertRecord();
        alertRecord.setAlertChannelId(alertChannel.getId());
        alertRecord.setAlertGateType(alertChannel.getAlertGateType());
        alertRecord.setAlertContentId(alarmSendDTO.getContentId());
        alertRecord.setTenantId(alarmSendDTO.getTenantId());
        alertRecord.setAppType(alarmSendDTO.getAppType());
        alertRecord.setUserId(userId);
        alertRecord.setReadStatus(ReadStatus.UNREAD.getStatus());
        alertRecord.setTitle(StringUtils.isNotBlank(alarmSendDTO.getTitle())?alarmSendDTO.getTitle():"");
        alertRecord.setStatus(alarmSendDTO.getStatus() == null ? 0 : alarmSendDTO.getStatus());
        alertRecord.setJobId(StringUtils.isNotBlank(alarmSendDTO.getJobId())?alarmSendDTO.getJobId():"");
        alertRecord.setAlertRecordStatus(AlertRecordStatusEnum.NO_WARNING.getType());
        alertRecord.setAlertRecordSendStatus(AlertSendStatusEnum.NO_SEND.getType());
        alertRecord.setFailureReason("");
        alertRecord.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        alertRecord.setNodeAddress(environmentContext.getLocalAddress());
        alertRecord.setSendEndTime("");
        alertRecord.setSendTime("");
        alertRecord.setContext("");
        alertRecord.setReadId(alarmSendDTO.getReadId());
        return alertRecord;
    }

    public Long findMinIdByStatus(AlertRecordStatusEnum recordStatusEnum, String nodeAddress, Long startDate, Long endDate) {
        return alertRecordMapper.findMinIdByStatus(recordStatusEnum.getType(),nodeAddress,startDate,endDate);
    }

    public List<AlertRecord> findListByStatus(List<Integer> recordStatus, String nodeAddress, Long startDate, Long endDate, Long minId,Integer alertRecordSendStatus) {
        return alertRecordMapper.findListByStatus(recordStatus,nodeAddress,startDate,endDate,minId,alertRecordSendStatus);
    }

    public void updateByMap(AlertRecord update, Map<String, Object> param) {
        alertRecordMapper.updateByMap(update,param);
    }


    public void updateByMapAndIds(AlertRecord alertRecord, Map<String, Object> params, List<Long> ids) {
        alertRecordMapper.updateByMapAndIds(alertRecord,params,ids);
    }
}
