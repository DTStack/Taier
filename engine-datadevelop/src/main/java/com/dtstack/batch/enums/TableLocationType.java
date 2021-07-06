package com.dtstack.batch.enums;

/**
 * Date: 2019/12/16
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public enum TableLocationType {

    /**
     * hive
     */
    HIVE("hive"),

    /**
     * kudu
     */
    KUDU("kudu"),
    ;



    private String value;

    TableLocationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String key() {
        return "tableLocationType";
    }


    public static TableLocationType getTableLocationType(String value) {
        for (TableLocationType tableLocationType: TableLocationType.values()) {
            if (tableLocationType.getValue().equals(value)) {
                return tableLocationType;
            }
        }
        return null;
    }


}
