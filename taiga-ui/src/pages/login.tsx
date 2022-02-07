import { useState, useLayoutEffect, useEffect } from 'react';
import { Button, Checkbox, Form, Input, message, Modal, Select } from 'antd';
import api from '@/api/console';
import { formItemLayout } from '@/constant';
import { getTenantId, getCookie } from '@/utils';
import './login.scss';

const { Option } = Select;

interface IFormField {
	username: string;
	password: string;
}

interface ITenantProps {
	tenantId: number;
	tenantName: string;
}

/**
 * For storing the login modal visible
 */
const listener: Record<string, React.Dispatch<React.SetStateAction<boolean>>> = {};

/**
 * Execute this function to open login Modal everywhere
 */
export function showLoginModal() {
	listener.setVisible(true);
}

export default () => {
	const [curTenantId] = useState(getTenantId());
	const [isLogin, setLogin] = useState(() => !!getCookie('token'));
	const [isModalVisible, setVisible] = useState(false);
	const [submitLoading, setLoading] = useState(false);
	const [form] = Form.useForm<IFormField>();
	const [tenantForm] = Form.useForm<{
		change_ten_id: number;
		change_ten_isdefault: boolean;
	}>();
	const [tenants, setTenants] = useState<ITenantProps[]>([]);

	const handleOk = () => {
		form.validateFields().then(async (values) => {
			setLoading(true);
			api.login(values)
				.then((res) => {
					if (res.code === 1) {
						// 判断是否有默认绑定的租户，如果有则绑定默认租户
						const userId = getCookie('userId');
						const defaultTenant = localStorage.getItem(`${userId}_default_tenant`);
						if (defaultTenant) {
							doTenantChange(Number(doTenantChange), true);
						} else {
							setLogin(true);
						}
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

	const handleTenantSubmit = () => {
		tenantForm.validateFields().then((values) => {
			doTenantChange(values.change_ten_id, values.change_ten_isdefault);
		});
	};

	const doTenantChange = (tenantId: number, isDefault: boolean) => {
		api.switchTenant({ tenantId }).then((res) => {
			if (res.code === 1) {
				if (isDefault) {
					const userId = getCookie('userId');
					// 保存租户信息
					localStorage.setItem(`${userId}_default_tenant`, tenantId.toString());
				}
				setVisible(false);
				window.location.reload();
			} else {
				message.error(res.message);
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

	useLayoutEffect(() => {
		listener.setVisible = setVisible;

		return () => {
			Reflect.deleteProperty(listener, 'setVisible');
		};
	}, []);

	const renderLoginForm = () => {
		return (
			<Form<IFormField>
				form={form}
				hidden={isLogin}
				preserve={false}
				wrapperCol={{ span: 24 }}
				autoComplete="off"
				onFinish={handleOk}
			>
				<Form.Item
					label=""
					name="username"
					rules={[
						{
							type: 'email',
							message: '请输入正确格式的邮箱账号',
						},
						{
							required: true,
							message: '账号不能为空',
						},
					]}
				>
					<Input
						className="dt-input-borderless"
						placeholder="请输入注册账号"
						bordered={false}
					/>
				</Form.Item>
				<Form.Item
					label=""
					name="password"
					rules={[
						{
							required: true,
							message: '密码不能为空',
						},
					]}
				>
					<Input.Password
						className="dt-input-borderless"
						placeholder="请输入密码"
						bordered={false}
					/>
				</Form.Item>
				<Form.Item>
					<Button
						className="dt-button"
						loading={submitLoading}
						block
						type="primary"
						htmlType="submit"
					>
						登录
					</Button>
				</Form.Item>
			</Form>
		);
	};

	const renderTenantForm = () => {
		return (
			<Form
				form={tenantForm}
				hidden={!isLogin}
				preserve={false}
				{...formItemLayout}
				autoComplete="off"
				initialValues={{
					change_ten_id: curTenantId === null ? undefined : Number(curTenantId),
					change_ten_isdefault: !!localStorage.getItem(
						`${getCookie('userId')}_default_tenant`,
					),
				}}
				onFinish={handleTenantSubmit}
			>
				<Form.Item label="租户名称" name="change_ten_id">
					<Select style={{ width: '100%' }} showSearch placeholder="请选择租户">
						{tenants.map((o) => (
							<Option key={o.tenantId} value={o.tenantId}>
								{o.tenantName}
							</Option>
						))}
					</Select>
				</Form.Item>
				<Form.Item
					name="change_ten_isdefault"
					valuePropName="checked"
					wrapperCol={{
						offset: formItemLayout.labelCol.sm.span,
						span: 16,
					}}
				>
					<Checkbox
						style={{
							width: 310,
							fontSize: 12,
							color: '#666666',
							fontWeight: 'normal',
						}}
					>
						是否默认进入该租户
					</Checkbox>
				</Form.Item>
				<Form.Item
					wrapperCol={{
						offset: formItemLayout.labelCol.sm.span,
						span: 16,
					}}
				>
					<Button
						className="dt-button"
						loading={submitLoading}
						block
						type="primary"
						htmlType="submit"
					>
						确认
					</Button>
				</Form.Item>
			</Form>
		);
	};

	return (
		<Modal
			className="dt-login"
			title={isLogin ? '请选择租户' : '欢迎登录 Taiga'}
			visible={isModalVisible}
			footer={null}
			destroyOnClose
			onCancel={handleCancel}
		>
			{renderTenantForm()}
			{renderLoginForm()}
		</Modal>
	);
};
