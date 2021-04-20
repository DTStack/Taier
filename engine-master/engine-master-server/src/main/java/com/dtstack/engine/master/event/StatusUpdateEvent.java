package com.dtstack.engine.master.event;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.alert.AdapterEventMonitor;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.enums.AlertRecordStatusEnum;
import com.dtstack.engine.alert.exception.AlterEventInterruptException;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.dao.AlertRecordDao;
import com.dtstack.engine.api.domain.AlertRecord;
import com.dtstack.engine.master.enums.AlertSendStatusEnum;
import com.dtstack.lang.data.R;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/20 2:09 下午
 * @Email:dazhi@dtstack.com
 * @Description: 复制记录状态修改
 */
@Component
public class StatusUpdateEvent extends AdapterEventMonitor implements Ordered {

    public final static String RECORD_PATH = "alertRecord";

    @Autowired
    private AlertRecordDao alertRecordDao;

    @Override
    public Boolean startEvent(AlterContext alterContext) {
        updateStatus(alterContext,(record,context)->{
            // 拒绝进入队列，更新状态成扫描中
            AlertRecord update = new AlertRecord();
            update.setContext(JSON.toJSONString(alterContext));
            Map<String,Object> param = Maps.newHashMap();
            param.put("id",record.getId());
            param.put("is_deleted",IsDeletedEnum.NOT_DELETE.getType());
            param.put("alert_record_status",AlertRecordStatusEnum.NO_WARNING.getType());
            return alertRecordDao.updateByMap(update,param);
        });
        return super.startEvent(alterContext);
    }

    @Override
    public void refuseEvent(AlterContext alterContext) {
        updateStatus(alterContext,(record,context)->{
            // 拒绝进入队列，更新状态成扫描中
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.TO_BE_SCANNED.getType());
            Map<String,Object> param = Maps.newHashMap();
            param.put("id",record.getId());
            param.put("is_deleted",IsDeletedEnum.NOT_DELETE.getType());
            param.put("alert_record_status",AlertRecordStatusEnum.NO_WARNING.getType());
            return alertRecordDao.updateByMap(update,param);
        });
    }

    @Override
    public void joiningQueueEvent(AlterContext alterContext) {
        updateStatus(alterContext, (record, context) -> {
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.ALARM_QUEUE.getType());
            Map<String,Object> param = Maps.newHashMap();
            param.put("id", record.getId());
            param.put("is_deleted", IsDeletedEnum.NOT_DELETE.getType());
            param.put("alert_record_status", AlertRecordStatusEnum.NO_WARNING.getType());
            return alertRecordDao.updateByMap(update, param);
        });
    }

    @Override
    public void leaveQueueAndSenderBeforeEvent(AlterContext alterContext) {
        updateStatus(alterContext, (record, context) -> {
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.SENDING_ALARM.getType());
            update.setSendTime(DateTime.now().toString("yyyyMMddHHmmss"));
            update.setContext(JSON.toJSONString(alterContext));
            Map<String,Object> param = Maps.newHashMap();
            param.put("id", record.getId());
            param.put("is_deleted", IsDeletedEnum.NOT_DELETE.getType());
            param.put("alert_record_status", AlertRecordStatusEnum.ALARM_QUEUE.getType());
            return alertRecordDao.updateByMap(update, param);
        });
    }

    @Override
    public void alterSuccess(AlterContext alterContext, R r) {
        updateStatus(alterContext, (record, context) -> {
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.ALERT_SUCCESS.getType());
            if (r.isSuccess()) {
                update.setAlertRecordSendStatus(AlertSendStatusEnum.SEND_SUCCESS.getType());
            } else {
                update.setAlertRecordSendStatus(AlertSendStatusEnum.SEND_FAILURE.getType());
                update.setFailureReason(r.getMessage());
            }
            update.setSendEndTime(DateTime.now().toString("yyyyMMddHHmmss"));
            Map<String,Object> param = Maps.newHashMap();
            param.put("id", record.getId());
            param.put("is_deleted", IsDeletedEnum.NOT_DELETE.getType());
            param.put("alert_record_status", AlertRecordStatusEnum.SENDING_ALARM.getType());
            return alertRecordDao.updateByMap(update, param);
        });
    }

    @Override
    public void alterFailure(AlterContext alterContext, R r, Exception e) {
        updateStatus(alterContext, (record, context) -> {
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.ALERT_SUCCESS.getType());
            update.setAlertRecordSendStatus(AlertSendStatusEnum.SEND_FAILURE.getType());
            update.setFailureReason("reason ："+(r!=null?r.getMessage():"")+"-- exception :"+ ExceptionUtil.getErrorMessage(e));
            update.setSendEndTime(DateTime.now().toString("yyyyMMddHHmmss"));
            Map<String,Object> param = Maps.newHashMap();
            param.put("id", record.getId());
            param.put("is_deleted", IsDeletedEnum.NOT_DELETE.getType());
            param.put("alert_record_status", AlertRecordStatusEnum.SENDING_ALARM.getType());

            return alertRecordDao.updateByMap(update, param);
        });
    }

    private void updateStatus(AlterContext alterContext, Callback callback) {
        Map<String, Object> extendedPara = alterContext.getExtendedParam();

        Object param = extendedPara.get(RECORD_PATH);

        if (param instanceof AlertRecord) {
            AlertRecord alertRecord = (AlertRecord)param;
            Long id = alertRecord.getId();
            if (id != null) {
                Integer update = callback.callback(alertRecord, alterContext);

                if (update <= 0) {
                    // 抛出中断异常
                    throw new AlterEventInterruptException("alarm record:"+alterContext.getMark()+" update status failed");
                }

            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private interface Callback {
        Integer callback(AlertRecord alertRecord,AlterContext alterContext);
    }
}
