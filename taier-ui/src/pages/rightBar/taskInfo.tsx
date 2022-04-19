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
import { Col, Row, Collapse } from 'antd';
import type { IEditor } from '@dtinsight/molecule/esm/model';
import { formatDateTime } from '@/utils';
import { taskTypeText } from '@/utils/enums';
import classNames from 'classnames';
import { TAB_WITHOUT_DATA } from '.';
import './taskInfo.scss';

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
		const labelPrefix = '任务';

		return (
			<Row className="dt-taskinfo">
				<Col className="dt-taskinfo-key" span={8}>
					{labelPrefix}名称：
				</Col>
				<Col className="dt-taskinfo-value" span={16}>
					{tab.name}
				</Col>
				<Col className="dt-taskinfo-key" span={8}>
					{labelPrefix}类型：
				</Col>
				<Col className="dt-taskinfo-value" span={16}>
					<span>{taskTypeText(tab.data.taskType)}</span>
				</Col>
				<Col className="dt-taskinfo-key" span={8}>
					创建时间：
				</Col>
				<Col className="dt-taskinfo-value" span={16}>
					{formatDateTime(tab.data.gmtCreate)}
				</Col>
				<Col className="dt-taskinfo-key" span={8}>
					修改时间：
				</Col>
				<Col className="dt-taskinfo-value" span={16}>
					{formatDateTime(tab.data.gmtModified)}
				</Col>
				<Col className="dt-taskinfo-key" span={8}>
					描述：
				</Col>
				<Col className={classNames('dt-taskinfo-value', 'leading-20px')} span={16}>
					{tab.data.taskDesc || '-'}
				</Col>
			</Row>
		);
	};

	return (
		<Collapse defaultActiveKey={['1']} bordered={false} className="bg-transparent">
			<Panel header="活动属性" key="1">
				{renderTaskInfo()}
			</Panel>
		</Collapse>
	);
}
