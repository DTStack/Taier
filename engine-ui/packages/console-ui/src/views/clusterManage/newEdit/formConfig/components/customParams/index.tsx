import * as React from 'react'
import { Form, Row, Col, Input } from 'antd'
import { formItemLayout } from '../../../../../../consts'
import { getCustomerParams, giveMeAKey, isNeedTemp } from '../../../help'
import { CONFIG_ITEM_TYPE } from '../../../const'

interface IProp {
    typeCode: number;
    form: any;
    view: boolean;
    template: any;
    maxWidth?: number;
    labelCol?: number;
    wrapperCol?: number;
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
        const { template, form, typeCode } = this.props
        const { customParams } = this.state
        const isGroup = template.type == CONFIG_ITEM_TYPE.GROUP
        const feildName = isGroup ? `${typeCode}.customParam.${template.key}` : `${typeCode}.customParam`

        /**
         * 与已渲染表单值、模版固定参数比较自定义参数是否相同
         *  yarn等组件只比较已渲染表单值
         */
        let sameAtTemp = -1
        let sameAtParams = false
        if (!isNeedTemp(typeCode)) {
            sameAtTemp = (isGroup ? template.values : template)?.findIndex(param => (param.key == value && !param.id))
        }
        for (let [key, name] of Object.entries(form.getFieldValue(feildName))) {
            if (key.startsWith('%') && key.endsWith('-key') && value == name) {
                sameAtParams = true
                break
            }
        }

        const newCustomParam = customParams.map((param: any) => {
            if (param.id == id) {
                return {
                    ...param,
                    isSameKey: sameAtParams || sameAtTemp > -1,
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
        const { typeCode, form, view, template, maxWidth, labelCol, wrapperCol } = this.props
        const { customParams } = this.state
        const groupKey = template.key

        if (customParams.length == 0) {
            return !view && this.renderAddCustomParam()
        }

        return <>
            {customParams && customParams.map((param: any) => {
                const fieldName = groupKey ? `${typeCode}.customParam.${groupKey}` : `${typeCode}.customParam`
                return param.id && (<Row key={param.id}>
                    <Col span={labelCol ?? formItemLayout.labelCol.sm.span}>
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
                    <Col span={wrapperCol ?? formItemLayout.wrapperCol.sm.span}>
                        <FormItem key={param.id + '-value'}>
                            {form.getFieldDecorator(`${fieldName}.%${param.id}-value`, {
                                rules: [{
                                    required: true,
                                    message: '请输入参数属性值'
                                }],
                                initialValue: param.value || ''
                            })(
                                <Input disabled={view} style={{ maxWidth: maxWidth ? 680 : 'unset' }} />
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
