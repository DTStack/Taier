import * as React from 'react';
import {
    Row, Col, Tooltip, Form, Input, Radio, Select, Checkbox
} from 'antd';
import { cloneDeep, isArray } from 'lodash';
import {
    COMPONENT_TYPE_VALUE, COMPONEMT_CONFIG_KEY_ENUM,
    COMPONEMT_CONFIG_KEYS, formItemLayout } from '../../../consts';
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;

function renderFormItem ({ itemConfig, formItemLayout, getFieldDecorator }) {
    const { label, key, required, component, options = {}, rules, style } = itemConfig
    return (
        <FormItem key={key} label={<Tooltip title={label}>{label}</Tooltip>} {...formItemLayout} style={style}>
            {getFieldDecorator(key, {
                ...options,
                rules: rules || [{ required, message: `${label}为空` }]
            })(component || <Input />)}
        </FormItem>
    )
}

class ComponentsConfig extends React.Component<any, any> {
    state: any = {
        isSameKey: {} // 自定义参数是否重复
    }
    getComponentConfig = () => {
        const { componentConfig, components } = this.props;
        const componentTypeCode = components.componentTypeCode;
        return componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {}
    }
    renderYarnOrHdfsConfig = (value) => {
        const config = this.getComponentConfig();
        const { configInfo = {} } = config;
        const keyAndValue = Object.entries(configInfo);
        const view = keyAndValue.map(([key, value]: any[]) => {
            return (
                <Row key={key} className="zipConfig-item">
                    <Col className="formitem-textname" span={formItemLayout.labelCol.sm.span + 4}>
                        {key.length > 38
                            ? <Tooltip title={key}>{key.substr(0, 38) + '...'}</Tooltip>
                            : key}：
                    </Col>
                    <Col className="formitem-textvalue" span={formItemLayout.wrapperCol.sm.span - 1}>
                        {`${value}`}
                    </Col>
                </Row>
            )
        })
        if (view?.length !== 0) {
            view.push(this.renderCustomParam(value))
            view.push(this.renderAddCustomParam())
        }
        return view
    }
    renderKubernetsConfig = () => {
        const config = this.getComponentConfig();
        const { configInfo = '' } = config;
        return configInfo && (
            <div className="c-componentConfig__kubernetsContent">
                配置文件参数已被加密，此处不予显示
            </div>
        )
    }
    renderCompsContent = (item: any) => {
        const { isView } = this.props;
        switch (item.type) {
            case 'INPUT':
                return (<Input disabled={isView} style={{ maxWidth: 680 }} />)
            case 'RADIO':
                return (
                    <RadioGroup disabled={isView}>
                        {item.values.map((comp: any) => {
                            return <Radio key={comp.key} value={comp.value}>{comp.key}</Radio>
                        })}
                    </RadioGroup>
                )
            case 'SELECT':
                return (
                    <Select disabled={isView} style={{ width: 200 }}>
                        {item.values.map((comp: any) => {
                            return <Option key={comp.key} value={comp.value}>{comp.key}</Option>
                        })}
                    </Select>
                )
            case 'CHECKBOX':
                return (
                    <CheckboxGroup disabled={isView} className="c-componentConfig__checkboxGroup">
                        {item.values.map((comp: any) => {
                            return <Checkbox key={comp.key} value={comp.value}>{comp.key}</Checkbox>
                        })}
                    </CheckboxGroup>
                )
            default:
                return null;
        }
    }

    rendeConfigForm = (comps: any, flag: any) => {
        const { componentConfig, components } = this.props;
        const componentTypeCode = components.componentTypeCode;
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {}
        const loadTemplate = config.loadTemplate || [];
        // console.log('loadTemplate===========', loadTemplate)
        let cloneLoadTemplate = cloneDeep(loadTemplate)
        if (cloneLoadTemplate.length > 0) {
            const view = cloneLoadTemplate.map((item: any, index: any) => {
                if (item.type === 'GROUP') {
                    return this.renderConfigGroup(comps, item);
                }
                return !item.id && this.renderConfigFormItem(comps, item);
            })
            if (view.length !== 0 && flag) {
                view.push(this.renderCustomParam(comps))
                view.push(this.renderAddCustomParam())
            }
            return view
        }
    }

