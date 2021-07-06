package com.dtstack.batch.engine.core.domain;

import java.text.SimpleDateFormat;

/**
 * @author jiangbo
 * @explanation
 * @date 2018/11/27
 */
public class ImportColum {

    private String key;

    private String format;

    private SimpleDateFormat dateFormat;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
}
