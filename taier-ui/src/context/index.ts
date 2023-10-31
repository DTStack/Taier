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

import { createContext } from 'react';

import type { DATA_SOURCE_ENUM, TASK_TYPE_ENUM } from '@/constant';
import type { IComputeType, IJobType, IOfflineTaskProps } from '@/interface';

export interface IPersonLists {
    email: string;
    id: number;
    phoneNumber: string;
    status: number;
    userName: string;
}

export interface ISupportJobTypes {
    key: TASK_TYPE_ENUM;
    /**
     * 任务名称
     */
    value: string;
    computeType: IComputeType;
    jobType: IJobType;
    /**
     * 任务渲染相关属性
     */
    taskProperties: {
        /**
         * 当前任务渲染所需组件，不填默认用 `editor` 渲染
         * @notice 需要在 `src/pages/editor` 目录下存在对应的组件
         */
        renderKind?: string;
        /**
         * 当前组件只有满足该条件下使用 `renderKind` 定义的组件渲染，否则用 `editor` 渲染
         */
        renderCondition?: {
            key: keyof IOfflineTaskProps;
            value: any;
        };
        /**
         * 当前组件在被创建的时候所需要的额外字段
         * @notice 需要在 `src/components/scaffolds/create.tsx` 中导出相应组件
         */
        formField?: string[];
        /**
         * 当前组件的右侧栏
         * @notice 需要在 {@link https://github.com/DTStack/Taier/blob/master/taier-ui/src/services/rightBarService.tsx#L142-L186 rightBarService#createContent} 方法中返回对应值的组件
         */
        barItem?: string[];
        /**
         * 当前组件在满足该条件下会返回 `barItemCondition.barItem` 作为右侧栏
         */
        barItemCondition?: {
            key: keyof IOfflineTaskProps;
            value: any;
            /**
             * 当满足条件时返回的右侧栏
             */
            barItem: string[];
        };
        /**
         * 当前组件在 actions 按钮
         * @notice 需要和 `src/components/scaffolds/editorActions.tsx` 中导出的组件名一一对应
         */
        actions?: string[];
        /**
         * 当前组件满足该条件下会返回 `actionsCondition.actions` 作为 actions 按钮
         */
        actionsCondition?: {
            key: keyof IOfflineTaskProps;
            value: any;
            /**
             * 当满足条件时返回的 actions 按钮
             */
            actions: string[];
        };
        /**
         * 当前任务类型支持的绑定数据源的任务类型
         */
        dataTypeCodes?: DATA_SOURCE_ENUM[];
    };
}

export interface IContext {
    /**
     * 当前应用全部用户列表
     */
    personList: IPersonLists[];
    /**
     * 当前用户名称
     */
    username?: string;
    /**
     * 当前应用所支持的任务类型
     */
    supportJobTypes: ISupportJobTypes[];
}

export default createContext<IContext>({
    personList: [],
    username: undefined,
    supportJobTypes: [],
});
