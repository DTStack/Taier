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
}

export interface IContext {
	personList: IPersonLists[];
	username?: string;
	supportJobTypes: ISupportJobTypes[];
}

export default createContext<IContext>({
	personList: [],
	username: undefined,
	supportJobTypes: [],
});
