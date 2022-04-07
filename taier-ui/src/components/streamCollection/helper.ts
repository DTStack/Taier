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

import { cloneDeep } from 'lodash';
import { TASK_TYPE_ENUM, DATA_SOURCE_ENUM } from '@/constant'

export function cleanCollectionParams (data: any) {
    let newData = cloneDeep(data);
    if (newData.taskType != TASK_TYPE_ENUM.DATA_COLLECTION) {
        return data;
    }
    const { sourceMap = {}, targetMap = {} } = newData;
    if (!sourceMap || !targetMap) {
        return data;
    }
    const isMysqlSource = isMysqlTypeSource(sourceMap.type)
    if (!isMysqlSource) {
        targetMap.analyticalRules = undefined;
    }
    return newData;
}

export function isMysqlTypeSource (type: number) {
    return [
        DATA_SOURCE_ENUM.MYSQL,
        DATA_SOURCE_ENUM.UPDRDB,
        DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
        DATA_SOURCE_ENUM.ORACLE,
        DATA_SOURCE_ENUM.SQLSERVER,
        DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
        DATA_SOURCE_ENUM.POSTGRESQL
    ].includes(type);
}

export function sourceDefaultValue (key: string): boolean {
    const keys = ['type', 'port', 'rdbmsDaType', 'sourceId', 'topic',
        'collectType', 'cat', 'pavingData', 'journalName', 'multipleTable',
        'increColumn', 'startLocation', 'schema', 'tableName', 'extralConfig',
        'startSCN', 'codec', 'isCleanSession', 'qos', 'lsn', 'message', 'retry',
        'attr', 'codecType', 'collectPoint', 'parse', 'decoder', 'protocol',
        'url', 'requestMode', 'header', 'param', 'body', 'strategy', 'decode',
        'requestInterval', 'startLocation', 'fields', 'fieldDelimiter', 'slotConfig',
        'slotName', 'temporary', 'pdbName', 'mode', 'offset', 'transferType', 'tableMappingList']
    return keys.indexOf(key) > -1
}

export function targetDefaultValue (key: string): boolean {
    const keys = ['sourceId', 'topic', 'writeTableType', 'table', 'partition',
        'writeMode', 'encoding', 'fieldDelimiter', 'fileName', 'path', 'partitionType',
        'isCleanSession', 'qos', 'dataSequence', 'partitionKey', 'schema', 'mappingType']
    return keys.indexOf(key) > -1
}

export function channelDefaultValue (key: string): boolean {
    const keys = ['speed', 'readerChannel', 'writerChannel', 'isSaveDirty', 'sourceId', 'tableName', 'lifeDay']
    return keys.indexOf(key) > -1
}

export function needTables (type: number): boolean {
    return [DATA_SOURCE_ENUM.MYSQL, DATA_SOURCE_ENUM.UPDRDB, DATA_SOURCE_ENUM.POLAR_DB_For_MySQL].includes(type)
}

export function isShowCollapse (type: number): boolean {
    return [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES7, DATA_SOURCE_ENUM.HBASE, DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(type)
}

export function checkUrl (url: string): boolean {
    return /http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- ./?%&=]*)?/.test(url)
}

export function isPostgre (type: number): boolean {
    return [DATA_SOURCE_ENUM.POSTGRESQL].indexOf(type) > -1
}
