package com.dtstack.engine.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiangbo
 * @explanation
 * @date 2018/11/22
 */
public class NameUtil {

    private static final String DELIM = "_";

    private static final String NAME_SUFFIX = "_copy";

    private static final String SUFFIX_REGEX = ".*_copy((_(?<copyNum>\\d+))*)$";

    private static final Pattern SUFFIX_PATTERN = Pattern.compile(SUFFIX_REGEX);

    public static String getCopyName(String srcName){
        String distName = null;

        if (srcName.matches(SUFFIX_REGEX)){
            Matcher matcher = SUFFIX_PATTERN.matcher(srcName);
            if (matcher.find()){
                String copyNum = matcher.group("copyNum");
                if(copyNum == null){
                    distName = srcName + DELIM + "1";
                } else {
                    copyNum = String.valueOf(Integer.valueOf(copyNum) + 1);
                    srcName = srcName.substring(0,srcName.lastIndexOf(DELIM));
                    distName = srcName + DELIM + copyNum;
                }
            }
        } else {
            distName = srcName + NAME_SUFFIX;
        }

        return distName;
    }
}
