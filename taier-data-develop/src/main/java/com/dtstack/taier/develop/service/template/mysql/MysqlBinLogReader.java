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

package com.dtstack.taier.develop.service.template.mysql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.dtstack.taier.develop.service.template.PluginName;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 2020/2/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class MysqlBinLogReader extends BaseReaderPlugin implements Reader {
    private static final Logger logger = LoggerFactory.getLogger(MysqlBinLogReader.class);
    private static final List<String> CAT_LIST = Lists.newArrayList("update", "delete", "insert");


    private String jdbcUrl;

    private String username;

    private String password;

    private String host;

    private Integer port;

    private List<String> table ;

    private String filter;

    private String cat;

    private Start start;

    private Boolean pavingData;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<String> getTable() {
        return table;
    }

    public void setTable(List<String> table) {
        this.table = table;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public Start getStart() {
        return start;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public Boolean getPavingData() {
        return pavingData;
    }

    public void setPavingData(Boolean pavingData) {
        this.pavingData = pavingData;
    }

    public static class Start {
        private String journalName;

        private Long timestamp;

        public void setJournalName(String journalName) {
            this.journalName = journalName;
        }

        public String getJournalName() {
            return this.journalName;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }



    @Override
    public String pluginName() {
        return PluginName.BINLOG_R;
    }

    @Override
    public void checkFormat(JSONObject data) {

        data = data.getJSONObject("parameter");
        if (StringUtils.isEmpty(data.getString("username"))) {
            throw new TaierDefineException("MySql数据源username 不能为空");
        }
        if (StringUtils.isEmpty(data.getString("password"))) {
            throw new TaierDefineException("MySql数据源password 不能为空");
        }
        if (StringUtils.isEmpty(data.getString("jdbcUrl"))) {
            throw new TaierDefineException("MySql数据源jdbcUrl 不能为空");
        }
        if (StringUtils.isEmpty(data.getString("cat"))) {
            throw new TaierDefineException("MySql数据源cat字段 不能为空 例：update,insert,delete");
        } else {
            String cat = data.getString("cat");
            checkCat(cat);
        }
        JSONArray tableArray;
        try {
            tableArray = data.getJSONArray("table");
        } catch (Exception e) {
            logger.error("table field must be array, error message ={}", e.getMessage(), e);
            throw new TaierDefineException("table字段应为数组形式", e);
        }
        if (tableArray == null) {
            throw new TaierDefineException("table数组不能为空");
        }
        JSONObject start = data.getJSONObject("start");
        if (start != null && start.size() > 0) {
            String timestamp = start.getString("timestamp");
            String journalName = start.getString("journalName");
            if (timestamp != null && journalName != null) {
                throw new TaierDefineException("采集起点不能同时配置按时间和按文件选择");
            }
            if (journalName != null) {
                if (StringUtils.isBlank(journalName)) {
                    throw new TaierDefineException("采集起点配置失败，采集起点文件名不存在");
                }

            }
        }
    }

    private void checkCat(String cat) {
        try {
            String[] split = cat.split(",");
            if (cat.startsWith(",")) {
                throw new TaierDefineException("不能用逗号开头\n");
            }
            if (cat.endsWith(",")) {
                throw new TaierDefineException("不能用逗号结尾\n");
            }
            if (split.length > 3) {
                throw new TaierDefineException("");
            }
            List<String> strings = new ArrayList<>(Arrays.asList(split));
            for (String s : CAT_LIST) {
                if (strings.indexOf(s) == strings.lastIndexOf(s)) {
                    strings.remove(s);
                } else {
                    throw new TaierDefineException("参数不能重复\n");
                }
            }
        } catch (TaierDefineException e) {
            throw new TaierDefineException("Binlog中cat字段填写格式错误，" + e.getMessage() + "正确应为：\"update,insert,delete\"", e);
        }
    }

}
