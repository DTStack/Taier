package com.dtstack.engine.alert.param;

import java.util.List;

/**
 * Date: 2020/5/19
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class SmsAlertParam extends AlertParam {
    private List<String> phones;

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }
}
