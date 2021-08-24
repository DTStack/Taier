package com.dtstack.engine.master.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/8/11 3:12 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RequestUtil {

    /**
     * 切割cookie
     *
     * @param header
     * @return
     */
    public static Map<String, Object> paramToMap(String header) {
        Map<String, Object> map = Maps.newHashMap();

        List<String> strings = Splitter.on(";").trimResults().splitToList(header);

        for (String param : strings) {
            String[] split1 = param.split("=");
            if (ArrayUtils.isNotEmpty(split1) && split1.length == 2) {
                map.put(split1[0],split1[1]);
            }
        }

        return map;
    }
}
