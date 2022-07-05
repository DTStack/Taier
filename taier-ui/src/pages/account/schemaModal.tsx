import api from '@/api';
import { formItemLayout } from '@/constant';
import { getTenantId } from '@/utils';
import { Badge, Form, message, Modal, Select, Spin } from 'antd';
import { useEffect, useState } from 'react';
import type { TASK_TYPE_ENUM } from '@/constant';

interface IFormFieldProps {
	taskType: TASK_TYPE_ENUM;
	schema: string;
}

interface IComponentListProps {
	taskType: TASK_TYPE_ENUM;
	taskTypeName: string;
	schema: string;
}

export default function SchemaModal() {
	const [form] = Form.useForm<IFormFieldProps>();

	const [loading, setLoading] = useState(false);
	const [taskList, setTaskList] = useState<IComponentListProps[]>([]);
	const [schemaList, setSchemaList] = useState<string[]>([]);
	const [fetching, setFetching] = useState(false);
	const [confirmLoading, setConfirmLoading] = useState(false);

	const getComponentList = () => {
		setLoading(true);
		api.getComponentSchemaConfig<IComponentListProps[]>({ tenantId: getTenantId() })
			.then((res) => {
				if (res.code === 1) {
					setTaskList(res.data || []);
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const getSchemaViaType = (taskType: TASK_TYPE_ENUM) => {
		setFetching(true);
		api.getSchemaListByComponent<string[]>({ taskType })
			.then((res) => {
				if (res.code === 1) {
					setSchemaList(res.data || []);
				}
			})
			.finally(() => {
				setFetching(false);
			});
	};

	const handleConfigurateSchema = () => {
		form.validateFields().then((values) => {
			setConfirmLoading(true);
			api.saveComponentSchemaConfig(values)
				.then((res) => {
					if (res.code === 1) {
						message.success('配置成功');
						setTaskList((l) => {
							const next = [...l];
							const target = next.find((x) => x.taskType === values.taskType);
							if (target) {
								target.schema = values.schema;
							}
							return next;
						});
					}
				})
				.finally(() => {
					setConfirmLoading(false);
				});
		});
	};

	const handleFormFieldChanged = (changed: Partial<IFormFieldProps>) => {
		if ('taskType' in changed) {
			getSchemaViaType(changed.taskType!);

			const target = taskList.find((i) => i.taskType === changed.taskType);
			form.setFieldsValue({
				schema: target?.schema,
			});
		}
	};

	const handleCloseModal = () => {
		const wrapper = document.querySelector('#add-tenant-modal');
		if (wrapper) {
			wrapper.remove();
		}
	};

	useEffect(() => {
		getComponentList();
	}, []);

	return (
		<Modal
			visible
			title="配置 Schema"
			confirmLoading={confirmLoading}
			onOk={handleConfigurateSchema}
			onCancel={handleCloseModal}
			destroyOnClose
			okText="确认"
			cancelText="取消"
			getContainer={() => document.querySelector('#add-tenant-modal') || document.body}
		>
			<Spin spinning={loading}>
				<Form<IFormFieldProps>
					form={form}
					{...formItemLayout}
					autoComplete="off"
					preserve={false}
					onValuesChange={handleFormFieldChanged}
				>
					<Form.Item name="taskType" label="任务类型" required>
						<Select style={{ width: '100%' }}>
							{taskList.map((l) => {
								return (
									<Select.Option value={l.taskType}>
										<Badge status={l.schema ? 'success' : 'default'} />
										{l.taskTypeName}
									</Select.Option>
								);
							})}
						</Select>
					</Form.Item>
					<Form.Item name="schema" label="Schema" required>
						<Select<string>
							style={{ width: '100%' }}
							placeholder="请选择 Schema"
							showSearch
							notFoundContent={fetching ? <Spin size="small" /> : null}
							optionFilterProp="label"
							options={schemaList.map((l) => ({ label: l, value: l }))}
						/>
					</Form.Item>
				</Form>
			</Spin>
		</Modal>
	);
}
