package com.dtstack.engine.api.vo.alert;

import java.util.List;


public class AlertGateTestVO extends AlertGateVO {

    private List<String> phones;

    private List<String> emails;

    private List<String> dings;

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getDings() {
        return dings;
    }

    public void setDings(List<String> dings) {
        this.dings = dings;
    }
}
