package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.dto.UserDTO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Date: 2020/8/10
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class CacheUtils {
    public static final Cache<String, UserDTO> cache = CacheBuilder.newBuilder()
            .maximumSize(1000L).initialCapacity(1000).expireAfterAccess(10, TimeUnit.MINUTES).build();

    public static UserDTO getUser(String token) {
        return cache.getIfPresent(token);
    }

}
