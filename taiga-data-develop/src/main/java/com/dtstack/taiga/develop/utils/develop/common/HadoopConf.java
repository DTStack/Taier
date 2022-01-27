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

package com.dtstack.taiga.develop.utils.develop.common;

import com.dtstack.taiga.common.util.PublicUtil;
import com.dtstack.taiga.develop.utils.develop.service.impl.Engine2DTOService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author sishu.yss
 */
public class HadoopConf {

    private static Logger logger = LoggerFactory.getLogger(HadoopConf.class);

    public static Map<String, Object> getConfiguration(Long tenantId) {
        return Engine2DTOService.getHdfs(tenantId);
    }

    public static Map<String, Object> getHadoopKerberosConf(Long tenantId) {
        try {
            Map<String, Object> hadoop = Engine2DTOService.getHdfs(tenantId);
            if (MapUtils.isNotEmpty(hadoop)) {
                Object kerberosConfig = hadoop.get("kerberosConfig");
                if (Objects.isNull(kerberosConfig)) {
                    return new HashMap<>(4);
                }
                if (kerberosConfig instanceof Map) {
                    return PublicUtil.objectToMap(kerberosConfig);
                }
                if (kerberosConfig instanceof String) {
                    return PublicUtil.strToMap((String) kerberosConfig);
                }
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return new HashMap<>(4);
    }

    public static String getDefaultFs(Long tenantId) {
        return getConfiguration(tenantId).getOrDefault("fs.defaultFS", "").toString();
    }
}
