package com.dtstack.batch.common.template;

import com.alibaba.fastjson.JSONObject;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public interface Reader extends CheckFormat{
    JSONObject toReaderJson();

    String toReaderJsonString();
}
