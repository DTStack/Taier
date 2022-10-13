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

package com.dtstack.taier.local.test;

import com.dtstack.taier.pluginapi.callback.ClassLoaderCallBackMethod;
import com.dtstack.taier.pluginapi.client.IClient;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.loader.DtClassLoader;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;


public class ClientFactory {

    private static Map<String, ClassLoader> pluginClassLoader = Maps.newConcurrentMap();

    public static IClient createPluginClass(ClassLoader classLoader) throws Exception {
        return ClassLoaderCallBackMethod.callbackAndReset(() -> {
            ServiceLoader<IClient> serviceLoader = ServiceLoader.load(IClient.class);

            List<IClient> matchingClient = new ArrayList<>();
            serviceLoader.iterator().forEachRemaining(matchingClient::add);

            if (matchingClient.size() != 1) {
                throw new RuntimeException("zero or more than one plugin client found" + matchingClient);
            }
            return matchingClient.get(0);
        }, classLoader, true);
    }

    public static IClient buildPluginClient(String pluginInfo, String pluginPath) throws Exception {
        Map<String, Object> params = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        String clientTypeStr = MathUtil.getString(params.get(ConfigConstant.TYPE_NAME_KEY));
        if (StringUtils.isBlank(clientTypeStr)) {
            throw new RuntimeException("not support for typeName:" + clientTypeStr + " pluginInfo:" + pluginInfo);
        }

        ClassLoader classLoader = pluginClassLoader.computeIfAbsent(clientTypeStr, type -> {
            String plugin = pluginPath + File.separator + type;
            File pluginFile = new File(plugin);
            if (!pluginFile.exists()) {
                throw new RuntimeException(String.format("%s directory not found", plugin));
            }
            return createDtClassLoader(pluginFile);
        });

        return ClientFactory.createPluginClass(classLoader);
    }


    private static URLClassLoader createDtClassLoader(File dir) {
        File[] files = dir.listFiles();
        URL[] urls = Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().endsWith(".jar"))
                .map(file -> {
                    try {
                        return file.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("file to url error ", e);
                    }
                })
                .toArray(URL[]::new);

        return new DtClassLoader(urls, ClientFactory.class.getClassLoader());
    }
}

