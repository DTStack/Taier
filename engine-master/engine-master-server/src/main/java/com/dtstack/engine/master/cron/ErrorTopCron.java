package com.dtstack.engine.master.cron;

import com.dtstack.engine.domain.ScheduleJobFailed;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.common.pager.PageQuery;
import com.dtstack.engine.domain.po.JobTopErrorPO;
import com.dtstack.engine.master.vo.JobTopErrorVO;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.common.enums.AppType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/8/11 7:44 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class ErrorTopCron implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorTopCron.class);
    @Autowired
    private ScheduleJobFailedDao scheduleJobFailedDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleDictDao scheduleDictDao;

    private final String CODE = "DAG_JOB_ERROR_FIRST";
    private final String NAME = "FAILED_OPEN";
    private final Integer DAY_COUNT = 30;
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private final static List<Integer> FAILED_STATUS = Lists.newArrayList(RdosTaskStatus.FAILED.getStatus(), RdosTaskStatus.SUBMITFAILD.getStatus(), RdosTaskStatus.KILLED.getStatus());

    /**
     * 每天1点跑任务
     */
    @Scheduled(cron = "${error.top.cron:0 0 1 * * ? } ")
    public void runErrorTop() {
        if (!environmentContext.getOpenErrorTop()) {
            return;
        }

        if (!RUNNING.get()) {
            LOGGER.info("DataClearSchedule node is not master");
            return;
        }

        findTenantAndProject((uicTenantId, projectId) -> {
            // 计算前一天ErrorTop
            DateTime dateTime = new DateTime(DateUtil.getTodayStart(System.currentTimeMillis(), "MS"));
            addErrorTop(uicTenantId, projectId, dateTime);
            // 删除30天之前的
            deleteErrorTop30(uicTenantId, projectId);
        });
    }

    public void setIsMaster(boolean isMaster) {
        RUNNING.set(isMaster);
    }

    private void findTenantAndProject(ErrorTopLogicalBusiness errorTopLogicalBusiness) {
        // 查出所有租户绑定集群的租户

        List<Long> uicTenantIds = tenantDao.listDtUicTenantIdByIds(null);;

        // 通过租户查询离线工程
        List<List<Long>> tenants = Lists.partition(uicTenantIds, environmentContext.getMaxTenantSize());
        for (List<Long> tenantId : tenants) {
            List<ScheduleTaskShade> taskShades = scheduleTaskShadeDao.listByUicTenantId(tenantId, AppType.RDOS.getType());
            Map<Long, List<ScheduleTaskShade>> groupTenants = taskShades.stream().collect(Collectors.groupingBy(ScheduleTaskShade::getDtuicTenantId));

            for (Map.Entry<Long, List<ScheduleTaskShade>> entry : groupTenants.entrySet()) {
                Long uicTenantId = entry.getKey();
                List<ScheduleTaskShade> taskShadeList = entry.getValue();

                // 按照工程计算出
                for (ScheduleTaskShade scheduleTaskShade : taskShadeList) {
                    Long projectId = scheduleTaskShade.getProjectId();
                    errorTopLogicalBusiness.logicalBusiness(uicTenantId,projectId);
                }

            }
        }
    }

    private void deleteErrorTop30(Long uicTenantId, Long projectId) {
        DateTime dateTime = new DateTime(DateUtil.getTodayStart(System.currentTimeMillis(), "MS"));
        DateTime yesterdayDate = dateTime.minusDays(30);
        scheduleJobFailedDao.deleteByGmtCreate(AppType.RDOS.getType(),uicTenantId,projectId,yesterdayDate.toDate());
    }

    private void addErrorTop(Long uicTenantId, Long projectId,DateTime dateTime) {
        PageQuery<Object> pageQuery = new PageQuery<>(1, 20);
        // 按照tenant和project跑出TopError
        DateTime yesterdayDate = dateTime.minusDays(1);
        String startCycTime = yesterdayDate.toString("yyyyMMddHHmmss");
        String endCycTime = dateTime.toString("yyyyMMddHHmmss");
        List<JobTopErrorPO> jobTopErrorPOs = scheduleJobDao.listTopError(uicTenantId, projectId,
                EScheduleType.NORMAL_SCHEDULE.getType(), startCycTime,endCycTime, FAILED_STATUS, pageQuery, AppType.RDOS.getType());

        if (CollectionUtils.isNotEmpty(jobTopErrorPOs)) {
            List<ScheduleJobFailed> faileds = buildError(uicTenantId, projectId, yesterdayDate.toDate(), jobTopErrorPOs);
            // 插入top
            scheduleJobFailedDao.insertBatch(faileds);
        }
    }

    private List<ScheduleJobFailed> buildError(Long uicTenantId, Long projectId, Date toDate, List<JobTopErrorPO> jobTopErrorPOs) {
        List<ScheduleJobFailed> faileds = Lists.newArrayList();

        for (JobTopErrorPO jobTopErrorPO : jobTopErrorPOs) {
            ScheduleJobFailed scheduleJobFailed = new ScheduleJobFailed();
            scheduleJobFailed.setAppType(AppType.RDOS.getType());
            scheduleJobFailed.setProjectId(projectId);
            scheduleJobFailed.setUicTenantId(uicTenantId);
            scheduleJobFailed.setGmtCreate(toDate);
            scheduleJobFailed.setTaskId(jobTopErrorPO.getTaskId());
            scheduleJobFailed.setErrorCount(jobTopErrorPO.getErrorCount());
            faileds.add(scheduleJobFailed);
        }

        return faileds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if (!environmentContext.getOpenErrorTop()) {
                return;
            }

            Integer count = scheduleDictDao.update(CODE, NAME, "true", "false");

            if (count > 0) {
                // 初始化前30天的数据
                findTenantAndProject((uicTenantId, projectId) -> {
                    DateTime dateTime = new DateTime(DateUtil.getTodayStart(System.currentTimeMillis(), "MS"));
                    for (int i = 0; i < DAY_COUNT; i++) {
                        try {
                            addErrorTop(uicTenantId, projectId, dateTime);
                            dateTime = dateTime.minusDays(1);
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    interface ErrorTopLogicalBusiness {

        /**
         *
         * @param uicTenantId 租户id
         * @param projectId 项目id
         */
        void logicalBusiness(Long uicTenantId,Long projectId);
    }

}
