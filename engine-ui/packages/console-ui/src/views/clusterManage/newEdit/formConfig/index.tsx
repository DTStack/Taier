import * as React from 'react'
import { isArray } from 'lodash'
import { Input, Form, Radio, Select, Checkbox,
    Tooltip, Row, Col } from 'antd'
import { COMPONENT_TYPE_VALUE, CONFIG_ITEM_TYPE } from '../const'
import { getValueByJson, isDeployMode,
    isRadioLinkage, isCustomType, isMultiVersion,
    isDtscriptAgent } from '../help'
import { formItemLayout } from '../../../../consts'
import CustomParams from './components/customParams'
import NodeLabel from './components/nodeLabel'
interface IProps {
    comp: any;
    form: any;
    view: boolean;
    itemLayout?: any;
}

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
export default class FormConfig extends React.PureComponent<IProps, any> {
    renderOptoinsType = (temp: any) => {
        const { view } = this.props
        switch (temp.type) {
            case CONFIG_ITEM_TYPE.RADIO:
            case CONFIG_ITEM_TYPE.RADIO_LINKAGE:
                return <RadioGroup disabled={view}>
                    {temp.values.map((comp: any) => {
                        return <Radio key={comp.key} value={comp.value}>{comp.key}</Radio>
                    })}
                </RadioGroup>
            case CONFIG_ITEM_TYPE.SELECT:
                return <Select disabled={view} style={{ width: 200 }}>
                    {temp.values.map((comp: any) => {
                        return <Option key={comp.key} value={comp.value}>{comp.key}</Option>
                    })}
                </Select>
            case CONFIG_ITEM_TYPE.CHECKBOX:
                return <CheckboxGroup disabled={view} className="c-componentConfig__checkboxGroup">
                    {temp.values.map((comp: any) => {
                        return <Checkbox key={comp.key} value={`${comp.value}`}>{comp.key}</Checkbox>
                    })}
                </CheckboxGroup>
            case CONFIG_ITEM_TYPE.PASSWORD:
                return <Input.Password disabled={view} style={{ maxWidth: 680 }} visibilityToggle={false} />
            default:
                return <Input disabled={view} style={{ maxWidth: 680 }} />
        }
    }

    // 渲染单个配置项
    renderConfigItem = (temp: any, groupKey?: string) => {
        const { form, comp, itemLayout } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''
        const layout = itemLayout ?? formItemLayout
        const initialValue = temp.key === 'deploymode' && !isArray(temp.value) ? temp.value.split() : temp.value

        let formField = typeCode
        if (isMultiVersion(typeCode)) formField = formField + '.' + hadoopVersion

        const fieldName = groupKey ? `${formField}.componentConfig.${groupKey}` : `${formField}.componentConfig`;

        return !isCustomType(temp.type) && <FormItem
            label={<Tooltip title={temp.key}>
                <span className="c-formConfig__label">{temp.key}</span>
            </Tooltip>}
            key={temp.key}
            {...layout}
        >
            {form.getFieldDecorator(`${fieldName}.${temp.key.split('.').join('%')}`, {
                rules: [{
                    required: temp.required,
                    message: `请输入${temp.key}`
                }],
                initialValue: initialValue
            })(this.renderOptoinsType(temp))}
            {isDtscriptAgent(typeCode) && <NodeLabel form={form} />}
        </FormItem>
    }

