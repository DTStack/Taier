package com.dtstack.engine.common.log;

import java.io.File;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月23日 下午1:27:16
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class LogbackComponent {

	private static String logback = System.getProperty("user.dir")+"/conf/logback.xml";

	public static void setupLogger() throws IOException, JoranException {
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

}
