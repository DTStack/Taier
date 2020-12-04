package com.dtstack.sql.handler;

import com.dtstack.sql.utils.SqlFormatUtil;
import com.dtstack.sql.utils.SqlRegexUtil;

import java.util.regex.Matcher;

/**
 * @author yuebai
 * @date 2019-12-23
 */
public class HiveUglySqlHandler implements IUglySqlHandler {
    private String sql;

    private String formattedSql;

    private int uglySqlMode = 0b00000000;
    private int sqlMode = 0b00000000;

    private static final int CACHE_TABLE = 0b00000010;


    public int getSqlMode() {
        return sqlMode;
    }

    @Override
    public boolean isTemp() {
        return (this.getSqlMode() & IUglySqlHandler.CREATE_TEMP) == IUglySqlHandler.CREATE_TEMP;
    }


    private void initSql() {
        formattedSql = SqlFormatUtil.getStandardSql(SqlFormatUtil.formatSql(sql));
        formattedSql = formattedSql.trim();
        if (formattedSql.endsWith(";")) {
            formattedSql = formattedSql.substring(0, formattedSql.length() - 1);
        }
    }

    private void initSqlMode() {
        this.sqlMode = 0b00000000;
        this.uglySqlMode = 0b00000000;
        Matcher cacheMatcher = SqlRegexUtil.CACHE_TABLE_PATTEN.matcher(formattedSql);
        if (cacheMatcher.matches()){
            uglySqlMode = uglySqlMode + CACHE_TABLE;
        }
        sqlMode = uglySqlMode;
    }

    private boolean parseUgly(int sqlMode) {
        int flag = uglySqlMode & sqlMode;
        if (flag == 0) {
            return false;
        }
        Matcher matcher = null;
        String group = "";
        switch (sqlMode) {
            case CACHE_TABLE:
                matcher = SqlRegexUtil.CACHE_TABLE_PATTEN.matcher(formattedSql);
                if (matcher.find()) {
                    group = matcher.group("cache");
                    formattedSql = formattedSql.replaceFirst(group, "create ");
                    //临时表也需要解析血缘，因为，临时表可能作为中间表，最终血缘落在普通表上。
//                    formattedSql = "";
                }
                break;
            default:
                return false;
        }
        return true;
    }


    @Override
    public String parseUglySql(String sql) {
        this.sql = sql;
        initSql();
        initSqlMode();
        int i = 1;
        int currentMode = uglySqlMode;
        while (currentMode > 0 && i <= currentMode) {
            boolean b = parseUgly(i);
            if (b) {
                currentMode -= i;
            }
            i <<= 1;
        }
        if (formattedSql.endsWith(",")) {
            formattedSql = formattedSql.substring(0, formattedSql.length() - 1);
        }
        return formattedSql;
    }
}
