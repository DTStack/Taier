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

package com.dtstack.taier.pluginapi;

import com.google.common.base.Preconditions;

/**
 * Reason:
 * Date: 2018/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobParam {

    private String jarPath;

    private String mainClass;

    private String jobName;

    private String classArgs;

    /**
     * 用于mr类型的参数构造---只允许添加一个附件
     * @param jobClient
     */
    public JobParam(JobClient jobClient){

        JarFileInfo jarFileInfo = jobClient.getCoreJarInfo();
        Preconditions.checkNotNull(jarFileInfo, "submit need to add jar operator.");

        jarPath = jarFileInfo.getJarPath();
        mainClass = jarFileInfo.getMainClass();

        Preconditions.checkNotNull(jarPath, "submit need to add jar operator.");

        jobName = jobClient.getJobName();
        classArgs = jobClient.getClassArgs();
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getClassArgs() {
        return classArgs;
    }

    public void setClassArgs(String classArgs) {
        this.classArgs = classArgs;
    }
}
