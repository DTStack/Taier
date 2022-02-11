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

package com.dtstack.taiga.flink.base.enums;

import com.dtstack.taiga.pluginapi.exception.PluginDefineException;

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

        throw new PluginDefineException("not support clusterMode: " + clusterMode);
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
