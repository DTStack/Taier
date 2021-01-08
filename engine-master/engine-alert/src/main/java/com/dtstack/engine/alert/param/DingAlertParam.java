package com.dtstack.engine.alert.param;

import java.util.List;

/**
 * Date: 2020/5/19
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class DingAlertParam extends AlertParam {
    private List<String> dings;
    private String subject;

    public List<String> getDings() {
        return dings;
    }

    public void setDings(List<String> dings) {
        this.dings = dings;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
