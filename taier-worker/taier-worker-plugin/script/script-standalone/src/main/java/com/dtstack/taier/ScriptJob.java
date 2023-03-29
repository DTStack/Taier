package com.dtstack.taier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptJob implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptJob.class);

    private String jobId;

    /**
     * 进程执行状态
     */
    private Boolean status;

    /**
     * 进程ID
     */
    private Integer processId;

    /**
     * shell 运行命令
     */
    private String command;

    /**
     * 进程
     */
    private Process process;

    /**
     * 开始时间
     */
    private Long execStartTime;

    /**
     * 结束时间
     */
    private Long execEndTime;


    public ScriptJob(String jobId, String command) {
        this.jobId = jobId;
        this.command = command;
    }

    public ScriptJob() {
    }

    @Override
    public void run() {
        execStartTime = System.currentTimeMillis();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            processId = ProcessUtil.getProcessId(process);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                status = false;
                return;
            }
            status = true;
        } catch (Exception e) {
            LOGGER.error("jobId: {} script job start failure", jobId, e);
            status = false;
        } finally {
            execEndTime = System.currentTimeMillis();
        }
    }

    public Boolean getStatus() {
        return status;
    }

    public Integer getProcessId() {
        return processId;
    }

    public Long getExecEndTime() {
        return execEndTime;
    }
}
