package com.dtstack.engine.common;

import com.google.common.base.Preconditions;

/**
 * TODO 改造
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
