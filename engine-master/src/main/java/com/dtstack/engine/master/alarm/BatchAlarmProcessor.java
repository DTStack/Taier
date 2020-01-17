package com.dtstack.engine.master.alarm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.engine.dao.BatchAlarmDao;
import com.dtstack.engine.dao.BatchAlarmRecordDao;
import com.dtstack.engine.dao.BatchAlarmRecordUserDao;
import com.dtstack.engine.dao.BatchJobAlarmDao;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.dao.BatchTaskShadeDao;
import com.dtstack.engine.dao.NotifyDao;
import com.dtstack.engine.dao.NotifyRecordDao;
import com.dtstack.engine.dao.NotifyUserDao;
import com.dtstack.engine.domain.BatchAlarm;
import com.dtstack.engine.domain.BatchAlarmRecord;
import com.dtstack.engine.domain.BatchAlarmRecordUser;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.domain.BatchJobAlarm;
import com.dtstack.engine.domain.BatchTaskShade;
import com.dtstack.engine.domain.Notify;
import com.dtstack.engine.domain.NotifyRecord;
import com.dtstack.sdk.console.client.ConsoleNotifyApiClient;
import com.dtstack.sdk.console.domain.SetAlarmUserDTO;
import com.dtstack.sdk.console.domain.parameter.NotifyRecordParam;
import com.dtstack.sdk.console.domain.parameter.SetAlarmNotifyRecordParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dto.UserDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/30
 */
@Component
public class BatchAlarmProcessor implements Runnable, InitializingBean, DisposableBean {

    private final static Logger logger = LoggerFactory.getLogger(BatchAlarmProcessor.class);

    @Autowired
    private BatchAlarmDao batchAlarmDao;

    @Autowired
    private BatchJobDao batchJobDao;

    @Autowired
    private BatchJobAlarmDao batchJobAlarmDao;

    @Autowired
    private BatchAlarmRecordDao batchAlarmRecordDao;

    @Autowired
    private BatchAlarmRecordUserDao batchAlarmRecordUserDao;

    @Autowired
    private BatchTaskShadeDao batchTaskShadeDao;

    @Autowired
    private NotifyDao notifyDao;

    @Autowired
    private NotifyRecordDao notifyRecordDao;

    @Autowired
    private NotifyUserDao notifyUserDao;

    @Autowired
    private ConsoleNotifyApiClient consoleNotifyApiClient;

    @Autowired
    private EnvironmentContext environmentContext;

    private static final String TASK_FAIL_ALARM_MESSAGE_TEMPLATE = "[%s]离线计算任务：%s失败告警\n" +
            "离线任务：%s\n" +
            "调度类型：周期调度\n" +
            "告警类型：%s\n" +
            "计划时间：%s\n" +
            "开始时间：%s\n" +
            "当前任务状态：%s\n" +
            "责任人：%s";

    private static final String TASK_TIMING_ALARM_MESSAGE_TEMPLATE = "[%s]离线计算任务：%s超时告警\n" +
            "离线任务：%s\n" +
            "调度类型：周期调度\n" +
            "告警类型：%s\n" +
            "计划时间：%s\n" +
            "开始时间：%s\n" +
            "当前任务状态：%s\n" +
            "责任人：%s";

    private static final String TITLE = "批处理任务监控告警";

    private static final SimpleDateFormat day_sdf = new SimpleDateFormat("yyyyMMdd");

