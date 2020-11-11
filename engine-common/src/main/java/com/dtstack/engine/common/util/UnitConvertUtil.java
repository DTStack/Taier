package com.dtstack.engine.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 单位转换工具
 * Date: 2017/11/30
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class UnitConvertUtil {

    private static final Logger logger = LoggerFactory.getLogger(UnitConvertUtil.class);

    private static final Pattern capacityPattern = Pattern.compile("(\\d+)\\s*([a-zA-Z]{1,2})");

    /**
     * 暂时只做 kb, mb, gb转换
     * eg: 1g --> 1024
     *     1024k --> 1
     *     1mb --> 1
     * @param memStr
     * @return
     */
    public static Integer convert2Mb(String memStr){
        Matcher matcher = capacityPattern.matcher(memStr);
        if(matcher.find() && matcher.groupCount() == 2){
            String num = matcher.group(1);
            String unit = matcher.group(2).toLowerCase();
            if(unit.contains("g")){
                Double mbNum = MathUtil.getDoubleVal(num) * 1024;
                return mbNum.intValue();
            }else if(unit.contains("m")){
                return MathUtil.getDoubleVal(num).intValue();
            }else if(unit.contains("k")){
                Double mbNum = MathUtil.getDoubleVal(num) / 1024;
                return mbNum.intValue();
            }else{
                logger.error("can not convert memStr:" + memStr +", return default 512.");
            }
        }else{
            logger.error("can not convert memStr:" + memStr +", return default 512.");
        }

        return 512;
    }


    public static int getNormalizedMem(String rawMem) {
        if (rawMem.endsWith("G") || rawMem.endsWith("g")) {
            return Integer.parseInt(rawMem.substring(0, rawMem.length() - 1)) * 1024;
        } else if (rawMem.endsWith("M") || rawMem.endsWith("m")) {
            return Integer.parseInt(rawMem.substring(0, rawMem.length() - 1));
        } else {
            return Integer.parseInt(rawMem);
        }
    }
}
