package com.dtstack.batch.engine.rdbms.common.util;

import com.dtstack.dtcenter.common.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chener
 * @Classname SqlFormatterUtil
 * @Description TODO
 * @Date 2020/8/11 17:39
 * @Created chener@dtstack.com
 */
public class SqlFormatterUtil {

    private static final Logger logger = LoggerFactory.getLogger(SqlFormatterUtil.class);
    private static final Pattern note_pattern = Pattern.compile("--.*\n|/\\*\\*[\\s\\S]*\\*/");
    private static final Pattern note_pattern_new = Pattern.compile("##.*\n");

    public static String dealAnnotationBefore(String sql) {
        sql = sql + "\n";
        for (Matcher matcher = note_pattern.matcher(sql);
             matcher.find(); matcher = note_pattern.matcher(sql)) {
            sql = matcher.replaceFirst("##" +
                    Base64Util.baseEncode(matcher.group()) + "\n");
        }
        return sql;
    }

    public static String dealAnnotationAfter(String sql) {
        for (Matcher matcher = note_pattern_new.matcher(sql);
             matcher.find(); matcher = note_pattern_new.matcher(sql)) {
            String group = matcher.group();
            if (group.endsWith("\n")) {
                group = group.substring(2, group.length() - 1);
            }
            String s = group;
            try {
                s = Base64Util.baseDecode(group);
            } catch (IllegalArgumentException var5) {
                logger.warn("baseEncode failed, sql={}, e={}", sql, var5);
            }
            s = s.replaceAll("\\$", "RDS_CHAR_DOLLAR");
            sql = matcher.replaceFirst(s);
            sql = sql.replaceAll("RDS_CHAR_DOLLAR", "\\$");
        }
        return sql;
    }

}
