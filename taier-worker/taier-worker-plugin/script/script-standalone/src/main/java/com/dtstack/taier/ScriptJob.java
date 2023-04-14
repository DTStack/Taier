package com.dtstack.taier;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptJob implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptJob.class);

    // sh aaa.sh > /a/b/c.log  2>&1
    private final String LINUX_SHELL_COMMAND = " %s > %s 2>&1";

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
     * shell 运行参数
     */
    private String shellParams;

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

    private String shellCommand;

    private String shellFullCommand;

    private String shellLogPath;


    public ScriptJob(String jobId, String shellParams) {
        this.jobId = jobId;
        this.shellParams = shellParams;
        JSONObject shellParamObj = JSONObject.parseObject(shellParams);
        this.shellCommand = shellParamObj.getString("shellCommand");
        this.shellLogPath = shellParamObj.getString("shellLogPath");
    }

    public ScriptJob() {
    }

    @Override
    public void run() {
        execStartTime = System.currentTimeMillis();
        try {
            if (ProcessUtil.isWindows()) {
                process = Runtime.getRuntime().exec(new String[]{"bash", "-c", shellFullCommand});
            } else {
                shellFullCommand = String.format(LINUX_SHELL_COMMAND, shellCommand, shellLogPath);
                process = Runtime.getRuntime().exec(new String[]{"sh", "-c", shellFullCommand});
            }
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
            LOGGER.info("jobId: {} script job start end", jobId);
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

    public String getShellFullCommand() {
        return shellFullCommand;
    }

    public String getShellLogPath() {
        return shellLogPath;
    }

}
