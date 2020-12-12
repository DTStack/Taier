import * as React from 'react'
import { Form, Upload, Button, Icon } from 'antd'

interface IProp {
    label: any;
    form: any;
    icons?: any;
    fileInfo: any;
    deleteIcon?: boolean;
    uploadFile: Function;
    view?: boolean;
}

const FormItem = Form.Item

export default class UploadFile extends React.PureComponent<IProp, any> {
    render () {
        const { label, form, icons, deleteIcon, fileInfo, uploadFile, view } = this.props
        const uploadFileProps = {
            name: fileInfo.uploadProps.name,
            accept: fileInfo.uploadProps.accept,
            beforeUpload: (file: any) => {
                uploadFile(file, fileInfo.uploadProps.type, () => {
                    this.props.form.setFieldsValue({
                        [`${fileInfo.typecode}.${fileInfo.name}`]: file
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
                {form.getFieldDecorator(`${fileInfo.typecode}.${fileInfo.name}`, {
                    initialValue: fileInfo?.value || ''
                })(<div />)}
                {!view && <div className="c-fileConfig__config">
                    <Upload {...uploadFileProps}>
                        <Button style={{ width: 172 }} icon="upload" loading={fileInfo.loading}>点击上传</Button>
                    </Upload>
                    <span className="config-desc">{fileInfo.desc}</span>
                </div>}
                {form.getFieldValue(`${fileInfo.typecode}.${fileInfo.name}`) && <span className="config-file">
                    <Icon type="paper-clip" />
                    {form.getFieldValue(`${fileInfo.typecode}.${fileInfo.name}`)?.name ?? fileInfo?.value}
                    {icons ?? icons}
                    {!deleteIcon ? !view && <Icon type="delete" onClick={() => {
                        form.setFieldsValue({
                            [`${fileInfo.typecode}.${fileInfo.name}`]: ''
                        })
                    }} /> : null}
                </span>}
            </FormItem>
        )
    }
}
