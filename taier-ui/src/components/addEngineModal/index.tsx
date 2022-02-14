import type { ModalProps } from 'antd';
import { Form, Modal, Input } from 'antd';
import { formItemLayout } from '@/constant';
import './index.scss';

const FormItem = Form.Item;

interface IEngineModalProps extends Omit<ModalProps, 'onOk'> {
	onOk?: (values: { clusterName: string }) => void;
}

/**
 * 集群名称表单域组件
 * 新增集群、增加组件、增加引擎共用组件
 */
export default ({ onOk, ...restModalProps }: IEngineModalProps) => {
	const [form] = Form.useForm();

	const handleSubmit = () => {
		form.validateFields().then((values) => {
			onOk?.({ clusterName: values.clusterName });
		});
	};

	return (
		<Modal onOk={handleSubmit} className="c-clusterManage__modal" {...restModalProps}>
			<Form form={form} autoComplete="off">
				<FormItem
					label="集群名称"
					{...formItemLayout}
					name="clusterName"
					rules={[
						{
							required: true,
							message: '集群标识不可为空！',
						},
						{
							pattern: /^[a-z0-9_]{1,64}$/i,
							message: '集群标识不能超过64字符，支持英文、数字、下划线',
						},
					]}
				>
					<Input placeholder="请输入集群标识" />
				</FormItem>
			</Form>
		</Modal>
	);
};
