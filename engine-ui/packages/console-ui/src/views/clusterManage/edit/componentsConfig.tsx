import * as React from 'react';
import {
    Row, Col, Tooltip, Form, Input, Radio, Select
} from 'antd';
import { cloneDeep } from 'lodash';
import utils from 'dt-common/src/utils';
import {
    COMPONENT_TYPE_VALUE, COMPONEMT_CONFIG_KEY_ENUM,
    COMPONEMT_CONFIG_KEYS } from '../../../consts';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const formItemLayout: any = { // 表单常用布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 12 }
    }
};

class ComponentsConfig extends React.Component<any, any> {
    renderYarnOrHdfsConfig = () => {
        const { componentConfig, components } = this.props;
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[components.componentTypeCode]] || {}
        const configInfo = config.configInfo || {}
        let keyAndValue: any;
        keyAndValue = Object.entries(configInfo);
        utils.sortByCompareFunctions(keyAndValue,
            ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                if (key == 'yarn.resourcemanager.ha.rm-ids') {
                    return -1;
                }
                if (compareKey == 'yarn.resourcemanager.ha.rm-ids') {
                    return 1;
                }
                return 0;
            },
            ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                const checkKey = key.indexOf('yarn.resourcemanager.address') > -1
                const checkCompareKey = compareKey.indexOf('yarn.resourcemanager.address') > -1
                if (checkKey && checkCompareKey) {
                    return key > compareKey ? 1 : -1
                } else if (checkKey) {
                    return -1;
                } else if (checkCompareKey) {
                    return 1;
                } else {
                    return 0;
                }
            },
            ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                const checkKey = key.indexOf('yarn.resourcemanager.webapp.address') > -1
                const checkCompareKey = compareKey.indexOf('yarn.resourcemanager.webapp.address') > -1
                if (checkKey && checkCompareKey) {
                    return key > compareKey ? 1 : -1
                } else if (checkKey) {
                    return -1;
                } else if (checkCompareKey) {
                    return 1;
                } else {
                    return 0;
                }
            });
        return keyAndValue.map(
            ([key, value]: any[]) => {
                return (<Row key={key} className="zipConfig-item">
                    <Col className="formitem-textname" span={formItemLayout.labelCol.sm.span + 4}>
                        {key.length > 38
                            ? <Tooltip title={key}>{key.substr(0, 38) + '...'}</Tooltip>
                            : key}：
                    </Col>
                    <Col className="formitem-textvalue" span={formItemLayout.wrapperCol.sm.span - 1}>
                        {`${value}`}
                    </Col>
                </Row>)
            }
        )
    }
    compsContent = (item: any) => {
        const { isView } = this.props;
        if (item.type === 'INPUT') { return (<Input disabled={isView} />) }
        if (item.type === 'RADIO') {
            return (
                <RadioGroup disabled={isView}>
                    {item.values.map((comp: any, index) => {
                        return <Radio key={comp.key} value={comp.value}>{comp.key}</Radio>
                    })}
                </RadioGroup>
            )
        }
        if (item.type === 'SELECT') {
            return (
                <Select disabled={isView}>
                    {item.values.map((comp: any, index) => {
                        return <Option key={comp.key} value={comp.value}>{comp.key}</Option>
                    })}
                </Select>
            )
        }
    }
    rendeConfigInfo = (comps: any) => {
        const { componentConfig, components, getFieldDecorator, getFieldValue } = this.props;
        const componentTypeCode = components.componentTypeCode;
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {}
        const loadTemplate = config.loadTemplate || [];
        const configInfo = config.configInfo || {};
        let cloneLoadTemplate = cloneDeep(loadTemplate)
        if (cloneLoadTemplate.length > 0) {
            return cloneLoadTemplate.map((item: any, index: any) => {
                return (
                    item.dependencyValue
                        ? getFieldValue(`${comps}.configInfo.${item.dependencyKey}`) === item.dependencyValue
                            ? <FormItem
                                label={item.key}
                                key={item.key}
                                {...formItemLayout}
                            >
                                {getFieldDecorator(`${comps}.configInfo.${item.key}`, {
                                    rules: [{
                                        required: item.required,
                                        message: `请输入${item.key}`
                                    }],
                                    // initialValue: item.value
                                    initialValue: configInfo[item.key] || ''
                                })(
                                    this.compsContent(item)
                                )}
                            </FormItem>
                            : null
                        : <FormItem
                            label={item.key}
                            key={item.key}
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${comps}.configInfo.${item.key}`, {
                                rules: [{
                                    required: item.required,
                                    message: `请输入${item.key}`
                                }],
                                // initialValue: item.value
                                initialValue: configInfo[item.key] || ''
                            })(
                                this.compsContent(item)
                            )}
                        </FormItem>
                )
            })
        }
    }

    renderCustomParam = (comps: any) => {
        const { componentConfig, components, getFieldDecorator, isView, deleteParams } = this.props;
        const componentTypeCode = components.componentTypeCode;
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {}
        const params = config.params;
        return params && params.map((param: any) => {
            return (<Row key={param.id}>
                <Col span={formItemLayout.labelCol.sm.span}>
                    <FormItem key={param.id + '-key'}>
                        {getFieldDecorator(`${comps}.params.%${param.id}-key`, {
                            rules: [{
                                required: true,
                                message: '请输入参数属性名'
                            }],
                            initialValue: param.key || ''
                        })(
                            <Input disabled={isView} style={{ width: 'calc(100% - 12px)' }} />
                        )}
                        :
                    </FormItem>
                </Col>
                <Col span={formItemLayout.wrapperCol.sm.span}>
                    <FormItem key={param.id + '-value'}>
                        {getFieldDecorator(`${comps}.params.%${param.id}-value`, {
                            rules: [{
                                required: true,
                                message: '请输入参数属性值'
                            }],
                            initialValue: param.value || ''
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>

                </Col>
                {isView ? null : (<a className="formItem-right-text" onClick={() => deleteParams(components, param.id)}>删除</a>)}
            </Row>)
        })
    }

    // 自定义参数
    renderAddCustomParam = () => {
        const { isView, components } = this.props;
        return isView ? null : (
            <Row>
                <Col span={formItemLayout.labelCol.sm.span}></Col>
                <Col className="m-card" style={{ marginBottom: '20px' }} span={formItemLayout.wrapperCol.sm.span}>
                    <a onClick={() => this.props.addParams(components)}>添加自定义参数</a>
                </Col>
            </Row>
        )
    }
    renderComponentsConfig = () => {
        const { components } = this.props;
        switch (components.componentTypeCode) {
            case COMPONENT_TYPE_VALUE.YARN:
                return (
                    <React.Fragment>
                        {this.renderYarnOrHdfsConfig()}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.HDFS:
                return (
                    <React.Fragment>
                        {this.renderYarnOrHdfsConfig()}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.SFTP:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.SFTP)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.TIDB_SQL:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.TIDB_SQL)}
                        {this.renderCustomParam(COMPONEMT_CONFIG_KEYS.TIDB_SQL)}
                        {this.renderAddCustomParam()}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.LIBRA_SQL:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.LIBRA_SQL)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.ORACLE_SQL:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.ORACLE_SQL)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.IMPALA_SQL)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.SPARK_THRIFT_SERVER)}
                        {this.renderCustomParam(COMPONEMT_CONFIG_KEYS.SPARK_THRIFT_SERVER)}
                        {this.renderAddCustomParam()}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.FLINK:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.FLINK)}
                        {this.renderCustomParam(COMPONEMT_CONFIG_KEYS.FLINK)}
                        {this.renderAddCustomParam()}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.HIVE_SERVER)}
                        {this.renderCustomParam(COMPONEMT_CONFIG_KEYS.HIVE_SERVER)}
                        {this.renderAddCustomParam()}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.LEARNING:
                return (
                    <React.Fragment>
                        {this.rendeConfigInfo(COMPONEMT_CONFIG_KEYS.LEARNING)}
                        {this.renderCustomParam(COMPONEMT_CONFIG_KEYS.LEARNING)}
                        {this.renderAddCustomParam()}
                    </React.Fragment>
                )
            default:
                break;
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
