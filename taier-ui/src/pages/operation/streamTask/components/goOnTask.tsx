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

import { useEffect, useMemo, useState } from 'react';
import moment from 'moment';
import { Space, message, Modal, DatePicker, Radio, Select, Alert, Input, Form } from 'antd';
import type { RadioChangeEvent } from 'antd';
import { DateTime } from '@dtinsight/dt-utils';
import { CHECK_TYPE_VALUE } from '@/constant';
import type { FormItemProps, ValidateStatus } from 'antd/lib/form/FormItem';
import type { RangePickerProps } from 'antd/lib/date-picker';
import { isEmpty } from 'lodash';

const Api = {
	getSavePoint: () =>
		Promise.resolve({
			code: 1,
			message: null,
			data: { id: null, time: null, externalPath: null },
			space: 0,
			version: null,
			success: true,
		}),
	getCheckPointRange: () =>
		Promise.resolve({
			code: 1,
			message: null,
			data: { startTime: null, endTime: null },
			space: 0,
			version: null,
			success: true,
		}),
} as any;

const { RangePicker } = DatePicker;
const { Option } = Select;

interface IProps {
	taskId: number | undefined;
	visible: boolean;
	onOk: () => void;
	onCancel: () => void;
}

interface ICheckPoint {
	id: number;
	time: string;
	externalPath: string;
}

// 文件路径校验
function validateFilePath(value: string) {
	let help = '';
	let validateStatus: ValidateStatus = 'success';
	if (!/^hdfs:\/\//.test(value)) {
		help = '请输入以”hdfs://”开头的HDFS地址';
		validateStatus = 'error';
	} else if (/\s/.test(value)) {
		help = '文件路径不支持空格';
		validateStatus = 'error';
	}
	return { help, validateStatus };
}

