package com.dtstack.taier.flink.constant;

/**
 * @program: engine-all
 * @author: wuren
 * @create: 2021/02/18
 **/
// TODO 这个代码未来要抽到统一的Flink base模块中
public class ErrorMessageConstant {

    public static String WAIT_SESSION_RECOVER = "Flink session cluster is unhealthy, waiting to reboot cluster.";

    public final static String FLINK_GET_LOG_ERROR_UNDO_RESTART_EXCEPTION = "Failed to get the task log";

    public final static String FLINK_UNALE_TO_GET_CLUSTERCLIENT_STATUS_EXCEPTION = "Unable to get ClusterClient status from Application Client";

}
