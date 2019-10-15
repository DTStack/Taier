/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.rdos.engine.execution.flink180.classloader;

import com.dtstack.rdos.engine.execution.base.loader.DtClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/14
 */
public class ClassLoaderManager {

    private static final Logger LOG = LoggerFactory.getLogger(ClassLoaderManager.class);

    private static final String JAR_SUFFIX = ".jar";

    private static Map<String, DtClassLoader> pluginClassLoader = new ConcurrentHashMap<>();

    public static <R> R newInstance(String pluginJarPath, ClassLoaderSupplier<R> callBack) throws Exception {
        ClassLoader classLoader = retrieveClassLoad(pluginJarPath);
        return ClassLoaderSupplierCallBack.callbackAndReset(callBack, classLoader);
    }

    private static DtClassLoader retrieveClassLoad(String pluginJarPath) {
        return pluginClassLoader.computeIfAbsent(pluginJarPath, k -> {
            try {
                URL[] urls = getPluginJarUrls(pluginJarPath);
                ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
                DtClassLoader classLoader = new DtClassLoader(urls, parentClassLoader);
                LOG.info("pluginJarPath:{} create ClassLoad successful...", pluginJarPath);
                return classLoader;
            } catch (Throwable e) {
                LOG.error("retrieve ClassLoad happens error:{}", e);
                throw new RuntimeException("retrieve ClassLoad happens error");
            }
        });
    }

    private static URL[] getPluginJarUrls(String pluginDir) throws MalformedURLException {
        List<URL> urlList = new ArrayList<>();
        File dirFile = new File(pluginDir);
        if(!dirFile.exists() || !dirFile.isDirectory()){
            throw new RuntimeException("plugin path:" + pluginDir + "is not exist.");
        }

        File[] files = dirFile.listFiles(tmpFile -> tmpFile.isFile() && tmpFile.getName().endsWith(JAR_SUFFIX));
        if(files == null || files.length == 0){
            throw new RuntimeException("plugin path:" + pluginDir + " is null.");
        }

        for(File file : files){
            URL pluginJarURL = file.toURI().toURL();
            urlList.add(pluginJarURL);
        }
        return urlList.toArray(new URL[urlList.size()]);
    }
}
