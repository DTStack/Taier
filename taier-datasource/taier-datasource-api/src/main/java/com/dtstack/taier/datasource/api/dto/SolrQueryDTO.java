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

package com.dtstack.taier.datasource.api.dto;

import com.dtstack.taier.datasource.api.dto.contant.SolrCommonParams;
import com.dtstack.taier.datasource.api.utils.AssertUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This is an augmented SolrParams with set fields for common fields used
 */
public class SolrQueryDTO {

    public SolrQueryDTO() {
    }

    private Map<String, String[]> vals = new HashMap<>();

    /**
     * Maintains a map of current sorts
     */
    private List<SortClause> sortClauses;


    public enum ORDER {
        desc, asc;
    }

    public SolrQueryDTO setRequestHandler(String qt) {
        this.set(SolrCommonParams.QT, qt);
        return this;
    }

    public SolrQueryDTO(String q) {
        this();
        this.set(SolrCommonParams.Q, q);
    }

    public SolrQueryDTO(String k, String v, String... params) {
        AssertUtils.isTrue(params.length % 2 == 0, "params length % 2 != 0 ");
        this.set(k, v);
        for (int i = 0; i < params.length; i += 2) {
            this.set(params[i], params[i + 1]);
        }
    }

    /**
     * 设置查询条件
     *
     * @param query
     * @return
     */
    public SolrQueryDTO setQuery(String query) {
        this.set(SolrCommonParams.Q, query);
        return this;
    }


    /**
     * 设置过滤条件
     *
     * @param fq
     * @return
     */
    public SolrQueryDTO setFilterQueries(String... fq) {
        this.set(SolrCommonParams.FQ, fq);
        return this;
    }

    /**
     * 排序条件
     *
     * @param value
     * @return
     */
    public SolrQueryDTO setSorts(List<SortClause> value) {
        sortClauses = new ArrayList<>(value);
        serializeSorts();
        return this;
    }


    /**
     * 排序
     *
     * @param field
     * @param order
     * @return
     */
    public SolrQueryDTO setSort(String field, ORDER order) {
        return setSort(new SortClause(field, order));
    }

    /**
     * 排序
     *
     * @param sortClause
     * @return
     */
    public SolrQueryDTO setSort(SortClause sortClause) {
        return addSort(sortClause);
    }

    /**
     * 排序
     *
     * @param sortClause
     * @return
     */
    public SolrQueryDTO addSort(SortClause sortClause) {
        if (sortClauses == null) {
            sortClauses = new ArrayList<>();
        }
        sortClauses.add(sortClause);
        serializeSorts();
        return this;
    }

    private void serializeSorts() {
        if (sortClauses == null || sortClauses.isEmpty()) {
            remove(SolrCommonParams.SORT);
        } else {
            StringBuilder sb = new StringBuilder();
            for (SortClause sortClause : sortClauses) {
                if (sb.length() > 0) sb.append(",");
                sb.append(sortClause.getItem());
                sb.append(" ");
                sb.append(sortClause.getOrder());
            }
            set(SolrCommonParams.SORT, sb.toString());
        }
    }

    /**
     * 用于分页定义结果起始记录数，默认为0
     *
     * @param start
     * @return
     */
    public SolrQueryDTO setStart(Integer start) {
        if (start == null) {
            this.remove(SolrCommonParams.START);
        } else {
            this.set(SolrCommonParams.START, start);
        }
        return this;
    }

    /**
     * 用于分页定义结果每页返回记录数，默认为10
     *
     * @param rows
     * @return
     */
    public SolrQueryDTO setRows(Integer rows) {
        if (rows == null) {
            this.remove(SolrCommonParams.ROWS);
        } else {
            this.set(SolrCommonParams.ROWS, rows);
        }
        return this;
    }

    /**
     * 设置 指定返回结果字段，以空格“ ”或逗号“,”分隔
     *
     * @param fields
     * @return
     */
    public SolrQueryDTO setFields(String... fields) {
        if (fields == null || fields.length == 0) {
            this.remove(SolrCommonParams.FL);
            return this;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            sb.append(',');
            sb.append(fields[i]);
        }
        this.set(SolrCommonParams.FL, sb.toString());
        return this;
    }

    /**
     * 默认搜索域
     *
     * @param defaultFiled
     * @return
     */
    public SolrQueryDTO setDefaultFiled(String defaultFiled) {
        this.set(SolrCommonParams.DF, defaultFiled);
        return this;
    }

    /**
     * 设置返回结果是否显示Debug信息。
     *
     * @param showDebugInfo
     * @return
     */
    public SolrQueryDTO setShowDebugInfo(boolean showDebugInfo) {
        this.set(SolrCommonParams.DEBUG_QUERY, String.valueOf(showDebugInfo));
        return this;
    }

    /**
     * 自定义参数
     *
     * @param name
     * @param values
     * @return
     */
    public SolrQueryDTO setParam(String name, String... values) {
        this.set(name, values);
        return this;
    }

    public SolrQueryDTO setParam(String name, boolean value) {
        this.set(name, value);
        return this;
    }


    /**
     * A single sort clause, encapsulating what to sort and the sort order.
     * new SortClause("product", SolrQueryDTO.ORDER.asc);
     * new SortClause("product", "asc");
     * SortClause.asc("product");
     */
    public static class SortClause implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        private final String item;
        private final ORDER order;

        public SortClause(String item, ORDER order) {
            this.item = item;
            this.order = order;
        }

        public SortClause(String item, String order) {
            this(item, ORDER.valueOf(order));
        }

        public static SortClause create(String item, ORDER order) {
            return new SortClause(item, order);
        }

        public static SortClause create(String item, String order) {
            return new SortClause(item, ORDER.valueOf(order));
        }

        public static SortClause asc(String item) {
            return new SortClause(item, ORDER.asc);
        }


        public static SortClause desc(String item) {
            return new SortClause(item, ORDER.desc);
        }


        public String getItem() {
            return item;
        }


        public ORDER getOrder() {
            return order;
        }

    }


    /**
     * Replace any existing parameter with the given name.  if val==null remove key from params completely.
     */
    private void set(String name, String... val) {
        if (val == null || (val.length == 1 && val[0] == null)) {
            vals.remove(name);
        } else {
            vals.put(name, val);
        }
    }

    private void set(String name, int val) {
        set(name, String.valueOf(val));
    }

    private void set(String name, boolean val) {
        set(name, String.valueOf(val));
    }

    private String[] remove(String name) {
        return vals.remove(name);
    }

    public Map<String, String[]> getQueryParamMap() {
        return vals;
    }
}
