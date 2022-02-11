package com.dtstack.taier.develop.sql.formate;

import com.dtstack.taier.common.util.Base64Util;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 格式化sql语句注意需要区分create table 语句和其他
 * Date: 2018/7/6
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class SqlFormatter {

    private static final Logger logger = LoggerFactory.getLogger(SqlFormatter.class);

    private static final String CRLF = System.getProperty("line.separator", "\n");

    private static final String SQL_DELIMITER = ";";

    private static Formatter ddlFormat = new DDLFormatterImpl();

    private static Formatter basicFormat = new BasicFormatterImpl();

    public static Pattern createTablePattern = Pattern.compile("(?i)[0-1\\s]*create(\\s+|\\s+[0-1]*\\s+)table");

    public static Pattern alterTablePattern = Pattern.compile("(?i)[0-1\\s]*alter(\\s+|\\s+[0-1]*\\s+)table");

    public static Pattern commentOnPattern = Pattern.compile("(?i)[0-1\\s]*comment(\\s+|\\s+[0-1]*\\s+)on");

    private static final String NOTE = "--.*\n|/\\*\\*[\\s\\S]*\\*/";

    private static final Pattern note_pattern = Pattern.compile(NOTE);

    private static final String NEW_NOTE = "##.*\n";

    private static final Pattern note_pattern_new = Pattern.compile(NEW_NOTE);

    private static final Pattern delimiter_pattern = Pattern.compile(SQL_DELIMITER);

    private static final Pattern from_pattern = Pattern.compile("\n\\s*\n");

    private static final String WHITE_SPACE = " ";

    public static final String RDOSFORMAT_BINARY = SqlFormatter.toBinary("RDOSFORMAT");

    private static final String annotate_STR = WHITE_SPACE + RDOSFORMAT_BINARY + WHITE_SPACE;

    private static final String ANNOTATE_LIST = "annotateList";

    private static final String SQL = "sql";

    public static String format(String sql) throws Exception {
        Map<String, Object> map = addSplitWithNote(sql);
        sql = (String) map.get(SQL);
        List<String> annotateList = (List<String>) map.get(ANNOTATE_LIST);

        String format = formatSql(sql);

        //替换为注释语句
        for (int j = 0; j < annotateList.size(); j++) {
            format = format.replaceFirst(annotate_STR.trim(), "\n" + annotateList.get(j) + "\n");
        }
        //删除无用换行
        format = format.trim();
        Matcher matcher = from_pattern.matcher(format);
        while (matcher.find()) {
            format = matcher.replaceAll("\n");
        }
        return format.replaceAll(";", ";\n");
    }

    /**
     * 处理sqlText里的注释，先base64编码
     *
     * @param sql
     * @return
     */
    public static String dealAnnotationBefore(String sql) {
        //兼容注释regex
        sql = sql + "\n";
        Matcher matcher = note_pattern.matcher(sql);
        while (matcher.find()) {
            sql = matcher.replaceFirst("##" + Base64Util.baseEncode(matcher.group()) + "\n");
            matcher = note_pattern.matcher(sql);
        }
        return sql;
    }

    /**
     * 处理sqlText里的注释，后base64解码
     *
     * @param sql
     * @return
     */
    public static String dealAnnotationAfter(String sql) {
        Matcher matcher = note_pattern_new.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group();
            if (group.endsWith("\n")) {
                group = group.substring(2, group.length() - 1);
            }
            String s = group;
            try {
                s = Base64Util.baseDecode(group);
            } catch (IllegalArgumentException e) {
                logger.warn("baseEncode failed, sql={}, e={}", sql, e);
            }
            //替换 $ 符号
            s = s.replaceAll("\\$", "RDS_CHAR_DOLLAR");
            sql = matcher.replaceFirst(s);
            sql = sql.replaceAll("RDS_CHAR_DOLLAR", "\\$");
            matcher = note_pattern_new.matcher(sql);
        }
        return sql;
    }

    /**
     * 提取出注释语句
     *
     * @param sql
     * @return
     */
    private static Map<String, Object> addSplitWithNote(String sql) {
        //兼容注释regex
        sql = sql + "\n";
        List<String> annotateList = new ArrayList<>();
        Matcher matcher = note_pattern.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group().trim();
            if (group.endsWith("\n")) {
                group = group.substring(0, group.length() - 2);
            }
            annotateList.add(group);
            sql = matcher.replaceFirst(annotate_STR);
            matcher = note_pattern.matcher(sql);
        }

        Map<String, Object> result = new HashMap<>(2);
        result.put(ANNOTATE_LIST, annotateList);
        result.put(SQL, sql);
        return result;
    }


    public static String formatSql(String sql) {
        int allDelimiter = getDelimiterCount(sql);
        String[] arrSql = com.dtstack.taier.common.util.Strings.splitIgnoreQuotaBrackets(sql, SQL_DELIMITER);
        StringBuffer sb = new StringBuffer("");

        int index = 0;
        for (String tmpSql : arrSql) {
            tmpSql = tmpSql.trim();
            if (Strings.isNullOrEmpty(tmpSql)) {
                continue;
            }

            if (checkIsCreateTable(tmpSql)) {
                sb.append(ddlFormat.format(tmpSql));
            } else {
                sb.append(basicFormat.format(tmpSql));
            }
            if (index < allDelimiter) {
                sb.append(SQL_DELIMITER);
            }
            sb.append("\n");
            index++;
        }

        return sb.toString();
    }

    private static boolean checkIsCreateTable(String sql) {
        if (createTablePattern.matcher(sql).find()
                || alterTablePattern.matcher(sql).find()
                || commentOnPattern.matcher(sql).find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取所有分号数量
     *
     * @param sql
     * @return
     */
    private static int getDelimiterCount(String sql) {
        Matcher matcher = delimiter_pattern.matcher(sql);
        int allDelimiter = 0;
        while (matcher.find()) {
            allDelimiter++;
        }
        return allDelimiter;
    }


    public static int[] BinstrToIntArray(String binStr) {
        char[] temp = binStr.toCharArray();
        int[] result = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] - 48;
        }
        return result;
    }

    //将二进制转换成字符
    public static char BinstrToChar(String binStr) {
        int[] temp = BinstrToIntArray(binStr);
        int sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[temp.length - 1 - i] << i;
        }
        return (char) sum;
    }

    public static String toBinary(String str) {
        char[] strChar = str.toCharArray();
        String result = "";
        for (int i = 0; i < strChar.length; i++) {
            result += Integer.toBinaryString(strChar[i]);
        }
        return result;
    }
}
