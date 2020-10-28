package com.dtstack.engine.dtscript.util;


import com.dtstack.engine.common.exception.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DebugUtil.class);

    public static void pause() {
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            LOG.error("DebugUtil.pause error:{}", ExceptionUtil.getErrorMessage(e));
        }
    }

    public static String stackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

}
