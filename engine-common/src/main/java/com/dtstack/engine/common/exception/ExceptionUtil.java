package com.dtstack.engine.common.exception;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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
     * 获取日志信息异常统一返回信息
     *
     * @return
     */
    public static String getTaskLogError(Throwable e) {
        Map<String, String> map = new HashMap<>(4);
        map.put("engineLogErr", getErrorMessage(e));
        return JSONObject.toJSONString(map);
    }

    /**
     * 获取错误的堆栈信息
     *
     * @param e throwable
     * @return 堆栈信息
     */
    public static String getErrorMessage(Throwable e) {
        StringWriter stringWriter = null;
        PrintWriter writer = null;
        try {
            stringWriter = new StringWriter();
            writer = new PrintWriter(stringWriter);
            e.printStackTrace(writer);
            writer.flush();
            stringWriter.flush();
            StringBuffer buffer = stringWriter.getBuffer();
            String result = buffer.toString();
            return result;
        } catch (Throwable ee) {
            logger.error("", ee);

        } finally {
            if (writer != null) {
                writer.close();
            }
            if (stringWriter != null) {
                try {
                    stringWriter.close();
                } catch (Throwable ee) {
                    logger.error("", ee);
                }
            }
        }
        return "";
    }

    public static String stackTrack() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        if (st == null) {
            return null;
        }
        StringBuffer sbf = new StringBuffer();
        sbf.append(System.getProperty("line.separator"));

        for (StackTraceElement e : st) {
            if (sbf.length() > 0) {
                sbf.append(" <- ");
                sbf.append(System.getProperty("line.separator"));
            }
            sbf.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , e.getClassName()
                    , e.getMethodName()
                    , e.getLineNumber()));
        }
        return sbf.toString();
    }
}
