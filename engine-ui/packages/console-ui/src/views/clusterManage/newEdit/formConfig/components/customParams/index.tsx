import * as React from 'react'
import { Form, Row, Col, Input } from 'antd'
import { formItemLayout } from '../../../../../../consts'
import { getCustomerParams, isNeedTemp, giveMeAKey,
    getValueByJson, isGroupType, isMultiVersion } from '../../../help'

interface IProp {
    typeCode: number;
    form: any;
    view: boolean;
    template: any;
    hadoopVersion?: string | number;
    comp?: any;
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
        this.setState({
            customParams: getCustomerParams(
                isGroupType(template.type) ? template.values : template
            )
        })
    }

    // 新增自定义参数
    addCustomerParams = () => {
        this.setState((preState) => ({
            customParams: [...preState.customParams, {}]
        }))
    }

    // 新增自定义参数
    deleteCustomerParams = (id: number) => {
        const { customParams } = this.state
        const newCustomParam = customParams.filter((param: any, index: number) => index !== id)
        this.setState({ customParams: newCustomParam })
    }

    handleCustomParam = (e: any, id: number, type?: string) => {
        const value = e.target.value
        const { template, form, typeCode, comp, hadoopVersion } = this.props
        const { customParams } = this.state
        let formField = typeCode + ''
        if (isMultiVersion(typeCode)) formField = formField + '.' + hadoopVersion

        const feildName = isGroupType(template.type) ? (formField + '.customParam.' + template.key)
            : (formField + '.customParam')

        const compConfig = getValueByJson(comp?.componentConfig) ?? {}
        const config = form.getFieldValue(formField + '.specialConfig') ?? compConfig
        const keyAndValue = Object.entries(config)

        if (type) {
            const newCustomParam = customParams.map((param: any, index: number) => {
                if (index == id) {
                    return { ...param, value: value }
                }
                return param
            })
            this.setState({
                customParams: newCustomParam
            })
            return
        }

        /**
         * 与已渲染表单值、模版固定参数比较自定义参数是否相同
         *  yarn、hdfs组件需要比较componentConfig中的key值是否相同
         */
        let sameAtTemp = -1
        let sameAtParams = false

        if (!isNeedTemp(typeCode)) {
            sameAtTemp = (isGroupType(template.type) ? template.values : template)?.findIndex(param => (param.key == value))
        } else {
            sameAtTemp = keyAndValue.findIndex(([key, name]: any[]) => key == value)
        }

        for (let [key, name] of Object.entries(form.getFieldValue(feildName))) {
            if (key.startsWith('%') && key.endsWith('-key') && value == name) {
                sameAtParams = true
                break
            }
        }

        const newCustomParam = customParams.map((param: any, index: number) => {
            if (index == id) {
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
        const { labelCol } = this.props
        return <Row>
            <Col span={labelCol ?? formItemLayout.labelCol.sm.span}></Col>
            <Col className="m-card" style={{ marginBottom: '20px' }} span={formItemLayout.wrapperCol.sm.span}>
                <a onClick={() => this.addCustomerParams()}>添加自定义参数</a>
            </Col>
        </Row>
    }

    render () {
        const { typeCode, form, view, template, maxWidth, labelCol, wrapperCol, hadoopVersion } = this.props
        const { customParams } = this.state
        const groupKey = template.key

        if (customParams.length == 0) {
            return !view && this.renderAddCustomParam()
        }

        return <>
            {customParams && customParams.map((param: any, index: number) => {
                let formField = typeCode + ''
                if (isMultiVersion(typeCode)) formField = formField + '.' + hadoopVersion
                const fieldName = groupKey ? `${formField}.customParam.${groupKey}` : `${formField}.customParam`
                const publicKey = giveMeAKey()
                return (<Row key={index}>
                    <Col span={labelCol ?? formItemLayout.labelCol.sm.span}>
                        <FormItem>
                            {form.getFieldDecorator(`${fieldName}.%${publicKey}-key`, {
                                rules: [{
                                    required: true,
                                    message: '请输入参数属性名'
                                }],
                                initialValue: param.key || ''
                            })(
                                <Input
                                    disabled={view}
                                    style={{ width: 'calc(100% - 12px)' }}
                                    onChange={(e) => this.handleCustomParam(e, index)}
                                />
                            )}
                            <span style={{ marginLeft: 2 }}>:</span>
                        </FormItem>
                    </Col>
                    <Col span={wrapperCol ?? formItemLayout.wrapperCol.sm.span}>
                        <FormItem>
                            {form.getFieldDecorator(`${fieldName}.%${publicKey}-value`, {
                                rules: [{
                                    required: true,
                                    message: '请输入参数属性值'
                                }],
                                initialValue: param.value || ''
                            })(
                                <Input
                                    disabled={view}
                                    style={{ maxWidth: maxWidth ? 680 : 'unset' }}
                                    onChange={(e) => this.handleCustomParam(e, index, 'value')}
                                />
                            )}
                        </FormItem>
                    </Col>
                    {!view && <a className="formItem-right-text" onClick={() => this.deleteCustomerParams(index)}>删除</a>}
                    {!view && param.isSameKey && (<span className="formItem-right-text">该参数已存在</span>)}
                </Row>)
            })}
            {!view && this.renderAddCustomParam()}
        </>
    }
}