export default function GoOnTask({ visible, taskId, onCancel, onOk }: IProps) {
	const [checkedValue, setCheckedValue] = useState(CHECK_TYPE_VALUE.CHECK_POINT_FILE);
	const [savePoint, setSavePoint] = useState<{ externalPath: null; id: null; time: null }>();
	const [dateRange, setDateRange] = useState<null | { startTime: string; endTime: string }>(null);
	const [rangeValue, setRangeValue] = useState<RangePickerProps['value'] | undefined>(undefined);
	const [externalPath, setExternalPath] = useState<string | undefined>(undefined);
	const [checkPoints, setCheckPoints] = useState<ICheckPoint[]>([]);
	const [filePath, setFilePath] = useState<{
		value?: string;
		help?: FormItemProps['help'];
		validateStatus?: FormItemProps['validateStatus'];
	}>({ value: undefined, help: undefined, validateStatus: undefined });

	const getCheckPointRange = (params: { taskId: number }) => {
		Api.getCheckPointRange(params).then((res: any) => {
			if (res.code === 1) {
				const { startTime, endTime } = res.data;
				if (startTime && endTime) {
					setDateRange(res.data);
					// get the lastest savepoint value and check the existence
					setSavePoint((prev) => {
						setCheckedValue(
							isEmpty(savePoint)
								? CHECK_TYPE_VALUE.CHECK_POINT
								: CHECK_TYPE_VALUE.SAVE_POINT,
						);

						return prev;
					});
				}
			}
		});
	};

	const getCheckPoints = (params: any) => {
		if (!dateRange) return;
		Api.getCheckPoints(params).then((res: any) => {
			if (res.code === 1) {
				setCheckPoints(res.data);
			}
		});
	};

	const getSavePoint = async (params: { taskId: number }) => {
		const res = await Api.getSavePoint(params);
		if (res.code === 1) {
			if (Object.values(res.data).every((d) => !!d)) {
				setSavePoint(res.data);
				setCheckedValue(CHECK_TYPE_VALUE.SAVE_POINT);
			}
		}

		return res;
	};

	const handleCheckedChange = (e: RadioChangeEvent) => {
		setCheckedValue(e.target.value);
	};

	// 通过文件续跑
	const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const { value } = e.target;
		const filePathValid = validateFilePath(value);
		setFilePath({
			value,
			...filePathValid,
		});
	};

	const taskReadRangeChange = (value: RangePickerProps['value']) => {
		setRangeValue(value);
		setExternalPath(undefined);
		setCheckPoints([]);
		if (!value) return;

		const start = value[0]?.hour(0).minute(0).second(0);
		const end = value[1]?.hour(23).minute(59).second(59);

		getCheckPoints({
			taskId,
			startTime: start?.valueOf(),
			endTime: end?.valueOf(),
		});
	};

	/**
	 * 置灰startTime至endTime时间段之外的时间
	 */
	const disabledDate = (current: moment.Moment) => {
		if (!dateRange) return false;
		const startTime = moment(dateRange.startTime);
		const endTime = moment(dateRange.endTime);
		startTime.set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
		endTime.set({ hour: 23, minute: 59, second: 59, millisecond: 0 });

		return current.valueOf() < startTime.valueOf() || current.valueOf() > endTime.valueOf();
	};

	// 处理 path
	const getExternalPath = () => {
		switch (checkedValue) {
			case CHECK_TYPE_VALUE.CHECK_POINT:
				return externalPath;
			case CHECK_TYPE_VALUE.SAVE_POINT:
				return savePoint?.externalPath;
			case CHECK_TYPE_VALUE.CHECK_POINT_FILE:
				return filePath.value;
			default:
				return null;
		}
	};

	// 提交续跑
	const doGoOn = () => {
		// 文件路径校验
		if (
			checkedValue === CHECK_TYPE_VALUE.CHECK_POINT_FILE &&
			filePath.validateStatus === 'error'
		) {
			return;
		}
		const pathInParam = getExternalPath();
		if (!pathInParam) {
			message.error('请选择续跑点！');
			return;
		}
		Api.startTask({
			id: taskId,
			externalPath: pathInParam,
			isRestoration: 0,
		}).then((res: any) => {
			if (res.code === 1) {
				message.success('续跑操作成功！');
				onOk();
			}
		});
	};

	const cancel = () => {
		// reset all values in form
		setExternalPath(undefined);
		setDateRange(null);
		setCheckPoints([]);
		setRangeValue(undefined);
		setSavePoint(undefined);
		setCheckedValue(CHECK_TYPE_VALUE.CHECK_POINT_FILE);
		setFilePath({ value: undefined, help: undefined, validateStatus: undefined });
		onCancel();
	};

	useEffect(() => {
		if (visible && taskId) {
			getSavePoint({ taskId }).then(() => {
				// 优先级,有 savepoint 默认选 savepoint 续跑，没有就默认选择 checkpoint, 最后文件续跑
				getCheckPointRange({ taskId });
			});
		}
	}, [visible]);

	const options = useMemo(
		() =>
			checkPoints?.map((item) => {
				const { time, id, externalPath: value } = item || {};
				const title = DateTime.formatDateTime(time);
				const nameFix = { name: item };

				return (
					<Option title={title} key={id} value={value} {...nameFix}>
						{time}
					</Option>
				);
			}),
		[checkPoints],
	);

	return (
		<Modal
			title="续跑任务"
			visible={visible}
			okText="确认"
			onCancel={cancel}
			onOk={doGoOn}
			cancelText="取消"
			maskClosable={false}
		>
			<Alert
				message="续跑，任务将恢复至停止前的状态继续运行，若存在启停策略，将恢复自动启停!"
				type="warning"
				showIcon
			/>
			<Radio.Group value={checkedValue} onChange={handleCheckedChange}>
				<Space direction="vertical" size={20}>
					<Radio disabled={!savePoint?.time} value={CHECK_TYPE_VALUE.SAVE_POINT}>
						{`通过SavePoint恢复并续跑（上次保存时间：${
							savePoint?.time ? DateTime.formatDateTime(savePoint.time) : '- '
						}）`}
					</Radio>
					<Radio disabled={!savePoint?.time} value={CHECK_TYPE_VALUE.SAVE_POINT}>
						{`通过SavePoint恢复并续跑（上次保存时间：${
							savePoint?.time ? DateTime.formatDateTime(savePoint.time) : '- '
						}）`}
					</Radio>
					<div>
						<Radio disabled={!dateRange} value={CHECK_TYPE_VALUE.CHECK_POINT}>
							通过CheckPoint恢复并续跑
						</Radio>
						<div>
							<span style={{ marginRight: '12px' }}>
								<RangePicker
									style={{ width: '280px' }}
									format="YYYY-MM-DD"
									disabledDate={disabledDate}
									onChange={taskReadRangeChange}
									value={rangeValue}
									disabled={
										!dateRange || checkedValue !== CHECK_TYPE_VALUE.CHECK_POINT
									}
								/>
							</span>
							<span>
								<Select
									showSearch
									style={{ width: '180px' }}
									placeholder="时间点"
									optionFilterProp="name"
									onChange={(value) => setExternalPath(value)}
									disabled={
										!dateRange || checkedValue !== CHECK_TYPE_VALUE.CHECK_POINT
									}
									value={externalPath}
								>
									{options}
								</Select>
							</span>
						</div>
					</div>
					<div>
						<Radio value={CHECK_TYPE_VALUE.CHECK_POINT_FILE}>
							通过指定文件恢复并续跑
						</Radio>
						<Form.Item
							style={{ marginBottom: 0 }}
							help={filePath.help}
							validateStatus={filePath.validateStatus}
						>
							<Input
								placeholder="请输入HDFS中CheckPoin文件完整路径，例如：hdfs://"
								disabled={checkedValue !== CHECK_TYPE_VALUE.CHECK_POINT_FILE}
								value={filePath.value}
								onChange={handleInputChange}
							/>
						</Form.Item>
					</div>
				</Space>
			</Radio.Group>
		</Modal>
	);
}
