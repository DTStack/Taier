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

import { Col, Form, Input, Row } from "antd";
import { CloseOutlined } from '@ant-design/icons'

interface ICustomParamsProps {
    customParams: any;
    onChange: (type: any, id?: any, value?: any) => void;
    formItemLayout: any
}

export const CustomParams = ({
    customParams,
    onChange,
    formItemLayout
}: ICustomParamsProps) => {
    const renderCustomParams = () => {
        return customParams.map((customParam: any, index: number) => {
            return (
                <Row key={customParam.id}>
                    <Col offset={index ? formItemLayout.labelCol.sm.span : 0} span={formItemLayout.labelCol.sm.span + 2}>
                        <Form.Item
                            name={customParam.id + '-key'}
                            rules={[{ required: true, message: '请输入参数名' }]}
                        >
                            <Input onChange={(e: any) => {
                                onChange('key', customParam.id, e.target.value);
                            }} style={{ width: 'calc(100% - 20px)' }} />
                        </Form.Item>
                        :
                    </Col>
                    <Col span={formItemLayout.labelCol.sm.span + 4}>
                        <Form.Item
                            name={customParam.id + '-value'}
                            rules={[{ required: true, message: '请输入参数值' }]}
                        >
                            <Input onChange={(e: any) => {
                                onChange('value', customParam.id, e.target.value);
                            }} style={{ width: 'calc(100% - 30px)' }} />
                        </Form.Item>
                        <CloseOutlined className="delete-action" onClick={deleteCustomParam.bind(undefined, customParam.id)} />
                    </Col>
                </Row>
            )
        })
    }
    const deleteCustomParam = (id: any) => {
        onChange?.('deleteCustomParam', id)
    }
    const addCustomParams = () => {
        onChange?.('newCustomParam')
    }
    return <div> {customParams.length > 0 && <Row>
        <Col span={formItemLayout.labelCol.sm.span} className="c-sidePanel__customParams">
            自定义参数:
        </Col>
        <Col>
            {renderCustomParams()}
        </Col>
    </Row>}
        <Row>
            <Col offset={formItemLayout.labelCol.sm.span} span={formItemLayout.wrapperCol.sm.span} style={{ marginBottom: '12px' }}>
                <a style={{ color: '#3f87ff' }} onClick={addCustomParams.bind(this)}>添加自定义参数</a>
            </Col>
        </Row>
    </div>
}