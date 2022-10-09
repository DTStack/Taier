package com.dtstack.taier.datasource.api.dto.yarn;

/**
 * yarn 任务状态
 *
 * @author ：wangchuan
 * date：Created in 下午2:53 2022/3/17
 * company: www.dtstack.com
 */
public enum YarnApplicationStatus {

    /**
     * Application which was just created.
     */
    NEW,

    /**
     * Application which is being saved.
     */
    NEW_SAVING,

    /**
     * Application which has been submitted.
     */
    SUBMITTED,

    /**
     * Application has been accepted by the scheduler
     */
    ACCEPTED,

    /**
     * Application which is currently running.
     */
    RUNNING,

    /**
     * Application which finished successfully.
     */
    FINISHED,

    /**
     * Application which failed.
     */
    FAILED,

    /**
     * Application which was terminated by a user or admin.
     */
    KILLED
}
