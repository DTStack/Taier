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

import * as React from 'react'
import { Checkbox, Form, Modal } from 'antd'
import { MAPPING_DATA_CHECK, COMPONENT_CONFIG_NAME } from '../../../const'

const confirm = Modal.confirm

interface IProps {
    comp: any;
    form: any;
    view: boolean;
    isCheckBoxs: boolean;
    disabledMeta: boolean;
}

export default class DataCheckbox extends React.PureComponent<IProps, any> {
    getCheckValue = () => {
        const { comp, isCheckBoxs, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        if (!isCheckBoxs) return true
        return form.getFieldValue(`${typeCode}.isMetadata`) ?? comp?.isMetadata ?? false
    }

    handleChange = (e: any) => {
        const { form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const showConfirm = () => {
            /**
             * 勾选后保持勾选之前的状态，使用setState过度平缓，使用setTimeOut会有闪现的效果
             */
            this.setState({ show: true }, () => {
                form.setFieldsValue({ [`${typeCode}.isMetadata`]: !e.target.checked })
            })
            const source = !e.target.checked ? COMPONENT_CONFIG_NAME[typeCode]
                : COMPONENT_CONFIG_NAME[MAPPING_DATA_CHECK[typeCode]]
            const target = !e.target.checked ? COMPONENT_CONFIG_NAME[MAPPING_DATA_CHECK[typeCode]]
                : COMPONENT_CONFIG_NAME[typeCode]
            confirm({
                title: `确认将元数据获取方式由${source}切换为${target}？`,
                onOk: () => {
                    form.setFieldsValue({
                        [`${MAPPING_DATA_CHECK[typeCode]}.isMetadata`]: !e.target.checked,
                        [`${typeCode}.isMetadata`]: e.target.checked
                    })
                }
            })
        }
        if (this.props.isCheckBoxs) {
            showConfirm()
        }
    }

    validMetadata = (rule: any, value: any, callback: any) => {
        let error = null
        if (!this.props.isCheckBoxs && !value) {
            error = '请设置元数据获取方式'
            callback(error)
        }
        callback()
    }

    render () {
        const { form, comp, view, disabledMeta } = this.props
        const typeCode = comp?.componentTypeCode ?? ''

        return <>
            <Form.Item
                label={null}
                colon={false}
            >
                {form.getFieldDecorator(`${typeCode}.isMetadata`, {
                    valuePropName: 'checked',
                    initialValue: this.getCheckValue(),
                    rules: [{
                        validator: this.validMetadata
                    }]
                })(
                    <Checkbox
                        disabled={view || disabledMeta}
                        onChange={this.handleChange}
                    >
                        设为元数据获取方式
                    </Checkbox>
                )}
            </Form.Item>
        </>
    }
}
