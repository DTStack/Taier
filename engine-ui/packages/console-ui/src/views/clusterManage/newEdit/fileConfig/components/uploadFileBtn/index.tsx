import * as React from 'react'
import { Form, Upload, Button, Icon, Tooltip } from 'antd'
import { isMulitiVersion } from '../../../help'

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
        if (isMulitiVersion(typeCode) && hadoopVersion) formField = formField + '.' + hadoopVersion
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
