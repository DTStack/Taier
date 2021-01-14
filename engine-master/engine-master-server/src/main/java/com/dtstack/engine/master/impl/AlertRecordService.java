package com.dtstack.engine.master.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.engine.api.dto.AlarmSendDTO;
import com.dtstack.engine.api.dto.AlertRecordJoinDTO;
import com.dtstack.engine.api.dto.NotifyRecordReadDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.NotifyRecordParam;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.AlertRecordDao;
import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.domain.AlertContent;
import com.dtstack.engine.domain.AlertRecord;
import com.dtstack.engine.master.controller.NotifyRecordController;
import com.dtstack.engine.master.enums.AlertMessageStatusEnum;
import com.dtstack.engine.master.enums.ReadStatus;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.util.List;
import java.util.function.IntConsumer;

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

    public NotifyRecordReadDTO getOne(AlertRecordJoinDTO dto) {
        AlertRecord alertRecord = alertRecordMapper.selectOne(new QueryWrapper<AlertRecord>()
                .eq("id",dto.getRecordId())
                .eq("app_type",dto.getAppType())
                .eq("is_deleted", IsDeletedEnum.NOT_DELETE)
        );

        if (alertRecord != null) {
            return build(alertRecord);
        }

        return null;
    }

    private NotifyRecordReadDTO build(AlertRecord alertRecord) {
        NotifyRecordReadDTO notifyRecordReadDTO = new NotifyRecordReadDTO();
        notifyRecordReadDTO.setContent(alertRecord.getSendContent());
        notifyRecordReadDTO.setStatus(alertRecord.getStatus());
        notifyRecordReadDTO.setGmtCreateFormat(DateUtil.getDate(alertRecord.getGmtCreated(),DateUtil.STANDARD_DATETIME_FORMAT));
        notifyRecordReadDTO.setAppType(alertRecord.getAppType());
        notifyRecordReadDTO.setUserId(alertRecord.getUserId());
        notifyRecordReadDTO.setReadStatus(alertRecord.getReadStatus());
        notifyRecordReadDTO.setTenantId(alertRecord.getTenantId());
        notifyRecordReadDTO.setId(alertRecord.getId());
        notifyRecordReadDTO.setGmtCreate(alertRecord.getGmtCreated());
        notifyRecordReadDTO.setGmtModified(alertRecord.getGmtModified());
        notifyRecordReadDTO.setNotifyRecordId(alertRecord.getId());
        return notifyRecordReadDTO;
    }

    public PageResult<List<NotifyRecordReadDTO>> pageQuery(AlertRecordJoinDTO alertRecordJoinDTO) {
        Page<AlertRecord> page = new Page<>(alertRecordJoinDTO.getCurrentPage(),alertRecordJoinDTO.getPageSize());
        IPage<AlertRecord> alertRecordIPage = alertRecordMapper.selectPage(page,new QueryWrapper<AlertRecord>()
                .eq("is_deleted", IsDeletedEnum.NOT_DELETE)
                .eq("tenant_id", alertRecordJoinDTO.getTenantId())
                .eq("user_id",alertRecordJoinDTO.getUserId())
                .eq("app_type",alertRecordJoinDTO.getAppType())
                .eq(alertRecordJoinDTO.getReadStatus()!=null, "read_status",alertRecordJoinDTO.getReadStatus())
                .orderByDesc("id")
        );

        List<NotifyRecordReadDTO> notifyRecordReadDTOS = Lists.newArrayList();
        alertRecordIPage.getRecords().forEach(alertRecord -> notifyRecordReadDTOS.add(build(alertRecord)));
        return new PageResult<>((int)alertRecordIPage.getCurrent(),(int)alertRecordIPage.getPages(),
                (int)alertRecordIPage.getTotal(),(int)alertRecordIPage.getPages(),notifyRecordReadDTOS);
    }

    public void tabRead(AlertRecordJoinDTO alertRecordJoinDTO) {
        AlertRecord alertRecord = new AlertRecord();
        alertRecord.setReadStatus(ReadStatus.READ.getStatus());
        alertRecordMapper.update(alertRecord,new UpdateWrapper<AlertRecord>()
                .eq("is_deleted", IsDeletedEnum.NOT_DELETE)
                .eq("tenant_id", alertRecordJoinDTO.getTenantId())
                .eq("user_id", alertRecordJoinDTO.getUserId())
                .eq("app_type", alertRecordJoinDTO.getAppType())
                .in(CollectionUtils.isNotEmpty(alertRecordJoinDTO.getRecordIds()), "id", alertRecordJoinDTO.getRecordIds()));
    }

    public void allRead(AlertRecordJoinDTO alertRecordJoinDTO) {
        alertRecordJoinDTO.setRecordIds(null);
        tabRead(alertRecordJoinDTO);
    }

    public void delete(AlertRecordJoinDTO alertRecordJoinDTO) {
        AlertRecord alertRecord = new AlertRecord();
        alertRecord.setIsDeleted(IsDeletedEnum.DELETE.getType());
        alertRecordMapper.update(alertRecord,new UpdateWrapper<AlertRecord>()
                .eq("tenant_id", alertRecordJoinDTO.getTenantId())
                .eq("user_id", alertRecordJoinDTO.getUserId())
                .eq("app_type", alertRecordJoinDTO.getAppType())
                .in( "id", alertRecordJoinDTO.getRecordIds()));
    }

    public void sendAlarm(AlarmSendDTO alarmSendDTO) {
        try {
            List<AlertChannel> alertChannels = alertChannelService.selectAlertByIds(alarmSendDTO.getAlertGateSources());

            if (CollectionUtils.isEmpty(alertChannels)) {
                throw new RdosDefineException("发送告警必须设置通道");
            }

            String content = alarmSendDTO.getContent();
            if (StringUtils.isBlank(content)) {
                Long contentId = alarmSendDTO.getContentId();
                AlertContent alertContent = alertContentService.findContentById(contentId);
                content = alertContent.getContent();
            }

            // 生成告警记录


        } catch (Exception e) {
            log.error(ExceptionUtil.getErrorMessage(e));

            if (e instanceof RdosDefineException) {
                throw (RdosDefineException)e;
            }
        } finally {
            if (alarmSendDTO.getContentId()!=null) {
                alertContentService.updateContent(alarmSendDTO.getContentId(),alarmSendDTO, AlertMessageStatusEnum.ALTER.getType());
            }
        }







    }
}
