package com.dtstack.taier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
     * 日志打印
     */
    private StringBuilder logBuilder = new StringBuilder();

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

    public ScriptJob() {}

    @Override
    public void run() {
        execStartTime = System.currentTimeMillis();
        try {
            process = Runtime.getRuntime().exec(command);
            processId = ProcessUtil.getProcessId(process);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            if ((line = reader.readLine()) != null) {
                logBuilder.append(line);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine;
            if ((errorLine = errorReader.readLine()) != null) {
                logBuilder.append(errorLine);
            }

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

    public StringBuilder getLogBuilder() {
        return logBuilder;
    }

    public Long getExecEndTime() {
        return execEndTime;
    }
}
