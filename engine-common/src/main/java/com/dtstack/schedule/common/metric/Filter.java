package com.dtstack.schedule.common.metric;

/**
 * Reason:
 * Date: 2018/10/9
 * Company: www.dtstack.com
 * @author xuchao
 */

public class Filter {
//[{type: "=~", tagk: "operator_name", filter: "*"}]

    // 1:=, 2:!=, 3: =~, 4:!~
    private String type;

    private String tagk;

    private String filter;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTagk() {
        return tagk;
    }

    public void setTagk(String tagk) {
        this.tagk = tagk;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
