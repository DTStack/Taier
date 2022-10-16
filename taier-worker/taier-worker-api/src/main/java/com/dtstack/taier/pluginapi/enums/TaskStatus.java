/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.pluginapi.enums;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public enum TaskStatus implements Serializable {
    //
    UNSUBMIT(0),
    //
    CREATED(1),
    //
    SCHEDULED(2),
    //
    RUNNING(4),
    //
    FINISHED(5),
    //
    CANCELLING(6),
    //
    CANCELED(7),
    //
    FAILED(8),
    //
    SUBMITFAILD(9),
    //
    SUBMITTING(10),
    //
    RESTARTING(11),
    //
    MANUALSUCCESS(12),
    //
    KILLED(13),
    //
    SUBMITTED(14),
    //
    NOTFOUND(15),
    //
    WAITENGINE(16),
    //
    WAITCOMPUTE(17),
    //
    FROZEN(18),
    //
    ENGINEACCEPTED(19),
    //
    PARENTFAILED(21),

    COMPUTING(23),

    LACKING(25),

    AUTOCANCELED(26),
    ;

    private static final Logger logger = LoggerFactory.getLogger(TaskStatus.class);

    private static final long serialVersionUID = 1L;

    private final int status;


    public final static List<Integer> STOPPED_STATUS = Lists.newArrayList(
            MANUALSUCCESS.getStatus(),
            PARENTFAILED.getStatus(),
            FAILED.getStatus(),
            CANCELED.getStatus(),
            SUBMITFAILD.getStatus(),
            KILLED.getStatus(),
            FINISHED.getStatus(),
            FROZEN.getStatus(),
            AUTOCANCELED.getStatus()
    );

    TaskStatus(int status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    /**
     * Exception cannot be caught. Conversion is required
     *
     * @param taskStatus
     * @return
     */
    public static TaskStatus getTaskStatus(String taskStatus) {

        if (Strings.isNullOrEmpty(taskStatus)) {
            return null;
        } else if ("ERROR".equalsIgnoreCase(taskStatus)) {
            return TaskStatus.FAILED;
        } else if ("RESTARTING".equalsIgnoreCase(taskStatus)) {
            //yarn做重试认为运行中
            return TaskStatus.RUNNING;
        } else if ("INITIALIZING".equalsIgnoreCase(taskStatus)) {
            return TaskStatus.SCHEDULED;
        } else if ("SUSPENDED".equalsIgnoreCase(taskStatus)) {
            return TaskStatus.FINISHED;
        } else if ("RECONCILING".equalsIgnoreCase(taskStatus)) {
            return TaskStatus.WAITENGINE;
        }

        try {
            return TaskStatus.valueOf(taskStatus);
        } catch (Exception e) {
            logger.info("No enum constant :" + taskStatus);
            return null;
        }
    }

    public static TaskStatus getTaskStatus(int status) {
        for (TaskStatus tmp : TaskStatus.values()) {
            if (tmp.getStatus() == status) {
                return tmp;
            }
        }

        return null;
    }

    public static boolean needClean(Integer status) {

        if (STOPPED_STATUS.contains(status) || TaskStatus.RESTARTING.getStatus().equals(status)) {
            return true;
        }
        return false;
    }

    public static boolean canStart(Integer status) {
        if (TaskStatus.SUBMITTING.getStatus().equals(status) || TaskStatus.UNSUBMIT.getStatus().equals(status)) {
            return true;
        }

        return false;
    }

    public static boolean canReset(Integer currStatus) {
        return STOPPED_STATUS.contains(currStatus) || TaskStatus.UNSUBMIT.getStatus().equals(currStatus);

    }

    public static boolean isStopped(Integer status) {
        return STOPPED_STATUS.contains(status);
    }


    public static List<Integer> getStoppedStatus() {
        return STOPPED_STATUS;
    }

    public static List<Integer> getWaitStatus() {
        return WAIT_STATUS;
    }

    public final static List<Integer> UNSUBMIT_STATUS = Lists.newArrayList(UNSUBMIT.getStatus());
    public final static List<Integer> RUNNING_STATUS = Lists.newArrayList(RUNNING.getStatus(), NOTFOUND.getStatus());
    public final static List<Integer> FINISH_STATUS = Lists.newArrayList(FINISHED.getStatus(), MANUALSUCCESS.getStatus());
    public final static List<Integer> FAILED_STATUS = Lists.newArrayList(FAILED.getStatus(), SUBMITFAILD.getStatus());
    public final static List<Integer> SUBMITFAILD_STATUS = Lists.newArrayList(SUBMITFAILD.getStatus());
    public final static List<Integer> PARENTFAILED_STATUS = Lists.newArrayList(PARENTFAILED.getStatus());
    public final static List<Integer> RUN_FAILED_STATUS = Lists.newArrayList(FAILED.getStatus());
    public final static List<Integer> WAIT_STATUS = Lists.newArrayList(WAITENGINE.getStatus(), WAITCOMPUTE.getStatus(),
            RESTARTING.getStatus(), SUBMITTED.getStatus(), ENGINEACCEPTED.getStatus(), SCHEDULED.getStatus(), CREATED.getStatus(), COMPUTING.getStatus(), LACKING.getStatus());
    public final static List<Integer> SUBMITTING_STATUS = Lists.newArrayList(SUBMITTING.getStatus());

    public final static List<Integer> STOP_STATUS = Lists.newArrayList(KILLED.getStatus(), CANCELED.getStatus(), AUTOCANCELED.getStatus());
    ;
    public final static List<Integer> FROZEN_STATUS = Lists.newArrayList(FROZEN.getStatus());

    public static String getCode(Integer status) {
        String key = null;
        if (FINISH_STATUS.contains(status)) {
            key = FINISHED.name();
        } else if (RUNNING_STATUS.contains(status)) {
            key = RUNNING.name();
        } else if (PARENTFAILED_STATUS.contains(status)) {
            key = PARENTFAILED.name();
        } else if (SUBMITFAILD_STATUS.contains(status)) {
            key = SUBMITFAILD.name();
        } else if (RUN_FAILED_STATUS.contains(status)) {
            key = FAILED.name();
        } else if (UNSUBMIT_STATUS.contains(status)) {
            key = UNSUBMIT.name();
        } else if (WAIT_STATUS.contains(status)) {
            key = WAITENGINE.name();
        } else if (SUBMITTING_STATUS.contains(status)) {
            key = SUBMITTING.name();
        } else if (STOP_STATUS.contains(status)) {
            key = CANCELED.name();
        } else if (FROZEN_STATUS.contains(status)) {
            key = FROZEN.name();
        } else {
            key = UNSUBMIT.name();
        }
        return key;
    }


    private final static List<Integer> UNFINISHED_STATUSES = Lists.newArrayList(
            RUNNING.getStatus(),
            UNSUBMIT.getStatus(),
            NOTFOUND.getStatus(),
            RESTARTING.getStatus(),
            SUBMITTING.getStatus());


    private final static List<Integer> UN_SUBMIT_STATUSES = Lists.newArrayList(
            ENGINEACCEPTED.getStatus(),
            UNSUBMIT.getStatus());

    static {
        UNFINISHED_STATUSES.addAll(WAIT_STATUS);
    }


    /**
     * 可以运行的状态
     */
    public final static List<Integer> CAN_RUN_STATUS = Lists.newArrayList(
            TaskStatus.UNSUBMIT.getStatus(),
            TaskStatus.FAILED.getStatus(),
            TaskStatus.FINISHED.getStatus(),
            TaskStatus.CANCELED.getStatus(),
            TaskStatus.KILLED.getStatus(),
            TaskStatus.SUBMITFAILD.getStatus()
    );

    /**
     * Incomplete job
     */
    public static List<Integer> getUnfinishedStatuses() {
        return UNFINISHED_STATUSES;
    }

    public static List<Integer> getUnSubmitStatus() {
        return UN_SUBMIT_STATUSES;
    }


    private final static Map<Integer, List<Integer>> STATUS_FAILED_DETAIL = new HashMap<>();

    static {
        STATUS_FAILED_DETAIL.put(UNSUBMIT.getStatus(), Lists.newArrayList(UNSUBMIT.getStatus()));
        STATUS_FAILED_DETAIL.put(RUNNING.getStatus(), Lists.newArrayList(RUNNING.getStatus(), NOTFOUND.getStatus()));
        STATUS_FAILED_DETAIL.put(FINISHED.getStatus(), FINISH_STATUS);
        STATUS_FAILED_DETAIL.put(FAILED.getStatus(), Lists.newArrayList(FAILED.getStatus()));
        STATUS_FAILED_DETAIL.put(SUBMITFAILD.getStatus(), Lists.newArrayList(SUBMITFAILD.getStatus()));
        STATUS_FAILED_DETAIL.put(PARENTFAILED.getStatus(), Lists.newArrayList(PARENTFAILED.getStatus()));
        STATUS_FAILED_DETAIL.put(WAITENGINE.getStatus(), WAIT_STATUS);
        STATUS_FAILED_DETAIL.put(SUBMITTING.getStatus(), Lists.newArrayList(SUBMITTING.getStatus()));
        STATUS_FAILED_DETAIL.put(CANCELED.getStatus(), Lists.newArrayList(KILLED.getStatus(), CANCELED.getStatus(), AUTOCANCELED.getStatus()));
        STATUS_FAILED_DETAIL.put(FROZEN.getStatus(), Lists.newArrayList(FROZEN.getStatus()));

    }

    private final static Map<Integer, List<Integer>> STATUS_FAILED_DETAIL_EXPIRE = new HashMap<>();

    static {
        STATUS_FAILED_DETAIL_EXPIRE.putAll(STATUS_FAILED_DETAIL);
    }


    public static Map<Integer, List<Integer>> getStatusFailedDetail() {
        return STATUS_FAILED_DETAIL;
    }

    public static Map<Integer, List<Integer>> getStatusFailedDetailAndExpire() {
        return STATUS_FAILED_DETAIL_EXPIRE;
    }

    public static int getShowStatus(Integer status) {
        if (FAILED_STATUS.contains(status)) {
            status = FAILED.getStatus();
        } else if (PARENTFAILED_STATUS.contains(status)) {
            status = PARENTFAILED.getStatus();
        } else {
            status = getShowStatusWithoutStop(status);
        }
        return status;
    }

    /**
     * Merge the status of process refinement into
     * completed, running, waiting for submission, waiting for operation, submitting, canceling and freezing
     * There is no need to merge the stop status (users need to view the stop status directly)
     *
     * @param status
     * @return
     */
    public static int getShowStatusWithoutStop(Integer status) {
        if (FINISH_STATUS.contains(status)) {
            status = FINISHED.getStatus();
        } else if (RUNNING_STATUS.contains(status)) {
            status = RUNNING.getStatus();
        } else if (UNSUBMIT_STATUS.contains(status)) {
            status = UNSUBMIT.getStatus();
        } else if (WAIT_STATUS.contains(status)) {
            status = WAITENGINE.getStatus();
        } else if (SUBMITTING_STATUS.contains(status)) {
            status = SUBMITTING.getStatus();
        } else if (STOP_STATUS.contains(status)) {
            status = CANCELED.getStatus();
        } else if (FROZEN_STATUS.contains(status)) {
            status = FROZEN.getStatus();
        }
        return status;
    }

    public static List<Integer> getStoppedAndNotFound() {
        List<Integer> status = new ArrayList<>(STOPPED_STATUS);
        status.add(NOTFOUND.getStatus());
        return status;
    }
}
