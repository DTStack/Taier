import * as React from 'react'
import { Form, Upload, Button, Icon, Tooltip } from 'antd'

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
        const fileName = form.getFieldValue(`${fileInfo.typeCode}.${fileInfo.name}`)?.name ?? fileInfo?.value
        const uploadFileProps = {
            name: fileInfo.uploadProps.name,
            accept: fileInfo.uploadProps.accept,
            beforeUpload: (file: any) => {
                uploadFile(file, fileInfo.uploadProps.type, () => {
                    this.props.form.setFieldsValue({
                        [`${fileInfo.typeCode}.${fileInfo.name}`]: file
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
                {form.getFieldDecorator(`${fileInfo.typeCode}.${fileInfo.name}`, {
                    initialValue: fileInfo?.value || '',
                    rules: rules ?? []
                })(<div />)}
                {!view && <div className="c-fileConfig__config">
                    <Upload {...uploadFileProps}>
                        <Button style={{ width: 172 }} icon="upload" loading={fileInfo.loading}>点击上传</Button>
                    </Upload>
                    <span className="config-desc">{fileInfo.desc}</span>
                </div>}
                {form.getFieldValue(`${fileInfo.typeCode}.${fileInfo.name}`) && !notDesc && <span className="config-file">
                    <Icon type="paper-clip" />
                    <Tooltip title={fileName} placement="topLeft">
                        {fileName}
                    </Tooltip>
                    {icons ?? icons}
                    {!deleteIcon ? !view && <Icon type="delete" onClick={() => {
                        form.setFieldsValue({
                            [`${fileInfo.typeCode}.${fileInfo.name}`]: ''
                        })
                        deleteFile && deleteFile()
                    }} /> : null}
                </span>}
            </FormItem>
        )
    }
}
