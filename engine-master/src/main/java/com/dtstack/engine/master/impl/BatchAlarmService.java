package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.dtcenter.common.util.MathUtil;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.BatchAlarmDao;
import com.dtstack.engine.dao.BatchAlarmRecordDao;
import com.dtstack.engine.dao.BatchAlarmRecordUserDao;
import com.dtstack.engine.domain.BatchAlarm;
import com.dtstack.engine.domain.BatchAlarmRecordUser;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.dto.BatchAlarmDTO;
import com.dtstack.engine.dto.BatchAlarmRecordDTO;
import com.dtstack.engine.master.vo.AlarmSearchRecordVO;
import com.dtstack.engine.master.vo.AlarmSearchVO;
import com.dtstack.engine.master.vo.AlarmVO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.dtstack.dtcenter.common.util.PublicUtil.getConvertSendType;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class BatchAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(BatchAlarmService.class);

    private static int ALARM_NAME_LENGTH_LIMIT = 64;

    @Autowired
    private BatchAlarmDao batchAlarmDao;

    @Autowired
    private BatchAlarmRecordDao batchAlarmRecordDao;

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private BatchJobAlarmService batchJobAlarmService;

    @Autowired
    private BatchAlarmRecordUserDao batchAlarmRecordUserDao;

    @Autowired
    private NotifyService notifyService;

    /**
     * 创建报警规则
     *
     * @param batchAlarm
     * @param receiveUsers
     * @param userId
     * @author jiangbo
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createAlarm(@Param("batchAlarm")String batchAlarm,
                            @Param("senderTypes") List<Integer> senderTypes,
                            @Param("receiveUsers") String receiveUsers,
                            @Param("userId") long userId, @Param("appType") Integer appType) {
        BatchAlarmDTO alarm = JSONObject.parseObject(batchAlarm, BatchAlarmDTO.class);

        this.checkAlarm(alarm, senderTypes);

        BatchAlarmDTO batchAlarmDTO = new BatchAlarmDTO();
        batchAlarmDTO.setName(alarm.getName());
        batchAlarmDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchAlarmDTO.setProjectId(alarm.getProjectId());
        batchAlarmDTO.setIsTaskHolder(alarm.getIsTaskHolder());
        batchAlarmDTO.setAppType(appType);

        PageQuery<BatchAlarmDTO> baPageQuery = new PageQuery<>(batchAlarmDTO);

        List<BatchAlarm> existAlarm = batchAlarmDao.generalQuery(baPageQuery);
        if (existAlarm.size() > 0) {
            throw new RdosDefineException(ErrorCode.ALARM_ALREADY_EXIST);
        }

        alarm.setAppType(appType);
        alarm.setCreateUserId(userId);
        alarm.setIsDeleted(Deleted.NORMAL.getStatus());
        alarm.setStatus(AlarmStatus.NORMAL.getStatus());
        alarm.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        alarm.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        alarm.setReceivers(receiveUsers);
        if(Objects.isNull(alarm.getWebhook())){
            alarm.setWebhook("");
        }
        batchAlarmDao.insert(alarm);

        //添加对应的job到jobAlarm表

        List<Integer> notFinishedStatus = Lists.newArrayList();
        //UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4) SUBMITTING(10), RESTARTING(11)
        notFinishedStatus.add(TaskStatus.UNSUBMIT.getStatus());
        notFinishedStatus.add(TaskStatus.CREATED.getStatus());
        notFinishedStatus.add(TaskStatus.SCHEDULED.getStatus());
        notFinishedStatus.add(TaskStatus.DEPLOYING.getStatus());
        notFinishedStatus.add(TaskStatus.RUNNING.getStatus());
        notFinishedStatus.add(TaskStatus.SUBMITTING.getStatus());
        notFinishedStatus.add(TaskStatus.RESTARTING.getStatus());

        List<Long> batchJobs = batchJobService.getJobByTaskIdAndStatus(alarm.getTaskId(), notFinishedStatus, appType);
        for (Long batchJob : batchJobs) {
            //status只是作为清除判断--》所以这个地方统一给
            BatchJob batchJobAlarm = new BatchJob();
            batchJobAlarm.setTenantId(alarm.getTenantId());
            batchJobAlarm.setProjectId(alarm.getProjectId());
            batchJobAlarm.setDtuicTenantId(alarm.getDtuicTenantId());
            batchJobAlarm.setAppType(appType);
            batchJobAlarm.setId(batchJob);
            batchJobAlarm.setStatus(TaskStatus.RUNNING.getStatus());
            batchJobAlarm.setTaskId(alarm.getTaskId());
            batchJobAlarmService.saveBatchJobAlarm(batchJobAlarm);
        }

        /**
         * 新增通知对象
         */
        notifyService.addNotify(alarm.getId(),
                alarm.getTenantId(),
                alarm.getProjectId(),
                NotifyType.BATCH.getType(),
                alarm.getMyTrigger(),
                alarm.getTaskId(),
                alarm.getStartTime(),
                alarm.getEndTime(),
                alarm.getUncompleteTime(),
                senderTypes,
                receiveUsers,
                userId,
                alarm.getName(),
                alarm.getWebhook(),appType);
        return alarm.getId();
    }


    private void checkAlarm(BatchAlarmDTO batchAlarm, @Param("senderTypes") List<Integer> senderTypes) {
        if ((batchAlarm.getMyTrigger() == AlarmTrigger.TIMING_UNCOMPLETED.getTrigger() || batchAlarm.getMyTrigger() == AlarmTrigger.TIMING_EXEC_OVER.getTrigger())
                && Strings.isNullOrEmpty(batchAlarm.getUncompleteTime())) {
            throw new RdosDefineException("(参数 uncompleteTime 不能为空)", ErrorCode.INVALID_PARAMETERS);
        }
        if ((batchAlarm.getMyTrigger() == AlarmTrigger.TIMING_UNCOMPLETED.getTrigger() || batchAlarm.getMyTrigger() == AlarmTrigger.TIMING_EXEC_OVER.getTrigger())) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }

        setConvertSendType(batchAlarm, senderTypes);

        if (batchAlarm.getName().length() > ALARM_NAME_LENGTH_LIMIT) {
            throw new RdosDefineException(ErrorCode.ALARM_NAME_LENGTH_GT_16);
        }
    }


    /**
     * 更新报警规则
     *
     * @param receiveUsers
     * @author jiangbo
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAlarm(@Param("batchAlarm") String batchAlarmVo,
                            @Param("senderTypes") List<Integer> senderTypes,
                            @Param("receiveUsers") String receiveUsers, @Param("appType") Integer appType) {

        BatchAlarmDTO batchAlarm = JSONObject.parseObject(batchAlarmVo,BatchAlarmDTO.class);

        this.checkAlarm(batchAlarm, senderTypes);

        BatchAlarm updatedAlarm = batchAlarmDao.getOne(batchAlarm.getId());
        if (updatedAlarm == null) {
            throw new RdosDefineException(ErrorCode.ALARM_NOT_EXIST);
        }

        BatchAlarm checkNameAlarm = batchAlarmDao.getByNameAndProjectId(batchAlarm.getName(), batchAlarm.getProjectId());
        if (checkNameAlarm != null && checkNameAlarm.getId() != batchAlarm.getId()) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST);
        }
        updatedAlarm = new BatchAlarm();
        updatedAlarm.setId(batchAlarm.getId());
        updatedAlarm.setProjectId(batchAlarm.getProjectId());
        updatedAlarm.setTenantId(batchAlarm.getTenantId());
        updatedAlarm.setTaskId(batchAlarm.getTaskId());
        updatedAlarm.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        updatedAlarm.setSenderType(batchAlarm.getSenderType());
        updatedAlarm.setName(batchAlarm.getName());
        updatedAlarm.setUncompleteTime(batchAlarm.getUncompleteTime());
        updatedAlarm.setMyTrigger(batchAlarm.getMyTrigger());
        updatedAlarm.setIsTaskHolder(batchAlarm.getIsTaskHolder());
        updatedAlarm.setWebhook(batchAlarm.getWebhook());
        if (StringUtils.isNotBlank(receiveUsers)) {
            updatedAlarm.setReceivers(receiveUsers);
        }
        batchAlarmDao.update(updatedAlarm);

        /**
         * 更新通知对象
         */
