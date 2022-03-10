import { useEffect, useMemo, useState } from 'react';
import { DATA_SOURCE_ENUM, formItemLayout } from '@/constant';
import {
	AutoComplete,
	Button,
	Checkbox,
	Form,
	Input,
	InputNumber,
	Select,
	Space,
	Spin,
} from 'antd';
import {
	breakpointContinualTransferHelp,
	errorCount,
	errorPercentConfig,
	jobConcurrence,
	jobSpeedLimit,
	S3Concurrence,
	transTableConcurrence,
} from '../helpDoc/docs';
import ajax from '../../api';
import type { IChannelFormProps, ISourceMapProps, ITargetMapProps } from './interface';
import { isRDB } from '@/utils';
import classNames from 'classnames';

const FormItem = Form.Item;

interface IChannelProps {
	/**
	 * 第一步所填写的信息
	 */
	sourceMap: ISourceMapProps;
	/**
	 * 第二步所填写的信息
	 */
	targetMap: ITargetMapProps;
	/**
	 * 第三步所填写的信息
	 */
	setting?: IChannelFormProps;
	/**
	 * 是否只读，用于预览数据
	 */
	readonly?: boolean;
	/**
	 * @deprecated
	 * 是否是单例
	 */
	isStandeAlone?: boolean;
	/**
	 * @deprecated
	 * 是否是增量模式
	 */
	isIncrementMode?: boolean;
	/**
	 * 下一步或上一步的回调函数
	 * @param next 为 true 时表示下一步，否则为上一步
	 * @param values 当前表单的域，由于当前组件存在默认值，所以当用户没有修改表单域时，则通过当前方法获取默认值
	 */
	onNext?: (next: boolean, values?: IChannelFormProps) => void;
	/**
	 * 表单域的值被修改时触发的回调函数
	 */
	onFormValuesChanged?: (values: IChannelFormProps) => void;
}

export const UnlimitedSpeed = '不限制传输速率';
/**
 * 传输速率选择项
 */
const SPEED_OPTIONS = new Array(20).fill(1).map((_, index) => ({ value: (index + 1).toString() }));
/**
 * 不限制传输速率选择项
 */
const UNLIMITED_SPEED_OPTION = [
	{
		value: UnlimitedSpeed,
	},
];

const CHANNEL_OPTIONS = new Array(5)
	.fill(1)
	.map((_, index) => ({ label: index + 1, value: (index + 1).toString() }));

