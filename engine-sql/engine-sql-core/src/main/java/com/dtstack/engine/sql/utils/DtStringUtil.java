package com.dtstack.engine.sql.utils;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理
 * Date: 2018/6/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public class DtStringUtil {

    private static final Pattern NO_VERSION_PATTERN = Pattern.compile("([a-zA-Z]+).*");

    /**
     * 根据指定分隔符分割字符串---忽略在引号里面的分隔符
     * @param str
     * @param delimiter
     * @return
     */
    public static List<String> splitIgnoreQuota(String str, char delimiter){
        List<String> tokensList = new ArrayList<>();
        boolean inQuotes = false;
        boolean inSingleQuotes = false;
        StringBuilder b = new StringBuilder();
        for (char c : str.toCharArray()) {
            if(c == delimiter){
                if (inQuotes) {
                    b.append(c);
                } else if(inSingleQuotes){
                    b.append(c);
                }else {
                    tokensList.add(b.toString());
                    b = new StringBuilder();
                }
            }else if(c == '\"'){
                inQuotes = !inQuotes;
                b.append(c);
            }else if(c == '\''){
                inSingleQuotes = !inSingleQuotes;
                b.append(c);
            }else{
                b.append(c);
            }
        }

        tokensList.add(b.toString());

        return tokensList;
    }

    /**
     * 根据指定分隔符分割字符串---忽略在引号里面的分隔符
     * @param str
     * @param delimter
     * @Deprecated Reason : 针对200K大小的sql任务,会存在OOM的问题, http://redmine.prod.dtstack.cn/issues/22749
     * @return
     */
    @Deprecated
    public static String[] splitIgnoreQuota(String str, String delimter){
        String splitPatternStr = delimter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)";
        return str.split(splitPatternStr);
    }

    /***
     * 根据指定分隔符分割字符串---忽略在引号 和 括号 里面的分隔符
     * @param str
     * @param delimter
     * @return
     */
    public static String[] splitIgnoreQuotaBrackets(String str, String delimter){
        String splitPatternStr = delimter + "(?![^()]*+\\))(?![^{}]*+})(?![^\\[\\]]*+\\])(?=(?:[^\"]|\"[^\"]*\")*$)";
        return str.split(splitPatternStr);
    }

    /**
     * 使用非正则表达式的方法来实现 `根据指定分隔符分割字符串---忽略在引号里面的分隔符`
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
                if(flag){
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

    public static String replaceIgnoreQuota(String str, String oriStr, String replaceStr){
        String splitPatternStr = oriStr + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)";
        return str.replaceAll(splitPatternStr, replaceStr);
    }

    /**
     * 处理 sql 中 "--" 注释，而不删除引号内的内容
     *
     * @param sql 解析出来的 sql
     * @return 返回无注释内容的 sql
     */
    public static String dealSqlComment(String sql) {
        boolean inQuotes = false;
        boolean inSingleQuotes = false;
        int bracketLeftNum = 0;
        StringBuilder b = new StringBuilder(sql.length());
        char[] chars = sql.toCharArray();
        for (int index = 0; index < chars.length; index ++) {
            if (index == chars.length) {
                return b.toString();
            }
            StringBuilder tempSb = new StringBuilder(2);
            if (index > 1) {
                tempSb.append(chars[index - 1]);
                tempSb.append(chars[index]);
            }

            if (tempSb.toString().equals("--")) {
                if (inQuotes) {
                    b.append(chars[index]);
                } else if (inSingleQuotes) {
                    b.append(chars[index]);
                } else if (bracketLeftNum > 0) {
                    b.append(chars[index]);
                } else {
                    b.deleteCharAt(b.length() - 1);
                    while (chars[index] != '\n') {
                        // 判断注释内容是不是行尾或者 sql 的最后一行
                        if (index == chars.length - 1) {
                            break;
                        }
                        index++;
                    }
                }
            } else if (chars[index] == '\"' && '\\' != chars[index] && !inSingleQuotes) {
                inQuotes = !inQuotes;
                b.append(chars[index]);
            } else if (chars[index] == '\'' && '\\' != chars[index] && !inQuotes) {
                inSingleQuotes = !inSingleQuotes;
                b.append(chars[index]);
            } else {
                b.append(chars[index]);
            }
        }
        return b.toString();
    }

    public static String getPluginTypeWithoutVersion(String engineType){

        Matcher matcher = NO_VERSION_PATTERN.matcher(engineType);
        if(!matcher.find()){
            return engineType;
        }

        return matcher.group(1);
    }

    public static List<String> splitField(String str) {
        final char delimiter = ',';
        List<String> tokensList = new ArrayList<>();
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
                    tokensList.add(b.toString());
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
            } else if (c == '<' && !inSingleQuotes && !inQuotes) {
                bracketLeftNum++;
                b.append(c);
            } else if (c == '>' && !inSingleQuotes && !inQuotes) {
                bracketLeftNum--;
                b.append(c);
            } else {
                b.append(c);
            }
            idx++;
        }

        tokensList.add(b.toString());

        return tokensList;
    }
}
