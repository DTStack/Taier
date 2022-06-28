import { useEffect, useState } from 'react';
import { Divider, Form, message, Modal, Select, Tooltip } from 'antd';
import api from '@/api';
import {
	DATA_SOURCE_ENUM_OBJ,
	DATA_SOURCE_TEXT,
	formItemLayout,
	notSupportSourceTypesInScript,
	notSupportTargetTypesInScript,
} from '@/constant';
import type { IDataSourceUsedInSyncProps } from '@/interface';
import { getTenantId } from '@/utils';

const FormItem = Form.Item;
const { Option } = Select;

const SOURCE_TYPE_OPTIONS = Object.values(DATA_SOURCE_ENUM_OBJ).map((sourceType) => {
	const disableSelect = notSupportSourceTypesInScript.includes(sourceType);
	const sourceName = DATA_SOURCE_TEXT[sourceType] || `未知数据源类型`;
	return (
		<Option key={sourceType} value={sourceType} disabled={disableSelect}>
			{disableSelect ? <Tooltip title="未支持数据读取">{sourceName}</Tooltip> : sourceName}
		</Option>
	);
});

const TARGET_TYPE_OPTIONS = Object.values(DATA_SOURCE_ENUM_OBJ).map((targetType) => {
	const disableSelect = notSupportTargetTypesInScript.includes(targetType);
	const sourceName = DATA_SOURCE_TEXT[targetType] || '未知数据源类型';
	return (
		<Option key={targetType} value={targetType} disabled={disableSelect}>
			{disableSelect ? <Tooltip title="未支持数据写入">{sourceName}</Tooltip> : sourceName}
		</Option>
	);
});

interface IImportTemplateProps {
	taskId: number;
	onSuccess?: (data: string) => void;
}

interface IFormFieldProps {
	sourceType: Valueof<typeof DATA_SOURCE_ENUM_OBJ>;
	sourceId: number;
	targetType: Valueof<typeof DATA_SOURCE_ENUM_OBJ>;
	targetSourceId: number;
}

export default function ImportTemplate({ taskId, onSuccess }: IImportTemplateProps) {
	const [form] = Form.useForm<IFormFieldProps>();
	const [visible, setVisible] = useState(true);
	const [loading, setLoading] = useState(false);
	const [dataSourceList, setDataSourceList] = useState<IDataSourceUsedInSyncProps[]>([]);

	const getDataSourceList = () => {
		api.queryByTenantId({ tenantId: getTenantId() }).then((res) => {
			if (res.code === 1) {
				setDataSourceList(res.data || []);
			}
		});
	};

	const getSourceList = (sourceType: Valueof<typeof DATA_SOURCE_ENUM_OBJ>) => {
		return dataSourceList
			.filter((d) => d.dataTypeCode === sourceType)
			.map((i) => ({
				label: `${i.dataName}（${DATA_SOURCE_TEXT[i.dataTypeCode] || '未知数据源类型'}）`,
				value: i.dataInfoId,
			}));
	};

	const handleFieldsChange = (changedValues: Partial<IFormFieldProps>) => {
		if (changedValues.hasOwnProperty('sourceType')) {
			form.setFieldsValue({
				sourceId: undefined,
			});
		}

		if (changedValues.hasOwnProperty('targetType')) {
			form.setFieldsValue({
				targetSourceId: undefined,
			});
		}
	};

	const getTemplateFromNet = () => {
		form.validateFields().then((values) => {
			setLoading(true);
			api.getSyncTemplate({
				id: taskId,
				sourceMap: {
					sourceId: values.sourceId,
					type: values.sourceType,
				},
				targetMap: {
					sourceId: values.targetSourceId,
					type: values.targetType,
				},
				taskId,
			})
				.then((res) => {
					if (res.code === 1) {
						message.success('导入成功');
						onSuccess?.(res.data);
						handleCancel();
					}
				})
				.finally(() => {
					setLoading(false);
				});
		});
	};

	const handleCancel = () => {
		setVisible(false);
	};

	useEffect(() => {
		getDataSourceList();
	}, []);

	return (
		<Modal
			maskClosable
			visible={visible}
			confirmLoading={loading}
			title="导入模版"
			onCancel={handleCancel}
			onOk={getTemplateFromNet}
		>
			<Form<IFormFieldProps>
				{...formItemLayout}
				form={form}
				onValuesChange={handleFieldsChange}
			>
				<FormItem
					label="来源类型"
					name="sourceType"
					rules={[
						{
							required: true,
							message: '来源类型不可为空！',
						},
					]}
				>
					<Select
						showSearch
						optionFilterProp="children"
						placeholder="请选择来源类型"
						// onChange={this.sourceTypeChange.bind(this)}
					>
						{SOURCE_TYPE_OPTIONS}
					</Select>
				</FormItem>
				<FormItem noStyle dependencies={['sourceType']}>
					{({ getFieldValue }) => (
						<FormItem
							label="数据源"
							name="sourceId"
							rules={[
								{
									required: true,
									message: '数据源不可为空！',
								},
							]}
						>
							<Select
								optionFilterProp="children"
								showSearch
								placeholder="请选择数据源"
								options={getSourceList(getFieldValue('sourceType'))}
							/>
						</FormItem>
					)}
				</FormItem>
				<Divider />
				<FormItem
					label="目标类型"
					name="targetType"
					rules={[
						{
							required: true,
							message: '目标类型不可为空！',
						},
					]}
				>
					<Select
						showSearch
						optionFilterProp="children"
						placeholder="请选择目标类型"
						// onChange={this.targetTypeChange.bind(this)}
					>
						{TARGET_TYPE_OPTIONS}
					</Select>
				</FormItem>
				<FormItem noStyle dependencies={['targetType']}>
					{({ getFieldValue }) => (
						<FormItem
							label="数据源"
							name="targetSourceId"
							rules={[
								{
									required: true,
									message: '数据源不可为空！',
								},
							]}
						>
							<Select
								showSearch
								optionFilterProp="children"
								placeholder="请选择目标数据源"
								options={getSourceList(getFieldValue('targetType'))}
							/>
						</FormItem>
					)}
				</FormItem>
			</Form>
		</Modal>
	);
}