    // 渲染group级别配置项
    renderGroupConfigItem = (temps: any, notParams?: boolean) => {
        const { form, comp, view } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''
        let formField = typeCode
        if (isMultiVersion(typeCode)) formField = formField + '.' + hadoopVersion

        const dependencyValue = temps?.dependencyKey
            ? form.getFieldValue(formField + '.componentConfig.' + temps?.dependencyKey)
            : []

        if (dependencyValue.includes(temps?.dependencyValue) || !temps?.dependencyValue) {
            if (notParams) {
                return temps.values.map((temp: any) => {
                    return this.renderConfigItem(temp)
                })
            }
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
                            hadoopVersion={hadoopVersion}
                            form={form}
                            view={view}
                            template={temps}
                            maxWidth={680}
                        />
                    </div>
                </div>
            )
        }
    }

    rendeConfigForm = () => {
        const { comp, form, view, itemLayout } = this.props;
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''
        const template = getValueByJson(comp?.componentTemplate) ?? []

        return template.map((temps: any, index: number) => {
            /**
             * 根据根结点deploymode判断是否需要读取二级数据
             * Radio联动类型数据不添加自定义参数
             */
            if (isDeployMode(temps.key) || isRadioLinkage(temps.type)) {
                return <>
                    {this.renderConfigItem(temps)}
                    {temps.values.map((temp: any) => this.renderGroupConfigItem(temp, isRadioLinkage(temps.type)))}
                </>
            } else if (temps.type == CONFIG_ITEM_TYPE.GROUP) {
                return this.renderGroupConfigItem(temps)
            } else {
                return <>
                    {this.renderConfigItem(temps)}
                    {(index === template.length - 1) ? <CustomParams
                        typeCode={typeCode}
                        hadoopVersion={hadoopVersion}
                        form={form}
                        view={view}
                        template={template}
                        maxWidth={680}
                        labelCol={itemLayout?.labelCol?.sm?.span}
                        wrapperCol={itemLayout?.wrapperCol?.sm?.span}
                    /> : null}
                </>
            }
        })
    }

    renderKubernetsConfig = () => {
        const { comp, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const config = form.getFieldValue(typeCode + '.specialConfig') ?? comp?.componentConfig ?? ''

        return <>
            {config ? <div className="c-formConfig__kubernetsContent">
                配置文件参数已被加密，此处不予显示
            </div> : null}
            {form.getFieldDecorator(`${typeCode}.specialConfig`, {
                initialValue: config || {}
            })(<></>)}
        </>
    }

    renderYarnOrHdfsConfig = () => {
        const { comp, view, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const template = getValueByJson(comp?.componentTemplate) ?? []
        const compConfig = getValueByJson(comp?.componentConfig) ?? {}
        const config = form.getFieldValue(`${typeCode}.specialConfig`) ?? compConfig
        const keyAndValue = Object.entries(config)

        return <>
            {keyAndValue.map(([key, value]: any[]) => {
                return (
                    <Row key={key} className="zipConfig-item">
                        <Col className="formitem-textname" span={formItemLayout.labelCol.sm.span + 2}>
                            <Tooltip title={key} placement="topRight">
                                <span className="form-text-name">{key}</span>
                            </Tooltip>
                            <span>：</span>
                        </Col>
                        <Col className="formitem-textvalue" span={formItemLayout.wrapperCol.sm.span + 1}>
                            {`${value}`}
                        </Col>
                    </Row>
                )
            })}
            {form.getFieldDecorator(`${typeCode}.specialConfig`, {
                initialValue: config || {}
            })(<span style={{ display: 'none' }}></span>)}
            <CustomParams
                key={String(template.length)}
                typeCode={typeCode}
                form={form}
                comp={comp}
                view={view}
                template={template}
                labelCol={formItemLayout.labelCol.sm.span + 2}
                wrapperCol={formItemLayout.wrapperCol.sm.span - 3}
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
            case COMPONENT_TYPE_VALUE.PRESTO_SQL:
            case COMPONENT_TYPE_VALUE.FLINK:
            case COMPONENT_TYPE_VALUE.SPARK:
            case COMPONENT_TYPE_VALUE.DTYARNSHELL:
            case COMPONENT_TYPE_VALUE.LEARNING:
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
            case COMPONENT_TYPE_VALUE.NFS:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
            case COMPONENT_TYPE_VALUE.DTSCRIPT_AGENT:
            case COMPONENT_TYPE_VALUE.INCEPTOR_SQL:
            case COMPONENT_TYPE_VALUE.ANALYTIC_DB:
            case COMPONENT_TYPE_VALUE.FLINK_ON_STANDALONE: {
                return this.rendeConfigForm()
            }
            default:
                return null;
        }
    }

    render () {
        const typeCode = this.props.comp?.componentTypeCode ?? ''
        const className = 'c-formConfig__container ' + (isDtscriptAgent(typeCode) ? 'c-formConfig__full' : '')

        return (
            <div className={className}>
                {this.renderComponentsConfig()}
            </div>
        )
    }
}
