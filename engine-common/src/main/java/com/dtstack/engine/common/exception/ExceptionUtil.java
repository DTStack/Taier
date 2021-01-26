package com.dtstack.engine.common.exception;

import com.dtstack.engine.common.io.UnsafeStringWriter;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

import org.slf4j.Logger;

/**
 * Date: 2016年11月31日 下午1:26:07
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class ExceptionUtil {

    private static Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);

    /**
     * 获取错误的堆栈信息
     *
     * @param e throwable
     * @return 堆栈信息
     */
    public static String getErrorMessage(Throwable e) {
        UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    public static String stackTrack() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        StringBuffer mBuffer = new StringBuffer();
        mBuffer.append(System.getProperty("line.separator"));

        for (StackTraceElement e : st) {
            if (mBuffer.length() > 0) {
                mBuffer.append("  ");
                mBuffer.append(System.getProperty("line.separator"));
            }
            mBuffer.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , e.getClassName()
                    , e.getMethodName()
                    , e.getLineNumber()));
        }
        return mBuffer.toString();
    }
}
