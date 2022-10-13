import api from '@/api';
import {formItemLayout} from '@/constant';
import {IRightBarComponentProps} from '@/services/rightBarService';
import molecule from '@dtinsight/molecule';
import {Collapse, Form, Select} from 'antd';
import {useEffect, useState} from 'react';

export default function QueueConfig({current}: IRightBarComponentProps) {
	const [form] = Form.useForm();
	const [resourceList, setResourceList] = useState<string[]>([]);

	const handleFormValuesChange = () => {
		setTimeout(() => {
			molecule.editor.updateTab({
				...current!.tab!,
				data: {
					...current!.tab!.data,
					queueName: form.getFieldValue('queueName'),
				},
			});
		}, 0);
	};

	useEffect(() => {
		api.getResourceByTenant({}).then((res) => {
			if (res.code === 1) {
				setResourceList(res.data.queues?.map((q: any) => q.queueName));
			}
		});
	}, []);

	return (
		<Collapse bordered={false} ghost defaultActiveKey={['1']}>
			<Collapse.Panel key="1" header="队列管理">
				<Form
					form={form}
					preserve={false}
					initialValues={{
						queueName: current?.tab?.data.queueName,
					}}
					onValuesChange={handleFormValuesChange}
					{...formItemLayout}
				>
					<Form.Item name="queueName" label="YARN 队列">
						<Select
							placeholder="请选择 YARN 队列"
							getPopupContainer={(node) => node.parentNode}
							options={resourceList.map((r) => ({label: r, value: r}))}
						></Select>
					</Form.Item>
				</Form>
			</Collapse.Panel>
		</Collapse>
	);
}
