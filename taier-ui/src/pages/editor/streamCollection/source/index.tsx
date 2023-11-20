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

import React, { Fragment, useState } from 'react';
import molecule from '@dtinsight/molecule';
import { connect as moleculeConnect } from '@dtinsight/molecule/esm/react';
import { Button, Form, message,Radio, Select } from 'antd';
import { cloneDeep, debounce } from 'lodash';
import moment from 'moment';

import { kafkaTip, sqlserverTip, syncSourceType } from '@/components/helpDoc/docs';
import {
    COLLECT_TYPE,
    DATA_SOURCE_ENUM,
    formItemLayout,
    KAFKA_DATA_TYPE,
    RESTFUL_METHOD,
    RESTFUL_RESP_MODE,
    SYNC_TYPE,
} from '@/constant';
import { IDataSourceUsedInSyncProps } from '@/interface';
import { getFlinkDisabledSource } from '@/utils/enums';
import { isKafka } from '@/utils/is';
import { sourceDefaultValue } from '../helper';
import { streamTaskActions } from '../taskFunc';
import Beats from './component/beats';
import Emq from './component/emq';
import Kafka from './component/kafka';
import Rdb from './component/rdb';
import Restful from './component/restful';
import Socket from './component/socket';
import Websocket from './component/websocket';

const FormItem = Form.Item;
const Option = Select.Option;

const dataSourceTypes = [
    {
        name: 'MySQL',
        value: DATA_SOURCE_ENUM.MYSQL,
    },
    {
        name: 'Oracle',
        value: DATA_SOURCE_ENUM.ORACLE,
    },
];

