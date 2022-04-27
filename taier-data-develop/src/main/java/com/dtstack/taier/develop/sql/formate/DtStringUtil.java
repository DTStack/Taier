package com.dtstack.taier.develop.sql.formate;


import com.dtstack.taier.develop.sql.Pair;

import java.util.ArrayList;
import java.util.List;

public class DtStringUtil {

    /**
     * 根据指定分隔符分割字符串---忽略在引号里面的分隔符
     *
     * @param str
     * @param delimiter
     * @return
     */
    public static List<String> splitIgnoreQuota(String str, char delimiter) {
        List<String> tokensList = new ArrayList<>();
        boolean inQuotes = false;
        boolean inSingleQuotes = false;
        StringBuilder b = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == delimiter) {
                if (inQuotes) {
                    b.append(c);
                } else if (inSingleQuotes) {
                    b.append(c);
                } else {
                    tokensList.add(b.toString());
                    b = new StringBuilder();
                }
            } else if (c == '\"') {
                inQuotes = !inQuotes;
                b.append(c);
            } else if (c == '\'') {
                inSingleQuotes = !inSingleQuotes;
                b.append(c);
            } else {
                b.append(c);
            }
        }

        tokensList.add(b.toString());

        return tokensList;
    }

    /**
     * 根据指定分隔符分割字符串---忽略在引号里面的分隔符
     *
     * @param str
     * @param delimter
     * @return
     * @Deprecated Reason : 针对200K大小的sql任务,会存在OOM的问题, http://redmine.prod.dtstack.cn/issues/22749
     */
    @Deprecated
    public static String[] splitIgnoreQuota(String str, String delimter) {
        String splitPatternStr = delimter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)";
        return str.split(splitPatternStr);
    }

    /***
     * 根据指定分隔符分割字符串---忽略在引号 和 括号 里面的分隔符
     * @param str
     * @param delimter
     * @return
     */
    public static String[] splitIgnoreQuotaBrackets(String str, String delimter) {
        String splitPatternStr = delimter + "(?![^()]*+\\))(?![^{}]*+})(?![^\\[\\]]*+\\])(?=(?:[^\"]|\"[^\"]*\")*$)";
        return str.split(splitPatternStr);
    }

    /**
     * 使用非正则表达式的方法来实现 `根据指定分隔符分割字符串---忽略在引号里面的分隔符`
     *
     * @param str
     * @param delimiter 分隔符
     * @return
     */
    public static String[] splitIgnoreQuotaNotUsingRegex(String str, String delimiter) {
        // trim
        str = str.trim();
        // 遍历出成对的双引号的位置区间,排除转义的双引号
        List<Pair<Integer, Integer>> doubleQuotas = getQuotaIndexPairs(str, '\"');
        // 遍历出成对的单引号的位置区间,排除转义的单引号
        List<Pair<Integer, Integer>> singleQuotas = getQuotaIndexPairs(str, '\'');

        // 遍历出所有的delimiter的位置,排除掉在上述两个区间中的,排除掉转义的,按该delimiter位置拆分字符串
        List<String> splitList = new ArrayList<>(128);
        // index 表示目前搜索指针下标
        // beforeIndex 表示目前已经成功匹配到的指针下标
        int index = 0, beforeIndex = -1;
        while ((index = str.indexOf(delimiter, Math.max(beforeIndex + 1, index))) != -1) {
            // 排除转义
            if (index == 0 || str.charAt(index - 1) != '\\') {
                boolean flag = false;
                // 排除双引号内的
                for (Pair<Integer, Integer> p : doubleQuotas) {
                    if (p.getKey() <= index && p.getValue() >= index) {
                        flag = true;
                        break;
                    }
                }
                // 排除单引号内的
                for (int i = 0; !flag && i < singleQuotas.size(); i++) {
                    Pair<Integer, Integer> p = singleQuotas.get(i);
                    if (p.getKey() <= index && p.getValue() >= index) {
                        flag = true;
                        break;
                    }
                }
                // flag = true, 表示该字符串在匹配的成对引号,跳过
                if (flag) {
                    index++;
                    continue;
                }
                // 这里的substring只取到分隔符的前一位,分隔符不加进来
                splitList.add(str.substring(beforeIndex + 1, index));
                beforeIndex = index;
            } else {
                index++;
            }
        }
        // 收尾串
        if (beforeIndex != str.length()) {
            splitList.add(str.substring(beforeIndex + 1, str.length()));
        }
        return splitList.toArray(new String[0]);
    }

    /**
     * 遍历出成对的双/单引号的位置区间,排除转义的双引号
     *
     * @param str
     * @param quotaChar
     * @return
     */
    private static List<Pair<Integer, Integer>> getQuotaIndexPairs(String str, char quotaChar) {
        List<Pair<Integer, Integer>> quotaPairs = new ArrayList<>(64);
        List<Integer> posList = new ArrayList<>(128);
        for (int idx = 0; idx < str.length(); idx++) {
            if (str.charAt(idx) == quotaChar) {
                if (idx == 0 || str.charAt(idx - 1) != '\\') {
                    posList.add(idx);
                }
            }
        }
        // 每两个装进Pair中,总数为奇数的话最后一个舍掉
        for (int idx = 0; idx <= posList.size() - 2; idx += 2) {
            quotaPairs.add(new Pair<>(posList.get(idx), posList.get(idx + 1)));
        }
        return quotaPairs;
    }
}