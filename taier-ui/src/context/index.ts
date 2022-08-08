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
import type { TASK_TYPE_ENUM } from '@/constant';
import type { IComputeType } from '@/interface';

export enum SupportJobActionKind {
	/**
	 * 请求支持任务
	 */
	REQUEST = 'request',
}

export interface IPersonLists {
	email: string;
	id: number;
	phoneNumber: string;
	status: number;
	userName: string;
}

export interface ISupportJobTypes {
	key: TASK_TYPE_ENUM;
	value: string;
	computeType: IComputeType;
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
	/**
	 * 用于出发当前应用重新获取支持的任务类型
	 * @param verbose 是否输出错误信息，默认不输出
	 */
	dispatch: (action: { type: SupportJobActionKind, verbose?: boolean }) => void;
}

export default createContext<IContext>({
	personList: [],
	username: undefined,
	supportJobTypes: [],
	dispatch: () => {},
});
