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

import { CloseOutlined } from '@ant-design/icons';
import { Button, Col, Form, Input, Row } from 'antd';

import './customParams.scss';

interface ICustomParamsProps {
    index: number;
}

export const CustomParams = ({ index }: ICustomParamsProps) => {
    return (
        <Form.Item label="自定义参数" className="custom-params">
            <Form.List name={[index, 'customParams']}>
                {(fields, { add, remove }) => (
                    <>
                        {fields.map((field, i) => (
                            <Row key={field.key} justify="center" className="ant-form-item">
                                <Col span={10}>
                                    <Form.Item
                                        noStyle
                                        name={[i, 'key']}
                                        rules={[{ required: true, message: '请输入参数名' }]}
                                    >
                                        <Input className="w-full" />
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
                                        name={[i, 'value']}
                                        rules={[{ required: true, message: '请输入参数值' }]}
                                    >
                                        <Input className="w-full" />
                                    </Form.Item>
                                </Col>
                                <Col span={2}>
                                    <CloseOutlined className="delete-action" onClick={() => remove(field.name)} />
                                </Col>
                            </Row>
                        ))}
                        <Button block type="link" onClick={() => add()}>
                            添加自定义参数
                        </Button>
                    </>
                )}
            </Form.List>
        </Form.Item>
    );
};
