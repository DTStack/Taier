package com.dtstack.engine.master.temp;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.master.AbstractCommonTest;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class TestQuan extends AbstractCommonTest {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeService taskShadeService;

    @Test
    public void testQuan() {
//        scheduleJobService.createTodayTaskShade(219L, 2);
        scheduleJobService.testTrigger("7be2c4f8");
    }


    @Test
    public void updateInfo() {
        taskShadeService.info(225L, 2, "{\"sqlText\":\"SELECT * FROM DUAL\", \"computeType\":1,\"exeArgs\":\" null\",\"engineType\":\"mysql\", \"multiEngineType\":6,\"pluginInfo\":{\"jdbcUrl\":\"jdbc:mysql://172.16.100.115:3306/dt_assets?serverTimezone=UTC&characterEncoding=UTF-8&useSSL=false\",\"password\":\"DT@Stack#123\",\"username\":\"drpeco\",\"typeName\":\"mysql\"}}");
    }

    private List<String> jobsIds = new ArrayList<>();

    public static void main(String[] args) {
        List<ScheduleBatchJob> jobs = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJobId(UUID.randomUUID().toString().substring(0,6));

            ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
            jobs.add(scheduleBatchJob);
        }
        new TestQuan().insertJobList(jobs, EScheduleType.NORMAL_SCHEDULE.getType());
    }

    public Long insertJobList(Collection<ScheduleBatchJob > batchJobCollection, Integer scheduleType) {
        if (CollectionUtils.isEmpty(batchJobCollection)) {
            return null;
        }

        Iterator<ScheduleBatchJob> batchJobIterator = batchJobCollection.iterator();

        //count%20 为一批
        //1: 批量插入BatchJob
        //2: 批量插入BatchJobJobList
        int count = 0;
        int jobBatchSize = 20;
        int jobJobBatchSize = 100;
        Long minJobId=null;

        Map<String, Integer> nodeJobSize = computeJobSizeForNode(batchJobCollection.size(), scheduleType);
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            List<ScheduleJob> jobWaitForSave = Lists.newArrayList();
            List<ScheduleJobJob> jobJobWaitForSave = Lists.newArrayList();
            while (nodeSize > 0 && batchJobIterator.hasNext()) {
                nodeSize--;
                count++;

                ScheduleBatchJob scheduleBatchJob = batchJobIterator.next();

                ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
                scheduleJob.setNodeAddress(nodeAddress);

                jobWaitForSave.add(scheduleJob);
                jobJobWaitForSave.addAll(scheduleBatchJob.getBatchJobJobList());

                if (count % jobBatchSize == 0 || count == (batchJobCollection.size() - 1) || jobJobWaitForSave.size() > jobJobBatchSize) {
                    minJobId = persistJobs(jobWaitForSave, jobJobWaitForSave, minJobId,jobJobBatchSize);
                }
            }
            //结束前persist一次，flush所有jobs
            minJobId = persistJobs(jobWaitForSave, jobJobWaitForSave, minJobId,jobJobBatchSize);

        }
        return minJobId;
    }

    private Map<String, Integer> computeJobSizeForNode(int jobSize, int scheduleType) {
        Map<String, Integer> jobSizeInfo = new HashMap<>();
        jobSizeInfo.put("127.0.0.1:8090", jobSize / 2);
        jobSizeInfo.put("127.0.0.1:8091", jobSize / 2 + 1);
        return jobSizeInfo;
    }

    private Long persistJobs(List<ScheduleJob> jobWaitForSave, List<ScheduleJobJob> jobJobWaitForSave, Long minJobId,Integer jobJobBatchSize) {
        try {
            return RetryUtil.executeWithRetry(() -> {
                Long curMinJobId=minJobId;
                if (jobWaitForSave.size() > 0) {
                    try {
                        for (ScheduleJob scheduleJob : jobWaitForSave) {
                            if (jobsIds.contains(scheduleJob.getJobId())) {
                                System.exit(-1);
                            }else{
                                jobsIds.add(scheduleJob.getJobId());
                            }

                        }
                    } catch (Exception e) {
                        throw new RdosDefineException(e);
                    }
                    if (Objects.isNull(minJobId)) {
                        curMinJobId = jobWaitForSave.stream().map(ScheduleJob::getId).min(Long::compareTo).orElse(null);
                    }
                }

                return curMinJobId;
            }, 3, 200, false);
        } catch (Exception e) {
//            LOGGER.error("!!!!! persistJobs job error !!!! job {} jobjob {}", jobWaitForSave, jobJobWaitForSave, e);
            throw new RdosDefineException(e);
        }
    }
}
