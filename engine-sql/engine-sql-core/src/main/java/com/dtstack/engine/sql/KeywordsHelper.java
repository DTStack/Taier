package com.dtstack.engine.sql;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/9/23 18:18
 * @Description:
 */
public class KeywordsHelper {

    private static final String SUFFIX = "_RAMEN";

    private static final String ERROR_INFO_REGEX = "(?i)Encountered\\s\"(.*)\"\\sat\\sline\\s([0-9]+),\\scolumn\\s([0-9]+)\\.";

    private static final Pattern ERROR_INFO_PATTERN = Pattern.compile(ERROR_INFO_REGEX);

    private static char SPACE =' ';
    private static char SPLIT_DOT = ',';

    private String sql;

    private String originSql;

    public String getSql() {
        return sql;
    }

    public String getOriginSql() {
        return originSql;
    }

    public KeywordsHelper(String sql) {
        this.sql = sql;
        this.originSql = sql;
    }

    public static boolean exceptionWithKeywords( String errorInfo) {
        if (StringUtils.isEmpty(errorInfo)) {
            return false;
        }
        Matcher matcher = ERROR_INFO_PATTERN.matcher(errorInfo);
        if (matcher.find()) {
            String info = matcher.group(1);
            if (StringUtils.isEmpty(info)) {
                return false;
            }
            String[] split = info.split("\\s");
            if (split.length == 1){
                String fWord = split[0];
                Keywords fkeywords = Keywords.getKeywords(fWord.trim());
                if (fkeywords != null) {
                    return true;
                }
                return false;
            }
            //第一种情况：Encountered ", position" at line 1, column 86.
            String fWord = split[0];
            Keywords fkeywords = Keywords.getKeywords(fWord.trim());
            if (fkeywords != null) {
                return true;
            }
            //第二种情况：Encountered "position ," at line 1, column 1884.
            String bWord = split[1];
            Keywords bkeywords = Keywords.getKeywords(bWord.trim());
            if (bkeywords != null) {
                return true;
            }
        }
        return false;
    }

    public boolean parseErrorInfo(String errorInfo) {
        Matcher matcher = ERROR_INFO_PATTERN.matcher(errorInfo);
        if (matcher.find()) {
            String info = matcher.group(1);
            if (StringUtils.isEmpty(info)) {
                return false;
            }
            String[] split = info.split("\\s");
            String lineStr = matcher.group(2);
            int line = Integer.valueOf(lineStr);
            String columnStr = matcher.group(3);
            Integer column = Integer.valueOf(columnStr);
            //第一种情况：Encountered ", position" at line 1, column 86.
            String fWord = split[0];
            Keywords fkeywords = Keywords.getKeywords(fWord.trim());
            if (split.length==1){
                if (fkeywords != null) {
                    dealKeywords(fWord.trim(), column, SUFFIX);
                    return true;
                }
                return false;
            }
            if (fkeywords != null) {
                dealKeywords(fWord.trim(), column, SUFFIX);
                return true;
            }
            //第二种情况：Encountered "position ," at line 1, column 1884.
            String bWord = split[1];
            Keywords bkeywords = Keywords.getKeywords(bWord.trim());
            if (bkeywords != null) {

                Integer offset = sql.substring(column).indexOf(bWord);
                dealKeywords(bWord.trim(),column+(offset==0?0:offset) , SUFFIX);
                return true;
            }
        }
        return false;
    }

    private void dealKeywords(String keywords, int start, String suffix) {
        if (sql.charAt(start) == SPACE || sql.charAt(start) == SPLIT_DOT){
            start++;
        }else {
            Integer pos = Keywords.findCharFirstPosition(sql.charAt(start), keywords);
            if (pos != null){
                start-=pos;
            }
        }

        String head = sql.substring(0, start);
        String tail = sql.substring(start + keywords.length(), sql.length());
        sql = head + keywords+suffix + tail;
    }

    public static String removeKeywordsSuffix(String columnName){
        if (StringUtils.contains(columnName,SUFFIX)){
            return StringUtils.remove(columnName,SUFFIX);
        }
        return columnName;
    }



    public enum Keywords {
        /**
         *
         */
        SCOPE,
        /**
         *
         */
        CHARACTER,
        /**
         *
         */
        DESCRIBE,
        /**
         *
         */
        INT,
        /**
         *
         */
        AT,
        /**
         *
         */
        YEAR,
        /**
         *
         */
        MONTH,
        /**
         *
         */
        KEY,
        /**
         *
         */
        VALUE,
        /**
         *
         */
        POSITION;

        public static Keywords getKeywords(String keywords) {
            if (StringUtils.isEmpty(keywords)) {
                return null;
            }
            for (Keywords key : values()) {
                if (StringUtils.equalsIgnoreCase(keywords, key.name())) {
                    return key;
                }
            }
            return null;
        }

        /**
         * 获取char在关键字字符串中的第一个位置
         * @param start
         * @param keywords
         * @return
         */
        public static Integer findCharFirstPosition(char start,String keywords){
            if (getKeywords(keywords) == null){
                return null;
            }
            for (int i=0;i<keywords.length();i++){
                if (start == keywords.charAt(i)){
                    return i;
                }
            }
            return null;
        }
    }
}