//        notifyService.updateNotify(orginalName, batchAlarm.getTenantId(),
//                batchAlarm.getProjectId(),
//                NotifyType.BATCH.getType(),
//                batchAlarm.getTaskId(),
//                batchAlarm.getStartTime(),
//                batchAlarm.getEndTime(),
//                batchAlarm.getUncompleteTime(),
//                senderTypes,
//                receiveUsers,
//                batchAlarm.getName(),
//                batchAlarm.getWebhook());
    }

    private void setConvertSendType(BatchAlarm alarm, List<Integer> senderTypes) {
        if (senderTypes.isEmpty()) {
            throw new RdosDefineException("senderTypes 参数不能为空.", ErrorCode.INVALID_PARAMETERS);
        } else {
            int senderType = 0;
            for (Integer type : senderTypes) {
                senderType += Math.pow(16, type - 1);
            }
            alarm.setSenderType(senderType);
        }
    }


    /**
     * 关闭报警规则
     *
     * @param alarmId
     * @author jiangbo
     */
    public void closeAlarm(@Param("alarmId") long alarmId, @Param("projectId") long projectId, @Param("tenantId") long tenantId) {

        closeOrStartAlarm(alarmId, true, projectId, tenantId);
    }

    /**
     * 开启报警规则
     *
     * @param alarmId
     * @author jiangbo
     */
    public void startAlarm(@Param("alarmId") long alarmId, @Param("projectId") long projectId, @Param("tenantId") long tenantId) {

        closeOrStartAlarm(alarmId, false, projectId, tenantId);
    }

    /**
     * 删除报警规则
     *
     * @param alarmId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlarm(@Param("alarmId") long alarmId, @Param("appType") Integer appType) {
        //删除报警规则
        BatchAlarm deleteAlarm = new BatchAlarm();
        deleteAlarm.setId(alarmId);
        deleteAlarm.setIsDeleted(Deleted.DELETED.getStatus());
        deleteAlarm.setStatus(AlarmStatus.DELETE.getStatus());
        deleteAlarm.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));

        batchAlarmDao.update(deleteAlarm);


        // 删除告警记录
        batchAlarmRecordDao.deleteByAlarmId(alarmId);

        /**
         * 删除通知对象
         */
