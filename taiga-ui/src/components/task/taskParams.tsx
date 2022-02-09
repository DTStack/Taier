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

import { Collapse, Input, Form } from 'antd';
import molecule from '@dtinsight/molecule/esm';
import {
	formItemLayout,
	EDIT_TASK_PREFIX,
	EDIT_FOLDER_PREFIX,
	CREATE_TASK_PREFIX,
	PARAMS_ENUM,
	CREATE_DATASOURCE_PREFIX,
} from '@/constant';
import classNames from 'classnames';
import HelpDoc from '../../components/helpDoc';
import type { IOfflineTaskProps, ITaskVariableProps } from '@/interface';
import { useMemo } from 'react';
import './taskParams.scss';

const FormItem = Form.Item;
const { Panel } = Collapse;

// 匹配规则：$[函数]或$[a-z0-9+-两个字符]或随意输入几个字符
// 原来的正则：/(^\$\[(\S+\(\S*\)|[a-z0-9\+\-\:\s\/\\\*]{2,})\]$)|(^(?!\$)\S+$)/i;
// eslint-disable-next-line no-useless-escape
const paramsRegPattern = /^\$[\{\[\(](\S+\((.*)\)|.+)[\}\]\)]$|^(?!\$)\S+$/i;

interface ITaskParamsProps {
	current?: molecule.model.IEditorGroup<any, IOfflineTaskProps> | null;
	onChange?: (
		currentTab: molecule.model.IEditorTab<IOfflineTaskProps>,
		variables: ITaskVariableProps[],
	) => void;
}

/**
 * 不存在调度配置的 tab，譬如修改任务 tab 等
 */
const TAB_WITHOUT_SCHEDULE = [
	EDIT_TASK_PREFIX,
	EDIT_FOLDER_PREFIX,
	CREATE_TASK_PREFIX,
	CREATE_DATASOURCE_PREFIX,
];

export default function TaskParams({ current, onChange }: ITaskParamsProps) {
	const [form] = Form.useForm();

	const handleFormChanged = (changed: Record<string, string>, tabData: IOfflineTaskProps) => {
		const nextTaskVariables = (tabData.taskVariables || []).concat();
		Object.keys(changed).forEach((key) => {
			const target = nextTaskVariables.find((v) => v.paramName === key)!;
			target.paramCommand = changed[key];
		});
		onChange?.(current!.tab!, nextTaskVariables);
	};

	const renderNothing = (text: string) => {
		return (
			<p
				style={{
					textAlign: 'center',
					fontSize: '14px',
					color: '#a1a1a1',
				}}
			>
				{text || '无参数'}
			</p>
		);
	};

	/**
	 * 当前的 tab 是否不合法，如不合法则展示 Empty
	 */
	const isInValidTab = useMemo(
		() =>
			!current ||
			!current.activeTab ||
			TAB_WITHOUT_SCHEDULE.some((prefix) => current.activeTab?.toString().includes(prefix)),
		[current],
	);

	const systemParams = useMemo(() => {
		if (isInValidTab) {
			return [];
		}
		return (
			current?.tab?.data?.taskVariables?.filter((p) => p.type === PARAMS_ENUM.SYSTEM) || []
		);
	}, [current, isInValidTab]);

	const customParams = useMemo(() => {
		if (isInValidTab) {
			return [];
		}
		return (
			current?.tab?.data?.taskVariables?.filter((p) => p.type === PARAMS_ENUM.CUSTOM) || []
		);
	}, [current, isInValidTab]);

	if (isInValidTab) {
		return <div className={classNames('text-center', 'mt-10px')}>无法获取任务参数</div>;
	}

	const tabData = current!.tab!.data!;
	const isLocked = tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock;

	return (
		<molecule.component.Scrollable>
			<Form
				form={form}
				onValuesChange={(changed) => handleFormChanged(changed, tabData)}
				className="taskParams"
				style={{ position: 'relative' }}
			>
				{isLocked ? <div className="cover-mask" /> : null}
				<Collapse className="bg-transparent" bordered={false} defaultActiveKey={['1', '2']}>
					<Panel
						key="1"
						header={
							<span>
								系统参数配置
								<HelpDoc doc="customSystemParams" />
							</span>
						}
					>
						{systemParams.length
							? systemParams.map((param) => (
									<FormItem
										key={param.paramName}
										{...formItemLayout}
										label={param.paramName}
										name={param.paramName}
										rules={[
											{
												pattern: paramsRegPattern,
												message: '参数格式不正确',
											},
										]}
										initialValue={param.paramCommand}
									>
										<Input disabled />
									</FormItem>
							  ))
							: renderNothing('无系统参数')}
					</Panel>
					<Panel
						key="2"
						header={
							<span>
								自定义参数配置
								<HelpDoc doc="customParams" />
							</span>
						}
					>
						{customParams.length
							? customParams.map((param) => (
									<FormItem
										key={param.paramName}
										{...formItemLayout}
										label={param.paramName}
										name={param.paramName}
										rules={[
											{
												pattern: paramsRegPattern,
												message: '参数格式不正确',
											},
										]}
										initialValue={param.paramCommand}
									>
										<Input />
									</FormItem>
							  ))
							: renderNothing('无自定义参数')}
					</Panel>
				</Collapse>
			</Form>
		</molecule.component.Scrollable>
	);
}
