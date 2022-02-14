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

import moment from 'moment';
import get from 'lodash/get';
import type { SelectProps, CheckboxProps } from 'antd';
import { Form, Checkbox, DatePicker, Select, Input, Radio } from 'antd';
import HelpDoc from '../../../components/helpDoc';
import { formItemLayout, SCHEDULE_STATUS, TASK_PERIOD_ENUM } from '@/constant';
import type { IScheduleConfProps } from '@/interface';
import { forwardRef } from 'react';
import { useImperativeHandle } from 'react';

const { Group } = Radio;
const { Option } = Select;
const FormItem = Form.Item;

interface IFormWrapProps {
	scheduleConf: Partial<IScheduleConfProps>;
	status: SCHEDULE_STATUS;
	/**
	 * 是否为数据科学任务
	 */
	isScienceTask?: boolean;
	/**
	 * 是否为 workflow root
	 */
	isWorkflowRoot?: boolean;
	isWorkflowNode?: boolean;
	/**
	 * 调度配置发生修改的回调函数
	 */
	handleScheduleConf: () => void;
	/**
	 * 调度状态项发生修改的回调函数
	 */
	handleScheduleStatus: CheckboxProps['onChange'];
	/**
	 * 调度周期发生修改的回调函数
	 */
	handleScheduleType: SelectProps<string>['onChange'];
}

const HOURS_OPTIONS = new Array(24)
	.fill(1)
	.map((_, i) => ({ label: i < 10 ? `0${i}` : i.toString(), value: i.toString() }));
const MINS_OPTIONS = new Array(60)
	.fill(1)
	.map((_, i) => ({ label: i < 10 ? `0${i}` : i.toString(), value: i.toString() }));
const GAP_OPTIONS = new Array(11).fill(1).map((_, i) => ({
	label: `${(i + 1) * 5}分钟`,
	value: ((i + 1) * 5).toString(),
}));
const WEEKS_OPTIONS = ['一', '二', '三', '四', '五', '六', '天'].map((day, index) => ({
	label: `星期${day}`,
	value: (index + 1).toString(),
}));
const DAYS_OPTIONS = new Array(30).fill(1).map((_, i) => ({
	label: `每月${i + 1}号`,
	value: (i + 1).toString(),
}));
const RETRY_OPTIONS = new Array(5).fill(1).map((_, i) => ({
	label: i + 1,
	value: (i + 1).toString(),
}));

