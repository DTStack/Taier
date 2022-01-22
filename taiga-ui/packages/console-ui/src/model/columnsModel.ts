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

export default class ColumnsModel {
    public columnName: any;
    public type: any;
    public paramsName: any;
    public operator: any;
    public required: any;
    public desc: any;
    public id: any;
    constructor (column: any) {
        this.columnName = column.key;
        this.type = column.type;
        this.paramsName = column.paramsName || column.key;
        this.operator = column.operator || '=';
        this.required = typeof column.required == 'undefined' ? true : column.required;
        this.desc = column.desc || null;
        this.id = column.id || new Date().getTime() + '' + ~~(Math.random() * 10000);
    }
    resetDataFromService (column: any) {

    }
}
