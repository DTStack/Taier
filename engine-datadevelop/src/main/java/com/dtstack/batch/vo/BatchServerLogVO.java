package com.dtstack.batch.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author jiangbo
 */
@Data
public class BatchServerLogVO {

    private String name;
    private String logInfo;
    private Timestamp execStartTime;
    private Timestamp execEndTime;
    private Integer taskType = 0;
    private Integer computeType = 0;
    private SyncJobInfo syncJobInfo;
    private String downloadLog;
    private Map<String, String> subNodeDownloadLog;
    //经过几次任务重试
    private Integer pageSize;
    //当前页
    private Integer pageIndex;

    @Data
    public static class SyncJobInfo{

        private Integer readNum = 0;

        private Integer writeNum = 0;

        private Float dirtyPercent = 0.0F;

        private Long execTime = 0L;
    }

}
