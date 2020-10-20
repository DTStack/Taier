package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.dto.NotifyRecordReadDTO;
import com.dtstack.engine.api.dto.SetAlarmUserDTO;
import com.dtstack.engine.api.dto.UserMessageDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.NotifyRecordPageQueryParam;
import com.dtstack.engine.api.param.NotifyRecordParam;
import com.dtstack.engine.api.param.SetAlarmNotifyRecordParam;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.impl.NotifyRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private NotifyRecordService notifyRecordService;

    @ApiOperation("获取一条指定通知记录 用于替换console: /api/console/service/notifyRecord/getOne")
    @PostMapping("/getOne")
    public NotifyRecordReadDTO getOne(@RequestBody NotifyRecordParam param) {
        return notifyRecordService.getOne(param.getTenantId(),param.getProjectId(),param.getUserId(),param.getReadId(),param.getAppType());
    }

    @ApiOperation("分页查询消息列表 mode,1：普通查询，2：未读消息，3：已读消息 用于替换console: /api/console/service/notifyRecord/pageQuery")
    @PostMapping("/pageQuery")
    public PageResult<List<NotifyRecordReadDTO>> pageQuery(@RequestBody NotifyRecordPageQueryParam param) {
        return notifyRecordService.pageQuery(param.getTenantId(), param.getProjectId(),
                param.getCurrentPage(), param.getPageSize(), param.getUserId(), param.getMode(), param.getAppType());

    }

    @ApiOperation("标为已读 用于替换console: /api/console/service/notifyRecord/tabRead")
    @PostMapping("/tabRead")
    public void tabRead(@RequestBody NotifyRecordParam param) {
        notifyRecordService.tabRead(param.getNotifyRecordIds(), param.getUserId(), param.getTenantId(), param.getProjectId(), param.getAppType());
    }

    @ApiOperation("全部已读 用于替换console: /api/console/service/notifyRecord/allRead")
    @PostMapping("/allRead")
    public void allRead(@RequestBody NotifyRecordParam param) {
        notifyRecordService.allRead(param.getUserId(), param.getTenantId(), param.getProjectId(), param.getAppType());
    }


    @ApiOperation("删除 用于替换console: /api/console/service/notifyRecord/delete")
    @PostMapping("/delete")
    public void delete(@RequestBody NotifyRecordParam param) {
        notifyRecordService.delete(param.getNotifyRecordIds(), param.getUserId(), param.getTenantId(), param.getProjectId(), param.getAppType());
    }

    @ApiOperation("生成默认内容 用于替换console: /api/console/service/notifyRecord/generateContent")
    @PostMapping("/generateContent")
    public Long generateContent(@RequestBody NotifyRecordParam param) {
        return notifyRecordService.generateContent(param.getTenantId(), param.getProjectId(), param.getAppType(), param.getContent(), param.getStatus());
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

        notifyRecordService.sendAlarm(param.getTenantId(), param.getProjectId(), param.getNotifyRecordId(), param.getAppType(), param.getTitle(),
                param.getContentId(), userDTOS, param.getSenderTypes(), param.getWebhook(), param.getMailType());
    }


}
