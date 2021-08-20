package com.dtstack.engine.alert.client.ding.bean;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 10:39 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DingAt {

    private List<String> atMobiles;

    private Boolean isAtAll;

    public List<String> getAtMobiles() {
        return atMobiles;
    }

    public void setAtMobiles(List<String> atMobiles) {
        this.atMobiles = atMobiles;
    }

    public Boolean getAtAll() {
        return isAtAll;
    }

    public void setAtAll(Boolean atAll) {
        isAtAll = atAll;
    }
}
