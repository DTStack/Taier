package com.dtstack.rdos.engine.service.zk.cache;

import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public interface CopyOnWriteCache<S, V> {

    Map<S, V> cloneData();
}
