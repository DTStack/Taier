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

package com.dtstack.taier.develop.flink.sql.source;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KafkaSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.enums.KafkaTimeFeature;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.util.MapUtil;
import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;
import com.dtstack.taier.develop.flink.sql.core.SqlConstant;
import com.dtstack.taier.develop.flink.sql.core.SqlParamUtil;
import com.dtstack.taier.develop.flink.sql.source.param.KafkaSourceParamEnum;
import com.dtstack.taier.develop.flink.sql.source.param.OffsetModeEnum;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dtstack.taier.develop.enums.develop.FlinkVersion.FLINK_112;


/**
 * kafka 源表
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public class KafkaSourceTable extends AbstractSourceTable {

    private static final String NEW_LINE = "\n";

    private static final Pattern OFFSET_PATTERN = Pattern.compile("\\s*(?<part>[0-9a-zA-Z_]+)\\s+(?<offset>[0-9]+)\\s*");

    private static final String JSON_FORMAT = "json";

    private static final String DT_NEST_FORMAT = "dt_nest";

    @Override
    protected void addSelfParam(Map<String, Object> tableParam) {
        super.addSelfParam(tableParam);
        List<String> offsetList = new ArrayList<>();
        String offsetValue = getAllParam().getString(KafkaSourceParamEnum.OFFSET_VALUE.getFront());
        if (FLINK_112.equals(version)) {
            if (StringUtils.isNotBlank(getAllParam().getString(KafkaSourceParamEnum.OFFSET_RESET.getFront())) && OffsetModeEnum.SPECIFIC.getFlink112().equals(getAllParam().getString(KafkaSourceParamEnum.OFFSET_RESET.getFront()))) {
                if (StringUtils.isNotBlank(offsetValue)) {
                    String[] rows = offsetValue.split(NEW_LINE);
                    for (String row : rows) {
                        if (StringUtils.isNotBlank(row)) {
                            Matcher matcher = OFFSET_PATTERN.matcher(row);
                            if (matcher.find()) {
                                offsetList.add(String.format("partition:%s,offset:%s", matcher.group("part"), matcher.group("offset")));
                            } else {
                                throw new DtCenterDefException("kafka分区偏移量配置错误");
                            }
                        }
                    }
                } else {
                    throw new DtCenterDefException("kafka分区偏移量不能为空");
                }
                getAllParam().put(KafkaSourceParamEnum.OFFSET_VALUE.getFlink112(), String.join(SqlConstant.PARTITION_SEPARATOR, offsetList));
            }

        } else {
            if (StringUtils.isNotBlank(getAllParam().getString(KafkaSourceParamEnum.OFFSET_RESET.getFront())) && OffsetModeEnum.SPECIFIC.getFlink110().equals(getAllParam().getString(KafkaSourceParamEnum.OFFSET_RESET.getFront()))) {
                JSONObject custom = new JSONObject();
                if (StringUtils.isNotBlank(offsetValue)) {
                    String[] rows = offsetValue.split(NEW_LINE);
                    for (String row : rows) {
                        if (StringUtils.isNotBlank(row)) {
                            Matcher matcher = OFFSET_PATTERN.matcher(row);
                            if (matcher.find()) {
                                custom.put(matcher.group("part"), matcher.group("offset"));
                            } else {
                                throw new DtCenterDefException("kafka分区偏移量配置错误");
                            }
                        }
                    }
                } else {
                    throw new DtCenterDefException("kafka分区偏移量不能为空");
                }
                getAllParam().put(KafkaSourceParamEnum.OFFSET_RESET.getFront(), custom.toJSONString());
            } else {
                getAllParam().putIfAbsent(KafkaSourceParamEnum.OFFSET_RESET.getFront(), OffsetModeEnum.LATEST.getFlink110());
            }
        }
    }

    @Override
    protected void addTableStructureParam(List<String> tableStructure) {
        String timeColumnFront = KafkaSourceParamEnum.TIME_COLUMN.getFront();
        if (FLINK_112.equals(version)) {
            if (StringUtils.isNotBlank(getAllParam().getString(timeColumnFront))) {
                tableStructure.add(String.format("WATERMARK FOR %s AS %s - INTERVAL '%s'  %s", getAllParam().getString(timeColumnFront), getAllParam().getString(timeColumnFront), getAllParam().getInteger(KafkaSourceParamEnum.OFFSET.getFront()), getAllParam().getString(KafkaSourceParamEnum.OFFSET_UNIT.getFront())));
            }
            String procTime = getAllParam().getString(KafkaSourceParamEnum.PROC_TIME.getFront());
            if (StringUtils.isNotBlank(procTime)) {
                tableStructure.add(String.format("%s AS PROCTIME()", procTime));
            }
        } else {
            Integer timeType = getAllParam().getInteger(KafkaSourceParamEnum.TIME_TYPE.getFront());
            if (Objects.equals(KafkaTimeFeature.EVENTTIME.getValue(), timeType)) {
                tableStructure.add(String.format("WATERMARK FOR %s AS withOffset(%s,%s)", getAllParam().getString(KafkaSourceParamEnum.TIME_COLUMN.getFront()), getAllParam().getString(KafkaSourceParamEnum.TIME_COLUMN.getFront()), getAllParam().getInteger(KafkaSourceParamEnum.OFFSET.getFront())));
            }
        }
    }

    @Override
    public ISqlParamEnum[] getSqlParamEnums() {
        return KafkaSourceParamEnum.values();
    }

    @Override
    protected String getTypeBeforeVersion112() {
        return "kafka";
    }

    @Override
    protected String getTypeVersion112() {
        return "kafka-x";
    }

    @Override
    protected void convertParamValue() {
        // 处理启动时的偏移量方式
        SqlParamUtil.convertParamValue(getAllParam(), KafkaSourceParamEnum.OFFSET_RESET.getFront(),
                getVersion(), OffsetModeEnum.values(), true);
        if (FLINK_112.equals(version)) {
            // 特殊处理 dt_nest，flink1.12 没有 dt_nest 了
            String format = MapUtils.getString(getAllParam(), KafkaSourceParamEnum.SOURCE_DATA_TYPE.getFront());
            if (StringUtils.isBlank(format) || DT_NEST_FORMAT.equals(format)) {
                getAllParam().put(KafkaSourceParamEnum.SOURCE_DATA_TYPE.getFront(), JSON_FORMAT);
            }
            // 去除 avro schema 信息 flink 会自动推断
            getAllParam().remove(KafkaSourceParamEnum.SCHEMA_INFO.getFront());

            // 处理 bootstrap、brokerList 问题
            String brokerList = StringUtils.isNotBlank(getAllParam().getString(KafkaSourceParamEnum.BOOT_STRAP_SERVERS.getFront())) ?
                    getAllParam().getString(KafkaSourceParamEnum.BOOT_STRAP_SERVERS.getFront()) : getAllParam().getString("brokerList");
            if (StringUtils.isBlank(brokerList)) {
                brokerList = getAllBrokersAddress(getAllParam().getString("address"), null);
            }
            MapUtil.putIfValueNotNull(getAllParam(), KafkaSourceParamEnum.BOOT_STRAP_SERVERS.getFront(), brokerList);
        }
    }

    public static String getAllBrokersAddress(String urls, String brokerUrls) {
        try {
            ISourceDTO sourceDTO = KafkaSourceDTO.builder().url(urls).build();
            String zkBrokersAddress = ClientCache.getKafka(DataSourceType.KAFKA.getVal()).getAllBrokersAddress(sourceDTO);
            brokerUrls = StringUtils.isBlank(brokerUrls) ? "" : brokerUrls;
            return StringUtils.isBlank(zkBrokersAddress) ? brokerUrls : zkBrokersAddress;
        } catch (Exception e) {
            throw new DtCenterDefException(e.getMessage(), e);
        }
    }
}
