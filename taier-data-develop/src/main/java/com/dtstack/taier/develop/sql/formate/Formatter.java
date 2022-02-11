package com.dtstack.taier.develop.sql.formate;

/**
 * copy from hibernate
 */
public interface Formatter {
    /**
     * Format the source SQL string.
     *
     * @param source The original SQL string
     * @return The formatted version
     */
    public String format(String source);
}
