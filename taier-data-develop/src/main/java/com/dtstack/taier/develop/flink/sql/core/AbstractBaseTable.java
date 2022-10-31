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

package com.dtstack.taier.develop.flink.sql.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.AssertUtils;
import com.dtstack.taier.common.util.MapUtil;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dtstack.taier.develop.enums.develop.FlinkVersion.FLINK_112;


/**
 * flink sql 表抽象基类
 *
 * @author qianyi
 * company: www.dtstack.com
 */
public abstract class AbstractBaseTable implements IFlinkSqlTable {

    /**
     * 数据源信息 + 自定义参数信息
     */
    private JSONObject allParam;

    /**
     * 组建版本，此处对应 flink 版本
     */
    protected FlinkVersion version;

    /**
     * flink sql 建表模版，三个 %s 依此代表: 1. 映射表名称 2. 表结构定义 3. 自定义参数
     */
    private static final String FLINK_SQL_CREATE_TEMPLATE = "CREATE TABLE %s ( %s ) WITH ( %s );";

    /**
     * 自定义参数 key-value 格式模版，如 parallelism = '1'
     */
    private static final String PARAM_KV_TEMPLATE = "%s = '%s'";

    /**
     * flink1.12 自定义参数 key-value 格式模版，如 'parallelism' = '1'
     */
    private static final String PARAM_KV_TEMPLATE_112 = "'%s' = '%s'";

    /**
     * 主键信息
     */
    protected static final String PRIMARY_KEY_TEMPLATE = "PRIMARY KEY ( %s ) NOT ENFORCED";

    @Override
    public String getCreateSql() {
        // 1. 先进行参数填充校验
        checkParam();
        // 2. 构建表结构参数集合，对应的 flink sql 建表模板的第二个参数
        String tableStructureParam = buildTableStructureParam();
        // 3. 构建表自定义参数集合，对应的 flink sql 建表模板的第三个参数
        String tableParam = buildTableParam();
        // 获取表名
        String tableName = MapUtils.getString(getAllParam(), SqlConstant.TABLE_NAME_KEY, getAllParam().getString(SqlConstant.TABLE_KEY));
        return String.format(getCreateTemplate(), tableName, tableStructureParam, tableParam);
    }

    /**
     * 添加表结构参数，源表、维表、结果表不相同，各抽象类去实现
     *
     * @param tableStructure 表结构参数集合
     */
    protected abstract void addTableStructureParam(List<String> tableStructure);

    /**
     * 添加表基本参数，各抽象类去实现
     *
     * @param tableParam 表参数集合
     */
    protected abstract void addBaseParam(Map<String, Object> tableParam);

    /**
     * 获取 flink1.12 之前的版本数据源类型
     *
     * @return 数据源类型
     */
    protected abstract String getTypeBeforeVersion112();

    /**
     * 获取 flink1.12 的版本数据源类型
     *
     * @return 数据源类型
     */
    protected abstract String getTypeVersion112();

    /**
     * 参数检查，用于判断必填参数是否填充、参数格式是否正确等，各数据源中若需要判断参数是否正确需要重写该方法并调用父类
     */
    protected void checkParam() {
        AssertUtils.notBlank(getAllParam().getString(SqlConstant.TABLE_NAME_KEY), "映射表名称不能为空.");
        AssertUtils.notNull(getAllParam().getString(SqlConstant.COLUMNS_KEY), "表字段信息不能为空.");
    }

    /**
     * 获取建表、catalog 等模版 Sql
     *
     * @return 模版 Sql
     */
    protected String getCreateTemplate() {
        return FLINK_SQL_CREATE_TEMPLATE;
    }

    /**
     * 填充相关参数，用于将数据源信息、表参数信息映射到当前对象中，需要保证对象中的参数名称、数据类型和json中一致
     * 不允许重写该方法
     *
     * @param dataJson  数据源信息
     * @param paramJson 参数信息
     * @param version   flink 版本
     */
    protected final void fillParam(JSONObject dataJson, JSONObject paramJson, String version) {
        JSONObject allParam = new JSONObject(paramJson);
        allParam.putAll(dataJson);
        this.allParam = allParam;
        this.version = FlinkVersion.getVersion(version);
        convertParamValue();
    }

    /**
     * 构建表结构定义参数
     *
     * @return 表结构定义参数
     */
    private String buildTableStructureParam() {
        // 表结构参数集合
        List<String> tableStructure = Lists.newArrayList();
        // 添加字段信息
        addColumnStr(tableStructure);
        addTableStructureParam(tableStructure);
        return String.join(SqlConstant.PARAM_SEPARATOR, tableStructure);
    }

    /**
     * 构建表自定义参数
     *
     * @return 表结构定义参数
     */
    private String buildTableParam() {
        Map<String, Object> tableParam = Maps.newHashMap();
        addConnectType(tableParam);
        addBaseParam(tableParam);
        addSelfParam(tableParam);
        addCustomParam(tableParam);
        List<String> tableParamList = Lists.newArrayList();
        for (String key : tableParam.keySet()) {
            if (FLINK_112.equals(version)) {
                tableParamList.add(String.format(PARAM_KV_TEMPLATE_112, key, tableParam.get(key)));
            } else {
                tableParamList.add(String.format(PARAM_KV_TEMPLATE, key, tableParam.get(key)));
            }
        }
        return String.join(SqlConstant.PARAM_SEPARATOR, tableParamList);
    }

