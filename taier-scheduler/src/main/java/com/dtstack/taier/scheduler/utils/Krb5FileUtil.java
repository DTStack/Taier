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

package com.dtstack.taier.scheduler.utils;

import com.dtstack.taier.common.exception.TaierDefineException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class Krb5FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(Krb5FileUtil.class);

    public static String mergeKrb5Content(Map<String, HashMap<String, String>> mergeKrb5Content, Map<String, HashMap<String, String>> localKrb5Content) throws Exception {
        Set<String> mapKeys = mergeKrb5ContentKey(mergeKrb5Content, localKrb5Content);

        for (String key : mapKeys) {
            HashMap<String, String> localKrb5Section = localKrb5Content.get(key);
            if (localKrb5Section == null) {
                continue;
            }
            mergeKrb5Content.merge(key, localKrb5Content.get(key), new BiFunction() {
                @Override
                public Map<String, String> apply(Object oldValue, Object newValue) {
                    Map<String, String> oldMap = (Map<String, String>) oldValue;
                    Map<String, String> newMap = (Map<String, String>) newValue;
                    if (oldMap == null) {
                        return newMap;
                    } else if (newMap == null) {
                        return oldMap;
                    } else {
                        oldMap.putAll(newMap);
                        return oldMap;
                    }
                }
            });
        }
        String content = convertMapToString(mergeKrb5Content);
        return content;
    }

    public static String resetMergeKrb5Content(String oldKrb5Content, String newKrb5Content) throws Exception {
        Map<String, HashMap<String, String>> localKrb5Content = readKrb5ByString(oldKrb5Content);
        Map<String, HashMap<String, String>> mergeKrb5Content = readKrb5ByString(newKrb5Content);
        String content = mergeKrb5Content(mergeKrb5Content, localKrb5Content);
        return content;
    }

    public static String mergeKrb5ContentByPath(String mergeKrb5Path, String localKrb5Path) throws Exception {
        Map<String, HashMap<String, String>> localKrb5Content = readKrb5ByPath(localKrb5Path);
        Map<String, HashMap<String, String>> mergeKrb5Content = readKrb5ByPath(mergeKrb5Path);
        String content = mergeKrb5Content(mergeKrb5Content, localKrb5Content);
        Files.write(Paths.get(mergeKrb5Path), Collections.singleton(content));
        return content;
    }

    public static String convertMapToString(Map<String, HashMap<String, String>> krb5) {
        StringBuffer content = new StringBuffer();
        for (String key : krb5.keySet()) {
            if (StringUtils.isNotEmpty(key)) {
                String keyStr = String.format("[%s]", key);
                content.append(keyStr).append(System.lineSeparator());
            }
            Map<String, String> options = krb5.get(key);
            for (String option : options.keySet()) {
                String optionValue = options.get(option);
                String optionStr = option;
                if (StringUtils.isNotEmpty(optionValue)) {
                    optionStr = String.format("%s = %s", option, optionValue);
                }
                content.append(optionStr).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    private static Set<String> mergeKrb5ContentKey(Map<String, HashMap<String, String>> remoteKrb5Content,
                                                   Map<String, HashMap<String, String>> localKrb5Content) {
        Set<String> mapKeys = new HashSet<>();
        mapKeys.addAll(remoteKrb5Content.keySet());
        mapKeys.addAll(localKrb5Content.keySet());
        return mapKeys;
    }

    public static Map<String, HashMap<String, String>> readKrb5ByPath(String krb5Path) {
        List<String> lines = new ArrayList<>();
        File krb5File = new File(krb5Path);
        try (
                InputStreamReader inputReader = new InputStreamReader(new FileInputStream(krb5File));
                BufferedReader br = new BufferedReader(inputReader);
        ) {
            for (; ; ) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        } catch (Exception e) {
            LOGGER.error("krb5.conf read error:", e);
            throw new TaierDefineException("krb5.conf read error");
        }
        return convertKrb5ToMap(lines);
    }

    public static Map<String, HashMap<String, String>> readKrb5ByString(String krb5Content) {
        String[] krb5Lines = krb5Content.split("[\\r\\n|\\r|\\n]");
        List<String> krb5LinesList = Arrays.asList(krb5Lines);
        return convertKrb5ToMap(krb5LinesList);
    }

    private static Map<String, HashMap<String, String>> convertKrb5ToMap(List<String> krb5Lines) {
        Map<String, HashMap<String, String>> krb5Contents = new HashMap<>();

        String section = "";
        boolean flag = true;
        String currentKey = "";
        StringBuffer content = new StringBuffer();

        for (String line : krb5Lines) {
            line = StringUtils.trim(line);
            if (StringUtils.isNotEmpty(line) && !StringUtils.startsWith(line, "#") && !StringUtils.startsWith(line, ";")) {
                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.substring(1, line.length() - 1).trim();
                } else {
                    if (line.contains("{")) {
                        flag = false;
                        content = new StringBuffer();
                        if (line.contains("=")) {
                            currentKey = line.split("=")[0].trim();
                            line = line.split("=")[1].trim();
                        }
                    }

                    if (flag) {
                        String[] cons = line.split("=");
                        String key = cons[0].trim();
                        String value = "";
                        if (cons.length > 1) {
                            value = cons[1].trim();
                        }
                        currentKey = key;
                        Map map = krb5Contents.computeIfAbsent(section, k -> new HashMap<String, String>());
                        map.put(key, value);
                    } else {
                        content.append(line).append(System.lineSeparator());
                    }

                    if (line.contains("}")) {
                        flag = true;
                        String value = content.toString();
                        Map map = krb5Contents.computeIfAbsent(section, k -> new HashMap<String, String>());
                        map.put(currentKey, value);
                    }
                }
            }
        }
        return krb5Contents;
    }

    public static boolean checkKrb5Content(String content) {

        if (StringUtils.isEmpty(content)) {
            throw new TaierDefineException("merge krb5.conf content is null");
        }
        String[] krb5Lines = content.split("[\\r\\n|\\r|\\n]");
        String var6 = null;
        for (int i = 0; i < krb5Lines.length; i++) {
            String krb5Line = krb5Lines[i];
            if (!krb5Line.isEmpty() && !krb5Line.startsWith("#") && !krb5Line.startsWith(";")) {
                if (krb5Line.startsWith("[")) {
                    if (!krb5Line.endsWith("]")) {
                        throw new TaierDefineException("Illegal config content:" + krb5Line);
                    }

                    String configContent = krb5Line.substring(1, krb5Line.length() - 1).trim();
                    if (configContent.isEmpty()) {
                        throw new TaierDefineException("Illegal config content:" + krb5Line);
                    }

                    var6 = configContent + " = {";
                } else if (krb5Line.startsWith("{")) {
                    if (var6 == null) {
                        throw new TaierDefineException("Config file should not start with \"{\"");
                    }

                    var6 = var6 + " {";
                    if (krb5Line.length() > 1) {
                        var6 = krb5Line.substring(1).trim();
                    }
                } else if (var6 != null) {
                    var6 = krb5Line;
                }
            }
        }
        return true;
    }
}
