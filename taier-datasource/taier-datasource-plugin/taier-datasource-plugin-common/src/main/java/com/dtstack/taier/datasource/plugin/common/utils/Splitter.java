package com.dtstack.taier.datasource.plugin.common.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 切分工具
 *
 * @author ：wangchuan
 * date：Created in 11:56 上午 2021/9/2
 * company: www.dtstack.com
 */
public class Splitter {

    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '\"';
    private static final char BACKSLASH = '\\';
    private final char delimiter;

    public Splitter(char delimiter) {
        this.delimiter = delimiter;
    }


    public List<String> splitEscaped(String string) {
        List<Token> tokens = tokenize(string.trim());
        return processTokens(tokens);
    }

    private List<String> processTokens(List<Token> tokens) {
        List<String> sqlStmts = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (Token token : tokens) {
            if (token.getKind() == TokenKind.DELIMITER) {
                sqlStmts.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(token.getVal());
            }
        }
        if (stringBuilder.length() > 0) {
            sqlStmts.add(stringBuilder.toString());
        }
        if (CollectionUtils.isNotEmpty(tokens) && TokenKind.DELIMITER.equals(tokens.get(tokens.size() - 1).getKind())) {
            sqlStmts.add("");
        }
        return sqlStmts;
    }


    private List<Token> tokenize(String str) {
        char[] chars = str.toCharArray();
        State state = State.UNQUOTED;
        StringBuilder buffer = new StringBuilder();
        List<Token> tokens = new ArrayList<>();

        for (char c : chars) {
            switch (state) {
                case UNQUOTED:
                    if (c == SINGLE_QUOTE) {
                        state = State.SINGLE_QUOTED;
                        handleStartQuote(buffer, tokens, c);
                    } else if (c == DOUBLE_QUOTE) {
                        state = State.DOUBLE_QUOTED;
                        handleStartQuote(buffer, tokens, c);
                    } else if (c == delimiter) {
                        handleDelimiter(buffer, tokens, c);
                    } else {
                        buffer.append(c);
                    }
                    break;
                case SINGLE_QUOTED:
                    if (c == SINGLE_QUOTE) {
                        state = handleEndQuote(buffer, tokens, c);
                    } else if (c == BACKSLASH) {
                        state = State.AFTER_BACKSLASH_SINGLE_QUOTED;
                        buffer.append(c);
                    } else {
                        buffer.append(c);
                    }
                    break;
                case DOUBLE_QUOTED:
                    if (c == DOUBLE_QUOTE) {
                        state = handleEndQuote(buffer, tokens, c);
                    } else if (c == BACKSLASH) {
                        state = State.AFTER_BACKSLASH_DOUBLE_QUOTED;
                        buffer.append(c);
                    } else {
                        buffer.append(c);
                    }
                    break;
                case AFTER_BACKSLASH_SINGLE_QUOTED:
                    state = State.SINGLE_QUOTED;
                    buffer.append(c);
                    break;
                case AFTER_BACKSLASH_DOUBLE_QUOTED:
                    state = State.DOUBLE_QUOTED;
                    buffer.append(c);
                    break;
                default:
                    break;
            }
        }
        // handle sql that is not end of ';'
        if (buffer.length() > 0) {
            convertToToken(buffer, tokens, TokenKind.STRING);
        }
        return tokens;
    }

    private void handleDelimiter(StringBuilder buffer, List<Token> tokens, char delimiter) {
        convertToToken(buffer, tokens, TokenKind.STRING);
        buffer.append(delimiter);
        convertToToken(buffer, tokens, TokenKind.DELIMITER);
    }

    private void handleStartQuote(StringBuilder buffer, List<Token> tokens, char quote) {
        convertToToken(buffer, tokens, TokenKind.STRING);
        buffer.append(quote);
    }

    private State handleEndQuote(StringBuilder buffer, List<Token> tokens, char quote) {
        buffer.append(quote);
        convertToToken(buffer, tokens, TokenKind.STRING);
        return State.UNQUOTED;
    }

    private void convertToToken(StringBuilder stringBuilder, List<Token> tokens, TokenKind kind) {
        Token token = new Token(kind, stringBuilder.toString());
        tokens.add(token);
        stringBuilder.setLength(0);
    }

    private enum State {
        /**
         * current char in single quote
         */
        SINGLE_QUOTED,
        /**
         * current char in single quote，and after '\'
         */
        AFTER_BACKSLASH_SINGLE_QUOTED,
        /**
         * current char in double quote
         */
        DOUBLE_QUOTED,
        /**
         * current char in double quote，and after '\'
         */
        AFTER_BACKSLASH_DOUBLE_QUOTED,
        /**
         * out of quote
         */
        UNQUOTED
    }

    private static class Token {

        private final TokenKind kind;
        private final String val;

        Token(TokenKind kind, String val) {
            this.kind = kind;
            this.val = val;
        }

        public TokenKind getKind() {
            return kind;
        }

        public String getVal() {
            return val;
        }
    }

    /**
     * There are only two kinds
     */
    private enum TokenKind {
        /**
         * SQL string
         */
        STRING,
        /**
         * SQL delimiter
         */
        DELIMITER
    }
}
