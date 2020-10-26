package com.dtstack.engine.sql.formate;

/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performs formatting of DDL SQL statements.
 *
 * @author Gavin King
 * @author Steve Ebersole
 */
public class DDLFormatterImpl implements Formatter {
    /**
     * Singleton access
     */
    public static final DDLFormatterImpl INSTANCE = new DDLFormatterImpl();

    private BasicFormatterImpl basicFormatter = new BasicFormatterImpl();

    private static final String AS_SELECT_REGEX = "(?i)as\\s+select";

    private static final Pattern asSelectPattern = Pattern.compile(AS_SELECT_REGEX);

    public static final String RDOSFORMAT_BINARY = SqlFormatter.toBinary("RDOSFORMAT");

    private static final Pattern NOTE_SQL_LINK = Pattern.compile(RDOSFORMAT_BINARY + "\\s+");

    @Override
    public String format(String sql) {
        if (StringHelper.isEmpty(sql)) {
            return sql;
        }
        if (SqlFormatter.createTablePattern.matcher(sql).find()) {
            Matcher matcher = asSelectPattern.matcher(sql);
            if (matcher.find()) {
                return formatCreateAsSelect(sql, matcher);
            } else {
                return formatCreateTable(sql);
            }
        } else if (SqlFormatter.alterTablePattern.matcher(sql).find()) {
            return formatAlterTable(sql);
        } else if (SqlFormatter.commentOnPattern.matcher(sql).find()) {
            return formatCommentOn(sql);
        } else {
            return "\n    " + sql;
        }
    }

    private String formatCommentOn(String sql) {
        final StringBuilder result = new StringBuilder(60);
        final StringTokenizer tokens = new StringTokenizer(sql, " '[]\"", true);

        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            result.append(token);
            if (isQuote(token)) {
                quoted = !quoted;
            } else if (!quoted) {
                if ("is".equals(token)) {
                    result.append("\n   ");
                }
            }
        }

        return result.toString();
    }

    private String formatAlterTable(String sql) {
        final StringBuilder result = new StringBuilder(60);
        final StringTokenizer tokens = new StringTokenizer(sql, " (,)'[]\"", true);

        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (isQuote(token)) {
                quoted = !quoted;
            } else if (!quoted) {
                if (isBreak(token)) {
                    result.append("\n    ");
                }
            }
            result.append(token);
        }

        return result.toString();
    }

    private String formatCreateTable(String sql) {
        final StringBuilder result = new StringBuilder(60);
        final StringTokenizer tokens = new StringTokenizer(sql, "(,)'[]\"", true);

        int depth = 0;
        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            token = token.trim();
            if (isQuote(token)) {
                quoted = !quoted;
                result.append(token);
            } else if (quoted) {
                result.append(token);
            } else if (token.length() >= SqlFormatter.RDOSFORMAT_BINARY.length() &&
                    token.contains(SqlFormatter.RDOSFORMAT_BINARY)) {
                Matcher matcher = NOTE_SQL_LINK.matcher(token);
                if (matcher.find()) {
                    //在注释后统一换行，多余的换行符另行去除
                    token = matcher.replaceAll(matcher.group() + "\n");
                }
                result.append(token);
            } else {
                if (")".equals(token)) {
                    depth--;
                    if (depth == 0) {
                        result.append("\n ");
                    } else {
                        result.append(token).append(" ");
                        continue;
                    }
                }
                result.append(token);
                if (",".equals(token) && depth == 1) {
                    result.append("\n    ");
                }
                if ("(".equals(token)) {
                    depth++;
                    if (depth == 1) {
                        result.append("\n    ");
                    }
                }
            }
        }

        return result.toString();
    }

    private String formatCreateAsSelect(String sql, Matcher matcher) {
        StringBuilder sb = new StringBuilder();
        String[] split = sql.split(AS_SELECT_REGEX);
        sb.append(formatCreateTable(split[0])).append("\n");
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                if (StringUtils.isNotBlank(split[i])) {
                    String formatAsSelect = basicFormatter.format(matcher.group() + split[1]);
                    sb.append(formatAsSelect);
                }
            }
        }
        return sb.toString();
    }


    private static boolean isBreak(String token) {
        return "drop".equals(token) ||
                "add".equals(token) ||
                "references".equals(token) ||
                "foreign".equals(token) ||
                "on".equals(token);
    }

    private static boolean isQuote(String tok) {
        return "\"".equals(tok) ||
                "`".equals(tok) ||
                "]".equals(tok) ||
                "[".equals(tok) ||
                "'".equals(tok) ||
                "\n".equals(tok);
    }

}

