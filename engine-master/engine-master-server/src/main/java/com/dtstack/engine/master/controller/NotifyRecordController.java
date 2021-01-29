package com.dtstack.engine.master.controller;

import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.api.dto.*;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.AlarmSendParam;
import com.dtstack.engine.api.param.NotifyRecordPageQueryParam;
import com.dtstack.engine.api.param.NotifyRecordParam;
import com.dtstack.engine.api.param.SetAlarmNotifyRecordParam;
import com.dtstack.engine.common.enums.NotifyMode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.master.enums.ReadStatus;
import com.dtstack.engine.master.impl.AlertChannelService;
import com.dtstack.engine.master.impl.AlertContentService;
import com.dtstack.engine.master.impl.AlertRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 9:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Api(tags = "告警/通知")
@RestController
@RequestMapping("/node/notifyRecord")
public class NotifyRecordController {

    private final Logger log = LoggerFactory.getLogger(NotifyRecordController.class);

    @Autowired
    private AlertRecordService alertRecordService;

    @Autowired
    private AlertContentService alertContentService;

    @ApiOperation("获取一条指定通知记录 用于替换console: /api/console/service/notifyRecord/getOne")
    @PostMapping("/getOne")
    public NotifyRecordReadDTO getOne(@RequestBody NotifyRecordParam param) {
        AlertRecordJoinDTO alertRecordJoinDTO = new AlertRecordJoinDTO();
        alertRecordJoinDTO.setRecordId(param.getReadId());
        alertRecordJoinDTO.setAppType(param.getAppType());
        return alertRecordService.getOne(alertRecordJoinDTO);
    }

    @ApiOperation("分页查询消息列表 mode,1：普通查询，2：未读消息，3：已读消息 用于替换console: /api/console/service/notifyRecord/pageQuery")
    @PostMapping("/pageQuery")
    public PageResult<List<NotifyRecordReadDTO>> pageQuery(@RequestBody NotifyRecordPageQueryParam param) {
        AlertRecordJoinDTO alertRecordJoinDTO = new AlertRecordJoinDTO();
        alertRecordJoinDTO.setTenantId(param.getTenantId());
        alertRecordJoinDTO.setAppType(param.getAppType());
        alertRecordJoinDTO.setProjectId(param.getProjectId());
        alertRecordJoinDTO.setUserId(param.getUserId());
        if (NotifyMode.UNREAD.getMode().equals(param.getMode())) {
            alertRecordJoinDTO.setReadStatus(ReadStatus.UNREAD.getStatus());
        } else if (NotifyMode.READ.getMode().equals(param.getMode())) {
            alertRecordJoinDTO.setReadStatus(ReadStatus.READ.getStatus());
        }
        alertRecordJoinDTO.setCurrentPage(param.getCurrentPage());
        alertRecordJoinDTO.setPageSize(param.getPageSize());
        return alertRecordService.pageQuery(alertRecordJoinDTO);

    }

    @ApiOperation("标为已读 用于替换console: /api/console/service/notifyRecord/tabRead")
    @PostMapping("/tabRead")
    public void tabRead(@RequestBody NotifyRecordParam param) {
        alertRecordService.tabRead(buildJoinDTO(param));
    }



    @ApiOperation("全部已读 用于替换console: /api/console/service/notifyRecord/allRead")
    @PostMapping("/allRead")
    public void allRead(@RequestBody NotifyRecordParam param) {
        alertRecordService.allRead(buildJoinDTO(param));
    }


    @ApiOperation("删除 用于替换console: /api/console/service/notifyRecord/delete")
    @PostMapping("/delete")
    public void delete(@RequestBody NotifyRecordParam param) {
        alertRecordService.delete(buildJoinDTO(param));
    }

    private AlertRecordJoinDTO buildJoinDTO(@RequestBody NotifyRecordParam param) {
        AlertRecordJoinDTO alertRecordJoinDTO = new AlertRecordJoinDTO();
        alertRecordJoinDTO.setTenantId(param.getTenantId());
        alertRecordJoinDTO.setAppType(param.getAppType());
        alertRecordJoinDTO.setProjectId(param.getProjectId());
        alertRecordJoinDTO.setUserId(param.getUserId());
        alertRecordJoinDTO.setRecordIds(param.getNotifyRecordIds());
        return alertRecordJoinDTO;
    }

    @ApiOperation("生成默认内容 用于替换console: /api/console/service/notifyRecord/generateContent")
    @PostMapping("/generateContent")
    public Long generateContent(@RequestBody NotifyRecordParam param) {
        AlertContentDTO alertContentDTO = new AlertContentDTO();
        alertContentDTO.setAppType(param.getAppType());
        alertContentDTO.setContent(param.getContent());
        alertContentDTO.setProjectId(param.getProjectId());
        alertContentDTO.setTenantId(param.getTenantId());
        alertContentDTO.setStatus(param.getStatus());
        return new Long(alertContentService.insertContent(alertContentDTO));
    }

    @ApiOperation("发送消息 用于替换console: /api/console/service/notifyRecord/sendAlarm")
    @PostMapping("/sendAlarm")
    public void sendAlarm(@RequestBody SetAlarmNotifyRecordParam param) {
        List<UserMessageDTO> userDTOS = new ArrayList<>();
        for (SetAlarmUserDTO receiver : param.getReceivers()) {
            try {
                userDTOS.add(PublicUtil.strToObject(PublicUtil.objectToStr(receiver), UserMessageDTO.class));
            } catch (Exception e){
                log.warn("SetAlarmUserDTO 转换成 UserDTO异常 ", e);
            }
        }

        AlarmSendDTO alarmSendDTO = new AlarmSendDTO();
        alarmSendDTO.setTitle(param.getTitle());
        alarmSendDTO.setContentId(param.getContentId());
        alarmSendDTO.setContent(param.getContent());
        alarmSendDTO.setReceivers(userDTOS);
        alarmSendDTO.setWebhook(param.getWebhook());

        alarmSendDTO.setStatus(param.getStatus());
        alarmSendDTO.setAppType(param.getAppType());
        alarmSendDTO.setTenantId(param.getTenantId());
        alarmSendDTO.setUserId(param.getUserId());
        alarmSendDTO.setProjectId(param.getProjectId());

        List<Integer> senderTypes = param.getSenderTypes();

        List<String> alertGateSources = AlertGateTypeEnum.transformSenderTypes(senderTypes);
        alarmSendDTO.setAlertGateSources(alertGateSources);
        alertRecordService.sendAlarm(alarmSendDTO);
    }

    @ApiOperation("发送消息: 新接口")
    @PostMapping("/sendAlarmNew")
    public void sendAlarmNew(@RequestBody AlarmSendParam param) {
        List<String> alertGateSources = param.getAlertGateSources();
        if (CollectionUtils.isEmpty(alertGateSources)) {
            throw  new RdosDefineException("发送告警必须设置通道");
        }

        AlarmSendDTO alarmSendDTO = new AlarmSendDTO();
        BeanUtils.copyProperties(param,alarmSendDTO);
        alertRecordService.sendAlarm(alarmSendDTO);
    }
}
