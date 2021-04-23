package com.dtstack.engine.api.vo.schedule.job;

import java.util.List;

/**
 * @author xinge
 */
public class ScheduleJobRuleTimeVO {
    private String jobId;
    private List<RuleTimeVO> paramReplace;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public List<RuleTimeVO> getParamReplace() {
        return paramReplace;
    }

    public void setParamReplace(List<RuleTimeVO> paramReplace) {
        this.paramReplace = paramReplace;
    }

    @Override
    public String toString() {
        return "ScheduleJobRuleTimeVO{" +
                "jobId='" + jobId + '\'' +
                ", paramReplace=" + paramReplace +
                '}';
    }

    /**
     * @author xinge
     */
    public static class RuleTimeVO{
        private String paramName;
        private String paramValue;
        private Integer timeType;
        private Integer type;

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public String getParamValue() {
            return paramValue;
        }

        public void setParamValue(String paramValue) {
            this.paramValue = paramValue;
        }

        public Integer getTimeType() {
            return timeType;
        }

        public void setTimeType(Integer timeType) {
            this.timeType = timeType;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "RuleTimeVO{" +
                    "paramName='" + paramName + '\'' +
                    ", paramValue='" + paramValue + '\'' +
                    ", timeType=" + timeType +
                    ", type=" + type +
                    '}';
        }
    }

}
