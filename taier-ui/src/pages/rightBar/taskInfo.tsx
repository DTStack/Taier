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

import { useMemo } from 'react';
import { Collapse } from 'antd';
import type { IEditor } from '@dtinsight/molecule/esm/model';
import classNames from 'classnames';
import { TAB_WITHOUT_DATA } from '.';
import DetailInfo from '@/components/detailInfo';

const { Panel } = Collapse;

export default function TaskInfo({ current }: Pick<IEditor, 'current'>) {
	/**
	 * 当前的 tab 是否不合法，如不合法则展示 Empty
	 */
	const isInValidTab = useMemo(
		() =>
			!current ||
			!current.activeTab ||
			TAB_WITHOUT_DATA.some((prefix) => current.activeTab?.toString().includes(prefix)),
		[current],
	);

	const renderTaskInfo = () => {
		if (isInValidTab) {
			return <div className={classNames('text-center', 'mt-10px')}>无法提供活动属性</div>;
		}
		const tab = current!.tab!;
		return <DetailInfo type="task" data={tab.data} />;
	};

	return (
		<Collapse defaultActiveKey={['1']} bordered={false} className="bg-transparent">
			<Panel header="活动属性" key="1">
				{renderTaskInfo()}
			</Panel>
		</Collapse>
	);
}
