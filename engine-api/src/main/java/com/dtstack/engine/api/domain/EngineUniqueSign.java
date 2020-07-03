package com.dtstack.engine.api.domain;

import com.dtstack.engine.api.annotation.Unique;

/**
 * Created by sishu.yss on 2018/3/8.
 */
public class EngineUniqueSign extends DataObject{

    @Unique
    private String uniqueSign;

    public String getUniqueSign() {
        return uniqueSign;
    }

    public void setUniqueSign(String uniqueSign) {
        this.uniqueSign = uniqueSign;
    }
}
