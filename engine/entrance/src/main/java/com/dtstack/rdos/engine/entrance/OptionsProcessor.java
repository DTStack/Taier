package com.dtstack.rdos.engine.entrance;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月08日 下午09:24:26
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class OptionsProcessor {

	public class Option {
		String flag, opt;

		public Option(String flag, String opt) {
			this.flag = flag;
			
			this.opt = opt;
		}
	}

   public static CommandLine parseArg(String[] args) throws ParseException {
        Options options = new Options();
		options.addOption("h", false, "usage help");
		options.addOption("help", false, "usage help");
		options.addOption("f", true, "configuration file");
		options.addOption("l", true, "log file");
		options.addOption("v", false, "print error log");
		options.addOption("vv", false, "print warn log");
		options.addOption("vvv", false, "print info log");
		options.addOption("vvvv", false, "print debug log");
		options.addOption("vvvvv", false, "print trace log");
		CommandLineParser paraer = new BasicParser();
		CommandLine cmdLine = paraer.parse(options, args);
		if (cmdLine.hasOption("help") || cmdLine.hasOption("h")) {
			usage();
			System.exit(-1);
		}

		if (!cmdLine.hasOption("f")) {
			throw new ParseException("Required -f argument to specify config file");
		}
		return cmdLine;
	}

	/**
	 * print help information
	 */
	private static void usage() {
		StringBuilder helpInfo = new StringBuilder();
		helpInfo.append("-h").append("\t\t\thelp command").append("\n")
				.append("-help").append("\t\t\thelp command").append("\n")
				.append("-f").append("\t\t\trequired config, indicate config file").append("\n")
				.append("-l").append("\t\t\tlog file that store the output").append("\n")
				.append("v").append("\t\t\tprint error log").append("\n")
				.append("vv").append("\t\t\tprint warn log").append("\n")
				.append("vvv").append("\t\t\tprint info log").append("\n")
				.append("vvvv").append("\t\t\tprint debug log").append("\n")
				.append("vvvvv").append("\t\t\tprint trace log").append("\n");
		System.out.println(helpInfo.toString());
	}
}
