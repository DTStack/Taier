
package com.dtstack.taier.datasource.plugin.hdfs.util;

import java.util.ArrayList;
import java.util.List;

/**
 * string 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午5:23 2021/7/26
 * company: www.dtstack.com
 */
public class StringUtil {

    /**
     * 按指定切割符进行切割字符串并忽略括号/单引号/双引号中的切割符
     *
     * @param str       字符串
     * @param delimiter 切割符
     * @return 切割后的字符串集合
     */
    public static List<String> splitIgnoreQuota(String str, char delimiter) {
        List<String> resultList = new ArrayList<>();
        boolean inQuotes = false;
        boolean inSingleQuotes = false;
        int bracketLeftNum = 0;
        StringBuilder b = new StringBuilder();
        char[] chars = str.toCharArray();
        int idx = 0;
        for (char c : chars) {
            char flag = 0;
            if (idx > 0) {
                flag = chars[idx - 1];
            }
            if (c == delimiter) {
                if (inQuotes) {
                    b.append(c);
                } else if (inSingleQuotes) {
                    b.append(c);
                } else if (bracketLeftNum > 0) {
                    b.append(c);
                } else {
                    resultList.add(b.toString());
                    b = new StringBuilder();
                }
            } else if (c == '\"' && '\\' != flag && !inSingleQuotes) {
                inQuotes = !inQuotes;
                b.append(c);
            } else if (c == '\'' && '\\' != flag && !inQuotes) {
                inSingleQuotes = !inSingleQuotes;
                b.append(c);
            } else if (c == '(' && !inSingleQuotes && !inQuotes) {
                bracketLeftNum++;
                b.append(c);
            } else if (c == ')' && !inSingleQuotes && !inQuotes) {
                bracketLeftNum--;
                b.append(c);
            } else {
                b.append(c);
            }
            idx++;
        }
        resultList.add(b.toString());
        return resultList;
    }
}
