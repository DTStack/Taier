import { useContext, useEffect, useMemo, useState } from 'react';
import { FormContext } from '@/services/rightBarService';
import { Checkbox, Form, InputNumber, Select } from 'antd';
import { Collapse } from 'antd';
import molecule from '@dtinsight/molecule';
import { DATA_SOURCE_ENUM, DIRTY_DATA_SAVE, formItemLayout } from '@/constant';
import {
	dirtyMaxRecord,
	dirtyFailRecord,
	dirtySaveType,
	logPrintTimes,
	recordDirtyTable,
} from '@/components/helpDoc/docs';
import LifeCycleSelect from '@/components/lifeCycleSelect';
import api from '@/api';
import { getTenantId } from '@/utils';
import type { IRightBarComponentProps } from '@/services/rightBarService';
import type { FormInstance } from 'antd';

const { Panel } = Collapse;

interface IFormFieldProps {
	openDirtyDataManage: boolean;
	maxRows?: number;
	maxCollectFailedRows?: number;
	outputType?: DIRTY_DATA_SAVE;
	sourceId?: number;
	tableName?: string;
	lifeCycle?: any;
	logPrintInterval?: number;
}

export default function TaskConfig({ current }: IRightBarComponentProps) {
	const { form } = useContext(FormContext) as { form?: FormInstance<IFormFieldProps> };

	const [dataSourceList, setDataSourceList] = useState<{ label: string; value: number }[]>([]);
	const [tableList, setTableList] = useState<string[]>([]);

	const getTableList = (sourceId: number) => {
		api.getOfflineTableList({
			sourceId,
			isSys: false,
			isRead: false,
		}).then((res) => {
			if (res.code === 1) {
				setTableList(res.data || []);
			}
		});
	};

	const handleFormValuesChange = (changedValues: Partial<IFormFieldProps>) => {
		if ('sourceId' in changedValues) {
			getTableList(changedValues.sourceId!);
		}

		setTimeout(() => {
			const { openDirtyDataManage, ...restValues } = form?.getFieldsValue() || {};

			molecule.editor.updateTab({
				...current!.tab!,
				data: {
					...current!.tab!.data,
					openDirtyDataManage,
					dataSyncTaskDirtyDataManageVO: {
						...current!.tab!.data!.dataSyncTaskDirtyDataManageVO,
						...restValues,
					},
				},
			});
		}, 0);
	};

	useEffect(() => {
		api.queryByTenantId<
			{ dataInfoId: number; dataName: string; dataTypeCode: DATA_SOURCE_ENUM }[]
		>({
			tenantId: getTenantId(),
		}).then((res) => {
			if (res.code === 1) {
				const mysqlDataSource =
					res.data?.filter((d) => d.dataTypeCode === DATA_SOURCE_ENUM.MYSQL) || [];
				setDataSourceList(
					mysqlDataSource.map((i) => ({ label: i.dataName, value: i.dataInfoId })),
				);
			}
		});
	}, []);

	const initialValues = useMemo<IFormFieldProps>(() => {
		if (current?.tab?.data) {
			const {
				maxRows,
				maxCollectFailedRows,
				outputType,
				sourceId,
				tableName,
				lifeCycle,
				logPrintInterval,
			} = (current.tab.data.dataSyncTaskDirtyDataManageVO || {}) as IFormFieldProps;

			return {
				openDirtyDataManage: current.tab.data.openDirtyDataManage,
				maxRows,
				maxCollectFailedRows,
				outputType,
				sourceId,
				tableName,
				lifeCycle,
				logPrintInterval,
			};
		}
		return {
			openDirtyDataManage: false,
		};
	}, [current?.activeTab]);

	return (
		<molecule.component.Scrollable>
			<Collapse bordered={false} ghost defaultActiveKey={['1']}>
				<Panel key="1" header="脏数据管理">
					<Form
						form={form}
						initialValues={initialValues}
						preserve={false}
						onValuesChange={handleFormValuesChange}
						{...formItemLayout}
					>
						<Form.Item
							label="脏数据记录"
							name="openDirtyDataManage"
							valuePropName="checked"
						>
							<Checkbox> 开启 </Checkbox>
						</Form.Item>
						<Form.Item dependencies={['openDirtyDataManage']} noStyle>
							{({ getFieldValue }) =>
								getFieldValue('openDirtyDataManage') && (
									<>
										<Form.Item
											label="脏数据最大值"
											name="maxRows"
											tooltip={dirtyMaxRecord}
											initialValue={100000}
										>
											<InputNumber
												style={{ width: '100%' }}
												addonAfter="条"
												max={1000000}
												min={-1}
											/>
										</Form.Item>
										<Form.Item
											label="失败条数"
											name="maxCollectFailedRows"
											tooltip={dirtyFailRecord}
											initialValue={100000}
										>
											<InputNumber
												style={{ width: '100%' }}
												addonAfter="条"
												max={1000000}
												min={-1}
											/>
										</Form.Item>
										<Form.Item
											label="脏数据保存"
											name="outputType"
											tooltip={dirtySaveType}
											initialValue={DIRTY_DATA_SAVE.NO_SAVE}
										>
											<Select>
												<Select.Option value={DIRTY_DATA_SAVE.NO_SAVE}>
													不保存，仅日志输出
												</Select.Option>
												<Select.Option value={DIRTY_DATA_SAVE.BY_MYSQL}>
													保存至MySQL
												</Select.Option>
											</Select>
										</Form.Item>
										<Form.Item dependencies={['outputType']} noStyle>
											{({ getFieldValue: getOutputType }) =>
												getOutputType('outputType') ===
													DIRTY_DATA_SAVE.BY_MYSQL && (
													<>
														<Form.Item
															label="脏数据写入库"
															name="sourceId"
															rules={[
																{
																	required: true,
																	message: '脏数据写入库为必填项',
																},
															]}
														>
															<Select
																placeholder="请选择脏数据写入的MySQL库"
																allowClear
																showSearch
																options={dataSourceList}
																filterOption={(input, option) =>
																	option!
																		.label!.toLowerCase()
																		.includes(
																			input.toLowerCase(),
																		)
																}
															/>
														</Form.Item>
														<Form.Item
															label="脏数据写入表"
															name="tableName"
															tooltip={recordDirtyTable}
														>
															<Select
																placeholder="请选择脏数据写入的MySQL表，为空则系统自动创建"
																allowClear
																showSearch
																options={tableList.map((o) => ({
																	label: o,
																	value: o,
																}))}
															/>
														</Form.Item>
														<Form.Item
															label="脏数据生命周期"
															name="lifeCycle"
															rules={[
																{
																	required: true,
																	message: '生命周期不可为空!',
																},
															]}
															initialValue={90}
														>
															<LifeCycleSelect width="100%" />
														</Form.Item>
													</>
												)
											}
										</Form.Item>
										<Form.Item dependencies={['outputType', 'maxRows']} noStyle>
											{({ getFieldValue: innerGetFieldValue }) =>
												innerGetFieldValue('outputType') ===
													DIRTY_DATA_SAVE.NO_SAVE && (
													<Form.Item
														label="日志打印频率"
														name="logPrintInterval"
														tooltip={logPrintTimes}
														initialValue={1}
													>
														<InputNumber
															style={{ width: '100%' }}
															addonAfter="条/次"
															max={
																typeof innerGetFieldValue(
																	'maxRows',
																) === 'number'
																	? innerGetFieldValue(
																			'maxRows',
																	  ) + 1
																	: 1000000
															}
															min={0}
														/>
													</Form.Item>
												)
											}
										</Form.Item>
									</>
								)
							}
						</Form.Item>
					</Form>
				</Panel>
			</Collapse>
		</molecule.component.Scrollable>
	);
}
