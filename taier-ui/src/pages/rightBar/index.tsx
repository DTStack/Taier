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
	TASK_TYPE_ENUM,
	DATA_SYNC_TYPE
} from '@/constant';
import './index.scss';
import StreamTaskDetail from '@/components/streamCollection/rightBar/taskDetail';
import StreamSetting from '@/components/streamCollection/rightBar/streamSetting';
import FlinkSourcePanel from '@/components/streamCollection/rightBar/flinkSource';
import FlinkResultPanel from '@/components/streamCollection/rightBar/flinkResult';
import FlinkDimensionPanel from '@/components/streamCollection/rightBar/flinkDimension';

enum RIGHT_BAR_ITEM {
	TASK = 'task',
	DEPENDENCY = 'dependency',
	TASK_PARAMS = 'task_params',
	ENV_PARAMS = 'env_params',
	STREAM_INFO = 'stream_info',
	STREAM_SETTING = 'stream_setting',
	FLINKSQL_SOURCE = 'flinksql_source',
	FLINKSQL_RESULT = 'flinksql_result',
	FLINKSQL_DIMENSION = 'flinksql_dimension',
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
const STREAM_RIGHTBAR = [
	{
		key: RIGHT_BAR_ITEM.STREAM_INFO,
		value: '任务详情',
	},
	{
		key: RIGHT_BAR_ITEM.ENV_PARAMS,
		value: '环境参数',
	},
	{
		key: RIGHT_BAR_ITEM.STREAM_SETTING,
		value: '任务设置',
	},
];
const FLINKSQL_RIGHTBAR = [
	{
		key: RIGHT_BAR_ITEM.STREAM_INFO,
		value: '任务详情',
	},
	{
		key: RIGHT_BAR_ITEM.FLINKSQL_SOURCE,
		value: '源表',
	},
	{
		key: RIGHT_BAR_ITEM.FLINKSQL_RESULT,
		value: '结果表',
	},
	{
		key: RIGHT_BAR_ITEM.FLINKSQL_DIMENSION,
		value: '维表',
	},
	{
		key: RIGHT_BAR_ITEM.ENV_PARAMS,
		value: '环境参数',
	},
	{
		key: RIGHT_BAR_ITEM.STREAM_SETTING,
		value: '任务设置',
	},
];

interface IProps extends molecule.model.IEditor {
	onTabClick?: (key: string) => void;
	width: number;
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

export default connect(molecule.editor, ({ current: propsCurrent, onTabClick, width }: IProps) => {
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

	const renderTabs = () => {
		switch (propsCurrent?.tab?.data?.taskType) {
			case TASK_TYPE_ENUM.SYNC:
				return RIGHTBAR;
			case TASK_TYPE_ENUM.DATA_COLLECTION:
				return STREAM_RIGHTBAR;
			case TASK_TYPE_ENUM.FLINKSQL:
				if (propsCurrent?.tab?.data?.createModel === DATA_SYNC_TYPE.GUIDE) {
					return FLINKSQL_RIGHTBAR;
				} else {
					return STREAM_RIGHTBAR
				}
			default:
				return FLINKSQL_RIGHTBAR
		}
	}

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
			case RIGHT_BAR_ITEM.STREAM_INFO:
				return <StreamTaskDetail current={propsCurrent} />
			case RIGHT_BAR_ITEM.STREAM_SETTING:
				return <StreamSetting current={propsCurrent} />
			case RIGHT_BAR_ITEM.FLINKSQL_SOURCE:
				return <FlinkSourcePanel current={propsCurrent}/>;
			case RIGHT_BAR_ITEM.FLINKSQL_RESULT:
				return <FlinkResultPanel current={propsCurrent} />;
			case RIGHT_BAR_ITEM.FLINKSQL_DIMENSION:
				return <FlinkDimensionPanel current={propsCurrent} />;
			default:
				break;
		}
	};

	return (
		<div className="dt-right-bar" style={{ width }}>
			<div className="dt-right-bar-content" key={propsCurrent?.activeTab}>
				{renderContent()}
			</div>
			<div className="dt-right-bar-title">
				{renderTabs().map((bar) => (
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
