package com.dtstack.engine.alert.client.ding.bean;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 10:33 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DingBean {

    private String msgtype;

    private DingText text;

    private DingMarkdown dingMarkdown;

    private DingAt at;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public DingText getText() {
        return text;
    }

    public void setText(DingText text) {
        this.text = text;
    }

    public DingMarkdown getDingMarkdown() {
        return dingMarkdown;
    }

    public void setDingMarkdown(DingMarkdown dingMarkdown) {
        this.dingMarkdown = dingMarkdown;
    }

    public DingAt getAt() {
        return at;
    }

    public void setAt(DingAt at) {
        this.at = at;
    }
}
