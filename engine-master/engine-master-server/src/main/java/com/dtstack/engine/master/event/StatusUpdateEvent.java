package com.dtstack.engine.master.event;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dtstack.engine.alert.AdapterEventMonitor;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.enums.AlertRecordStatusEnum;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.dao.AlertRecordDao;
import com.dtstack.engine.domain.AlertRecord;
import com.dtstack.engine.master.enums.AlertSendStatusEnum;
import com.dtstack.lang.data.R;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
    public void refuseEvent(AlterContext alterContext) {
        updateStatus(alterContext,(record,context)->{
            // 拒绝进入队列，更新状态成扫描中
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.TO_BE_SCANNED.getType());
            alertRecordDao.update(update,new UpdateWrapper<AlertRecord>()
                    .eq("id",record.getId())
                    .eq("is_deleted",IsDeletedEnum.NOT_DELETE.getType())
                    .eq("alert_record_status",AlertRecordStatusEnum.NO_WARNING.getType())
            );
        });
    }

    @Override
    public void joiningQueueEvent(AlterContext alterContext) {
        updateStatus(alterContext, (record, context) -> {
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.ALARM_QUEUE.getType());
            alertRecordDao.update(update, new UpdateWrapper<AlertRecord>()
                    .eq("id", record.getId())
                    .eq("is_deleted", IsDeletedEnum.NOT_DELETE.getType())
                    .eq("alert_record_status", AlertRecordStatusEnum.NO_WARNING.getType())
            );
        });
    }

    @Override
    public void leaveQueueAndSenderBeforeEvent(AlterContext alterContext) {
        updateStatus(alterContext, (record, context) -> {
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.SENDING_ALARM.getType());
            update.setSendContent(context.getContent());
            update.setSendTime(DateTime.now().toString("yyyyMMddHHmmss"));
            alertRecordDao.update(update, new UpdateWrapper<AlertRecord>()
                    .eq("id", record.getId())
                    .eq("is_deleted", IsDeletedEnum.NOT_DELETE.getType())
                    .eq("alert_record_status", AlertRecordStatusEnum.ALARM_QUEUE.getType())
            );
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
            alertRecordDao.update(update, new UpdateWrapper<AlertRecord>()
                    .eq("id", record.getId())
                    .eq("is_deleted", IsDeletedEnum.NOT_DELETE.getType())
                    .eq("alert_record_status", AlertRecordStatusEnum.SENDING_ALARM.getType())
            );
        });
    }

    @Override
    public void alterFailure(AlterContext alterContext, R r, Exception e) {
        updateStatus(alterContext, (record, context) -> {
            AlertRecord update = new AlertRecord();
            update.setAlertRecordStatus(AlertRecordStatusEnum.ALERT_SUCCESS.getType());
            update.setAlertRecordSendStatus(AlertSendStatusEnum.SEND_FAILURE.getType());
            update.setFailureReason("原因："+r.getMessage()+"--异常:"+ ExceptionUtil.getErrorMessage(e));
            update.setSendEndTime(DateTime.now().toString("yyyyMMddHHmmss"));
            alertRecordDao.update(update, new UpdateWrapper<AlertRecord>()
                    .eq("id", record.getId())
                    .eq("is_deleted", IsDeletedEnum.NOT_DELETE.getType())
                    .eq("alert_record_status", AlertRecordStatusEnum.SENDING_ALARM.getType())
            );
        });
    }

    private void updateStatus(AlterContext alterContext, Callback callback) {
        Map<String, Object> extendedPara = alterContext.getExtendedPara();

        Object param = extendedPara.get(RECORD_PATH);

        if (param instanceof AlertRecord) {
            AlertRecord alertRecord = (AlertRecord)param;
            Long id = alertRecord.getId();
            if (id != null) {
                callback.callback(alertRecord,alterContext);
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private interface Callback {
        void callback(AlertRecord alertRecord,AlterContext alterContext);
    }
}
