import * as React from 'react'
import { Form, Row, Col, Input } from 'antd'
import { formItemLayout } from '../../../../consts'
import { getCustomerParams, giveMeAKey } from '../help'
import { CONFIG_ITEM_TYPE } from '../const'

interface IProp {
    typeCode: number;
    form: any;
    view: boolean;
    template: any;
}

interface IState {
    customParams: any[];
}

const FormItem = Form.Item

export default class CustomParams extends React.PureComponent<IProp, IState> {
    state: IState = {
        customParams: []
    }

    componentDidMount () {
        const { template } = this.props
        const isGroup = template.type == CONFIG_ITEM_TYPE.GROUP
        this.setState({
            customParams: getCustomerParams(isGroup ? template.values : template)
        })
    }

    // 新增自定义参数
    addCustomerParams = () => {
        this.setState((preState) => ({
            customParams: [...preState.customParams, { id: giveMeAKey() }]
        }))
    }

    // 新增自定义参数
    deleteCustomerParams = (id: number) => {
        const { customParams } = this.state
        const newCustomParam = customParams.filter((param: any) => param.id !== id)
        this.setState({ customParams: newCustomParam })
    }

    handleCustomParam = (e: any, id: string) => {
        const value = e.target.value
        const { customParams } = this.state
        const target = customParams.findIndex(param => param.key == value)
        let newCustomParam = []
        newCustomParam = customParams.map((param: any) => {
            if (param.id == id) {
                return {
                    ...param,
                    isSameKey: target > -1,
                    key: value
                }
            }
            return param
        })
        this.setState({
            customParams: newCustomParam
        })
    }

    renderAddCustomParam = () => {
        return <Row>
            <Col span={formItemLayout.labelCol.sm.span}></Col>
            <Col className="m-card" style={{ marginBottom: '20px' }} span={formItemLayout.wrapperCol.sm.span}>
                <a onClick={() => this.addCustomerParams()}>添加自定义参数</a>
            </Col>
        </Row>
    }

    render () {
        const { typeCode, form, view, template } = this.props
        const { customParams } = this.state
        const groupKey = template.key

        if (customParams.length == 0) {
            return !view && this.renderAddCustomParam()
        }

        return <>
            {customParams && customParams.map((param: any) => {
                const fieldName = groupKey ? `${typeCode}.customParam.${groupKey}` : `${typeCode}.customParam`
                return param.id && (<Row key={param.id}>
                    <Col span={formItemLayout.labelCol.sm.span}>
                        <FormItem key={param.id + '-key'}>
                            {form.getFieldDecorator(`${fieldName}.%${param.id}-key`, {
                                rules: [{
                                    required: true,
                                    message: '请输入参数属性名'
                                }],
                                initialValue: param.key || ''
                            })(
                                <Input disabled={view} style={{ width: 'calc(100% - 12px)' }} onChange={(e) => this.handleCustomParam(e, param.id)} />
                            )}
                            <span style={{ marginLeft: 2 }}>:</span>
                        </FormItem>
                    </Col>
                    <Col span={formItemLayout.wrapperCol.sm.span}>
                        <FormItem key={param.id + '-value'}>
                            {form.getFieldDecorator(`${fieldName}.%${param.id}-value`, {
                                rules: [{
                                    required: true,
                                    message: '请输入参数属性值'
                                }],
                                initialValue: param.value || ''
                            })(
                                <Input disabled={view} style={{ maxWidth: 680 }} />
                            )}
                        </FormItem>
                    </Col>
                    {!view && <a className="formItem-right-text" onClick={() => this.deleteCustomerParams(param.id)}>删除</a>}
                    {!view && param.isSameKey && (<span className="formItem-right-text">该参数已存在</span>)}
                </Row>)
            })}
            {!view && this.renderAddCustomParam()}
        </>
    }
}
