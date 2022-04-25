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
import { Tabs, Radio } from 'antd';
import type { RadioChangeEvent } from 'antd/es/radio';
import SlidePane from '@/components/slidePane';
import type { IStreamTaskProps, ITaskParams } from '@/interface';
import { TaskStatus } from '@/utils/enums';
import RunLog from './components/runLog';
import Failover from './components/runLog/failover';
import CheckPoint from './components/runLog/checkPoint';
import RunCode from './components/runCode';
import History from './components/runLog/historyLog';
import { TASK_TYPE_ENUM } from '@/constant';
import TaskManager from './components/taskManager';
import RunMsg from './components/runMsg';
import StreamDetailGraph from './components/detailGraph';
import './index.scss';

const Api = {} as any;

const TabPane = Tabs.TabPane;

interface IProps {
	data?: IStreamTaskProps;
	visibleSlidePane: boolean;
	extButton: React.ReactNode;
	closeSlidePane: () => void;
}

export default function TaskDetailPane({
	visibleSlidePane,
	data,
	extButton,
	closeSlidePane,
}: IProps) {
	const [tabKey, setTabKey] = useState('taskGraph');
	const [logSubTabKey, setLogSubTabKey] = useState('runLog');
	const [taskParams, setTaskParams] = useState({});

	const getTaskParams = async () => {
		const { taskId, componentVersion } = data!;
		let res = await Api.getTaskParams({ taskId, componentVersion });
		if (res?.code === 1) {
			setTaskParams(res.data || {});
		}
	};

	const onTabChange = (activeKey: string) => {
		setTabKey(activeKey);
		if (activeKey === 'runCode') {
			getTaskParams();
		}
	};

	const subTabChange = (e: RadioChangeEvent) => {
		setLogSubTabKey(e.target.value);
	};

	const getTabs = () => {
		const { taskType, id, taskId } = data!;

		const scrollStyle: React.CSSProperties = {
			position: 'absolute',
			top: '40px',
			bottom: '1px',
			paddingBottom: '1px',
			paddingTop: '16px',
		};

		const scrollStyleNoPt: React.CSSProperties = {
			position: 'absolute',
			top: '40px',
			bottom: '1px',
			overflow: 'auto',
			paddingBottom: '1px',
		};

		const runCodeView = (
			<TabPane style={scrollStyle} tab="属性参数" key="runCode">
				<RunCode isShow={tabKey == 'runCode'} data={taskParams} />
			</TabPane>
		);

		const taskGraph = (
			<TabPane style={scrollStyleNoPt} tab="数据曲线" key="taskGraph">
				{tabKey == 'taskGraph' && <StreamDetailGraph data={data} />}
			</TabPane>
		);

		const runMsg = (
			<TabPane style={scrollStyleNoPt} tab="运行信息" key="runMsg">
				<RunMsg key={id} isShow={tabKey == 'runMsg'} data={data} />
			</TabPane>
		);

		const log = (
			<TabPane style={{ ...scrollStyle, paddingTop: 0 }} tab="日志" key="log">
				<Radio.Group
					style={{ padding: '12px 20px' }}
					value={logSubTabKey}
					onChange={subTabChange}
				>
					<Radio.Button value="runLog">运行日志</Radio.Button>
					<Radio.Button value="failover">failover</Radio.Button>
					<Radio.Button value="taskManager">Task Manager</Radio.Button>
					{taskType !== TASK_TYPE_ENUM.DATA_ACQUISITION && (
						<Radio.Button value="checkpoint">checkpoint</Radio.Button>
					)}
					<Radio.Button value="historyLog">历史日志</Radio.Button>
				</Radio.Group>
				{/* RunLog 里有定时器，tab 时不展示时不会销毁，手动销毁一下 */}
				{logSubTabKey === 'runLog' && tabKey === 'log' && (
					<RunLog key={id} data={data} isShow={logSubTabKey === 'runLog'} />
				)}
				{logSubTabKey === 'failover' && (
					<Failover key={id} isShow={logSubTabKey === 'failover'} data={data} />
				)}
				{logSubTabKey === 'taskManager' && (
					<TaskManager key={id} isShow={logSubTabKey === 'taskManager'} data={data} />
				)}
				{logSubTabKey === 'checkpoint' && <CheckPoint data={data} tabKey={logSubTabKey} />}
				{logSubTabKey === 'historyLog' && (
					<History id={id!} jobId={taskId!} isShow={logSubTabKey === 'historyLog'} />
				)}
			</TabPane>
		);

		let tabs: React.ReactNode[] = [];
		tabs.unshift(taskGraph, runMsg, log);
		tabs.push(runCodeView);
		return tabs.filter(Boolean);
	};

	if (!data) return null;

	return (
		<SlidePane
			onClose={closeSlidePane}
			visible={visibleSlidePane}
			className="dt-slide-pane"
			style={{
				top: '33px',
				right: '0px',
				bottom: '22px',
				width: '60%',
				position: 'fixed',
			}}
		>
			{
				<div className="c-operation__slidePane">
					<header className="detailPane-header">
						<span style={{ fontSize: 14, fontWeight: 500 }}>{data.name}</span>
						<span style={{ marginLeft: '25px' }}>
							<TaskStatus value={data.status} />
						</span>
						<span className='detailPane-header-extra'>{extButton}</span>
					</header>
					<Tabs
						className="c-operation__pane__tabs"
						style={{ position: 'relative' }}
						animated={false}
						onChange={onTabChange}
						activeKey={tabKey}
					>
						{getTabs()}
					</Tabs>
				</div>
			}
		</SlidePane>
	);
}
