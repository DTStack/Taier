package com.dtstack.yarn.container;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ProcessLogCollector {

    private static final Log LOG = LogFactory.getLog(ProcessLogCollector.class);
    private final Process process;
    private final ExecutorService executorService;
    private final StringBuilder errorLog = new StringBuilder(1000);

    public ProcessLogCollector(Process process) {
        LOG.info("init");
        this.process = process;
        this.executorService = Executors.newFixedThreadPool(2);
    }

    private void logStream (InputStream inputStream, boolean error) {

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String stdoutLog;
                    while ((stdoutLog = reader.readLine()) != null) {
                        if(error) {
                            LOG.error(stdoutLog);
                            errorLog.append("\n").append(stdoutLog);
                        } else {
                            LOG.info(stdoutLog);
                        }
                    }
                } catch (Exception e) {
                    LOG.warn("Exception in thread stdoutRedirectThread");
                    e.printStackTrace();
                }
            }
        });
    }

    public void start() {
        LOG.info("start");
        logStream(process.getInputStream(), false);
        logStream(process.getErrorStream(), true);
    }

    public void stop() {
        executorService.shutdown();
    }

    public String getErrorLog() {
        return errorLog.toString();
    }
}
