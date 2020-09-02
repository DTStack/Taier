package com.dtstack.engine.master;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.IOException;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/07/08
 */
public class LogbackComponent implements ApplicationListener {

    private static Logger LOGGER = LoggerFactory.getLogger(LogbackComponent.class);

    private static String logback = System.getProperty("user.dir") + "/conf/logback.xml";

    private void setupLogger() throws IOException, JoranException {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        File externalConfigFile = new File(logback);

        if (!externalConfigFile.exists()) {

            throw new IOException("Logback External Config File Parameter does not reference a file that exists");

        } else {

            if (!externalConfigFile.isFile()) {
                throw new IOException("Logback External Config File Parameter exists, but does not reference a file");

            } else {

                if (!externalConfigFile.canRead()) {
                    throw new IOException("Logback External Config File exists and is a file, but cannot be read.");

                } else {
                    JoranConfigurator configurator = new JoranConfigurator();
                    configurator.setContext(lc);
                    lc.reset();
                    configurator.doConfigure(logback);
                    StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
                }

            }
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        try {
            setupLogger();
        } catch (Throwable e) {
            LOGGER.error("{}", e);
        }
    }
}
