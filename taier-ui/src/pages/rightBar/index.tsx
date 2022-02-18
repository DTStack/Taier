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

import { useState } from 'react';
import classNames from 'classnames';
import { connect } from '@dtinsight/molecule/esm/react';
import molecule from '@dtinsight/molecule';
import TaskInfo from './taskInfo';
import SchedulingConfig from '@/components/task/schedulingConfig';
import EnvParams from '@/components/task/envParams';
import TaskParams from '@/components/task/taskParams';
import type { IEditorTab } from '@dtinsight/molecule/esm/model';
import type { IOfflineTaskProps, ITaskVariableProps } from '@/interface';
import {
	CREATE_DATASOURCE_PREFIX,
	CREATE_TASK_PREFIX,
	EDIT_DATASOURCE_PREFIX,
	EDIT_FOLDER_PREFIX,
	EDIT_TASK_PREFIX,
} from '@/constant';
import './index.scss';

enum RIGHT_BAR_ITEM {
	TASK = 'task',
	DEPENDENCY = 'dependency',
	TASK_PARAMS = 'task_params',
	ENV_PARAMS = 'env_params',
}

const RIGHTBAR = [
	{
		key: RIGHT_BAR_ITEM.TASK,
		value: '任务属性',
	},
	{
		key: RIGHT_BAR_ITEM.DEPENDENCY,
		value: '调度依赖',
	},
	{
		key: RIGHT_BAR_ITEM.TASK_PARAMS,
		value: '任务参数',
	},
	{
		key: RIGHT_BAR_ITEM.ENV_PARAMS,
		value: '环境参数',
	},
];

interface IProps extends molecule.model.IEditor {
	onTabClick?: (key: string) => void;
}

/**
 * 不存在右边属性配置的 tab，譬如修改任务 tab 等
 */
export const TAB_WITHOUT_DATA = [
	EDIT_TASK_PREFIX,
	EDIT_FOLDER_PREFIX,
	CREATE_TASK_PREFIX,
	CREATE_DATASOURCE_PREFIX,
	EDIT_DATASOURCE_PREFIX,
];

export default connect(molecule.editor, ({ current: propsCurrent, onTabClick }: IProps) => {
	const [current, setCurrent] = useState('');

	const handleClickTab = (key: string) => {
		const nextCurrent = current === key ? '' : key;
		setCurrent(nextCurrent);
		onTabClick?.(nextCurrent);
	};

	const changeScheduleConf = (currentTab: IEditorTab, value: any) => {
		const { data } = currentTab;
		const tab = {
			...currentTab,
			data: {
				...data,
				...value,
			},
		};
		molecule.editor.updateTab(tab);
	};

	const handleChangeVariables = (
		currentTab: IEditorTab<IOfflineTaskProps>,
		variables: ITaskVariableProps[],
	) => {
		molecule.editor.updateTab({
			...currentTab,
			data: {
				...currentTab.data,
				taskVariables: variables,
			},
		});
	};

	const handleValueChanged = (currentTab: IEditorTab, value: string) => {
		const tab = {
			...currentTab,
			data: {
				...currentTab.data,
				taskParams: value,
			},
		};
		molecule.editor.updateTab(tab);
	};

	const renderContent = () => {
		switch (current) {
			case RIGHT_BAR_ITEM.TASK:
				return <TaskInfo current={propsCurrent} />;
			case RIGHT_BAR_ITEM.DEPENDENCY:
				return (
					<SchedulingConfig
						current={propsCurrent}
						changeScheduleConf={changeScheduleConf}
					/>
				);
			case RIGHT_BAR_ITEM.TASK_PARAMS:
				return <TaskParams current={propsCurrent} onChange={handleChangeVariables} />;
			case RIGHT_BAR_ITEM.ENV_PARAMS:
				return <EnvParams current={propsCurrent} onChange={handleValueChanged} />;
			default:
				break;
		}
	};

	return (
		<div className="dt-right-bar">
			<div className="dt-right-bar-content" key={propsCurrent?.activeTab}>
				{renderContent()}
			</div>
			<div className="dt-right-bar-title">
				{RIGHTBAR.map((bar) => (
					<div
						className={classNames(
							'dt-right-bar-title-item',
							current === bar.key && 'active',
						)}
						role="tab"
						key={bar.key}
						onClick={() => handleClickTab(bar.key)}
					>
						{bar.value}
					</div>
				))}
			</div>
		</div>
	);
});
