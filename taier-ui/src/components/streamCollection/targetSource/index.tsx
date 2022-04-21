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

import api from "@/api";
import stream from "@/api/stream";
import { hiveWithAllTable } from "@/components/helpDoc/docs";
import { DATA_SOURCE_ENUM, DATA_SOURCE_TEXT, formItemLayout, PARTITION_TYPE, SYNC_TYPE, WRITE_TABLE_TYPE } from "@/constant";
import { getFlinkDisabledSource, isHive, isKafka, isMysqlTypeSource } from "@/utils/enums";
import molecule from "@dtinsight/molecule";
import { connect as moleculeConnect } from '@dtinsight/molecule/esm/react';
import { Button, Form, FormInstance, Radio, Select } from "antd";
import { ExclamationCircleOutlined } from '@ant-design/icons'
import React from "react";
import { streamTaskActions } from "../taskFunc";
import Kafka from "./component/kafka";
import Hdfs from "./component/hdfs";
import Hive from "./component/hive";
import Emq from "./component/emq";
import { targetDefaultValue } from "../helper";

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

// eslint-disable-next-line
const prefixRule = '${schema}_${table}';

function getSourceInitialField(sourceType: any, data: any) {
    const initialFields: any = { type: sourceType };
    const { sourceMap = {} } = data;
    const isMysqlSource = isMysqlTypeSource(sourceMap.type);
    switch (sourceType) {
        case DATA_SOURCE_ENUM.HDFS: {
            initialFields.fileType = 'orc';
            initialFields.fieldDelimiter = ',';
            initialFields.encoding = 'utf-8';
            initialFields.writeMode = 'APPEND';
            return initialFields;
        }
        case DATA_SOURCE_ENUM.HIVE: {
            // eslint-disable-next-line
            initialFields.partitionType = PARTITION_TYPE.DAY;
            initialFields.analyticalRules = isMysqlSource ? prefixRule : undefined;
            initialFields.partition = isMysqlSource ? 'pt' : undefined; // 后端（nanqi）要求自动建表默认加一个partition = pt。
            initialFields.writeTableType = isMysqlSource ? WRITE_TABLE_TYPE.AUTO : WRITE_TABLE_TYPE.HAND;
            initialFields.maxFileSize = `${10 * 1024 * 1024}`;
            initialFields.writeMode = 'insert';
            return initialFields;
        }
        case DATA_SOURCE_ENUM.KAFKA:
        case DATA_SOURCE_ENUM.KAFKA_2X:
        case DATA_SOURCE_ENUM.TBDS_KAFKA:
        case DATA_SOURCE_ENUM.KAFKA_HUAWEI: {
            initialFields.dataSequence = false;
            initialFields.partitionKey = undefined;
            return initialFields;
        }
        case DATA_SOURCE_ENUM.ADB_FOR_PG: {
            initialFields.writeMode = 'APPEND';
            initialFields.mappingType = 1;
            return initialFields;
        }
    }
    return initialFields;
}

// 是否可以填选 partition key
function canPartitionKeyWrite(sourceMap: any) {
    return [DATA_SOURCE_ENUM.MYSQL, DATA_SOURCE_ENUM.UPDRDB, DATA_SOURCE_ENUM.ORACLE].includes(sourceMap.type) && sourceMap.rdbmsDaType === SYNC_TYPE.INTERVAL;
}
class CollectionTarget extends React.Component<any, any> {
    formRef = React.createRef<FormInstance>()
    constructor(props: any) {
        super(props);
        this.state = {
            tableList: [],
            partitions: [],
            loading: false,
            dataSourceTypes: [],
            sourceId: null,
            schemaList: [],
            adbTableList: []
        }
    }

    componentDidMount() {
        const { collectionData } = this.props;
        const { targetMap = {}, sourceMap = {} } = collectionData;
        const { sourceId, type, table } = targetMap;
        const { type: dataSourceType, rdbmsDaType, transferType } = sourceMap;
        this.getTargetList({ dataSourceType, rdbmsDaType, transferType: transferType || null })

        if (sourceId) {
            this.onSourceIdChange(type, sourceId, transferType);
            if (isHive(type) && table) {
                this.onHiveTableChange(sourceId, table);
            }
        }
        if (!targetMap.tableMappingList && table?.length) {
            const tableMappingList = table.map((item: string) => {
                return {
                    source: item,
                    sink: null
                }
            })
            const fields = {
                ...targetMap,
                tableMappingList
            }
            streamTaskActions.updateTargetMap(fields, false, true);
        }
    }