    private ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("BatchAlarmProcessor"));

    @Override
    public void run() {
        logger.info("BatchAlarmJob process start...");
        this.processAlarm();
        logger.info("BatchAlarmJob process end...");
    }

    /**
     * 注意: 判断运行超时告警是否已经发送过了
     * 是根据rdos_batch_job_alarm 的 modifytime是否和createTime不同判断的.
     * 所以修改modifytime的时候需要注意
     */
    private void processAlarm() {

        List<BatchAlarm> alarmList = batchAlarmDao.getAllNeedMonitorAlarm(null);//获取所有定义的告警
        Calendar currTime = Calendar.getInstance();
        Integer currDayVal = Integer.valueOf(day_sdf.format(currTime.getTime()));
        Integer currHourMinVal = currTime.get(Calendar.HOUR_OF_DAY) * 100 + currTime.get(Calendar.MINUTE);
        Set<BatchJobAlarm> timeLimitAlarmUpdateList = Sets.newHashSet();
        Timestamp timeNow = Timestamp.valueOf(LocalDateTime.now());

        for (BatchAlarm batchAlarm : alarmList) {

            //查询当前batch_task 状态
            BatchTaskShade batchTask = batchTaskShadeDao.getOne(batchAlarm.getTaskId(), batchAlarm.getAppType());
            if (batchTask == null) {//task已经被删除
                logger.error("can not find batchTask by task id:{}!!!", batchAlarm.getId());
                continue;
            }

            Map<Long, UserDTO> userMaps = parseReceivers(batchAlarm.getReceivers());
            if(Objects.isNull(userMaps)){
                continue;
            }

            List<BatchJobAlarm> batchJobAlarmList = batchJobAlarmDao.getByTaskId(batchTask.getTaskId(), batchAlarm.getAppType());
            for (BatchJobAlarm batchJobAlarm : batchJobAlarmList) {
                try {
                    BatchJob batchJob = batchJobDao.getOne(batchJobAlarm.getJobId());
                    if (batchJob == null) {
                        logger.error("can not find batchJob by job_id:{}!!!", batchJobAlarm.getJobId());
                        continue;
                    }

                    //补数据/重跑不触发告警
                    if (batchJob.getType() == EScheduleType.FILL_DATA.getType()
                            || batchJob.getIsRestart() == Restarted.RESTARTED.getStatus()) {
                        continue;
                    }

                    Integer currStatus = batchJob.getStatus();

                    boolean flag = false;
                    String alarmContent = null;
                    if (currStatus != null) {
                        batchJobAlarm.setTaskStatus(currStatus);
                        batchJobAlarmDao.update(batchJobAlarm);
                    }
                    int myTrigger = batchAlarm.getMyTrigger();

                    UserDTO user = userMaps.get(batchTask.getCreateUserId());
                    if (user == null){
                        user = userMaps.values().iterator().next();
                    }
                    if (myTrigger == AlarmTrigger.FAILED.getTrigger()) {//失败告警
                        if (TaskStatusConstrant.FAILED_STATUS.contains(currStatus)) {
                            flag = true;
                            String cycTime = addTimeSplit(batchJob.getCycTime());
                            String scheduleType = EScheduleType.getTypeName(batchJob.getType());
                            alarmContent = buildFailAlarmContent(scheduleType, batchTask.getName(), cycTime,parseExeStartTime(batchJob.getExecStartTime()), currStatus, user.getUserName());
                        }
                    } else if (myTrigger == AlarmTrigger.CANCELED.getTrigger()) {//任务停止状态告警(人为杀死或取消)
                        if (TaskStatusConstrant.STOP_STATUS.contains(currStatus)) {
                            flag = true;
                            String cycTime = addTimeSplit(batchJob.getCycTime());
                            String scheduleType = EScheduleType.getTypeName(batchJob.getType());
                            alarmContent = buildStopAlarmContent(scheduleType, batchTask.getName(), cycTime, parseExeStartTime(batchJob.getExecStartTime()), currStatus, user.getUserName());
                        }
                    } else if (myTrigger == AlarmTrigger.TIMING_UNCOMPLETED.getTrigger()
                            || myTrigger == AlarmTrigger.TIMING_EXEC_OVER.getTrigger()) {//未完成-->需要判断定义的时间

                        if (batchJobAlarm.getGmtModified().after(batchJobAlarm.getGmtCreate())) {
                            continue;
                        }

                        Integer cycTimeOnlyDay = getCycTimeOnlyDay(batchJob.getCycTime());
                        Integer targetTime = chgHourMinStrToInt(batchAlarm.getUncompleteTime());
                        if (targetTime == null) {
                            logger.error("batchAlarm :{} UncompleteTime:{} setting is wrong!", batchAlarm.getId(),
                                    batchAlarm.getUncompleteTime());
                            continue;
                        }

                        if (myTrigger == AlarmTrigger.TIMING_UNCOMPLETED.getTrigger()) {
                            if (currDayVal < cycTimeOnlyDay || targetTime >= currHourMinVal) {
                                continue;
                            }
                            //定时未完成
                            if (!TaskStatusConstrant.FINISH_STATUS.contains(currStatus)
                                    && !TaskStatusConstrant.FAILED_STATUS.contains(currStatus)) {
                                flag = true;
                                String scheduleType = EScheduleType.getTypeName(batchJob.getType());
                                String cycTime = addTimeSplit(batchJob.getCycTime());
                                alarmContent = buildUncompletedAlarmContent(scheduleType, batchTask.getName(), cycTime
                                        , batchAlarm.getUncompleteTime(), parseExeStartTime(batchJob.getExecStartTime()),currStatus, user.getUserName());
                                timeLimitAlarmUpdateList.add(batchJobAlarm);
                            }
                        } else {
                            //超时未完成
                            if (batchJob.getExecTime() == null
                                    || batchJob.getExecTime() == 0L
                                    || targetTime >= batchJob.getExecTime()) {
                                continue;
                            }
                            if (currStatus != null && !TaskStatusConstrant.endStatusList.contains(currStatus)) {
                                flag = true;
                                String scheduleType = EScheduleType.getTypeName(batchJob.getType());
                                String cycTime = addTimeSplit(batchJob.getCycTime());
                                alarmContent = buildExecOverAlarmContent(scheduleType, batchTask.getName(), cycTime
                                        , batchAlarm.getUncompleteTime(), parseExeStartTime(batchJob.getExecStartTime()), currStatus, user.getUserName());
                                timeLimitAlarmUpdateList.add(batchJobAlarm);
                            }
                        }
                    } else {
                        logger.info("{} trigger type alarm not work!", batchAlarm.getMyTrigger());
                    }

                    if (flag) {
                        //插入一条告警记录
                        BatchAlarmRecord alarmRecord = new BatchAlarmRecord();
                        alarmRecord.setAlarmId(batchAlarm.getId());
                        alarmRecord.setProjectId(batchTask.getProjectId());
                        alarmRecord.setTenantId(batchTask.getTenantId());
                        alarmRecord.setAppType(batchTask.getAppType());
                        alarmRecord.setDtuicTenantId(batchTask.getDtuicTenantId());
                        alarmRecord.setAlarmContent(alarmContent);
                        alarmRecord.setTriggerType(myTrigger);
                        alarmRecord.setCycTime(batchJob.getCycTime());
                        alarmRecord.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
                        alarmRecord.setGmtModified(alarmRecord.getGmtCreate());
                        batchAlarmRecordDao.insert(alarmRecord);

                        for (long userId : userMaps.keySet()) {
                            BatchAlarmRecordUser batchAlarmRecordUser = new BatchAlarmRecordUser();
                            batchAlarmRecordUser.setProjectId(batchTask.getProjectId());
                            batchAlarmRecordUser.setTenantId(batchTask.getTenantId());
                            batchAlarmRecordUser.setAppType(batchTask.getAppType());
                            batchAlarmRecordUser.setDtuicTenantId(batchTask.getDtuicTenantId());
                            batchAlarmRecordUser.setAlarmRecordId(alarmRecord.getId());
                            batchAlarmRecordUser.setUserId(userId);
                            batchAlarmRecordUserDao.insert(batchAlarmRecordUser);
                        }


                        /**
                         * 插入一条通知记录
                         */
                        Notify notify = notifyDao.getByRelationIdAndBizTypeAndName(batchAlarm.getTaskId(), NotifyType.BATCH.getType(), batchAlarm.getName(), batchAlarm.getTenantId(), batchAlarm.getProjectId(), batchAlarm.getAppType());
                        if (notify == null) {
                            continue;
                        }

                        NotifyRecordParam param = new NotifyRecordParam();
                        param.setTenantId(notify.getTenantId());
                        param.setProjectId(notify.getProjectId());
                        param.setAppType(AppType.RDOS.getType());
                        param.setContent(alarmContent);
                        param.setStatus(batchJob.getStatus());

                        ApiResponse<Long> longApiResponse = consoleNotifyApiClient.generateContent(param);
                        Long contentId = null == longApiResponse ? 0L : longApiResponse.getData();

                        NotifyRecord notifyRecord = new NotifyRecord();
                        notifyRecord.setTenantId(batchAlarm.getTenantId());
                        notifyRecord.setProjectId(batchAlarm.getProjectId());
                        notifyRecord.setAppType(batchAlarm.getAppType());
                        notifyRecord.setDtuicTenantId(batchAlarm.getDtuicTenantId());
                        notifyRecord.setContentId(contentId);
                        notifyRecord.setCycTime(batchJob.getCycTime());
                        notifyRecord.setNotifyId(notify.getId());
                        notifyRecord.setStatus(batchJob.getStatus());
                        notifyRecordDao.insert(notifyRecord);

                        sendAlarm(notifyRecord, contentId, notify.getSendWay(), notify.getProjectId(), notify.getTenantId(), notify.getWebhook(), userMaps);
                    }
                } catch (Exception e) {
                    logger.error("alarm failed jobAlaram :{} e:{}", batchJobAlarm.toString(), e);
                }
            }
        }

        //修改已经发送了超时告警的被监听job
        for (BatchJobAlarm batchJobAlarm : timeLimitAlarmUpdateList) {
            batchJobAlarm.setGmtModified(timeNow);
            batchJobAlarmDao.update(batchJobAlarm);
        }

        //统一清理batchjobAlarm里面当前状态为失败或者完成的信息
        deleteFinishedJob();

    }

    private Map<Long, UserDTO> parseReceivers(String receivers) {
        if (StringUtils.isNotBlank(receivers)) {
            JSONArray receiversJson = JSON.parseArray(receivers);
            Map<Long, UserDTO> userDTOMap = new HashMap<>(receiversJson.size());
            for (Object jsonObject :receiversJson){
                UserDTO user = JSONObject.parseObject(jsonObject.toString(), UserDTO.class);
                userDTOMap.put(user.getId(), user);
            }
            return userDTOMap;
        }
        return null;
    }

    private String parseExeStartTime(Date date){
        if (Objects.isNull(date)) {
            return "未执行";
        } else {
            DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            return df.format(date);
        }
    }

    private String buildFailAlarmContent(String taskType, String taskName, String cycTime, String  startTime, int taskStatusVal, String createUserName) {
        TaskStatus taskStatus = TaskStatus.getTaskStatusByVal(taskStatusVal);
        String contentStr = String.format(TASK_FAIL_ALARM_MESSAGE_TEMPLATE, environmentContext.getAlarmTitle(), taskName, taskName, AlarmType.TASK_FAIL.getType(),
                cycTime, startTime, taskStatus.toString(), createUserName);
        return contentStr;
    }

    private String buildStopAlarmContent(String taskType, String taskName, String cycTime, String  startTime,  int taskStatusVal, String createUserName) {
        TaskStatus taskStatus = TaskStatus.getTaskStatusByVal(taskStatusVal);
        String contentStr = String.format(TASK_FAIL_ALARM_MESSAGE_TEMPLATE, environmentContext.getAlarmTitle(), taskName, taskName, AlarmType.TASK_STOP.getType(),
                cycTime, startTime, taskStatus.toString(), createUserName);
        return contentStr;
    }

    private String buildUncompletedAlarmContent(String taskType, String taskName, String cycTime, String timeOverLimit, String  startTime, int taskStatusVal,
                                                String createUserName) {
        TaskStatus taskStatus = TaskStatus.getTaskStatusByVal(taskStatusVal);
        String contentStr = String.format(TASK_TIMING_ALARM_MESSAGE_TEMPLATE, environmentContext.getAlarmTitle(), taskName, taskName, String.format(AlarmType.TIMING_UNCOMPLETED.getType(), timeOverLimit),
                cycTime, startTime, taskStatus.toString(), createUserName);
        return contentStr;
    }

    private String buildExecOverAlarmContent(String taskType, String taskName, String cycTime, String timeOverLimit, String  startTime, int taskStatusVal,
                                                String createUserName) {
        TaskStatus taskStatus = TaskStatus.getTaskStatusByVal(taskStatusVal);
        String contentStr = String.format(TASK_TIMING_ALARM_MESSAGE_TEMPLATE, environmentContext.getAlarmTitle(), taskName, taskName, String.format(AlarmType.TIMING_EXEC_OVER.getType(), Integer.parseInt(timeOverLimit)/60),
                cycTime, startTime,taskStatus.toString(), createUserName);
        return contentStr;
    }



    private void sendAlarm(NotifyRecord notifyRecord, Long contentId, String senderType, Long projectId, Long tenantId, String webhook, Map<Long, UserDTO> userMaps) {
        List<Long> revList = notifyUserDao.listUserIdByNotifyId(notifyRecord.getNotifyId());
        List<Integer> senderTypeList = this.getValues(senderType);
        if (CollectionUtils.isEmpty(revList) && CollectionUtils.isEmpty(senderTypeList)) {
            logger.error("receiverIds:{} is null or senderTypeList:{} is null", revList, senderTypeList);
            return;
        }


        List<SetAlarmUserDTO> receivers = new ArrayList<>(revList.size());
        for (Long receiverId : revList) {
            UserDTO user = userMaps.get(receiverId);
            if (user == null) {
                continue;
            }
            SetAlarmUserDTO userDTO = new SetAlarmUserDTO();
            userDTO.setEmail(user.getEmail());
            userDTO.setTelephone(user.getPhoneNumber());
            userDTO.setUserId(receiverId);
            userDTO.setUsername(user.getUserName());
            receivers.add(userDTO);
        }
        SetAlarmNotifyRecordParam param = new SetAlarmNotifyRecordParam();
        param.setReceivers(receivers);
        param.setTenantId(tenantId);
        param.setProjectId(projectId);
        param.setAppType(AppType.RDOS.getType());
        param.setContentId(contentId);
        param.setSenderTypes(senderTypeList);
        param.setTitle(TITLE);
        param.setNotifyRecordId(notifyRecord.getId());
        param.setWebhook(webhook);

        consoleNotifyApiClient.setAlarm(param);

    }

    private List<Integer> getValues(String bitVector) {
        if (StringUtils.isBlank(bitVector)) {
            return Collections.EMPTY_LIST;
        }
        List<Integer> values = new ArrayList<>(bitVector.length());
        char[] arr = bitVector.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != '0') {
                values.add(i);
            }
        }
        return values;
    }

    private Integer chgHourMinStrToInt(String str) {
        String timeStr = getTimeStrWithoutSymbol(str);
        return getIntegerVal(timeStr);
    }

    /**
     * 将yyyyMMddHHmmss ---> yyyy-MM-dd HH:mm:ss
     *
     * @param str
     * @return
     */
    private String addTimeSplit(String str) {

        if (str.length() != 14) {
            return str;
        }

        StringBuffer sb = new StringBuffer("");
        sb.append(str.substring(0, 4))
                .append("-")
                .append(str.substring(4, 6))
                .append("-")
                .append(str.substring(6, 8))
                .append(" ")
                .append(str.substring(8, 10))
                .append(":")
                .append(str.substring(10, 12))
                .append(":")
                .append(str.substring(12, 14));
        return sb.toString();
    }

    private String getTimeStrWithoutSymbol(String timeStr) {
        return timeStr.replace(" ", "").replace("-", "").replace(":", "");
    }

    private Integer getCycTimeOnlyDay(String cycTime) {
        String time = getTimeStrWithoutSymbol(cycTime);
        return Integer.valueOf(time.substring(0, 8));
    }

    public Integer getIntegerVal(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof String) {
            return Integer.valueOf((String) obj);
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        }

        throw new RdosDefineException("not support type of " + obj.getClass() + " convert to Integer.");
    }

    private void deleteFinishedJob() {
        List<Integer> deleteStatuses = Lists.newArrayList();
        deleteStatuses.addAll(TaskStatusConstrant.FAILED_STATUS);
        deleteStatuses.addAll(TaskStatusConstrant.STOP_STATUS);
        deleteStatuses.addAll(TaskStatusConstrant.FROZEN_STATUS);
        deleteStatuses.addAll(TaskStatusConstrant.FINISH_STATUS);
        batchJobAlarmDao.deleteFinishedJob(deleteStatuses);
    }

    @Override
    public void destroy() throws Exception {
        scheduledService.shutdownNow();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                environmentContext.getAlarmProcessorInterval(),
                TimeUnit.MILLISECONDS);
    }
}
