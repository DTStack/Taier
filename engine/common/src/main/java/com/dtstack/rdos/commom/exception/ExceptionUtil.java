package com.dtstack.rdos.commom.exception;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年11月31日 下午1:26:07
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ExceptionUtil {
	
    public static String getErrorMessage(Throwable e) {
            StringBuffer sb = new StringBuffer();
            Throwable next = e.getCause();
            if (next != null) {
                    sb.append(next.toString() + "\n");
                    StackTraceElement[] nexts = next.getStackTrace();
                    for (StackTraceElement st : nexts) {
                            sb.append(st.getClassName() + "." + st.getMethodName() + "("
                                            + st.getLineNumber() + ")" + "\n");
                    }
            } else {
                    sb.append(e.toString() + "\n");
                    StackTraceElement[] sts = e.getStackTrace();
                    if (sts != null) {
                            for (StackTraceElement st : sts) {
                                    sb.append(st.getClassName() + "." + st.getMethodName()
                                                    + "(" + st.getLineNumber() + ")" + "\n");
                            }
                    }
            }
            return sb.toString();
    }

}
