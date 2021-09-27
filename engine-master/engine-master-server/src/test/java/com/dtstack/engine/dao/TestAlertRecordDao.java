package com.dtstack.engine.dao;

import com.dtstack.engine.domain.AlertRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: dazhi
 * @Date: 2021/1/28 6:03 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface TestAlertRecordDao {


    @Insert({"insert into alert_record (\n" +
            "        `alert_channel_id`,\n" +
            "        `alert_gate_type`,\n" +
            "        `alert_content_id`,\n" +
            "        `tenant_id`,\n" +
            "        `app_type`,\n" +
            "        `user_id`,\n" +
            "        `read_status`,\n" +
            "        `title`,\n" +
            "        `status`,\n" +
            "        `job_id`,\n" +
            "        `alert_record_status`,\n" +
            "        `alert_record_send_status`,\n" +
            "        `failure_reason`,\n" +
            "        `is_deleted`,\n" +
            "        `node_address`,\n" +
            "        `send_time`,\n" +
            "        `send_end_time`,\n" +
            "        `gmt_create`,\n" +
            "        `gmt_modified`,\n" +
            "        `context`\n" +
            "        )\n" +
            "        values (\n" +
            "            #{record.alertChannelId},\n" +
            "            #{record.alertGateType},\n" +
            "            #{record.alertContentId},\n" +
            "            #{record.tenantId},\n" +
            "            #{record.appType},\n" +
            "            #{record.userId},\n" +
            "            #{record.readStatus},\n" +
            "            #{record.title},\n" +
            "            #{record.status},\n" +
            "            #{record.jobId},\n" +
            "            #{record.alertRecordStatus},\n" +
            "            #{record.alertRecordSendStatus},\n" +
            "            #{record.failureReason},\n" +
            "            #{record.isDeleted},\n" +
            "            #{record.nodeAddress},\n" +
            "            #{record.sendTime},\n" +
            "            #{record.sendEndTime},\n" +
            "            #{record.gmtCreate},\n" +
            "            #{record.gmtModified},\n" +
            "            #{record.context}\n" +
            "            )"})
    @Options()
    Integer insert(@Param("record") AlertRecord alertRecord);
}
