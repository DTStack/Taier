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

package com.dtstack.taier.datasource.api.dto.tsdb;

import com.dtstack.taier.datasource.api.enums.Granularity;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * TSDB 插入 point
 *
 * @author ：wangchuan
 * date：Created in 上午10:25 2021/6/23
 * company: www.dtstack.com
 */
@Data
public class TsdbPoint {

    /**
     * 指标名称
     */
    protected String metric;

    /**
     * tag集合
     */
    protected Map<String, String> tags;

    /**
     * 时间戳
     */
    protected Long timestamp;

    /**
     * 插入数据
     */
    private Object value;

    /**
     * 时间粒度
     */
    private String granularity;

    /**
     * 聚合器
     */
    private String aggregator;

    /**
     * 版本
     */
    private Long version;


    public static class TsdbPointBuilder {
        protected String metric;
        protected Map<String, String> tags = Maps.newHashMap();
        protected Long timestamp;
        private Object value;
        private String granularity;
        private String aggregator;
        private Long version;

        public TsdbPointBuilder(String metric) {
            this.metric = metric;
        }

        public TsdbPointBuilder tag(String tagName, String value) {
            AssertUtils.notNull(tagName, "tagName cannot be null");
            AssertUtils.notNull(value, "value cannot be null");
            if (!tagName.isEmpty()) {
                tags.put(tagName, value);
            }
            return this;
        }

        public TsdbPointBuilder tag(Map<String, String> tags) {
            if (tags != null) {
                this.tags.putAll(tags);
            }
            return this;
        }

        public TsdbPointBuilder aggregator(String aggregator) {
            this.aggregator = aggregator;
            return this;
        }

        public TsdbPointBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TsdbPointBuilder timestamp(Date date) {
            Objects.requireNonNull(date);
            this.timestamp = date.getTime();
            return this;
        }

        public TsdbPointBuilder value(Object value) {
            Objects.requireNonNull(value);
            this.value = value;
            return this;
        }

        public TsdbPointBuilder value(long timestamp, Object value) {
            Objects.requireNonNull(value);
            this.timestamp = timestamp;
            this.value = value;
            return this;
        }

        public TsdbPointBuilder value(Date date, Object value) {
            Objects.requireNonNull(value);
            Objects.requireNonNull(date);
            this.timestamp = date.getTime();
            this.value = value;
            return this;
        }

        public TsdbPointBuilder granularity(Granularity granularity) {
            if (granularity == null) {
                return this;
            }
            this.granularity = granularity.getName();
            return this;
        }

        public TsdbPointBuilder version(Long version) {
            this.version = version;
            return this;
        }

        public TsdbPoint build() {
            return build(true);
        }

        public TsdbPoint build(boolean checkPoint) {
            TsdbPoint point = new TsdbPoint();
            point.metric = this.metric;
            point.tags = this.tags;
            point.timestamp = this.timestamp;
            point.value = this.value;
            point.granularity = this.granularity;
            point.aggregator = this.aggregator;
            point.version = this.version;
            if (checkPoint) {
                checkPoint(point);
            }
            return point;
        }

        public static void checkPoint(TsdbPoint point) {

            AssertUtils.notBlank(point.metric, "The metric can't be empty");
            AssertUtils.notNull(point.metric, "The timestamp can't be null");
            AssertUtils.notNull(point.value, "The value can't be all null");

            if (point.value instanceof Number && point.value == (Number) Double.NaN) {
                throw new SourceException("The value can't be NaN");
            }

            if (point.value instanceof Number && point.value == (Number) Double.POSITIVE_INFINITY) {
                throw new SourceException("The value can't be POSITIVE_INFINITY");
            }

            if (point.value instanceof Number && point.value == (Number) Double.NEGATIVE_INFINITY) {
                throw new SourceException("The value can't be NEGATIVE_INFINITY");
            }

            if (point.tags == null || point.tags.isEmpty()) {
                return;
            }

            for (Map.Entry<String, String> entry : point.tags.entrySet()) {
                String tagKey = entry.getKey();
                String tagValue = entry.getValue();

                AssertUtils.notBlank(tagKey, "the tag key cannot be null or empty");
                AssertUtils.notNull(tagValue, "the tag key cannot be null");
            }
        }
    }

}
