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

import { FLINK_VERSIONS, SOURCE_TIME_TYPE, TABLE_SOURCE } from "@/constant";
import { isAvro, isKafka } from "@/utils/enums";
import { checkColumnsData } from "../taskFunc";

export function parseColumnText (text = ''): any {
    const columns = text.split('\n').filter(Boolean).map((v: any) => {
        let column: any;
        const asCase = /^.*\w.*\s+as\s+(\w+)$/i.exec(v);
        if (asCase) {
            return {
                column: asCase[1]
            }
        } else {
            column = v.trim().split(' ');
        }
        return { column: column[0], type: column[1] }
    }).filter((v: any) => {
        return v.column;
    })
    return columns;
}

/** 源表中的表单项 */
export function inputDefaultValue (key: string): boolean {
    const keys = ['topic', 'charset', 'table', 'columns', 'timeType', 'timeTypeArr', 'timeColumn', 'procTime',
        'offset', 'offsetUnit', 'offsetReset', 'columnsText', 'parallelism', 'schemaInfo', 'createType', 'dbId', 'tableId']
    return keys.includes(key)
}

export function outputDefaultValue (key: string): boolean {
    const keys = ['table', 'columns', 'columnsText', 'id', 'index',
        'writePolicy', 'esId', 'esType', 'topic', 'isUpsert', 'advanConf', 'rowKey', 'rowKeyType',
        'parallelism', 'batchWaitInterval', 'batchSize', 'tableName', 'primaryKey',
        'partitionfields', 'partitionKeys', 'enableKeyPartitions', 'schema', 'bucket', 'collection',
        'objectName', 'allReplace', 'updateMode', 'indexDefinition', 'schemaInfo', 'createType', 'dbId',
        'tableId', 'partitionType', 'bulkFlushMaxActions']
    return keys.includes(key)
}

export function dimensionDefaultValue (key: string): boolean {
    const keys = ['table', 'tableName', 'columns', 'index', 'esType', 'parallelism',
        'columnsText', 'partitionedJoin', 'lowerBoundPrimaryKey', 'upperBoundPrimaryKey',
        'keyFilter', 'isFaultTolerant', 'cache', 'cacheSize', 'cacheTTLMs', 'errorLimit',
        'primaryKey', 'hbasePrimaryKey', 'hbasePrimaryKeyType', 'advanConf', 'schema',
        'asyncPoolSize', 'createType', 'dbId', 'tableId']
    return keys.indexOf(key) > -1
}

export const assetValidRules = {
    dbId: [
        { required: true, message: '请选择数据库' }
    ],
    tableId: [
        { required: true, message: '请选择数据表' }
    ],
    columns: [
        { required: true, message: '字段信息不能为空', type: 'array' },
        { validator: checkColumnsData }
    ]
}

export const generateSourceValidDes = function (data: any, componentVersion?: string) {
    const isFlink112 = componentVersion == FLINK_VERSIONS.FLINK_1_12
    const haveSchema = isKafka(data?.type) && isAvro(data?.sourceDataType) &&
        componentVersion !== FLINK_VERSIONS.FLINK_1_12;

    const isCreateByStream = data?.createType !== TABLE_SOURCE.DATA_ASSET;
    const createByStreamRules = {
        type: [
            { required: true, message: '请选择类型' }
        ],
        sourceId: [
            { required: true, message: '请选择数据源' }
        ],
        topic: [
            { required: true, message: '请选择Topic' }
        ],
        table: [
            { required: true, message: '请输入映射表名' }
        ],
        columnsText: [
            { required: true, message: '字段信息不能为空！' }
        ]
    }
    return Object.assign({}, isCreateByStream ? createByStreamRules : assetValidRules, {
        sourceDataType: [
            { required: isKafka(data?.type), message: '请选择读取类型' }
        ],
        schemaInfo: [
            { required: haveSchema, message: '请输入Schema' }
        ],
        timeColumn: [
            {
                required: (!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
                    (isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
                message: '请选择时间列'
            }
        ],
        offset: [
            {
                required: (!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
                    (isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
                message: '请输入最大延迟时间'
            }
        ]
    })
}