    /**
     * 添加 connect 类型，注意区分版本
     *
     * @param tableParam 表参数集合
     */
    protected void addConnectType(Map<String, Object> tableParam) {
        if (FLINK_112.equals(version)) {
            tableParam.put(SqlConstant.CONNECTOR_KEY, getTypeVersion112());
        } else {
            tableParam.put(SqlConstant.TYPE_KEY, getTypeBeforeVersion112());
        }
    }

    /**
     * 添加表字段信息，无论源表、结果表、维表都需要由表字段信息，特殊情况下去重写该方法
     *
     * @param tableStructure 表定义参数集合
     */
    protected void addColumnStr(List<String> tableStructure) {
        JSONArray columns = getAllParam().getJSONArray(SqlConstant.COLUMNS_KEY);
        if (CollectionUtils.isEmpty(columns)) {
            return;
        }
        for (int i = 0; i < columns.size(); i++) {
            JSONObject column = columns.getJSONObject(i);
            StringBuilder columnStr = new StringBuilder();
            //1.12 需要处理 后续版本看情况修改 后续有更得的映射关系在 优化抽出
            if (FLINK_112.equals(version) && "datetime".equalsIgnoreCase(column.getString(SqlConstant.COLUMN_TYPE))) {
                columnStr.append(column.getString(SqlConstant.COLUMN_KEY)).append(SqlConstant.COLUMN_SEPARATOR).append("STRING");
            } else {
                columnStr.append(column.getString(SqlConstant.COLUMN_KEY)).append(SqlConstant.COLUMN_SEPARATOR).append(column.getString(SqlConstant.COLUMN_TYPE));
            }
            if (column.containsKey(SqlConstant.COLUMN_ALIAS) && StringUtils.isNotBlank(column.getString(SqlConstant.COLUMN_ALIAS))) {
                columnStr.append(SqlConstant.COLUMN_SEPARATOR).append(SqlConstant.COLUMN_ALIAS_SEPARATOR).append(SqlConstant.COLUMN_SEPARATOR).append(column.getString(SqlConstant.COLUMN_ALIAS));
            }
            tableStructure.add(columnStr.toString());
        }
    }

    /**
     * 添加用户自定义参数
     *
     * @param tableParam 表参数集合
     */
    protected void addCustomParam(Map<String, Object> tableParam) {
        JSONArray customParams = getAllParam().getJSONArray(SqlConstant.CUSTOM_PARAMS);
        if (CollectionUtils.isEmpty(customParams)) {
            return;
        }
        for (int i = 0; i < customParams.size(); i++) {
            JSONObject customParam = customParams.getJSONObject(i);
            tableParam.put(customParam.getString(SqlConstant.CUSTOM_PARAM_KEY), customParam.getString(SqlConstant.CUSTOM_PARAM_VALUE));
        }
    }

    /**
     * 根据版本添加数据源自己需要的参数
     *
     * @param tableParam 表参数
     */
    protected void addSelfParam(Map<String, Object> tableParam) {
        Map<String, String> frontFlinkXKeyMap = SqlParamUtil.getFrontFlinkXKeyMap(getVersion(), getSqlParamEnums());
        for (Map.Entry<String, String> entry : frontFlinkXKeyMap.entrySet()) {
            MapUtil.putIfValueNotEmpty(tableParam, entry.getValue(), getAllParam().getString(entry.getKey()));
        }
    }

    /**
     * 获取各数据源对应的参数枚举，各数据源自身去实现
     *
     * @return 参数枚举
     */
    public abstract ISqlParamEnum[] getSqlParamEnums();

    /**
     * 转换参数的值，不同版本之间对应的值可能不一样
     */
    protected void convertParamValue() {
    }

    /**
     * 添加主健信息
     *
     * @param tableStructure 表结构信息
     */
    protected void addPrimaryKey(List<String> tableStructure) {
        Object primaryKeyObject = getAllParam().get(SqlConstant.SideTable.PRIMARY_KEY);
        if (Objects.isNull(primaryKeyObject)) {
            return;
        }
        if (primaryKeyObject instanceof String) {
            tableStructure.add(String.format(PRIMARY_KEY_TEMPLATE, primaryKeyObject));
        } else if (primaryKeyObject instanceof JSONArray) {
            JSONArray primaryKey = (JSONArray) primaryKeyObject;
            if (CollectionUtils.isNotEmpty(primaryKey)) {
                String primaryKeyInfo = String.join(SqlConstant.PARAM_SEPARATOR, primaryKey.toJavaList(String.class));
                tableStructure.add(String.format(PRIMARY_KEY_TEMPLATE, primaryKeyInfo));
            }
        }
    }

    public JSONObject getAllParam() {
        return allParam;
    }

    public FlinkVersion getVersion() {
        return version;
    }


}
