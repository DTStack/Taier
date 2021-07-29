package com.dtstack.batch.web.pager;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:48 2021/1/5
 * @Description：排序规则
 */
public enum Sort {
    DESC("DESC"),
    ACS("ASC"),
    ;
    private String value;

    Sort(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
