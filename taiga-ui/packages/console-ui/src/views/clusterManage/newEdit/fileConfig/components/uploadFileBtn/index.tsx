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
import { Form, Upload, Button, Icon, Tooltip } from 'antd'
import { isMultiVersion } from '../../../help'

interface IProp {
    label: any;
    form: any;
    icons?: any;
    fileInfo: any;
    deleteIcon?: boolean;
    view?: boolean;
    rules?: any;
    notDesc?: boolean;
    deleteFile?: Function;
    uploadFile: Function;
}

const FormItem = Form.Item

export default class UploadFile extends React.PureComponent<IProp, any> {
    render () {
        const { label, form, icons, deleteIcon, fileInfo, uploadFile,
            view, rules, notDesc, deleteFile } = this.props
        const { typeCode, hadoopVersion, name } = fileInfo

        let formField = typeCode
        if (isMultiVersion(typeCode) && hadoopVersion) formField = formField + '.' + hadoopVersion
        formField = formField + '.' + name

        const fileName = form.getFieldValue(formField)?.name ?? fileInfo?.value
        const uploadFileProps = {
            name: fileInfo.uploadProps.name,
            accept: fileInfo.uploadProps.accept,
            beforeUpload: (file: any) => {
                uploadFile(file, fileInfo.uploadProps.type, () => {
                    this.props.form.setFieldsValue({
                        [formField]: file
                    })
                })
                return false;
            },
            fileList: []
        }
        return (
            <FormItem
                label={label ?? '参数上传'}
                colon={false}
            >
                {form.getFieldDecorator(formField, {
                    initialValue: fileInfo?.value || '',
                    rules: rules ?? []
                })(<div />)}
                {!view && <div className="c-fileConfig__config">
                    <Upload {...uploadFileProps}>
                        <Button style={{ width: 172 }} icon="upload" loading={fileInfo.loading}>点击上传</Button>
                    </Upload>
                    <span className="config-desc">{fileInfo.desc}</span>
                </div>}
                {form.getFieldValue(formField) && !notDesc && <span className="config-file">
                    <Icon type="paper-clip" />
                    <Tooltip title={fileName} placement="topLeft">
                        {fileName}
                    </Tooltip>
                    {icons ?? icons}
                    {!deleteIcon ? !view && <Icon type="delete" onClick={() => {
                        form.setFieldsValue({
                            [formField]: ''
                        })
                        deleteFile && deleteFile()
                    }} /> : null}
                </span>}
            </FormItem>
        )
    }
}
