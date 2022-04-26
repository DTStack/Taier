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

import { useEffect, useState } from 'react';
import type { RadioChangeEvent } from 'antd';
import { Modal, Alert, Radio, DatePicker, Tooltip, message, Space } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import moment from 'moment';
import { showTimeForOffsetReset, formatOffsetResetTime } from '@/utils';
import './reRunModal.scss';

const Api = {
	getTask: () =>
		Promise.resolve({
			code: 1,
			message: null,
			data: {
				source: [],
			},
			space: 0,
			version: null,
			success: true,
		}),
} as any;

const offsetResetFormat = 'YYYY-MM-DD HH:mm:ss';

interface IReRunModalProps {
	visible?: boolean;
	taskId?: number | undefined;
	refresh: () => void;
	onCancel: () => void;
}

interface IOffsetSource {
	sourceId: number;
	tableName?: string;
	timestampOffset?: number;
	type: number;
	table?: string;
}

enum RERUN_TYPE {
	LAST = 'last',
	OFFSET = 'offset',
}

function disabledDate(current: moment.Moment) {
	return current && current > moment().endOf('day');
}

function range(start: number, end: number) {
	const result = [];
	for (let i = start; i < end; i += 1) {
		result.push(i);
	}
	return result;
}

function disabledTime(date: moment.Moment) {
	const formatType = 'YYYY-MM-DD';
	const nowMomentDate = moment(new Date());
	const nowDate = nowMomentDate.format(formatType);
	if (nowDate === date.format(formatType)) {
		// select today
		if (date.hours() < nowMomentDate.hours()) {
			return {
				disabledHours: () => range(nowMomentDate?.hours() + 1, 24),
			};
		}
		if (date.minutes() < nowMomentDate.minutes()) {
			return {
				disabledHours: () => range(nowMomentDate?.hours() + 1, 24),
				disabledMinutes: () => range(nowMomentDate?.minutes() + 1, 60),
			};
		}
		return {
			disabledHours: () => range(nowMomentDate?.hours() + 1, 24),
			disabledMinutes: () => range(nowMomentDate?.minutes() + 1, 60),
			disabledSeconds: () => range(nowMomentDate?.seconds(), 60),
		};
	}
}

export default function ReRunModal({ taskId, visible, onCancel, refresh }: IReRunModalProps) {
	const [checkedValue, setCheckedValue] = useState(RERUN_TYPE.LAST);
	const [offsetSource, setOffsetSource] = useState<IOffsetSource[]>([]);

	const getTaskInfo = async (id?: number) => {
		const res = await Api.getTask({ id });
		if (res.code === 1) {
			const sourceMap: IOffsetSource[] = res.data?.source || [];
			const nextSource = sourceMap.map(({ sourceId, table, timestampOffset, type }) => ({
				sourceId,
				tableName: table,
				timestampOffset: timestampOffset || moment().valueOf(),
				type,
			}));
			setOffsetSource(nextSource);
		}
	};

	const handleChangeRadioValue = (e: RadioChangeEvent) => {
		setCheckedValue(e.target.value);
	};

	const handleConfirmReRun = async () => {
		let reqParams = {};
		let apiName = '';
		if (checkedValue === RERUN_TYPE.LAST) {
			reqParams = {
				id: taskId,
				isRestoration: 0,
			};
			apiName = 'startTask';
		} else {
			reqParams = {
				taskId,
				kafkaOffsetVOS: offsetSource,
			};
			apiName = 'reRunTaskByOffset';
		}
		const res = await Api[apiName](reqParams);
		if (res.code === 1) {
			message.success('任务操作成功！');
			onCancel();
			refresh();
		}
	};

	const changeDateTime = (value: moment.Moment | null, index: number) => {
		const nextSource = [...offsetSource];
		nextSource[index].timestampOffset = value?.valueOf();
		setOffsetSource(nextSource);
	};

	const loopOffsetDatePicker = () => {
		if (offsetSource?.length === 0)
			return <div className="o-modal__datepick--empty">暂未配置Offset Time</div>;
		return offsetSource.map((item, index) => {
			const { tableName, type, timestampOffset, sourceId } = item;
			return (
				<div key={sourceId} className="o-modal__radio--content">
					<span title={tableName}>{`源表${index + 1}(${tableName})`}</span> ：
					{showTimeForOffsetReset(type) ? (
						<DatePicker
							className="o-modal__datepick--content"
							showTime
							allowClear={false}
							disabledDate={disabledDate}
							disabledTime={disabledTime as any}
							onChange={(value) => changeDateTime(value, index)}
							value={formatOffsetResetTime(timestampOffset)}
							format={offsetResetFormat}
						/>
					) : (
						<span className="o-modal__datepick--text">
							Kafka 版本不支持指定Time重跑
						</span>
					)}
					<br />
				</div>
			);
		});
	};

	useEffect(() => {
		if (visible) {
			getTaskInfo(taskId);
		}
	}, [taskId, visible]);

	return (
		<Modal
			className="o-modal"
			title="重跑任务"
			visible={visible}
			onCancel={onCancel}
			onOk={handleConfirmReRun}
		>
			<Alert
				message="重跑，则任务将丢弃停止前的状态，重新运行，若存在启停策略，将恢复自动启停"
				type="warning"
			/>
			<Radio.Group value={checkedValue} onChange={handleChangeRadioValue}>
				<Space direction="vertical">
					<Radio className="o-modal__radio" value={RERUN_TYPE.LAST}>
						使用上次任务参数重跑
					</Radio>
					<Radio className="o-modal__radio--padding" value={RERUN_TYPE.OFFSET}>
						指定Offset Time位置重跑
						<Tooltip title="仅支持Kafka 0.10版本以上的源表从指定Offset Time开始消费，确定后任务自动保存历史版本并进行重跑">
							<ExclamationCircleOutlined />
						</Tooltip>
					</Radio>
				</Space>
			</Radio.Group>
			{checkedValue === RERUN_TYPE.OFFSET && (
				<div className="o-modal__content">{loopOffsetDatePicker()}</div>
			)}
		</Modal>
	);
}
