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

export interface IModelData {
	id: number;
	modelName: string;
	modelEnName: string;
	dataSourceType: number;
	datasourceUrl?: string;
	modelStatus: number;
	creator: string;
	createTime: string;
	updateTime: string;
	remark: string;
}

// 1 -> 增量; 2 -> 全量;
enum UPDATA_TYPE {
	INCREACEMENT_IPDATE = 1,
	FULL_UPDATE = 2,
}

export enum EnumModelStatus {
	UNRELEASE = 0,
	RELEASE = 1,
	OFFLINE = -1,
}

interface JoinKeyItem {
	schema: string;
	tableName: string;
	columnName: string;
	columnType: string;
	columnComment: string;
	tableAlias?: string;
}

// 关联键信息
export interface JoinKey {
	id?: number;
	leftValue: JoinKeyItem;
	rightValue: JoinKeyItem;
}

// 表关联方式
export enum JoinType {
	LEFT_JOIN = 1,
	RIGHT_JOIN = 2,
	INNER_JOIN = 3,
	// OUTTER_JOIN,
}

export interface FieldColumn {
	id?: number;
	columnComment: string;
	columnName: string;
	columnType: string;
	schema: string;
	tableName: string;
	tableAlias?: string;
	dimension?: boolean;
	metric?: boolean;
	_type?: any;
}

// 表关联信息
export interface TableJoinInfo {
	id?: number;
	leftTable: string;
	leftSchema: string;
	joinType: JoinType;
	schema: string;
	table: string;
	tableAlias: string;
	leftTableAlias?: string;
	updateType: UPDATA_TYPE;
	joinPairs: JoinKey[];
}
export interface IModelDetail {
	id: number;
	modelName: string;
	modelEnName: string;
	dsId: number; // 数据源id
	dsType: 1 | 2; // 1 -> Presto; 2 -> Impala;
	dsTypeName: string;
	dsUrl: string;
	remark: string;
	schema: string;
	tableName: string;
	updateType: UPDATA_TYPE;
	joinList: TableJoinInfo[];
	columns: Partial<FieldColumn>[];
	creator: string;
	// TODO: any类型
	modelPartition: any;
	partition?: boolean;
	createTime?: string;
	dsName?: string;
	step?: number; // 编辑步数step
	modelStatus: EnumModelStatus;
}