    renderConfigFormItem = (comps: any, item: any) => {
        const { getFieldValue, getFieldDecorator } = this.props;
        const isSelectGroup = comps === COMPONEMT_CONFIG_KEYS.FLINK || comps === COMPONEMT_CONFIG_KEYS.SPARK;
        const itemConfig = {
            label: item.key,
            style: { padding: isSelectGroup ? '0 20px' : '' },
            key: `${comps}.configInfo.${item.key.split('.').join('%')}`,
            options: {
                initialValue: item.key === 'deploymode' && !isArray(item.value) ? item.value.split() : item.value
            },
            component: this.renderCompsContent(item),
            rules: [{ required: item.required, message: `请输入${item.key}` }]
        }
        if (!item.dependencyKey) {
            return renderFormItem({ itemConfig, formItemLayout, getFieldDecorator })
        }
        if (item.dependencyKey && getFieldValue(`${comps}.configInfo.${item.dependencyKey}`) === item.dependencyValue) {
            return renderFormItem({ itemConfig, formItemLayout, getFieldDecorator })
        }
    }

    renderConfigGroup = (comps: any, group: any) => {
        const { getFieldDecorator, getFieldValue, components } = this.props;
        const componentTypeCode = components.componentTypeCode;
        const isHaveGroup = componentTypeCode === COMPONENT_TYPE_VALUE.DTYARNSHELL || componentTypeCode === COMPONENT_TYPE_VALUE.LEARNING;
        // console.log('group================', group)
        if (isHaveGroup || getFieldValue(`${comps}.configInfo.${group.dependencyKey}`).includes(group.dependencyValue)) {
            return (
                <div className="c-componentsConfig__group" key={group.key}>
                    <div className="c-componentsConfig__group__title">
                        {group.key}
                    </div>
                    <div className="c-componentsConfig__group__content">
                        {
                            group.values.length > 0 && group.values.map((item: any) => {
                                return (
                                    !item.id && <FormItem
                                        label={<Tooltip title={item.key}>{item.key}</Tooltip>}
                                        key={item.key}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator(`${comps}.configInfo.${group.key}.${item.key.split('.').join('%')}`, {
                                            rules: [{
                                                required: item.required,
                                                message: `请输入${item.key}`
                                            }],
                                            initialValue: item.value
                                        })(
                                            this.renderCompsContent(item)
                                        )}
                                    </FormItem>
                                )
                            })
                        }
                        {this.renderCustomParam(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode], group.key)}
                        {this.renderAddCustomParam(group)}
                    </div>
                </div>
            )
        }
    }

    compareLoadTemplateParamsKey = (paramsArr: any, paramKey: any) => {
        let paramKeyNum = 0;
        paramsArr.forEach((param: any) => {
            if (param.key === String(paramKey) && !param.id) paramKeyNum++;
        })
        if (paramKeyNum > 0) return true;
        return false;
    }

    compareParamsKey = (paramsArr: any, paramKey: any, field: any) => {
        const { getFieldValue } = this.props;
        let isSameParamsKey = false;
        paramsArr.forEach((p: any) => {
            if (getFieldValue(`${field}${p.id}-key`) === paramKey) isSameParamsKey = true
        })
        return isSameParamsKey;
    }

    /**
     * @paramKey 自定义参数key值
     * @groupKey 组件group值
     * @comps 组件对应名称
     */
    handleParamsKey = (paramKey: string, groupKey: string = '', comps: any) => {
        const config = this.getComponentConfig();
        const { params = [], loadTemplate = [] } = config;
        const field = groupKey ? `${comps}.params.${groupKey}.%` : `${comps}.params.%`;
        // console.log('paramKey========', paramKey, params, loadTemplate)
        if (!paramKey) return false;
        if (groupKey) {
            const groupLoadTemplate = loadTemplate.find((temp: any) => temp.key === groupKey).values
            const groupParams = params.find((p: any) => p.key === groupKey).groupParams
            if (this.compareLoadTemplateParamsKey(groupLoadTemplate, paramKey)) return true;
            if (this.compareParamsKey(groupParams, paramKey, field)) return true;
        }
        if (this.compareLoadTemplateParamsKey(loadTemplate, paramKey)) return true;
        if (this.compareParamsKey(params, paramKey, field)) return true;
        return false;
    }

    handleCustomParam = (e: any, groupKey: any, id: any, comps: any) => {
        const value = e.target.value;
        this.setState({
            isSameKey: {
                ...this.state.isSameKey,
                [id]: this.handleParamsKey(value, groupKey, comps)
            }
        })
        // console.log('val======sss=======groupKey', value, groupKey, this.compareParamsKey(value, groupKey))
    }

    renderCustomParam = (comps: any, groupKey: any = '') => {
        const { componentConfig, components, getFieldDecorator, isView, deleteParams } = this.props;
        const componentTypeCode = components.componentTypeCode;
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {}
        const cloneParams = cloneDeep(config.params || []);
        let params = cloneDeep(config.params || []);
        if (groupKey) {
            cloneParams.forEach((p: any) => {
                if (p.key === groupKey) {
                    params = p.groupParams
                }
            })
        }
        // console.log('groupKeyparams=========', params)
        return params && params.map((param: any) => {
            const { isSameKey } = this.state;
            const field = groupKey ? `${comps}.params.${groupKey}.%${param.id}` : `${comps}.params.%${param.id}`;
            // console.log('isSameKey=======', isSameKey)
            return (<Row key={param.id}>
                <Col span={formItemLayout.labelCol.sm.span}>
                    <FormItem key={param.id + '-key'}>
                        {getFieldDecorator(`${field}-key`, {
                            rules: [{
                                required: true,
                                message: '请输入参数属性名'
                            }],
                            initialValue: param.key || ''
                        })(
                            <Input disabled={isView} style={{ width: 'calc(100% - 12px)' }} onChange={(e: any) => this.handleCustomParam(e, groupKey, param.id, comps)} />
                        )}
                        <span style={{ marginLeft: 2 }}>:</span>
                    </FormItem>
                </Col>
                <Col span={formItemLayout.wrapperCol.sm.span}>
                    <FormItem key={param.id + '-value'}>
                        {getFieldDecorator(`${field}-value`, {
                            rules: [{
                                required: true,
                                message: '请输入参数属性值'
                            }],
                            initialValue: param.value || ''
                        })(
                            <Input disabled={isView} style={{ maxWidth: 680 }} />
                        )}
                    </FormItem>
                </Col>
                {isView ? null : (<a className="formItem-right-text" onClick={() => deleteParams(components, param.id, groupKey)}>删除</a>)}
                {isView ? null : isSameKey[param.id] && (<span className="formItem-right-text">该参数已存在</span>)}
            </Row>)
        })
    }

    // 自定义参数
    renderAddCustomParam = (group: any = {}) => {
        const { isView, components } = this.props;
        // console.log('ssssss======group', group)
        return isView ? null : (
            <Row>
                <Col span={formItemLayout.labelCol.sm.span}></Col>
                <Col className="m-card" style={{ marginBottom: '20px' }} span={formItemLayout.wrapperCol.sm.span}>
                    <a onClick={() => this.props.addParams(components, group.key)}>添加自定义参数</a>
                </Col>
            </Row>
        )
    }
    renderComponentsConfig = () => {
        const { componentTypeCode = '' } = this.props?.components
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.YARN:
            case COMPONENT_TYPE_VALUE.HDFS:
                return (
                <>
                    {this.renderYarnOrHdfsConfig(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}

                </>
                )
            case COMPONENT_TYPE_VALUE.KUBERNETES:
                return (
                <>
                    {this.renderKubernetsConfig()}
                </>
                )
            case COMPONENT_TYPE_VALUE.SFTP:
            case COMPONENT_TYPE_VALUE.TIDB_SQL:
            case COMPONENT_TYPE_VALUE.LIBRA_SQL:
            case COMPONENT_TYPE_VALUE.ORACLE_SQL:
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
            case COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
            case COMPONENT_TYPE_VALUE.FLINK:
            case COMPONENT_TYPE_VALUE.SPARK:
            case COMPONENT_TYPE_VALUE.LEARNING:
            case COMPONENT_TYPE_VALUE.DTYARNSHELL:
            case COMPONENT_TYPE_VALUE.PRESTO_SQL: {
                switch (componentTypeCode) {
                    case COMPONENT_TYPE_VALUE.FLINK:
                    case COMPONENT_TYPE_VALUE.SPARK:
                    case COMPONENT_TYPE_VALUE.DTYARNSHELL:
                    case COMPONENT_TYPE_VALUE.LEARNING: {
                        return (
                            <>
                                {this.rendeConfigForm(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode], false)}
                            </>
                        )
                    }
                    default: {
                        return (
                            <>
                                {this.rendeConfigForm(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode], true)}

                            </>
                        )
                    }
                }
            }
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
            case COMPONENT_TYPE_VALUE.NFS:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER: {
                return (
                    <React.Fragment>
                        {this.rendeConfigForm(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode], true)}
                    </React.Fragment>
                )
            }
            default:
                return null;
        }
    }

    render () {
        return (
            <div className="c-componentsConfig__container">
                {this.renderComponentsConfig()}
            </div>
        )
    }
}

export default ComponentsConfig;
