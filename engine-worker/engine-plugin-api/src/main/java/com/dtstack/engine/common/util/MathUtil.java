package com.dtstack.engine.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 数字转换
 * Date: 2017/4/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MathUtil {

    public static Long getLongVal(Object obj){
        if(obj == null){
            return null;
        }

        if(obj instanceof String){
            return Long.valueOf(((String) obj).trim());
        }else if(obj instanceof Long){
            return (Long) obj;
        }else if(obj instanceof Integer){
            return Long.valueOf(obj.toString());
        }

        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Long." );
    }

    public static Long getLongVal(Object obj, Long defaultVal){
        if(obj == null){
            return defaultVal;
        }

        return getLongVal(obj);
    }


    public static Integer getIntegerVal(Object obj){
        if(obj == null){
            return null;
        }

        if(obj instanceof String){
            if(StringUtils.isBlank((String)obj)){
                return null;
            }
            return Integer.valueOf(((String) obj).trim());
        }else if(obj instanceof Integer){
            return (Integer) obj;
        }else if(obj instanceof Long){
            return ((Long)obj).intValue();
        }

        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Integer." );
    }

    public static Integer getIntegerVal(Object obj, Integer defaultVal){
        if(obj == null){
            return defaultVal;
        }

        return getIntegerVal(obj);

    }

    public static Double getDoubleVal(Object obj){
        if(obj == null){
            return null;
        }

        if(obj instanceof String){
            return Double.valueOf(((String) obj).trim());
        }else if(obj instanceof Double){
            return (Double) obj;
        }

        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Integer." );
    }

    public static Boolean getBoolean(Object obj){
        if(obj == null){
            return null;
        }

        if(obj instanceof String){
            return Boolean.valueOf((String) obj);
        }else if(obj instanceof Boolean){
            return (Boolean) obj;
        }

        throw new RuntimeException("not support type of " + obj.getClass() + " convert to Boolean." );
    }

    public static Boolean getBoolean(Object obj, Boolean defaultVal){
        if(obj == null){
            return defaultVal;
        }

        return getBoolean(obj);
    }

    public static String getString(Object obj){
        if(obj == null){
            return null;
        }

        if(obj instanceof String){
            return (String) obj;
        }else{
            return obj.toString();
        }

    }

    public static String getString(Object obj, String defaultVal){
        if(obj == null){
            return defaultVal;
        }

        return getString(obj);
    }

    public static void main(String[] args) {
        Boolean target = true;
        System.out.println(MathUtil.getBoolean(target, false));
    }
}
