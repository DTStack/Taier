package com.dtstack.engine.common.util;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Date: 2016年11月31日 下午1:26:07
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ExceptionUtil {

    private static Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);

    /**
     * 获取日志信息异常统一返回信息
     * @return
     */
    public static String getTaskLogError(Throwable e){
        Map<String, String> map = new HashMap<>(4);
        map.put("engineLogErr", getErrorMessage(e));
        return new Gson().toJson(map);
    }

    /**
     * 获取错误的堆栈信息
     * @param e throwable
     * @return 堆栈信息
     */
    public static String getErrorMessage(Throwable e) {
        StringWriter stringWriter = null;
        PrintWriter writer = null;
        try{
                stringWriter= new StringWriter();
                writer = new PrintWriter(stringWriter);
                e.printStackTrace(writer);
                writer.flush();
                stringWriter.flush();
                StringBuffer buffer= stringWriter.getBuffer();
                String result = buffer.toString();
                return result;
        }catch(Throwable ee){
                logger.error("",ee);

        }finally {
                if(writer!=null){
                        writer.close();
                }
                if(stringWriter!=null){
                        try{
                                stringWriter.close();
                        }catch (Throwable ee){
                                logger.error("",ee);
                        }
                }
        }
        return null;
    }
}
