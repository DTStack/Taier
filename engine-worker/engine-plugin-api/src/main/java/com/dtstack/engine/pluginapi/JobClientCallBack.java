package com.dtstack.engine.pluginapi;

import java.io.Serializable;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public interface JobClientCallBack extends Serializable {

    void updateStatus(Integer status);
}
