package com.dtstack.engine.flink.base.enums;

import com.dtstack.engine.pluginapi.exception.RdosDefineException;

/**
 * Created by sishu.yss on 2018/3/9.
 */
public enum ClusterMode {

    // job executed in the per job
    PER_JOB,

    // job executed in the session
    SESSION,

    // job executed in the standalone
    STANDALONE;

    public static ClusterMode getClusteMode(String clusterMode) {
        if (SESSION.name().equalsIgnoreCase(clusterMode)) {
            return SESSION;
        } else if (PER_JOB.name().equalsIgnoreCase(clusterMode) || PER_JOB.name().replace("_", "").equalsIgnoreCase(clusterMode)) {
            return PER_JOB;
        } else if (STANDALONE.name().equalsIgnoreCase(clusterMode)) {
            return STANDALONE;
        }

        throw new RdosDefineException("not support clusterMode: " + clusterMode);
    }

    /**
     * check if it's perjob mode
     * @param clusterMode clusterMode
     * @return true if it's perjob mode
     */
    public static boolean isPerjob(ClusterMode clusterMode){
        return ClusterMode.PER_JOB.equals(clusterMode);
    }

    /**
     * check if it's session mode
     * @param clusterMode clusterMode
     * @return true if it's session mode
     */
    public static boolean isSession(ClusterMode clusterMode){
        return ClusterMode.SESSION.equals(clusterMode);
    }

    /**
     * check if it's standalone mode
     * @param clusterMode clusterMode
     * @return true if it's standalone mode
     */
    public static boolean isStandalone(ClusterMode clusterMode){
        return ClusterMode.STANDALONE.equals(clusterMode);
    }

}
