package com.dtstack.engine.sql.handler;

import com.dtstack.engine.sql.utils.SqlFormatUtil;
import com.dtstack.engine.sql.utils.SqlRegexUtil;

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

    public int getSqlMode() {
        return sqlMode;
    }

    @Override
    public boolean isTemp() {
        return (this.getSqlMode() & CREATE_TEMP) == CREATE_TEMP;
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
        if (SqlRegexUtil.isCreateTemp(formattedSql)) {
            uglySqlMode = uglySqlMode + CREATE_TEMP;
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
