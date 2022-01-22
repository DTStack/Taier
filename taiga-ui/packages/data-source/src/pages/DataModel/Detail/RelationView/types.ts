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

import { FieldColumn, JoinType } from '../../types';
export enum EnumNodeType {
  TABLE_NAME = 'TABLE_NAME',
  COLUMN_NAME = 'COLUMN_NAME',
  PARTITION_COLUMN = 'PARTITION_COLUMN',
}

export interface IRelationTreeJoinItem {
  tableAlias: string;
  tableName: string;
  schema: string;
  columnType: string;
  columnName: string;
}

export interface IRelationTree {
  tableName: string;
  columns: Partial<FieldColumn>[];
  tableAlias: string;
  joinInfo: null | {
    joinType: JoinType;
    joinPairs: {
      leftValue: IRelationTreeJoinItem;
      rightValue: IRelationTreeJoinItem;
    }[];
  };
  children: IRelationTree[];
  _tableType: EnumTableType;
}

export type LoopCallback = (item: IRelationTree) => void;

export enum EnumTableType {
  PRIMARY = 'PRIMARY',
  RELATION = 'RELATION',
}
