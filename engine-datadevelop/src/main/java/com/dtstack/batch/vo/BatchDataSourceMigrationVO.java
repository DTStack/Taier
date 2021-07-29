package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchDataSourceMigration;
import com.dtstack.batch.domain.BatchDataSourceMigrationTask;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/6/6
 */
@Slf4j
@Data
public class BatchDataSourceMigrationVO extends BatchDataSourceMigration {

    public static BatchDataSourceMigrationVO toVO(BatchDataSourceMigration origin) {
        BatchDataSourceMigrationVO vo = new BatchDataSourceMigrationVO();
        try {
            BeanUtils.copyProperties(origin, vo);
        } catch (Exception e) {
            log.error("", e);
        }
        return vo;
    }

    private List<TransformField> transformFields;

    private List<String> tables;

    private List<BatchDataSourceMigrationTask> migrationTasks;

    private String createUserName;

    private String gmtCreateFormat;

    private Integer taskCount;

    private ParallelConfig parallelConfig;

    public static class ParallelConfig {
        private Integer hourTime;
        //源字段
        private Integer tableNum;

        public Integer getHourTime() {
            return hourTime;
        }

        public void setHourTime(Integer hourTime) {
            this.hourTime = hourTime;
        }

        public Integer getTableNum() {
            return tableNum;
        }

        public void setTableNum(Integer tableNum) {
            this.tableNum = tableNum;
        }
    }

    public static class TransformField {

        /**
         * 目标字段
         */
        private String convertDest;
        /**
         * 源字段
         */
        private String convertSrc;
        /**
         * 转换类型（1：表名，2：字段名，3：字段类型）
         */
        private Integer convertType;
        /**
         * 转换方式（1：字符替换，2：添加前缀，3：添加后缀）
         */
        private Integer convertObject;

        public String getConvertDest() {
            return convertDest;
        }

        public void setConvertDest(String convertDest) {
            this.convertDest = convertDest;
        }

        public String getConvertSrc() {
            return convertSrc;
        }

        public void setConvertSrc(String convertSrc) {
            this.convertSrc = convertSrc;
        }

        public Integer getConvertObject() {
            return convertObject;
        }

        public void setConvertObject(Integer convertObject) {
            this.convertObject = convertObject;
        }

        public Integer getConvertType() {
            return convertType;
        }

        public void setConvertType(Integer convertType) {
            this.convertType = convertType;
        }
    }



    public static TaskResult createTaskResult(Integer status, String report) {
        TaskResult result = new TaskResult();
        result.setReport(report);
        result.setStatus(status);
        return result;
    }

    public static class TaskResult {
        private Integer status;
        private String report;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getReport() {
            return report;
        }

        public void setReport(String report) {
            this.report = report;
        }
    }
}
