package com.dtstack.rdos.commom.exception;

import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.PrintWriter;
import org.slf4j.Logger;
/**
 * 
 * Reason: TODO ADD REASON(可选)
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
    public static String getTaskLogError(){
        return "{\"engineLogErr\":\"获取任务日志异常\"}";
    }

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