const CollectionSource = (props: { readonly: any; collectionData: any; sourceList: IDataSourceUsedInSyncProps[] }) => {
    const { readonly, collectionData = {}, sourceList } = props;
    const { isEdit, sourceMap = {}, componentVersion } = collectionData;
    const showInterval = [
        DATA_SOURCE_ENUM.DB2,
        DATA_SOURCE_ENUM.MYSQL,
        DATA_SOURCE_ENUM.UPDRDB,
        DATA_SOURCE_ENUM.ORACLE,
        DATA_SOURCE_ENUM.SQLSERVER,
        DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
        DATA_SOURCE_ENUM.GREENPLUM6,
    ].includes(sourceMap?.type);

    const [form] = Form.useForm();
    const [invalidSubmit, setInvalidSubmit] = useState(false);

    const mapPropsToFields = () => {
        if (!sourceMap) return {};
        if (!sourceMap?.tableFields) {
            sourceMap.allFileds = true;
            sourceMap.tableFields = null;
        }
        if ((sourceMap.param || sourceMap.body)?.some(({ nest }: any) => nest) && !sourceMap.fieldDelimiter) {
            sourceMap.fieldDelimiter = '.';
        }
        let values: any = {};
        Object.entries(sourceMap).forEach(([key, value]) => {
            if (sourceDefaultValue(key)) {
                values = Object.assign({}, values, {
                    [key]: value,
                });
            }
        });
        return {
            ...values,
            table: sourceMap.allTable ? -1 : sourceMap.table,
            tableFields: sourceMap.allFileds ? -1 : sourceMap.tableFields,
            timestamp: sourceMap.timestamp ? moment(sourceMap.timestamp) : undefined,
            macAndIp: '任务运行时自动分配，无需手动指定',
            pollingInterval: sourceMap.pollingInterval ? sourceMap.pollingInterval / 1000 : sourceMap.pollingInterval,
        };
    };

    const validateParams = (dataSource: any, requireFields: any) => {
        if (!requireFields?.length || !dataSource?.length) return true;
        for (const data of dataSource) {
            for (const field of requireFields) {
                if (!data[field]) {
                    return false;
                }
            }
        }
        return true;
    };
    const validateFieldsCallBack = (err: any, values: any) => {
        const { body, param } = values;
        const dataSource = body || param;
        let curInvalidSubmit = false;
        if (dataSource) {
            curInvalidSubmit = !validateParams(dataSource, ['key', 'value']);
        }
        if (err) {
            curInvalidSubmit = true;
        }
        // invalidSubmit 用于控制是否 disable 保存按钮
        // updateCurrentPage 这个 dispatch 会导致表单 validate 状态重置
        // RESTFULAPI 的校验空格的提示会被重置。。
        if (collectionData?.sourceMap?.type === DATA_SOURCE_ENUM.RESTFUL) {
            // RESTFULAPI 中只有 invalidSubmit 与上次不同才调用 updateCurrentPage
            if (invalidSubmit !== curInvalidSubmit) {
                setInvalidSubmit(curInvalidSubmit);
                streamTaskActions.updateCurrentPage({ invalidSubmit: curInvalidSubmit });
            }
        } else {
            streamTaskActions.updateCurrentPage({ invalidSubmit: curInvalidSubmit });
        }
    };

    const onFormValuesChange = () => {
        setTimeout(() => {
            if (!form) {
                return;
            }
            form.validateFields()
                .then((values) => {
                    validateFieldsCallBack(false, values);
                })
                .catch(() => {
                    validateFieldsCallBack(true, {});
                });
        }, 200);
    };
    const debounceFormValuesChange = debounce(onFormValuesChange, 500);

    const renderForm = (type: DATA_SOURCE_ENUM) => {
        switch (type) {
            case DATA_SOURCE_ENUM.POLAR_DB_For_MySQL:
            case DATA_SOURCE_ENUM.MYSQL:
            case DATA_SOURCE_ENUM.UPDRDB:
            case DATA_SOURCE_ENUM.DB2:
            case DATA_SOURCE_ENUM.ORACLE:
            case DATA_SOURCE_ENUM.SQLSERVER:
            case DATA_SOURCE_ENUM.SQLSERVER_2017_LATER:
            case DATA_SOURCE_ENUM.POSTGRESQL:
            case DATA_SOURCE_ENUM.GREENPLUM6:
                return <Rdb collectionData={collectionData} sourceList={sourceList} />;
            case DATA_SOURCE_ENUM.BEATS:
                return <Beats />;
            case DATA_SOURCE_ENUM.TBDS_KAFKA:
            case DATA_SOURCE_ENUM.KAFKA:
            case DATA_SOURCE_ENUM.KAFKA_2X:
            case DATA_SOURCE_ENUM.KAFKA_11:
            case DATA_SOURCE_ENUM.KAFKA_09:
            case DATA_SOURCE_ENUM.KAFKA_10:
            case DATA_SOURCE_ENUM.KAFKA_HUAWEI:
                return <Kafka collectionData={collectionData} sourceList={sourceList} form={form} />;
            case DATA_SOURCE_ENUM.EMQ:
                return <Emq collectionData={collectionData} sourceList={sourceList} />;
            case DATA_SOURCE_ENUM.WEBSOCKET:
                return <Websocket collectionData={collectionData} sourceList={sourceList} />;
            case DATA_SOURCE_ENUM.SOCKET:
                return <Socket collectionData={collectionData} sourceList={sourceList} />;
            case DATA_SOURCE_ENUM.RESTFUL:
                return <Restful collectionData={collectionData} />;
            default:
                return null;
        }
    };
    const rdbmsDaTypeRadioDom = () => {
        const { type } = sourceMap;
        const isMYSQl = type === DATA_SOURCE_ENUM.MYSQL;
        const isORACLE = type === DATA_SOURCE_ENUM.ORACLE;
        const isDB2 = type == DATA_SOURCE_ENUM.DB2;
        const isSQLSERVER = type === DATA_SOURCE_ENUM.SQLSERVER;
        if (isDB2 || isSQLSERVER) {
            return (
                <Radio.Group disabled={isEdit}>
                    <Radio value={SYNC_TYPE.INTERVAL}>间隔轮询</Radio>
                </Radio.Group>
            );
        } else if (isMYSQl) {
            return (
                <Radio.Group disabled={isEdit}>
                    <Radio value={SYNC_TYPE.BINLOG}>Binlog</Radio>
                    {/* <Radio value={SYNC_TYPE.INTERVAL}>间隔轮询</Radio> */}
                </Radio.Group>
            );
        } else if (isORACLE) {
            return (
                <Radio.Group disabled={isEdit}>
                    <Radio value={SYNC_TYPE.LogMiner}>LogMiner</Radio>
                    {/* <Radio value={SYNC_TYPE.INTERVAL}>间隔轮询</Radio> */}
                </Radio.Group>
            );
        } else {
            return null;
        }
    };

    const checkGroup = () => {
        const { sourceMap = {} } = collectionData;
        const { distributeTable = [], multipleTable } = sourceMap;
        if (multipleTable) {
            if (!distributeTable.length) {
                message.warn('分表模式下至少需要一个分组！');
                return false;
            }
            const nameMap: any = {};
            for (let i = 0; i < distributeTable.length; i++) {
                const table = distributeTable[i];
                if (!table.name) {
                    message.warn('请填写分组名！');
                    return false;
                }
                if (!/^\w*$/.test(table.name)) {
                    message.warn('分组名只能由字母、数字和下划线组成！');
                    return false;
                }
                if (nameMap[table.name]) {
                    message.warn('分组名不允许重复！');
                    return false;
                }
                if (!table.tables?.length) {
                    message.warn('请选择表！');
                    return false;
                }
                nameMap[table.name] = true;
            }
        }
        return true;
    };
    const next = () => {
        form.validateFields().then((values) => {
            const { body, param } = values;
            const dataSource = body || param;
            if (validateParams(dataSource, ['key', 'value']) && checkGroup()) {
                streamTaskActions.navtoStep(1);
            }
        });
    };

    const onValuesChange = (fields: any) => {
        let clear = false;
        console.log(fields);
        /**
         * 联动的 targetMap, settingMap
         */
        const settingMap = cloneDeep(props.collectionData?.settingMap || {});
        const targetMap = cloneDeep(props.collectionData?.targetMap || {});
        const sourceMap = props.collectionData.sourceMap;
        /**
         * 数据源类型改变，清空数据源
         */
        if (fields.type != undefined) {
            fields.sourceId = undefined;
            fields.schema = undefined;
            fields.rdbmsDaType = SYNC_TYPE.BINLOG;
            // DB2，SQLSERVER，GREENPLUM 选中间隔轮询
            if ([DATA_SOURCE_ENUM.DB2, DATA_SOURCE_ENUM.SQLSERVER, DATA_SOURCE_ENUM.GREENPLUM6].includes(fields.type)) {
                fields.rdbmsDaType = SYNC_TYPE.INTERVAL;
            }
            if (fields.type === DATA_SOURCE_ENUM.ORACLE) {
                fields.rdbmsDaType = SYNC_TYPE.LogMiner;
            }
            // Oracle 需要处理格式转换
            fields.transferType = fields.type === DATA_SOURCE_ENUM.ORACLE ? 0 : undefined;
            /**
             * codec 在 emq 是编码类型，在 kafka 是读取类型
             */
            fields.codec = isKafka(fields.type) ? KAFKA_DATA_TYPE.TYPE_COLLECT_JSON : 'plain';
            clear = true;
            fields.multipleTable = false;
        } else if (fields.rdbmsDaType != undefined) {
            /**
             * 任务类型改变，清空数据
             */
            fields.sourceId = undefined;
            clear = true;
        }
        // PDB 改变，清空 schema
        if (fields.pdbName) {
            fields.schema = undefined;
        }
        /**
         * schema 或 PDB 改变，清空表信息
         */
        if (fields.schema || fields.pdbName) {
            fields.table = [];
            fields.tableName = undefined;
            fields.distributeTable = undefined;
            fields.allTable = false;
            fields.increColumn = undefined;
        }

        /**
         * sourceId改变,则清空表
         */
        if (fields.hasOwnProperty('sourceId')) {
            settingMap.readerChannel = '1';
            clear = true;
        }
        if (fields.hasOwnProperty('multipleTable')) {
            fields.table = [];
            fields.distributeTable = undefined;
            fields.allTable = false;
        }
        if (fields.hasOwnProperty('table')) {
            targetMap.tableMappingList = null;
        }
        // sourceId || topic 改变，重置作业读取并发数
        if (fields.topic) {
            settingMap.readerChannel = '1';
        }
        /**
         * kafka offset 更改，清空关联数据
         */
        if (fields.mode) {
            fields.timestamp = null;
            fields.offset = undefined;
        }
        /**
         * kafka timestamp
         * 将时间转换为时间戳
         */
        /**
         * 采集起点处理
         * moment=>时间戳,并且清除其他的选项
         */
        if (fields.timestamp) {
            if (!isKafka(sourceMap.type)) {
                fields.journalName = null;
                fields.lsn = null;
            }
            fields.timestamp = fields.timestamp.valueOf();
        }
        if (fields.journalName || fields.startSCN) {
            fields.timestamp = null;
            fields.lsn = null;
        }
        if (fields.lsn) {
            fields.journalName = null;
            fields.timestamp = null;
            fields.startSCN = null;
        }
        if (fields.collectType != undefined && fields.collectType == COLLECT_TYPE.ALL) {
            fields.journalName = null;
            fields.timestamp = null;
            fields.startSCN = null;
            fields.lsn = null;
        }
        /**
         * 转换毫秒
         */
        if (fields.pollingInterval) {
            fields.pollingInterval = fields.pollingInterval * 1000 || 5000;
        }
        /**
         * 增量同步表
         */
        if (fields.tableName) {
            fields.tableFields = undefined;
            fields.allFileds = true;
            fields.increColumn = undefined;
            fields.startLocation = undefined;
            targetMap.partitionKey = undefined;
        }
        /**
         * 改变table的情况
         * 1.包含全部，则剔除所有其他选项，设置alltable=true
         * 2.不包含全部，设置alltable=false
         */
        if (fields.table) {
            if (fields.table.includes(-1)) {
                fields.table = [];
                fields.allTable = true;
            } else {
                fields.allTable = false;
            }
        }
        if (fields.tableFields) {
            if (fields.tableFields.includes(-1)) {
                fields.tableFields = null;
                fields.allFileds = true;
            } else {
                fields.allFileds = false;
            }
        }
        if (fields.decoder) {
            fields.attr = null;
        }
        if (fields.requestMode) {
            const clearField = fields.requestMode === RESTFUL_METHOD[0].value ? 'body' : 'param';
            fields[clearField] = undefined;
            fields.fieldDelimiter = undefined;
        }
        if ((fields.param || fields.body) && !(fields.param || fields.body)?.some(({ nest }: any) => nest)) {
            fields.fieldDelimiter = undefined;
        }
        if (fields.resultType === RESTFUL_RESP_MODE[0].value) {
            fields.parseField = undefined;
        }
        if (fields.slotConfig) {
            fields.slotName = undefined;
            fields.temporary = false;
        }
        /**
         * 当 oracle 开启嵌套 json 平铺
         */
        if (fields.transferType) {
            fields.pavingData = fields.transferType === 2;
        }
        streamTaskActions.updateChannelControlMap(settingMap, false);
        streamTaskActions.updateTargetMap(targetMap, false);
        streamTaskActions.updateSourceMap(fields, clear);
        if (debounceFormValuesChange) {
            debounceFormValuesChange();
        }

        const data = cloneDeep(fields || {});
        if (data.allTable) {
            data.table = -1;
        }
        if (data.allFileds) {
            data.tableFields = -1;
        }
        if (data.timestamp) {
            data.timestamp = moment(fields.timestamp);
        }
        if (data.pollingInterval) {
            data.pollingInterval = fields.pollingInterval / 1000;
        }
        form.setFieldsValue(data);
    };

    return (
        <div id="test_source_form">
            <Form {...formItemLayout} initialValues={mapPropsToFields()} onValuesChange={onValuesChange} form={form}>
                <FormItem
                    label="数据源类型"
                    name="type"
                    rules={[{ required: true, message: '请选择数据源类型' }]}
                    tooltip={
                        sourceMap?.type == DATA_SOURCE_ENUM.KAFKA
                            ? kafkaTip
                            : sourceMap?.type == DATA_SOURCE_ENUM.SQLSERVER ||
                              sourceMap?.type == DATA_SOURCE_ENUM.SQLSERVER_2017_LATER
                            ? sqlserverTip
                            : null
                    }
                >
                    <Select
                        getPopupContainer={(triggerNode: any) => triggerNode}
                        allowClear
                        disabled={isEdit}
                        placeholder="请选择数据源类型"
                        style={{ width: '100%' }}
                    >
                        {dataSourceTypes
                            .filter((item: any) => {
                                return item.value != DATA_SOURCE_ENUM.HIVE;
                            })
                            .map((item: any) => {
                                const allow110List = [
                                    DATA_SOURCE_ENUM.TBDS_HBASE,
                                    DATA_SOURCE_ENUM.TBDS_KAFKA,
                                    DATA_SOURCE_ENUM.KAFKA_HUAWEI,
                                    DATA_SOURCE_ENUM.HBASE_HUAWEI,
                                ];
                                const allow112List = [DATA_SOURCE_ENUM.UPDRDB];
                                const { ONLY_ALLOW_FLINK_1_10_DISABLED, ONLY_ALLOW_FLINK_1_12_DISABLED } =
                                    getFlinkDisabledSource({
                                        version: componentVersion,
                                        value: item.value,
                                        allow110List,
                                        allow112List,
                                    });
                                return (
                                    <Option
                                        key={item.value}
                                        value={item.value}
                                        disabled={ONLY_ALLOW_FLINK_1_10_DISABLED || ONLY_ALLOW_FLINK_1_12_DISABLED}
                                    >
                                        {item.name}
                                    </Option>
                                );
                            })
                            .filter(Boolean)}
                    </Select>
                </FormItem>
                <FormItem noStyle dependencies={['type']}>
                    {(f) => {
                        return (
                            <Fragment>
                                {showInterval && (
                                    <FormItem
                                        label="任务类型"
                                        name="rdbmsDaType"
                                        rules={[{ required: true, message: '请选择任务类型' }]}
                                        tooltip={syncSourceType}
                                    >
                                        {rdbmsDaTypeRadioDom()}
                                    </FormItem>
                                )}
                                {renderForm(f.getFieldValue('type'))}
                            </Fragment>
                        );
                    }}
                </FormItem>
            </Form>
            {!readonly && (
                <div className="steps-action">
                    <Button type="primary" onClick={() => next()}>
                        下一步
                    </Button>
                </div>
            )}
        </div>
    );
    // }
};

export default moleculeConnect(molecule.editor, CollectionSource);