export default forwardRef(
	(
		{
			scheduleConf,
			status,
			isScienceTask,
			isWorkflowRoot,
			isWorkflowNode,
			handleScheduleStatus,
			handleScheduleConf,
			handleScheduleType,
		}: IFormWrapProps,
		ref,
	) => {
		const [form] = Form.useForm();
		const {
			periodType,
			beginDate,
			beginHour,
			beginMin,
			endHour,
			endMin,
			endDate,
			isLastInstance,
			gapMin,
			weekDay,
			hour,
			min,
			day,
			gapHour,
		} = scheduleConf;

		useImperativeHandle(ref, () => ({
			...form,
		}));

		const changeStartDisabledDate = (currentDate: moment.Moment) => {
			const date = form.getFieldValue('endDate');
			return date && currentDate.valueOf() > date;
		};

		const changeEndDisabledDate = (currentDate: moment.Moment) => {
			const date = form.getFieldValue('beginDate');
			return date && currentDate.valueOf() < date;
		};

		const checkTimeS1 = () => {
			const currentBeginHour = +form.getFieldValue('beginHour');
			const currentBeginMin = +form.getFieldValue('beginMin');
			const currentEndHour = +form.getFieldValue('endHour') * 60 + 59;

			if (currentBeginHour * 60 + currentBeginMin > currentEndHour) {
				return Promise.reject(new Error('开始时间不能晚于结束时间'));
			}
			return Promise.resolve();
		};

		const checkTimeE1 = () => {
			const currentBeginHour = +form.getFieldValue('beginHour');
			const currentBeginMin = +form.getFieldValue('beginMin');
			const currentEndHour = +form.getFieldValue('endHour') * 60 + 59;

			if (currentBeginHour * 60 + currentBeginMin > currentEndHour) {
				return Promise.reject(new Error('结束时间不能早于开始时间'));
			}
			return Promise.resolve();
		};

		const renderTimeConfig = (type: number) => {
			switch (type) {
				case TASK_PERIOD_ENUM.MINUTE: {
					return (
						<span key={type}>
							<FormItem {...formItemLayout} label="开始时间" required>
								<FormItem
									noStyle
									name="beginHour"
									rules={[
										{
											required: true,
										},
										{
											validator: checkTimeS1,
										},
									]}
									initialValue={`${beginHour}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={HOURS_OPTIONS}
									/>
								</FormItem>
								<span className="mx-5px">时</span>
								<FormItem
									noStyle
									name="beginMin"
									rules={[
										{
											required: true,
										},
										{
											validator: checkTimeS1,
										},
									]}
									initialValue={`${beginMin || '0'}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={MINS_OPTIONS}
									/>
								</FormItem>
								<span className="ml-5px">分</span>
							</FormItem>
							<FormItem
								{...formItemLayout}
								label="间隔时间"
								name="gapMin"
								rules={[
									{
										required: true,
									},
								]}
								initialValue={`${gapMin}`}
							>
								<Select
									disabled={isScienceTask}
									onChange={handleScheduleConf}
									options={GAP_OPTIONS}
								/>
							</FormItem>
							<FormItem {...formItemLayout} label="结束时间" required>
								<FormItem
									noStyle
									name="endHour"
									rules={[
										{
											required: true,
										},
										{
											validator: checkTimeE1,
										},
									]}
									initialValue={`${endHour}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={HOURS_OPTIONS}
									/>
								</FormItem>
								<span className="mx-5px">时</span>
								<FormItem
									noStyle
									name="endMin"
									rules={[
										{
											required: true,
										},
										{
											validator: checkTimeE1,
										},
									]}
									initialValue={`${endMin || '59'}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={MINS_OPTIONS}
									/>
								</FormItem>
								<span className="ml-5px">分</span>
							</FormItem>
						</span>
					);
				}
				case TASK_PERIOD_ENUM.HOUR: {
					return (
						<span key={type}>
							<FormItem {...formItemLayout} label="开始时间" required>
								<FormItem
									noStyle
									name="beginHour"
									rules={[
										{
											required: true,
										},
										{
											validator: checkTimeS1,
										},
									]}
									initialValue={`${beginHour}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={HOURS_OPTIONS}
									/>
								</FormItem>
								<span className="mx-5px">时</span>
								<FormItem
									noStyle
									name="beginMin"
									rules={[
										{
											required: true,
										},
										{
											validator: checkTimeS1,
										},
									]}
									initialValue={`${beginMin || '0'}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={MINS_OPTIONS}
									/>
								</FormItem>
								<span className="ml-5px">分</span>
							</FormItem>
							<FormItem
								{...formItemLayout}
								label="间隔时间"
								name="gapHour"
								rules={[
									{
										required: true,
									},
								]}
								initialValue={`${gapHour}`}
							>
								<Select onChange={handleScheduleConf} options={HOURS_OPTIONS} />
							</FormItem>
							<FormItem {...formItemLayout} label="结束时间" required>
								<FormItem
									noStyle
									name="endHour"
									rules={[
										{
											required: true,
										},
										{
											validator: checkTimeE1,
										},
									]}
									initialValue={`${endHour}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={HOURS_OPTIONS}
									/>
								</FormItem>
								<span className="mx-5px">时</span>
								<FormItem
									noStyle
									name="endMin"
									rules={[
										{
											required: true,
										},
										{
											validator: checkTimeE1,
										},
									]}
									initialValue={`${endMin || '59'}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={MINS_OPTIONS}
									/>
								</FormItem>
								<span className="ml-5px">分</span>
							</FormItem>
						</span>
					);
				}
				case TASK_PERIOD_ENUM.DAY: {
					const prefix = isWorkflowNode ? '起调' : '具体';
					return (
						<span key={type}>
							<FormItem {...formItemLayout} label={`${prefix}时间`} required>
								<FormItem
									noStyle
									name="hour"
									rules={[
										{
											required: true,
										},
									]}
									initialValue={`${hour}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={HOURS_OPTIONS}
									/>
								</FormItem>
								<span className="mx-5px">时</span>
								<FormItem
									noStyle
									name="min"
									rules={[
										{
											required: true,
										},
									]}
									initialValue={`${min}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={MINS_OPTIONS}
									/>
								</FormItem>
								<span className="ml-5px">分</span>
							</FormItem>
						</span>
					);
				}
				case TASK_PERIOD_ENUM.WEEK: {
					return (
						<span key={type}>
							<FormItem
								{...formItemLayout}
								label="选择时间"
								name="weekDay"
								rules={[
									{
										required: true,
									},
								]}
								initialValue={`${weekDay}`.split(',')}
							>
								<Select
									mode="multiple"
									style={{ width: '100%' }}
									disabled={isScienceTask}
									onChange={handleScheduleConf}
									options={WEEKS_OPTIONS}
								/>
							</FormItem>
							<FormItem {...formItemLayout} label="具体时间" required>
								<FormItem
									noStyle
									name="hour"
									rules={[
										{
											required: true,
										},
									]}
									initialValue={`${hour}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={HOURS_OPTIONS}
									/>
								</FormItem>
								<span className="mx-5px">时</span>
								<FormItem
									noStyle
									name="min"
									rules={[
										{
											required: true,
										},
									]}
									initialValue={`${min}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={MINS_OPTIONS}
									/>
								</FormItem>
								<span className="ml-5px">分</span>
							</FormItem>
						</span>
					);
				}
				case TASK_PERIOD_ENUM.MONTH: {
					return (
						<span key={type}>
							<FormItem
								{...formItemLayout}
								label="选择时间"
								name="day"
								rules={[
									{
										required: true,
									},
								]}
								initialValue={`${day}`.split(',')}
							>
								<Select
									mode="multiple"
									style={{ width: '100%' }}
									disabled={isScienceTask}
									onChange={handleScheduleConf}
									options={DAYS_OPTIONS}
								/>
							</FormItem>
							<FormItem {...formItemLayout} label="具体时间" required>
								<FormItem
									noStyle
									name="hour"
									rules={[
										{
											required: true,
										},
									]}
									initialValue={`${hour}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={HOURS_OPTIONS}
									/>
								</FormItem>
								<span className="mx-5px">时</span>
								<FormItem
									noStyle
									name="min"
									rules={[
										{
											required: true,
										},
									]}
									initialValue={`${min}`}
								>
									<Select
										style={{ width: '40%' }}
										onChange={handleScheduleConf}
										options={MINS_OPTIONS}
									/>
								</FormItem>
								<span className="ml-5px">分</span>
							</FormItem>
						</span>
					);
				}

				default:
					return <span>something wrong</span>;
			}
		};

		return (
			<Form
				form={form}
				className="schedule-form"
				initialValues={{
					scheduleStatus:
						status === SCHEDULE_STATUS.FORZON || status === SCHEDULE_STATUS.STOPPED,
					selfReliance: scheduleConf.selfReliance,
				}}
			>
				<FormItem
					{...formItemLayout}
					label="调度状态"
					name="scheduleStatus"
					valuePropName="checked"
				>
					<Checkbox disabled={isScienceTask} onChange={handleScheduleStatus}>
						冻结
					</Checkbox>
				</FormItem>
				{!isWorkflowRoot && (
					<>
						<FormItem
							{...formItemLayout}
							label="出错重试"
							name="isFailRetry"
							initialValue={get(scheduleConf, 'isFailRetry')}
							valuePropName="checked"
						>
							<Checkbox disabled={isScienceTask} onChange={handleScheduleConf}>
								是
							</Checkbox>
						</FormItem>
						<FormItem noStyle dependencies={['isFailRetry']}>
							{({ getFieldValue }) =>
								getFieldValue('isFailRetry') && (
									<FormItem {...formItemLayout} label="重试次数" required>
										<FormItem
											noStyle
											name="maxRetryNum"
											rules={[
												{
													required: true,
													message: '请选择重试次数',
												},
											]}
											initialValue={get(scheduleConf, 'maxRetryNum', 3)}
										>
											<Select
												style={{
													display: 'inline-block',
													width: 70,
												}}
												disabled={isScienceTask}
												onChange={handleScheduleConf}
												options={RETRY_OPTIONS}
											/>
										</FormItem>
										<span className="ml-5px">次，每次间隔2分钟</span>
									</FormItem>
								)
							}
						</FormItem>
					</>
				)}
				{!isWorkflowNode && (
					<div>
						<FormItem {...formItemLayout} label="生效日期" required>
							<FormItem
								name="beginDate"
								noStyle
								initialValue={moment(beginDate)}
								rules={[
									{
										required: true,
										message: '请选择生效日期开始时间',
									},
								]}
							>
								<DatePicker
									allowClear={false}
									disabledDate={changeStartDisabledDate}
									disabled={isScienceTask}
									style={{ width: 125 }}
									onChange={handleScheduleConf}
								/>
							</FormItem>
							<span className="mx-5px">-</span>
							<FormItem
								noStyle
								name="endDate"
								initialValue={moment(endDate)}
								rules={[
									{
										required: true,
										message: '请选择生效日期结束时间',
									},
								]}
							>
								<DatePicker
									allowClear={false}
									disabled={isScienceTask}
									disabledDate={changeEndDisabledDate}
									style={{ width: 125 }}
									onChange={handleScheduleConf}
								/>
							</FormItem>
						</FormItem>
						<FormItem
							{...formItemLayout}
							label="调度周期"
							name="periodType"
							initialValue={`${periodType}`}
							rules={[
								{
									required: true,
								},
							]}
						>
							<Select disabled={isScienceTask} onChange={handleScheduleType}>
								<Option key={0} value={TASK_PERIOD_ENUM.MINUTE.toString()}>
									分钟
								</Option>
								<Option key={1} value={TASK_PERIOD_ENUM.HOUR.toString()}>
									小时
								</Option>
								<Option key={2} value={TASK_PERIOD_ENUM.DAY.toString()}>
									天
								</Option>
								<Option key={3} value={TASK_PERIOD_ENUM.WEEK.toString()}>
									周
								</Option>
								<Option key={4} value={TASK_PERIOD_ENUM.MONTH.toString()}>
									月
								</Option>
							</Select>
						</FormItem>
					</div>
				)}
				<FormItem noStyle name="selfReliance">
					<Input disabled={isScienceTask} type="hidden" />
				</FormItem>
				<FormItem dependencies={['periodType']} noStyle>
					{({ getFieldValue }) => renderTimeConfig(Number(getFieldValue('periodType')))}
				</FormItem>
				<FormItem noStyle dependencies={['periodType']}>
					{({ getFieldValue }) =>
						// 调度周期为小时或者分钟
						[
							TASK_PERIOD_ENUM.MINUTE.toString(),
							TASK_PERIOD_ENUM.HOUR.toString(),
						].includes(getFieldValue('periodType').toString()) && (
							<FormItem {...formItemLayout} label="延迟实例">
								<FormItem
									noStyle
									name="isExpire"
									valuePropName="checked"
									initialValue={get(scheduleConf, 'isExpire')}
								>
									<Checkbox onChange={handleScheduleConf}>自动取消</Checkbox>
								</FormItem>
								<HelpDoc doc="autoSkipJobHelp" />
							</FormItem>
						)
					}
				</FormItem>
				<FormItem noStyle dependencies={['isExpire']}>
					{({ getFieldValue }) =>
						getFieldValue('isExpire') && (
							<FormItem {...formItemLayout} label="当天最后实例">
								<FormItem
									noStyle
									name="isLastInstance"
									initialValue={isLastInstance ?? true}
								>
									<Group onChange={handleScheduleConf}>
										<Radio value={true}>始终保留</Radio>
										<Radio value={false}>延迟至第二天后自动取消</Radio>
									</Group>
								</FormItem>
								<HelpDoc doc="theLastExample" />
							</FormItem>
						)
					}
				</FormItem>
			</Form>
		);
	},
);
