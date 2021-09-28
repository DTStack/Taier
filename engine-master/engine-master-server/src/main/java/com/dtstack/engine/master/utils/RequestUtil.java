/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
