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

declare module 'typing' {

    /**
     * 数组或者字符串类型
     */
    export type numOrStr = number | string;

    /**
     * 响应结果
     */
    export interface Response<D = any, R = any> {
        /**
         * 状态码, 1 成功， 0 失败
         */
        code: number;
        message?: string;
        result?: R;
        data?: D;
        space?: number;
    }

    /**
     * 分页类型
     */
    export interface Pagination {
        current?: number;
        total: number;
        pageSize?: number;
    }

}
