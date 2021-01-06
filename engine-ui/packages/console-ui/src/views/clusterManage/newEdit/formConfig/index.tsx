import * as React from 'react'
import { Input, Form, Radio, Select, Checkbox, Tooltip, Row, Col } from 'antd'
import { COMPONENT_TYPE_VALUE, CONFIG_ITEM_TYPE } from '../const'
import { formItemLayout } from '../../../../consts'
import CustomParams from '../components/customParams'
interface IProps {
    comp: any;
    form: any;
    view: boolean;
}

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
export default class FormConfig extends React.PureComponent<IProps, any> {
    // 渲染单个配置项
    renderConfigItem = (temp: any, groupKey?: string) => {
        const { view, form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        let fieldName = typeCode + '.componentConfig'
        if (groupKey) {
            fieldName = typeCode + '.componentConfig.' + groupKey
        }
        // const fieldName = groupKey ? `${typeCode}.componentConfig.${groupKey}` : `${typeCode}.componentConfig`;
        let content: any
        switch (temp.type) {
            case CONFIG_ITEM_TYPE.RADIO:
                content = <RadioGroup disabled={view}>
                    {temp.values.map((comp: any) => {
                        return <Radio key={comp.key} value={comp.value}>{comp.key}</Radio>
                    })}
                </RadioGroup>
                break;
            case CONFIG_ITEM_TYPE.SELECT:
                content = <Select disabled={view} style={{ width: 200 }}>
                    {temp.values.map((comp: any) => {
                        return <Option key={comp.key} value={comp.value}>{comp.key}</Option>
                    })}
                </Select>
                break;
            case CONFIG_ITEM_TYPE.CHECKBOX:
                content = <CheckboxGroup disabled={view} className="c-componentConfig__checkboxGroup">
                    {temp.values.map((comp: any) => {
                        return <Checkbox key={comp.key} value={comp.value}>{comp.key}</Checkbox>
                    })}
                </CheckboxGroup>
                break;
            default:
                content = <Input disabled={view} style={{ maxWidth: 680 }} />
                break;
        }

        return !temp.id && <FormItem
            label={<Tooltip title={temp.key}>{temp.key}</Tooltip>}
            key={temp.key}
            {...formItemLayout}
        >
            {form.getFieldDecorator(`${fieldName}.${temp.key.split('.').join('%')}`, {
                rules: [{
                    required: temp.required,
                    message: `请输入${temp.key}`
                }],
                initialValue: temp.value
            })(content)}
        </FormItem>
    }

    rendeConfigForm = () => {
        const { comp, form, view } = this.props;
        const typeCode = comp?.componentTypeCode ?? ''
        const template = comp?.componentTemplate ? JSON.parse(comp?.componentTemplate) : []
        let isHaveGroup = false

        return <>
            {template.map((temps: any) => {
                // 根据GROUP类型的模版对象的依赖值渲染单个配置项
                // 每个组件添加自定义参数
                if (temps.type == CONFIG_ITEM_TYPE.GROUP) {
                    isHaveGroup = true
                    const dependencyValue = form.getFieldValue(`${typeCode}.componentConfig.${temps.dependencyKey}`) ?? []
                    if (dependencyValue.includes(temps?.dependencyValue) || !temps.dependencyValue) {
                        return (
                            <div className="c-formConfig__group" key={temps.key}>
                                <div className="group__title">
                                    {temps.key}
                                </div>
                                <div className="group__content">
                                    {temps.values.map((temp: any) => {
                                        return this.renderConfigItem(temp, temps.key)
                                    })}
                                    <CustomParams
                                        typeCode={typeCode}
                                        form={form}
                                        view={view}
                                        template={temps}
                                    />
                                </div>
                            </div>
                        )
                    }
                } else if (temps.dependencyValue) {
                    const dependencyValue = form.getFieldValue(`${typeCode}.componentConfig.${temps.dependencyKey}`) ?? ''
                    if (dependencyValue == temps?.dependencyValue) {
                        return this.renderConfigItem(temps)
                    }
                } else {
                    return this.renderConfigItem(temps)
                }
            })}
            {!isHaveGroup && <CustomParams
                typeCode={typeCode}
                form={form}
                view={view}
                template={template}
                maxWidth={680}
            />}
        </>
    }

    renderKubernetsConfig = () => {
        const { comp, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const config = comp?.componentConfig ? JSON.parse(comp?.componentConfig) : ''
        return <>
            {config && <div className="c-formConfig__kubernetsContent">
                配置文件参数已被加密，此处不予显示
            </div>}
            {form.getFieldDecorator(`${typeCode}.specialConfig`, {
                initialValue: config || {}
            })(<></>)}
        </>
    }

    renderYarnOrHdfsConfig = () => {
        const { comp, view, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const template = comp?.componentTemplate ? JSON.parse(comp?.componentTemplate) : []
        const compConfig = comp?.componentConfig ? JSON.parse(comp?.componentConfig) : {}
        const config = form.getFieldValue(`${typeCode}.specialConfig`) ?? compConfig
        const keyAndValue = Object.entries(config);
        return <>
            {keyAndValue.map(([key, value]: any[]) => {
                return (
                    <Row key={key} className="zipConfig-item">
                        <Col className="formitem-textname" span={formItemLayout.labelCol.sm.span + 2}>
                            {key.length > 38
                                ? <Tooltip title={key}>{key.substr(0, 38) + '...'}</Tooltip>
                                : key}：
                        </Col>
                        <Col className="formitem-textvalue" span={formItemLayout.wrapperCol.sm.span + 1}>
                            {`${value}`}
                        </Col>
                    </Row>
                )
            })}
            {form.getFieldDecorator(`${typeCode}.specialConfig`, {
                initialValue: config || {}
            })(<></>)}
            <CustomParams
                typeCode={typeCode}
                form={form}
                view={view}
                template={template}
                labelCol={formItemLayout.labelCol.sm.span + 2}
                wrapperCol={formItemLayout.wrapperCol.sm.span - 2}
            />
        </>
    }

    renderComponentsConfig = () => {
        const { comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''

        switch (typeCode) {
            case COMPONENT_TYPE_VALUE.YARN:
            case COMPONENT_TYPE_VALUE.HDFS:
                return this.renderYarnOrHdfsConfig()
            case COMPONENT_TYPE_VALUE.KUBERNETES:
                return this.renderKubernetsConfig()
            case COMPONENT_TYPE_VALUE.SFTP:
            case COMPONENT_TYPE_VALUE.TIDB_SQL:
            case COMPONENT_TYPE_VALUE.LIBRA_SQL:
            case COMPONENT_TYPE_VALUE.ORACLE_SQL:
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
            case COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
            case COMPONENT_TYPE_VALUE.PRESTO_SQL: {
                return this.rendeConfigForm()
            }
            case COMPONENT_TYPE_VALUE.FLINK:
            case COMPONENT_TYPE_VALUE.SPARK:
            case COMPONENT_TYPE_VALUE.DTYARNSHELL:
            case COMPONENT_TYPE_VALUE.LEARNING: {
                return this.rendeConfigForm()
            }
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
            case COMPONENT_TYPE_VALUE.NFS:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER: {
                return this.rendeConfigForm()
            }
            default:
                return null;
        }
    }

    render () {
        return (
            <div className="c-formConfig__container">
                {this.renderComponentsConfig()}
            </div>
        )
    }
}
