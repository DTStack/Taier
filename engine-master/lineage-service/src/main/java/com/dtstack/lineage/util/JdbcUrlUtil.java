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


package com.dtstack.lineage.util;

import com.dtstack.lineage.bo.UrlInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiangbo
 * @date 2019/6/26
 */
public class JdbcUrlUtil {

    public static Pattern JDBC_PATTERN = Pattern.compile("(?i)jdbc(:[a-zA-Z0-9\\.]+){1,2}:(//|@)(?<host>[\\d{1,3}(\\.\\d{1,3}){3}]+):(?<port>\\d{1,5}+)((;DatabaseName=(?<db1>[a-z0-9]+))|((\\/|:)(?<db2>[a-z0-9]+))|$)");

    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";
    public static final String DB_KEY1 = "db1";
    public static final String DB_KEY2 = "db2";

    public static final String PARAM_KEY = "param";

    /**
     * @return host:port
     */
    public static UrlInfo getUrlInfo(String url){

        if (url.contains("%s")){
            url = String.format(url,"default");
        }

        UrlInfo urlInfo = new UrlInfo();

        Matcher matcher = JDBC_PATTERN.matcher(url);
        if (matcher.find()){
            urlInfo.setHost(matcher.group(HOST_KEY));
            urlInfo.setPort(Integer.valueOf(matcher.group(PORT_KEY)));
            if(matcher.group(DB_KEY1)!=null ){
                urlInfo.setDb(matcher.group(DB_KEY1));
            }
            if(matcher.group(DB_KEY2)!=null) {
                urlInfo.setDb(matcher.group(DB_KEY2));
            }
        }

        return urlInfo;
    }
}
