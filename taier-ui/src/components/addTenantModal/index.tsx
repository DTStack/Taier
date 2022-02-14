import { Modal, Form, message, Input } from 'antd';
import { formItemLayout } from '@/constant';
import api from '@/api/console';
import { getUserId } from '@/utils';

export default function AddTenantModal() {
	const [form] = Form.useForm();

	const handleAddTenant = () => {
		form.validateFields().then((values) => {
			api.addTenant({ tenantName: values.tenantName, userId: getUserId() }).then((res) => {
				if (res.code) {
					message.success('新增成功');
					handleCloseModal();
				}
			});
		});
	};

	const handleCloseModal = () => {
		const wrapper = document.querySelector('#add-tenant-modal');
		if (wrapper) {
			wrapper.remove();
		}
	};

	return (
		<Modal
			visible
			title="新增租户"
			onOk={handleAddTenant}
			onCancel={handleCloseModal}
			destroyOnClose
			okText="确认"
			cancelText="取消"
			getContainer={() => document.querySelector('#add-tenant-modal') || document.body}
		>
			<Form form={form} {...formItemLayout} autoComplete="off" preserve={false}>
				<Form.Item
					name="tenantName"
					label="租户名称"
					rules={[
						{ max: 64, message: '请输入 64 个字符以内' },
						{ required: true, message: '请输入租户名称!' },
						{
							pattern: /^[a-zA-Z0-9_]{1,64}$/,
							message: '租户名称只能由字母、数字、下划线组成，且长度不超过64个字符!',
						},
					]}
				>
					<Input style={{ width: '100%' }} placeholder="输入租户名称，64个字符以内" />
				</Form.Item>
			</Form>
		</Modal>
	);
}
