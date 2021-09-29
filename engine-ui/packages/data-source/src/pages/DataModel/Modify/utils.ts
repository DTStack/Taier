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

export const joinPairsParser = {
  // Object 2 string
  encode: (joinObj) => {
    const lSchema = joinObj.leftValue.schema;
    const lTable = joinObj.leftValue.tableName;
    const lCol = joinObj.leftValue.columnName;
    const rSchema = joinObj.rightValue.schema;
    const rTable = joinObj.rightValue.tableName;
    const rCol = joinObj.rightValue.columnName;
    return {
      leftValue: `${lSchema}-${lTable}-${lCol}`,
      rightvalue: `${rSchema}-${rTable}-${rCol}`,
    };
  },
  // string 2 Object
  /**
   * @param joinItemStr {leftValue: string, rightValue: string }
   * @returns
   */
  decode: (joinItemStr) => {
    const [lSchema, lTable, lCol] = joinItemStr.leftValue.split('-');
    const [rSchema, rTable, rCol] = joinItemStr.rightValue.split('-');
    return {
      leftValue: {
        schema: lSchema,
        tableName: lTable,
        columnName: lCol,
      },
      rightValue: {
        schema: rSchema,
        tableName: rTable,
        columnName: rCol,
      },
    };
  },
};
