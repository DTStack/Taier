package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;

public class Krb5FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(Krb5FileUtil.class);

    public static String mergeKrb5Content(String mergeKrb5Path, String localKrb5Path) throws Exception {
        Map<String, HashMap<String, String>> localKrb5Content = readKrb5(localKrb5Path);
        Map<String, HashMap<String, String>> mergeKrb5Content = readKrb5(mergeKrb5Path);

        Set<String> mapKeys = mergeKrb5ContentKey(localKrb5Content, localKrb5Content);

        for (String key: mapKeys) {
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

        String fileContent = writeKrb5(mergeKrb5Path, mergeKrb5Content);
        return fileContent;
    }

    public static String writeKrb5(String filePath, Map<String, HashMap<String, String>> krb5) throws Exception {

        StringBuffer content = new StringBuffer();
        for (String key : krb5.keySet()) {
            if (StringUtils.isNotEmpty(key)) {
                String keyStr = String.format("[%s]", key);
                content.append(keyStr).append(System.lineSeparator());
            }
            Map<String, String> options = krb5.get(key);
            for(String option : options.keySet()) {
                String optionStr = String.format("%s = %s", option, options.get(option));
                content.append(optionStr).append(System.lineSeparator());
            }
        }
        Files.write(Paths.get(filePath), Collections.singleton(content));
        return content.toString();
    }

    private static Set<String> mergeKrb5ContentKey(Map<String, HashMap<String, String>> remoteKrb5Content,
                                                   Map<String, HashMap<String, String>> localKrb5Content) {
        Set<String> mapKeys = new HashSet<>();
        mapKeys.addAll(remoteKrb5Content.keySet());
        mapKeys.addAll(localKrb5Content.keySet());
        return mapKeys;
    }

    public static Map<String, HashMap<String, String>> readKrb5(String krb5Path) {
        Map<String, HashMap<String, String>> krb5Contents = new HashMap<>();

        String section = "";
        boolean flag = true;
        String currentKey = "";
        StringBuffer content = new StringBuffer();

        List<String> lines = new ArrayList<>();
        File krb5File = new File(krb5Path);

        try(
                InputStreamReader inputReader = new InputStreamReader(new FileInputStream(krb5File));
                BufferedReader br = new BufferedReader(inputReader);
        ){
            for (;;) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        } catch (Exception e){
            LOGGER.error("krb5.conf read error:", e);
            throw new RdosDefineException("krb5.conf read error");
        }

        for (String line : lines) {
            line = StringUtils.trim(line);
            if (StringUtils.isNotEmpty(line) && !StringUtils.startsWith(line, "#") && !StringUtils.startsWith(line, ";")) {
                if (line.startsWith("[") && line.endsWith("]")){
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
}
