package com.dtstack.taier.scheduler.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ScriptUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptUtil.class);

    private static final String WINDOWS_SHELL_PREFIX =
            "@echo off \n" +
                    "cd /d %~dp0\n";

    private static final String LINUX_SHELL_PREFIX =
            "#!/bin/sh\n" +
                    "BASEDIR=$(cd `dirname $0`; pwd)\n" +
                    "cd $BASEDIR\n";

    private static final String PYTHON_PREFIX = "# coding=utf8";

    // python3 /a/b/b/python_failure.py  > /a/b/c.log 2>&1
    private static final String SHELL_COMMAND = "%s %s";

    private static final String WINDOWS_EXECUTE = "cmd";

    private static final String LINUX_EXECUTE = "sh";

    /**
     * 构建shell脚本运行脚本命令
     *
     * @param commandPath shell脚本存储文件绝对路径
     * @param command     shell命令
     * @return
     * @throws IOException
     */
    public static String buildShellCommand(String commandPath, String command) throws IOException {
        String fullCommand = String.format("%s\n%s\n", ProcessUtils.isWindows() ? WINDOWS_SHELL_PREFIX : LINUX_SHELL_PREFIX, command);
        LOGGER.info("build full shell command is : {}", fullCommand);
        FileUtils.writeStringToFile(new File(commandPath), fullCommand, StandardCharsets.UTF_8);
        return String.format(SHELL_COMMAND, ProcessUtils.isWindows() ? WINDOWS_EXECUTE : LINUX_EXECUTE, commandPath);
    }

    /**
     * 构建python脚本运行脚本命令
     *
     * @param commandPath   python脚本存储文件绝对路径
     * @param command       python命令
     * @param pythonBinPath python命令所在路径
     * @return
     * @throws IOException
     */
    public static String buildPythonCommand(String commandPath, String command, String pythonBinPath) throws IOException {
        String fullCommand = String.format("%s\n%s\n", PYTHON_PREFIX, command);
        LOGGER.info("build full python command is : {}", fullCommand);
        FileUtils.writeStringToFile(new File(commandPath), fullCommand, StandardCharsets.UTF_8);
        return String.format(SHELL_COMMAND, pythonBinPath, commandPath);
    }

}
