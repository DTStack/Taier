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

import type { CSSProperties } from 'react';
import { useEffect, useMemo, useRef, useState } from 'react';
import type { FormInstance, RadioChangeEvent } from 'antd';
import { Row, Col, Collapse, Radio, message } from 'antd';
import FormWrap from './scheduleForm';
import TaskDependence from './taskDependence';
import molecule from '@dtinsight/molecule/esm';
import HelpDoc from '../../../components/helpDoc';
import API from '@/api/operation';
import type { SCHEDULE_STATUS } from '@/constant';
import {
	EDIT_TASK_PREFIX,
	EDIT_FOLDER_PREFIX,
	CREATE_TASK_PREFIX,
	SCHEDULE_DEPENDENCY,
	TASK_PERIOD_ENUM,
} from '@/constant';
import { TASK_TYPE_ENUM } from '@/constant';
import classNames from 'classnames';
import type { IOfflineTaskProps, IScheduleConfProps, ITaskVOProps } from '@/interface';
import type { CheckboxChangeEvent } from 'antd/lib/checkbox';

const { Panel } = Collapse;
const RadioGroup = Radio.Group;

const radioStyle: CSSProperties = {
	display: 'flex',
	height: 30,
	fontSize: 12,
	lineHeight: '30px',
};

const getDefaultScheduleConf = (value: TASK_PERIOD_ENUM) => {
	const scheduleConf: Record<TASK_PERIOD_ENUM, Partial<IScheduleConfProps>> = {
		[TASK_PERIOD_ENUM.MINUTE]: {
			beginMin: 0,
			endMin: 59,
			beginHour: 0,
			endHour: 23,
			gapMin: 5,
			periodType: 0,
			beginDate: '2001-01-01',
			endDate: '2121-01-01',
		},
		[TASK_PERIOD_ENUM.HOUR]: {
			beginHour: 0,
			endHour: 23,
			beginMin: 0,
			gapHour: 5,
			periodType: 1,
		},
		[TASK_PERIOD_ENUM.DAY]: {
			min: 0,
			hour: 0,
			periodType: 2,
			beginDate: '2001-01-01',
			endDate: '2121-01-01',
		},
		[TASK_PERIOD_ENUM.WEEK]: {
			weekDay: 3,
			min: 0,
			hour: 23,
			periodType: 3,
		},
		[TASK_PERIOD_ENUM.MONTH]: {
			day: 5,
			hour: 0,
			min: 23,
			periodType: 4,
		},
	};

	return scheduleConf[value];
};

interface ISchedulingConfigProps extends Pick<molecule.model.IEditor, 'current'> {
	/**
	 * 是否是 workflow 任务, 目前暂不支持
	 */
	isWorkflowNode?: boolean;
	/**
	 * 是否有增量任务, 目前暂不支持
	 */
	isIncrementMode?: boolean;
	/**
	 * 是否是数据科学任务, 目前暂不支持
	 */
	isScienceTask?: boolean;
	/**
	 * 修改调度状态的回调函数
	 */
	changeScheduleConf?: (
		values: molecule.model.IEditorTab<any>,
		nextValue: Partial<{
			scheduleStatus: SCHEDULE_STATUS;
			scheduleConf: string;
			taskVOS: ITaskVOProps[];
		}>,
	) => void;
}

/**
 * 不存在调度配置的 tab，譬如修改任务 tab 等
 */
const TAB_WITHOUT_SCHEDULE = [EDIT_TASK_PREFIX, EDIT_FOLDER_PREFIX, CREATE_TASK_PREFIX];

