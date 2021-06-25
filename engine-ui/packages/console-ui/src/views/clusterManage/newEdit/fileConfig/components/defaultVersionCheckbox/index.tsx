import * as React from 'react'
import { Checkbox, Form, Tooltip, Icon } from 'antd'
import { MAPPING_DEFAULT_VERSION } from '../../../const'

interface IProps {
    comp: any;
    form: any;
    view: boolean;
    isDefault: boolean;
}

export default class DefaultVersionCheckbox extends React.PureComponent<IProps, any> {
    getCheckValue = () => {
        const { comp, isDefault, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''

        if (isDefault) {
            setTimeout(() => {
                form.setFieldsValue({
                    [`${typeCode}.${hadoopVersion}.isDefault`]: true
                })
            }, 0)
            return
        }
        return comp?.isDefault ?? false
    }

    handleChange = (e: any) => {
        const { form, comp, isDefault } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''

        if (!isDefault) {
            form.setFieldsValue({
                [`${typeCode}.${MAPPING_DEFAULT_VERSION[hadoopVersion]}.isDefault`]: !e.target.checked
            })
        }
        form.setFieldsValue({
            [`${typeCode}.${hadoopVersion}.isDefault`]: e.target.checked
        })
    }

    render () {
        const { form, comp, view } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''

        return <>
            <Form.Item
                label={null}
                colon={false}
            >
                {form.getFieldDecorator(`${typeCode}.${hadoopVersion}.isDefault`, {
                    valuePropName: 'checked',
                    initialValue: this.getCheckValue()
                })(
                    <Checkbox
                        disabled={view}
                        onChange={this.handleChange}
                    >
                        设置为默认版本
                    </Checkbox>
                )}
                <Tooltip overlayClassName="big-tooltip" title='默认版本将用于离线开发的数据同步与实时开发的实时采集'>
                    <Icon style={{ marginLeft: 4 }} type="question-circle-o" />
                </Tooltip>
            </Form.Item>
        </>
    }
}
