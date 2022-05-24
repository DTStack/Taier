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

import { Button, Col, Form, Input, Row } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { formItemLayout } from '@/constant';

interface ICustomParamsProps {
	customParams: any[];
	onChange: (type: any, id?: any, value?: any) => void;
}

export const CustomParams = ({ customParams, onChange }: ICustomParamsProps) => {
	const handleDeleteCustomParams = (id: any) => {
		onChange?.('deleteCustomParam', id);
	};
	const handleAddCustomParams = () => {
		onChange?.('newCustomParam');
	};

	const renderCustomParams = () => {
		return customParams.map((customParam: any) => {
			return (
				<Row key={customParam.id} justify="center" className="ant-form-item">
					<Col span={10}>
						<Form.Item
							noStyle
							name={`${customParam.id}-key`}
							rules={[{ required: true, message: '请输入参数名' }]}
						>
							<Input
								className="w-full"
								onChange={(e) => onChange('key', customParam.id, e.target.value)}
							/>
						</Form.Item>
					</Col>
					<Col span={2}>
						<div className="text-center" style={{ lineHeight: '32px' }}>
							:
						</div>
					</Col>
					<Col span={10}>
						<Form.Item
							noStyle
							name={`${customParam.id}-value`}
							rules={[{ required: true, message: '请输入参数值' }]}
						>
							<Input
								className="w-full"
								onChange={(e) => onChange('value', customParam.id, e.target.value)}
							/>
						</Form.Item>
					</Col>
					<Col span={2}>
						<CloseOutlined
							className="delete-action"
							onClick={handleDeleteCustomParams.bind(undefined, customParam.id)}
						/>
					</Col>
				</Row>
			);
		});
	};

	return (
		<div>
			{customParams.length > 0 && (
				<Form.Item label="自定义参数">{renderCustomParams()}</Form.Item>
			)}
			<Form.Item
				wrapperCol={{
					offset: formItemLayout.labelCol.sm.span,
					span: formItemLayout.wrapperCol.sm.span,
				}}
			>
				<Button block type="link" onClick={handleAddCustomParams}>
					添加自定义参数
				</Button>
			</Form.Item>
		</div>
	);
};