export default function Channel({
	targetMap,
	sourceMap,
	setting,
	readonly,
	isStandeAlone,
	isIncrementMode,
	onNext,
	onFormValuesChanged,
}: IChannelProps) {
	const [form] = Form.useForm<IChannelFormProps>();
	const [loading, setLoading] = useState(false);
	const [isTransTable, setTransTable] = useState(true);
	const [idFields, setIdFields] = useState<{ key: string; type: string }[]>([]); // 标识字段

	const loadIdFields = async () => {
		const res = await ajax.getIncrementColumns({
			sourceId: sourceMap.sourceId,
			tableName: sourceMap.type?.table,
			schema: sourceMap?.schema || sourceMap.type?.schema,
		});

		if (res.code === 1) {
			setIdFields(res.data || []);
		}
	};

	const handleSubmit = () => {
		form.validateFields().then((values) => {
			onNext?.(true, values);
		});
	};

	useEffect(() => {
		// 开启断点续传功能，需要加载标识字段
		const { type, sourceId } = targetMap;
		if (setting?.isRestore) {
			loadIdFields();
		}
		if (type?.type !== DATA_SOURCE_ENUM.INCEPTOR) {
			setTransTable(false);
			return;
		}
		setLoading(true);
		ajax.getTableInfoByDataSource({
			dataSourceId: sourceId,
			tableName: type.table,
			schema: type.schema,
		}).then((res) => {
			if (res) {
				if (res.data.isTransTable) {
					form.setFieldsValue({ channel: '1' });
				}
				setTransTable(res.data.isTransTable);
			}
		});
	}, []);

	const handleFormValuesChanged = (
		changedValue: Partial<IChannelFormProps>,
		values: IChannelFormProps,
	) => {
		if (changedValue.hasOwnProperty('isRestore') && changedValue.isRestore) {
			loadIdFields();
		}

		onFormValuesChanged?.(values);
	};

	const renderBreakpointContinualTransfer = () => {
		const sourceType = sourceMap?.type?.type;
		const targetType = targetMap?.type?.type;

		const idFieldInitialValue = isIncrementMode
			? // @ts-ignore
			  sourceMap.increColumn
			: setting?.restoreColumnName;

		return isRDB(sourceType) &&
			(isRDB(targetType) ||
				targetType === DATA_SOURCE_ENUM.HIVE1X ||
				targetType === DATA_SOURCE_ENUM.HIVE ||
				targetType === DATA_SOURCE_ENUM.MAXCOMPUTE) ? (
			<div>
				{!isStandeAlone && (
					<FormItem
						tooltip={breakpointContinualTransferHelp}
						label="断点续传"
						className="txt-left"
						name="isRestore"
						valuePropName="checked"
					>
						<Checkbox>开启</Checkbox>
					</FormItem>
				)}
				<FormItem noStyle dependencies={['isRestore']}>
					{({ getFieldValue }) =>
						getFieldValue('isRestore') ? (
							<FormItem
								name="restoreColumnName"
								label="标识字段"
								key="restoreColumnName"
								rules={[
									{
										required: true,
										message: '请选择标识字段',
									},
								]}
								initialValue={idFieldInitialValue}
							>
								<Select
									showSearch
									placeholder="请选择标识字段"
									disabled={isIncrementMode} // 增量模式时，默认使用增量字段，此处禁用选项
									options={idFields.map((o) => ({
										label: `${o.key}（${o.type}）`,
										value: o.key,
									}))}
								/>
							</FormItem>
						) : null
					}
				</FormItem>
			</div>
		) : null;
	};

	const targetType = useMemo(() => targetMap.type?.type, [targetMap]);
	const sourceType = useMemo(() => sourceMap.type?.type, [sourceMap]);
	const concurrenceTooltip = useMemo(
		() => (isTransTable ? transTableConcurrence : jobConcurrence),
		[isTransTable],
	);
	const isClickHouse =
		targetType === DATA_SOURCE_ENUM.CLICKHOUSE ||
		targetType === DATA_SOURCE_ENUM.S3 ||
		sourceType === DATA_SOURCE_ENUM.PHOENIX ||
		isTransTable;

	const initialValues = useMemo<IChannelFormProps>(() => {
		if (setting) {
			return {
				speed: setting.speed.toString() === '-1' ? UnlimitedSpeed : setting.speed,
				channel: `${!isClickHouse ? setting.channel : 1}`,
				record: setting.record,
				percentage: setting.percentage,
				isRestore: setting.isRestore,
			};
		}
		return {
			speed: UnlimitedSpeed,
			channel: '1',
			record: 100,
			isSaveDirty: 0,
		};
	}, []);

	return (
		<Spin spinning={loading}>
			<Form<IChannelFormProps>
				form={form}
				{...formItemLayout}
				onValuesChange={handleFormValuesChanged}
				initialValues={initialValues}
			>
				<FormItem tooltip={jobSpeedLimit} label="作业速率上限" name="speed" required>
					<AutoComplete options={UNLIMITED_SPEED_OPTION.concat(SPEED_OPTIONS)}>
						<Input suffix="MB/s" />
					</AutoComplete>
				</FormItem>
				<FormItem
					tooltip={
						targetType === DATA_SOURCE_ENUM.S3 ? S3Concurrence : concurrenceTooltip
					}
					label="作业并发数"
					name="channel"
					required
				>
					<AutoComplete
						disabled={isClickHouse}
						options={CHANNEL_OPTIONS}
						optionFilterProp="value"
					/>
				</FormItem>
				{!isStandeAlone && (
					<>
						{/* <FormItem
									{...formItemLayout}
									label={
										<span>
											错误记录管理
											<HelpDoc doc="recordDirtyData" />
										</span>
									}
									className="txt-left"
								>
									{getFieldDecorator('isSaveDirty', {
										rules: [],
										initialValue: !!setting.isSaveDirty,
									})(<Checkbox>记录保存</Checkbox>)}
								</FormItem> */}
						<FormItem tooltip={errorCount} label="错误记录数超过">
							<FormItem name="record" noStyle>
								<InputNumber />
							</FormItem>
							<span className={classNames('text-xs', 'ml-0.5')}>
								条, 任务自动结束
							</span>
						</FormItem>
						<FormItem tooltip={errorPercentConfig} label="错误记录比例配置">
							<span className={classNames('text-xs', 'mr-0.5')}>
								任务执行结束后，统计错误记录占比，大于
							</span>
							<FormItem name="percentage" noStyle>
								<InputNumber />
							</FormItem>
							<span className={classNames('text-xs', 'ml-0.5')}>
								%时，任务置为失败
							</span>
						</FormItem>
					</>
				)}
				{renderBreakpointContinualTransfer()}
			</Form>
			{!readonly && (
				<div className="steps-action">
					<Space>
						<Button onClick={() => onNext?.(false)}>上一步</Button>
						<Button type="primary" onClick={handleSubmit}>
							下一步
						</Button>
					</Space>
				</div>
			)}
		</Spin>
	);
}
