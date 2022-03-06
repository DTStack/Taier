package com.dtstack.taier.develop.vo.datasource;



/**
 * Date: 2020/1/10
 * Company: www.dtstack.com
 *
 * @author zhichen
 */
public class BinLogFileVO {

    private String journalName;

    private String scn;

    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public String getScn() {
        return scn;
    }

    public void setScn(String scn) {
        this.scn = scn;
    }
}
