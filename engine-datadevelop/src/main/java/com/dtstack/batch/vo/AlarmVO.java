package com.dtstack.batch.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by jiangbo on 2017/5/19 0019.
 */
@Data
public class AlarmVO {
    private Long alarmId;
    private String alarmName;
    private Long taskId;
    private String taskName;
    // 告警发送方式
    private List<String> senderTypes;
    // 告警发送方式名称
    private List<String> senderTypeNames;
    private Integer taskType = 0;
    private List<Receiver> receiveUsers;
    private Integer myTrigger = 0;
    private Integer alarmStatus = 0;
    private Timestamp createTime;
    private String createUser;
    private Long createUserId;
    private String uncompleteTime;
    private String webhook;
    // 是否项目责任人
    private Integer isTaskHolder = 0;
    private Long tenantId = 0L;
    private Long projectId = 0L;
    private String sendTime;
    private List<Integer> receiveTypes;
    private List<Long> receivers;

    public Integer getIsTaskHolder() {
        return isTaskHolder;
    }

    public void setIsTaskHolder(Integer isTaskHolder) {
        this.isTaskHolder = isTaskHolder;
    }

    public static class Receiver {

        private Long userId;
        private String userName;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