export default function SchedulingConfig({
	current,
	isWorkflowNode = false,
	isIncrementMode = false,
	isScienceTask = false,
	changeScheduleConf,
}: ISchedulingConfigProps) {
	const [selfReliance, setSelfReliance] = useState<number>(0);
	const form = useRef<FormInstance<IScheduleConfProps & { scheduleStatus: boolean }>>(null);

	const getInitScheduleConf = () => {
		const tabData: IOfflineTaskProps = current!.tab!.data;
		let initConf: Partial<IScheduleConfProps>;
		try {
			initConf = JSON.parse(tabData.scheduleConf);
		} catch (error) {
			initConf = {};
		}

		let scheduleConf = Object.assign(getDefaultScheduleConf(TASK_PERIOD_ENUM.MINUTE), {
			beginDate: '2001-01-01',
			endDate: '2121-01-01',
		});

		scheduleConf = Object.assign(scheduleConf, initConf);
		// 工作流更改默认调度时间配置
		if (isWorkflowNode) {
			scheduleConf = Object.assign(
				getDefaultScheduleConf(TASK_PERIOD_ENUM.DAY),
				{
					beginDate: '2001-01-01',
					endDate: '2121-01-01',
				},
				scheduleConf,
			);
			scheduleConf.periodType = 2;
		}

		return scheduleConf;
	};

	// 调度状态 change 处理函数
	const handleScheduleStatus = (evt: CheckboxChangeEvent) => {
		const { checked } = evt.target;
		const status = checked ? 2 : 1;
		const tabData: IOfflineTaskProps = current!.tab!.data;
		const sucInfo = checked ? '冻结成功' : '解冻成功';
		const errInfo = checked ? '冻结失败' : '解冻失败';
		API.forzenTask({
			taskIdList: [tabData.id],
			scheduleStatus: status,
		}).then((res) => {
			if (res.code === 1) {
				changeScheduleConf?.(current!.tab!, {
					scheduleStatus: status,
				});
				message.info(sucInfo);
			} else {
				message.error(errInfo);
			}
		});
	};

	// 调度依赖change处理方法
	const handleScheduleConf = () => {
		const tabData: IOfflineTaskProps = current!.tab!.data;
		let defaultScheduleConf: Partial<IScheduleConfProps>;
		try {
			defaultScheduleConf = JSON.parse(tabData.scheduleConf);
		} catch (error) {
			defaultScheduleConf = {};
		}
		if (!defaultScheduleConf.periodType) {
			defaultScheduleConf = getDefaultScheduleConf(TASK_PERIOD_ENUM.DAY);
		}

		form.current?.validateFields().then((values: Partial<IScheduleConfProps>) => {
			let formData = values;
			formData.selfReliance = selfReliance;
			/**
			 * 默认重试次数 3次
			 */
			if (formData.isFailRetry) {
				if (!formData.maxRetryNum) {
					formData.maxRetryNum = 3;
				}
			} else {
				formData.maxRetryNum = undefined;
			}
			formData = Object.assign(defaultScheduleConf, formData);
			Reflect.deleteProperty(formData, 'scheduleStatus');
			const newData = {
				scheduleConf: JSON.stringify(formData),
			};
			changeScheduleConf?.(current!.tab!, newData);
		});
	};

	// 调度周期 change 处理函数
	const handleScheduleType = (type: string) => {
		const dft = getDefaultScheduleConf(Number(type));
		if (form.current) {
			const {
				isFailRetry,
				scheduleStatus,
				beginDate,
				endDate,
				selfReliance: nextSelfReliance,
				maxRetryNum,
			} = form.current.getFieldsValue();
			const values = {
				...dft,
				scheduleStatus,
				periodType: type,
				isFailRetry,
				beginDate,
				endDate,
				selfReliance: nextSelfReliance,
			};
			if (isFailRetry) {
				values.maxRetryNum = maxRetryNum;
			}
			const newData = {
				scheduleConf: JSON.stringify(values),
			};
			changeScheduleConf?.(current!.tab!, newData);
		}
	};

	const handleAddVOS = (record: ITaskVOProps) => {
		const taskVOS = (current!.tab?.data.taskVOS || []).concat();
		taskVOS.push(record);
		changeScheduleConf?.(current!.tab!, { taskVOS });
	};

	const handleDelVOS = (record: ITaskVOProps) => {
		const taskVOS: ITaskVOProps[] = (current!.tab?.data.taskVOS || []).concat();
		const index = taskVOS.findIndex((vo) => vo.id === record.id);
		if (index === -1) return;
		taskVOS.splice(index, 1);
		changeScheduleConf?.(current!.tab!, { taskVOS });
	};

	const handleRadioChanged = (evt: RadioChangeEvent) => {
		const { value } = evt.target;
		setSelfReliance(value);
	};

	const isInValidTab = useMemo(
		() =>
			!current ||
			!current.activeTab ||
			TAB_WITHOUT_SCHEDULE.some((prefix) => current.activeTab?.toString().includes(prefix)),
		[current],
	);

	useEffect(() => {
		if (!isInValidTab) {
			handleScheduleConf();
		}
	}, [selfReliance]);

	if (isInValidTab) {
		return <div className={classNames('text-center', 'mt-10px')}>无法获取调度依赖</div>;
	}

	const tabData: IOfflineTaskProps = current!.tab!.data;
	const scheduleConf = getInitScheduleConf();

	return (
		<molecule.component.Scrollable>
			<div className="m-scheduling" style={{ position: 'relative' }}>
				<Collapse bordered={false} defaultActiveKey={['1', '2', '3']}>
					<Panel key="1" header="调度属性">
						<FormWrap
							scheduleConf={scheduleConf}
							status={tabData.scheduleStatus}
							handleScheduleStatus={handleScheduleStatus}
							handleScheduleConf={handleScheduleConf}
							handleScheduleType={handleScheduleType}
							ref={form}
						/>
					</Panel>
					{!isWorkflowNode && tabData.taskType !== TASK_TYPE_ENUM.VIRTUAL_NODE && (
						<Panel key="2" header="任务间依赖">
							<TaskDependence
								handleAddVOS={handleAddVOS}
								handleDelVOS={handleDelVOS}
								tabData={tabData}
							/>
						</Panel>
					)}
					{!isWorkflowNode && (
						<Panel key="3" header="跨周期依赖">
							<Row style={{ marginBottom: '16px' }}>
								<Col offset={1}>
									<RadioGroup
										disabled={isScienceTask}
										onChange={handleRadioChanged}
										value={selfReliance}
									>
										{!isIncrementMode && (
											<Radio
												style={radioStyle}
												value={SCHEDULE_DEPENDENCY.NULL}
											>
												不依赖上一调度周期
											</Radio>
										)}
										<Radio
											style={radioStyle}
											value={SCHEDULE_DEPENDENCY.AFTER_SUCCESS}
										>
											自依赖，等待上一调度周期成功，才能继续运行
										</Radio>
										<Radio
											style={radioStyle}
											value={SCHEDULE_DEPENDENCY.AFTER_DONE}
										>
											自依赖，等待上一调度周期结束，才能继续运行&nbsp;
											<HelpDoc
												style={{
													position: 'inherit',
												}}
												doc={
													!isIncrementMode
														? 'taskDependentTypeDesc'
														: 'incrementModeScheduleTypeHelp'
												}
											/>
										</Radio>
										{!isIncrementMode && (
											<Radio
												style={radioStyle}
												value={SCHEDULE_DEPENDENCY.AFTER_SUCCESS_IN_QUEUE}
											>
												等待下游任务的上一周期成功，才能继续运行
											</Radio>
										)}
										{!isIncrementMode && (
											<Radio
												style={radioStyle}
												value={SCHEDULE_DEPENDENCY.AFTER_DONE_IN_QUEUE}
											>
												等待下游任务的上一周期结束，才能继续运行&nbsp;
												<HelpDoc
													style={{
														position: 'inherit',
													}}
													doc="taskDependentTypeDesc"
												/>
											</Radio>
										)}
									</RadioGroup>
								</Col>
							</Row>
						</Panel>
					)}
				</Collapse>
			</div>
		</molecule.component.Scrollable>
	);
}
