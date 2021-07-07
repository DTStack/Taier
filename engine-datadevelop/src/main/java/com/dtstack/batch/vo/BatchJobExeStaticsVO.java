package com.dtstack.batch.vo;


import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * Reason:
 * Date: 2018/3/5
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
@Data
public class BatchJobExeStaticsVO {

    private Integer taskType = 0;

    private Integer cronExeNum = 0;

    private Integer fillDataExeNum = 0;

    private Integer failNum = 0;

    private List<BatchJobInfo> jobInfoList = Lists.newArrayList();

    public void addBatchJob(BatchJobInfo jobInfo) {
        jobInfoList.add(jobInfo);
    }

    @Data
    public static class BatchJobInfo {

        private String jobId;

        private Long exeStartTime;

        private Integer exeTime;

        private Long totalCount;

        private Long dirtyNum;
    }
}
