package com.dtstack.taier;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;


public class ProcessUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtil.class);

    private static final String KILL_COMMAND =  "kill -9 %d";

    public static Integer getProcessId(Process process) {
        Integer pid = -1;
        Field field;
        try {
            if (ProcessUtil.isWindows()) {
                field = process.getClass().getDeclaredField("handle");
                field.setAccessible(true);
                long handle = field.getLong(process);
                Kernel32 kernel = Kernel32.INSTANCE;
                WinNT.HANDLE winntHandle = new WinNT.HANDLE();
                winntHandle.setPointer(Pointer.createConstant(handle));
                pid = kernel.GetProcessId(winntHandle);
            } else {
                Class<?> clazz = Class.forName("java.lang.UNIXProcess");
                field = clazz.getDeclaredField("pid");
                field.setAccessible(true);
                pid = (Integer) field.get(process);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return pid;
    }

    public static void killProcess(Integer processId) throws IOException {
        if (processId != 0) {
            String cmd = String.format(KILL_COMMAND, processId);
            LOGGER.info("kill process command {}", cmd);
            Runtime.getRuntime().exec(cmd);
        }
    }

    public static Boolean isWindows() {
       return System.getProperty("os.name").startsWith("Windows");
    }

}
