package com.dtstack.engine.master.server.executor;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.AlterSender;
import com.dtstack.engine.alert.EventMonitor;
import com.dtstack.engine.alert.enums.AlertRecordStatusEnum;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.api.domain.AlertRecord;
import com.dtstack.engine.master.enums.AlertSendStatusEnum;
import com.dtstack.engine.master.impl.AlertRecordService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2021/1/26 5:35 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterRecordExecutor implements InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(AlterRecordExecutor.class);

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private AlertRecordService alertRecordService;

    @Autowired
    private List<EventMonitor> eventMonitors;

    @Autowired
    private AlterSender alterSender;

    @Override
    public void afterPropertiesSet() throws Exception {
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory( "alter_AcquireJob"));
        scheduledService.scheduleWithFixedDelay(
                this::emitJob2Queue,
                0,
                env.getAcquireQueueJobInterval(),
                TimeUnit.MILLISECONDS);
    }

    private void emitJob2Queue() {
        try {
            String nodeAddress = env.getLocalAddress();

            if (StringUtils.isBlank(nodeAddress)) {
                return;
            }

            Long minId = alertRecordService.findMinIdByStatus(AlertRecordStatusEnum.TO_BE_SCANNED, nodeAddress, DateUtil.calTodayMills(), System.currentTimeMillis());

            if (minId != null) {
                List<AlertRecord> alertRecordList = alertRecordService.findListByStatus(Lists.newArrayList(AlertRecordStatusEnum.TO_BE_SCANNED.getType()), nodeAddress, DateUtil.calTodayMills(), System.currentTimeMillis(), minId,null);

                while (CollectionUtils.isNotEmpty(alertRecordList)) {
                    for (AlertRecord record : alertRecordList) {
                        try {
                            String context = record.getContext();
                            AlterContext alterContext = JSON.parseObject(context, AlterContext.class);
                            Map<String,Object> param = Maps.newHashMap();
                            param.put("id", record.getId());
                            param.put("is_deleted", IsDeletedEnum.NOT_DELETE.getType());
                            param.put("alert_record_status", AlertRecordStatusEnum.TO_BE_SCANNED.getType());

                            AlertRecord update = new AlertRecord();
                            if (alterContext == null) {
                                // 直接设置成失败
                                update.setAlertRecordStatus(AlertRecordStatusEnum.ALERT_SUCCESS.getType());
                                update.setAlertRecordSendStatus(AlertSendStatusEnum.SEND_FAILURE.getType());
                                alertRecordService.updateByMap(update, param);
                            } else {
                                update.setAlertRecordStatus(AlertRecordStatusEnum.NO_WARNING.getType());
                                alertRecordService.updateByMap(update, param);
                                alterSender.sendAsyncAAlter(alterContext,eventMonitors);
                            }

                            minId = record.getId();
                        } catch (Exception e) {
                            LOGGER.error(ExceptionUtil.getErrorMessage(e));
                        }
                    }

                    alertRecordList = alertRecordService.findListByStatus(Lists.newArrayList(AlertRecordStatusEnum.TO_BE_SCANNED.getType()), nodeAddress, DateUtil.calTodayMills(), System.currentTimeMillis(), minId, null);
                }
            }

        } catch (Exception e) {
            LOGGER.error(ExceptionUtil.getErrorMessage(e));
        }
    }

}
