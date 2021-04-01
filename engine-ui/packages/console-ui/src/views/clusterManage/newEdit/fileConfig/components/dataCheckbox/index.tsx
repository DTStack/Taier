import * as React from 'react'
import { Checkbox, Form } from 'antd'
import { MAPPING_DATA_CHECK } from '../../../const'

interface IProps {
    comp: any;
    form: any;
    view: boolean;
}

export default class DataCheckbox extends React.PureComponent<IProps, any> {
    getCheckValue = () => {
        const { form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        if (!form.getFieldValue(`${MAPPING_DATA_CHECK[typeCode]}.hadoopVersion`) && !comp?.metastore) {
            return true
        }
        return comp?.metastore ?? false
    }

    handleChange = (e: any) => {
        const { form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        if (form.getFieldValue(`${MAPPING_DATA_CHECK[typeCode]}.hadoopVersion`)) {
            form.setFieldsValue({ [`${MAPPING_DATA_CHECK[typeCode]}.metastore`]: !e.target.checked })
        }
    }

    validMetastore = (rule: any, value: any, callback: any) => {
        const { form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        let error = null
        if (!form.getFieldValue(`${MAPPING_DATA_CHECK[typeCode]}.hadoopVersion`) && !value) {
            error = '请设置元数据获取方式'
            callback(error)
        }
        callback()
    }

    render () {
        const { form, comp, view } = this.props
        const typeCode = comp?.componentTypeCode ?? ''

        return <>
            <Form.Item
                label={null}
                colon={false}
            >
                {form.getFieldDecorator(`${typeCode}.metastore`, {
                    valuePropName: 'checked',
                    initialValue: this.getCheckValue(),
                    rules: [{
                        validator: this.validMetastore
                    }]
                })(
                    <Checkbox
                        disabled={view}
                        onChange={this.handleChange}
                    >
                        设为元数据获取方式
                    </Checkbox>
                )}
            </Form.Item>
        </>
    }
}