//        notifyService.delete(alarmId, NotifyType.BATCH.getType(), projectId, tenantId);
    }

    /**
     * 获取报警规则列表
     *
     * @return
     */
    public PageResult getAlarmList(AlarmSearchVO alarmSearchVO) {

        BatchAlarmDTO batchAlarmDTO = new BatchAlarmDTO();
        batchAlarmDTO.setProjectId(alarmSearchVO.getProjectId());
        batchAlarmDTO.setIsDeleted(Deleted.NORMAL.getStatus());

        //根据任务 ID 属性查询
        if (CollectionUtils.isNotEmpty(alarmSearchVO.getTaskIds())) {
            batchAlarmDTO.setTaskIds(alarmSearchVO.getTaskIds());
        }

        //按状态查找
        if (alarmSearchVO.getAlarmStatus() != -1) {
            batchAlarmDTO.setStatus(alarmSearchVO.getAlarmStatus());
        }

        if (alarmSearchVO.getOwnerId() > 0) {
            batchAlarmDTO.setCreateUserId(alarmSearchVO.getOwnerId());
        }

        PageQuery pageQuery = new PageQuery(alarmSearchVO.getPageIndex(), alarmSearchVO.getPageSize(), "gmt_create", "desc");
        pageQuery.setModel(batchAlarmDTO);

        List<BatchAlarm> batchAlarmList = batchAlarmDao.generalQuery(pageQuery);
        int totalCount = batchAlarmDao.generalCount(batchAlarmDTO);

        return transformData(batchAlarmList, totalCount, pageQuery, alarmSearchVO.getAppType());
    }

    /**
     * @param alarmId
     * @param isClose
     */
    @Forbidden
    private void closeOrStartAlarm(long alarmId, boolean isClose, long projectId, long tenantId) {
        BatchAlarm updatedAlarm = new BatchAlarm();
        updatedAlarm.setId(alarmId);
        if (isClose) {
            updatedAlarm.setStatus(AlarmStatus.CLOSE.getStatus());
        } else {
            updatedAlarm.setStatus(AlarmStatus.NORMAL.getStatus());
        }
        updatedAlarm.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        batchAlarmDao.update(updatedAlarm);

        /**
         * 更新通知状态
         */
//        notifyService.updateStatus(alarmId, isClose, NotifyType.BATCH.getType(), projectId, tenantId);
    }

    @Forbidden
    public PageResult transformData(List<BatchAlarm> batchAlarmList, int totalCount, PageQuery pageQuery, Integer appType) {

        List<AlarmVO> alarmVOS = new ArrayList<>();

        if (CollectionUtils.isEmpty(batchAlarmList)) {
            return new PageResult(alarmVOS, totalCount, pageQuery);
        }

        batchAlarmList.forEach(ba -> {
            AlarmVO alarmVO = new AlarmVO();
            alarmVO.setAlarmId(ba.getId());
            alarmVO.setAlarmName(ba.getName());
            alarmVO.setAlarmStatus(ba.getStatus());
            alarmVO.setCreateTime(ba.getGmtCreate());
            alarmVO.setSenderTypes(getConvertSendType(ba.getSenderType()));
            alarmVO.setMyTrigger(ba.getMyTrigger());
            alarmVO.setCreateUserId(ba.getCreateUserId());
            alarmVO.setTaskId(ba.getTaskId());
            alarmVO.setUncompleteTime(ba.getUncompleteTime());
            alarmVO.setProjectId(ba.getProjectId());
            alarmVO.setIsTaskHolder(ba.getIsTaskHolder());
            alarmVO.setWebhook(ba.getWebhook());
            if (StringUtils.isNotBlank(ba.getReceivers())) {
                try {
                    List<AlarmVO.Receiver> receivers = JSONObject.parseArray(ba.getReceivers(), AlarmVO.Receiver.class);
                    alarmVO.setReceiveUsers(receivers);
                } catch (Exception e) {
                    logger.error("",e);
                }
            }
            alarmVOS.add(alarmVO);
        });
        return new PageResult(alarmVOS, totalCount, pageQuery);
    }

    @Forbidden
    public List<Long> getAllNeedMonitorTaskId() {
        return batchAlarmDao.getAllNeedMonitorTaskId(null);
    }

    @Forbidden
    public List<Long> getAllNeedMonitorTaskId(Long projectId) {
        return batchAlarmDao.getAllNeedMonitorTaskId(projectId);
    }

    /**
     * 更新基础信息
     *
     * @param batchAlarm
     * @return
     */
    public BatchAlarm update(BatchAlarm batchAlarm) {
        if (null == batchAlarm || batchAlarm.getId() < 0) {
            return batchAlarm;
        }
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        batchAlarm.setGmtModified(timestamp);
        batchAlarmDao.update(batchAlarm);
        return batchAlarm;
    }

    public BatchAlarm getById(@Param("alarmId") Long alarmId) {
        return batchAlarmDao.getOne(alarmId);
    }


    /**
     * 统计对应的告警总数
     *
     * @param projectId
     * @param appType
     * @return
     */
    public Map<String, Integer> countAlarm(@Param("projectId") Long projectId, @Param("appType") Integer appType) {

        int today = batchAlarmRecordDao.countAlarmToday(projectId, appType);
        int thisWeek = batchAlarmRecordDao.countAlarmWeek(projectId, appType);
        int thisMonth = batchAlarmRecordDao.countAlarmMonth(projectId, appType);

        Map<String, Integer> data = new HashMap<>();
        data.put("today", today);
        data.put("week", thisWeek);
        data.put("month", thisMonth);
        return data;
    }


    /**
     * 获取对应对告警记录
     *
     * @param alarmSearchRecordVO
     * @return
     */
    public PageResult getAlarmRecordList(AlarmSearchRecordVO alarmSearchRecordVO) {
        //告警人 筛选
        BatchAlarmRecordDTO batchAlarmRecordDTO = new BatchAlarmRecordDTO();
        if (alarmSearchRecordVO.getReceive() != null && alarmSearchRecordVO.getReceive() > 0) {
            List<Long> alarmRecords = batchAlarmRecordUserDao.getAlarmByRecordUserId(alarmSearchRecordVO.getReceive(), alarmSearchRecordVO.getAppType(), alarmSearchRecordVO.getProjectId());
            if (CollectionUtils.isEmpty(alarmRecords)) {
                return PageResult.EMPTY_PAGE_RESULT;
            } else {
                List<Long> alarmIds = batchAlarmRecordDao.listAlarmIds(alarmRecords);
                batchAlarmRecordDTO.setAlarmIdList(alarmIds);
            }
        }

        if (alarmSearchRecordVO.getStartTime() != null && alarmSearchRecordVO.getStartTime() > 0) {
            batchAlarmRecordDTO.setStartTime(alarmSearchRecordVO.getStartTime());
        }

        if (alarmSearchRecordVO.getEndTime() != null && alarmSearchRecordVO.getEndTime() > 0) {
            batchAlarmRecordDTO.setEndTime(alarmSearchRecordVO.getEndTime());
        }

        batchAlarmRecordDTO.setProjectId(alarmSearchRecordVO.getProjectId());
        batchAlarmRecordDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchAlarmRecordDTO.setAppType(alarmSearchRecordVO.getAppType());
        batchAlarmRecordDTO.setTaskIdList(alarmSearchRecordVO.getTaskIds());
        PageQuery<BatchAlarmRecordDTO> pageQuery = new PageQuery(alarmSearchRecordVO.getPageIndex(), alarmSearchRecordVO.getPageSize(), "gmt_create", Sort.DESC.name());
        pageQuery.setModel(batchAlarmRecordDTO);
        List<Map<String, Object>> batchAlarmRecords = batchAlarmRecordDao.listByCondition(pageQuery);
        for (Map<String, Object> batchAlarmRecord : batchAlarmRecords) {
            Object alarmRecordId = batchAlarmRecord.get("id");
            if(Objects.nonNull(alarmRecordId)){
                List<Long> alarmUserIdByRecordId = batchAlarmRecordUserDao.getAlarmUserIdByRecordId(MathUtil.getLongVal(alarmRecordId), alarmSearchRecordVO.getAppType(), alarmSearchRecordVO.getProjectId());
                batchAlarmRecord.put("receiveUser",JSONObject.toJSONString(alarmUserIdByRecordId));
            }
        }
        int totalCount = batchAlarmRecordDao.countByCondition(batchAlarmRecordDTO);

        return new PageResult<>(batchAlarmRecords, totalCount, pageQuery);
    }


    /**
     * com.dtstack.batch.service.job.impl.BatchJobService#restartJobAndResume 使用
     *
     * @param projectId
     * @param jobs
     */
    public void saveBatchJobAlarm(@Param("projectId") Long projectId, @Param("jobs") String jobs, @Param("appType") Integer appType, @Param("dtuicTenantId") Long dtuicTenantId) {
        List<BatchJob> batchJobs = JSONObject.parseArray(jobs, BatchJob.class);
        if (CollectionUtils.isEmpty(batchJobs)) {
            return;
        }
        List<Long> allNeedMonitorTaskId = batchAlarmDao.getAllNeedMonitorTaskId(projectId);
        if (CollectionUtils.isEmpty(allNeedMonitorTaskId)) {
            return;
        }
        for (BatchJob batchJob : batchJobs) {
            if (null != batchJob.getTaskId() && allNeedMonitorTaskId.contains(batchJob.getTaskId())) {
                batchJob.setStatus(TaskStatus.UNSUBMIT.getStatus());
                batchJob.setAppType(appType);
                batchJob.setDtuicTenantId(dtuicTenantId);
                batchJobAlarmService.saveBatchJobAlarm(batchJob);
            }
        }

    }



    public List<BatchAlarm> listByTaskId(@Param("taskId") Long taskId, @Param("projectId") Long projectId, @Param("tenentId") Long tenantId) {
        return  batchAlarmDao.listByTaskId(taskId,projectId,tenantId);
    }

    public Integer deleteAlarmByTask(@Param("taskId") Long taskId, @Param("projectId") Long projectId, @Param("tenentId") Long tenantId) {
        return batchAlarmDao.deleteAlarmByTask(taskId, projectId, tenantId);
    }

    public BatchAlarm getByNameAndProjectId(@Param("name") String name, @Param("projectId") Long projectId) {
        return batchAlarmDao.getByNameAndProjectId(name,projectId);
    }

    /**
     * 转换原ide中的告警记录接受人(仅初始化使用)
     */
    public void formatRecordUser(@Param("data") String data) {
        if (StringUtils.isNotBlank(data)) {
            List<BatchAlarmRecordUser> batchAlarmRecordUsers = JSONObject.parseArray(data, BatchAlarmRecordUser.class);
            if (CollectionUtils.isNotEmpty(batchAlarmRecordUsers)) {
                batchAlarmRecordUserDao.batchInsert(batchAlarmRecordUsers);
            }
        }
    }
}
