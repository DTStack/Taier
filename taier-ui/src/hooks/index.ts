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

import { useEffect, useRef, useState } from 'react';
import api from '@/api';
import notification from '@/components/notification';
import type { ISupportJobTypes } from '@/context';
import { SupportJobActionKind } from '@/context';
import taskRenderService from '@/services/taskRenderService';

interface IPagination {
	current?: number;
	pageSize?: number;
	total?: number;
}

export const usePagination = ({
	current: initialCurrent = 1,
	pageSize: initalPageSize = 20,
	total: initialTotal = 0,
}: IPagination) => {
	const [current, setCurrent] = useState(initialCurrent);
	const [pageSize, setPageSize] = useState(initalPageSize);
	const [total, setTotal] = useState(initialTotal);

	// ensure get the lastest value inside async function
	const pageInfoRef = useRef({ current, pageSize, total });
	useEffect(() => {
		pageInfoRef.current = { current, pageSize, total };
	});

	const setPagination = ({
		current: c,
		pageSize: p,
		total: t,
	}: {
		current?: number;
		pageSize?: number;
		total?: number;
	}) => {
		if (c !== undefined) {
			setCurrent(c);
		}
		if (p !== undefined) {
			setPageSize(p);
		}
		if (t !== undefined) {
			setTotal(t);
		}
	};

	return { ...pageInfoRef.current, setPagination };
};

/**
 * 获取项目支持的任务类型的自定义 hooks
 * @notice 不用 useReducer 的原因是，reducer 貌似不支持 Promise
 */
export function useSupportJobType() {
	const [supportJobTypes, setSupportJobTypes] = useState<ISupportJobTypes[]>([]);

	const dispatch = (action: { type: SupportJobActionKind }) => {
		switch (action.type) {
			case SupportJobActionKind.REQUEST: {
				// 获取当前支持的任务类型
				api.getTaskTypes({}).then((res) => {
					if (res.code === 1) {
						setSupportJobTypes(res.data || []);
						taskRenderService.supportTaskList = res.data || [];
					} else {
						notification.error({
							key: 'FailedJob',
							message: `获取支持的类型失败，将无法创建新的任务！`,
						});
					}
				});
				break;
			}
			default:
				throw new Error();
		}
	};

	return [supportJobTypes, dispatch] as const;
}
