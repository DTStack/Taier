import { useEffect, useState } from 'react';
import { Form, Modal, Select } from 'antd';
import type { FormInstance } from 'antd/lib/form/Form';
import { formItemLayout } from '@/constant';
import { getCookie } from '@/utils';
import api from '@/api/console';
import API from '@/api';
import { debounce } from 'lodash';
import type { ITaskVOProps } from '@/interface';

const FormItem = Form.Item;
const { Option } = Select;

interface IUpstreamTaskProps {
	form: FormInstance;
	submitData: (task: ITaskVOProps) => void;
	onCancel: () => void;
	visible: boolean;
}

interface ITenantProps {
	tenantName: string;
	tenantId: number;
}

export default function UpstreamDependentTasks({
	form,
	submitData,
	visible,
	onCancel,
}: IUpstreamTaskProps) {
	const [tenants, setTenants] = useState<ITenantProps[]>([]);
	const [tasks, setTasks] = useState<ITaskVOProps[]>([]);

	const changeTenant = () => {
		form.setFieldsValue({
			taskId: undefined,
		});
	};

	const handleSearch = (value: string) => {
		if (value.trim() === '') {
			setTasks([]);
			return;
		}
		API.allProductGlobalSearch({
			taskName: value,
			selectTenantId: form.getFieldValue('tenantId'),
		}).then((res) => {
			if (res.code === 1) {
				if (!res.data?.length) {
					form.setFieldsValue({
						taskId: { errors: [new Error('没有符合条件的任务')] },
					});
				}
				setTasks(res.data || []);
			}
		});
	};

	const handleSubmit = () => {
		form.validateFields().then(({ taskId }) => {
			const task = tasks.find((t) => t.id === taskId);
			if (task) {
				submitData(task);
			}
		});
	};

	useEffect(() => {
		api.getTenantList().then((res) => {
			if (res.code === 1) {
				setTenants(res.data);
			}
		});
	}, []);

	return (
		<Modal visible={visible} title="添加上游依赖任务" onOk={handleSubmit} onCancel={onCancel}>
			<Form form={form}>
				<FormItem
					{...formItemLayout}
					label="所属租户"
					name="tenantId"
					rules={[{ required: true, message: '请选择所属租户!' }]}
					initialValue={Number(getCookie('tenantId'))}
				>
					<Select<number> onChange={changeTenant}>
						{tenants.map((tenantItem) => {
							return (
								<Option key={tenantItem.tenantId} value={tenantItem.tenantId}>
									{tenantItem.tenantName}
								</Option>
							);
						})}
					</Select>
				</FormItem>
				<FormItem {...formItemLayout} label="任务" required>
					<FormItem
						noStyle
						name="taskId"
						rules={[{ required: true, message: '请选择任务!' }]}
					>
						<Select
							showSearch
							placeholder="请输入任务名称搜索"
							style={{ width: '100%' }}
							defaultActiveFirstOption={false}
							showArrow={false}
							filterOption={false}
							onSearch={debounce(handleSearch, 500, { maxWait: 2000 })}
							notFoundContent={null}
						>
							{tasks.map((task) => (
								<Option key={task.id} value={task.id}>
									{task.name}
								</Option>
							))}
						</Select>
					</FormItem>
				</FormItem>
			</Form>
		</Modal>
	);
}