    /**
     * 获取目标数据源类型
     * @param params 
     */
    getTargetList(params: {
        dataSourceType: number;
        rdbmsDaType: number;
        transferType: number | null;
    }) {
        stream.getTargetList({ ...params }).then(
            (res: any) => {
                if (res.code == 1) {
                    this.setState({
                        dataSourceTypes: res.data
                    })
                }
            }
        )
    }
    getSchemaList = async (sourceId: any, searchKey?: string) => {
        const res = await stream.listSchemas({ sourceId, isSys: false, searchKey });
        if (res?.code === 1) {
            this.setState({
                schemaList: res.data
            })
        }
    }
    getTableList = (sourceId: number, searchKey?: string) => {
        stream.getStreamTablelist({
            sourceId,
            isSys: false,
            searchKey
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tableList: res.data || []
                });
            }
        });
    }
    /**
     * @param syncSchema -  props 里的 schema 可能是老的，react 还没异步更新，用 syncSchema 传个最新的值
     */
    getSchemaTableList = (searchKey?: string, syncSchema?: string) => {
        const { collectionData } = this.props;
        const { targetMap = {} } = collectionData;
        const { sourceId, schema } = targetMap;
        stream.listTablesBySchema({
            sourceId,
            searchKey,
            schema: syncSchema || schema
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    adbTableList: res.data || []
                });
            }
        });
    }
    /**
     * 数据源改变
     * @param type 
     * @param sourceId 
     * @param transferType 
     */
    onSourceIdChange(type: any, sourceId: any, transferType?: any) {
        this.setState({
            tableList: [],
            partitions: [],
            sourceId,
            schemaList: [],
            adbTableList: []
        })
        if (isHive(type)) {
            this.getTableList(sourceId);
        } else if (type === DATA_SOURCE_ENUM.ADB_FOR_PG) {
            this.getSchemaList(sourceId);
            transferType === 1 && this.getSchemaTableList()
        }
    }

    /**
     * hive表改变
     * @param sourceId 数据源ID
     * @param tableName 表名
     * @returns 
     */
    async onHiveTableChange(sourceId: any, tableName: any) {
        this.setState({
            partition: [],
            partitions: []
        })
        if (!sourceId || !tableName) {
            return;
        }
        this.setState({
            loading: true
        })
        let res = await api.getHivePartitions({
            sourceId,
            tableName
        });
        this.setState({
            loading: false
        })
        if (res && res.code == 1) {
            const partitions = res.data;
            if (partitions && partitions.length) {
                this.setState({
                    partitions: res.data
                });
            }
        }
    }

    onSelectSource = (value: any, option: any) => {
        const sourceType = option.props.data.type;
        const initialFields = getSourceInitialField(sourceType, this.props.collectionData);
        /**
         * sourceId 改变,则清空表
         */
        streamTaskActions.updateTargetMap({ ...initialFields, sourceId: value }, true);
    }


    prev() {
        streamTaskActions.navtoStep(0)
    }

    next() {
        this.formRef.current?.validateFields().then(values => {
            streamTaskActions.navtoStep(2)
        })
    }

    renderDynamic = () => {
        const { collectionData } = this.props;
        const { isEdit, targetMap = {}, sourceMap = {} } = collectionData;
        switch (targetMap.type) {
            case DATA_SOURCE_ENUM.TBDS_KAFKA:
            case DATA_SOURCE_ENUM.KAFKA:
            case DATA_SOURCE_ENUM.KAFKA_2X:
            case DATA_SOURCE_ENUM.KAFKA_09:
            case DATA_SOURCE_ENUM.KAFKA_10:
            case DATA_SOURCE_ENUM.KAFKA_11:
            case DATA_SOURCE_ENUM.KAFKA_HUAWEI: {
                return <Kafka collectionData={collectionData}/>
            }
            case DATA_SOURCE_ENUM.HDFS: {
                return <Hdfs collectionData={collectionData}/>
            }
            case DATA_SOURCE_ENUM.HIVE: {
                return <Hive collectionData={collectionData} />
            }
            case DATA_SOURCE_ENUM.EMQ: {
                return <Emq collectionData={collectionData} />
            }
            case DATA_SOURCE_ENUM.ADB_FOR_PG: {

            }
            default: {
                return null;
            }
        }
    }

    /**
     * 改变提交按钮状态
     */
    onFormValuesChange = () => {
        const { updateCurrentPage } = this.props;
        setTimeout(() => {
            this.formRef.current?.validateFields().then(values => {
                updateCurrentPage({
                    invalidSubmit: false
                });
            }).catch(err => {
                updateCurrentPage({
                    invalidSubmit: true
                });
            })
        }, 200)
    }

    /**
     * 表单值改变监听
     * @param fields 
     */
    handleValuesChange = (fields: any) => {
        if (fields.hasOwnProperty('analyticalRules')) {
            if (fields['analyticalRules']) {
                if (fields['analyticalRules'][0] == '_') {
                    fields['analyticalRules'] = prefixRule + fields['analyticalRules'];
                } else {
                    fields['analyticalRules'] = prefixRule + '_' + fields['analyticalRules'];
                }
            } else {
                fields['analyticalRules'] = prefixRule
            }
        }
        // 建表模式
        if (fields.hasOwnProperty('writeTableType')) {
            if (fields['writeTableType'] == WRITE_TABLE_TYPE.AUTO) {
                fields['fileType'] = 'orc';
                fields['analyticalRules'] = prefixRule;
            } else {
                fields['analyticalRules'] = undefined;
                fields['fileType'] = undefined;
            }
            fields['table'] = undefined;
            fields['partition'] = undefined;
        }
        // 写入表
        if (fields.hasOwnProperty('table')) {
            fields['partition'] = undefined;
        }
        if (fields['maxFileSize']) {
            fields['maxFileSize'] = fields['maxFileSize'] * 1024 * 1024;
        }
        streamTaskActions.updateTargetMap(fields, false);
        if (this.onFormValuesChange) {
            this.onFormValuesChange();
        }
    }
    mapPropsToFields () {
        const { collectionData } = this.props;
        const targetMap = collectionData.targetMap;
        if (!targetMap) return {};
        
        const initMaxFileSize = targetMap?.maxFileSize || targetMap.bufferSize;
        let values = {};
        Object.entries(targetMap).forEach(([key, value]) => {
            if (targetDefaultValue(key)) {
                values = Object.assign({}, values, {
                    [key]: value
                });
            }
        })
        return {
            ...values,
            analyticalRules: targetMap.analyticalRules ? targetMap.analyticalRules.replace(prefixRule, '') : '',
            fileType: targetMap.fileType || 'orc',
            maxFileSize: Number.isNaN(parseInt(initMaxFileSize)) ? undefined : initMaxFileSize / (1024 * 1024)
        }
    }

    render(): React.ReactNode {
        const { readonly, collectionData } = this.props;
        const { isEdit, sourceMap = {}, targetMap = {}, componentVersion } = collectionData;
        const { loading, dataSourceTypes } = this.state;
        return (<div>
            <Form
                ref={this.formRef}
                {...formItemLayout}
                onValuesChange={this.handleValuesChange}
                initialValues={this.mapPropsToFields()}
            >
                <FormItem
                    name='sourceId'
                    rules={[{ required: true, message: '请选择数据源' }]}
                    label="数据源"
                    tooltip={targetMap?.type == DATA_SOURCE_ENUM.HIVE && sourceMap?.allTable ? { title: hiveWithAllTable, icon: <ExclamationCircleOutlined /> } : ''}
                >
                    <Select
                        getPopupContainer={(triggerNode: any) => triggerNode}
                        disabled={isEdit}
                        placeholder="请选择数据源"
                        onSelect={this.onSelectSource}
                        style={{ width: '100%' }}
                        allowClear
                    >
                        {dataSourceTypes.map((item: any) => {
                            const allow110List = [DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.TBDS_KAFKA, DATA_SOURCE_ENUM.KAFKA_HUAWEI, DATA_SOURCE_ENUM.HBASE_HUAWEI];
                            const { ONLY_ALLOW_FLINK_1_10_DISABLED } = getFlinkDisabledSource({
                                version: componentVersion,
                                value: item.type,
                                allow110List
                            });
                            return <Option
                                {...{ data: item }}
                                key={item.id}
                                value={item.id}
                                disabled={ONLY_ALLOW_FLINK_1_10_DISABLED}
                            >
                                {item.dataName}({DATA_SOURCE_TEXT[item.type as DATA_SOURCE_ENUM]})
                            </Option>
                        }).filter(Boolean)}
                    </Select>
                </FormItem>
                {  this.renderDynamic() }
            </Form>
            {!readonly && (
                <div className="steps-action">
                    <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>上一步</Button>
                    <Button loading={loading} type="primary" onClick={() => this.next()}>下一步</Button>
                </div>
            )}
        </div>)
    }
}

export default moleculeConnect(molecule.editor, CollectionTarget);