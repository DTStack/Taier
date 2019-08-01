import * as React from 'react'

import {
    Input as mInput, Button, Form, Tooltip, Icon
} from 'antd'

import { isEmpty } from 'lodash';

import utils from 'utils';
import EngineSelect from '../../../components/engineSelect';

import {
    formItemLayout,
    tailFormItemLayout,
    ENGINE_SOURCE_TYPE
} from '../../../comm/const';
import {
    jdbcUrlExample
} from '../../../comm/JDBCCommon';

import HelpDoc from '../../helpDoc';
import CopyIcon from 'main/components/copy-icon';
const Input: any = mInput;
const FormItem = Form.Item;

const hdfsConf =
    `{
"dfs.nameservices": "defaultDfs", 
"dfs.ha.namenodes.defaultDfs": "namenode1", 
"dfs.namenode.rpc-address.defaultDfs.namenode1": "", 
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider" 
}`

class EngineSourceForm extends React.Component<any, any> {
    state: any = {
        sourceType: ENGINE_SOURCE_TYPE.HADOOP // Default is spark
    }

    componentDidMount () {
        const sourceData = this.props.sourceData;
        if (!isEmpty(sourceData) && sourceData.type) { // 初始化 sourceType
            let initialState: any = {
                sourceType: sourceData.type
            }
            this.setState(initialState);
        }
    }

    submit = (e: any) => {
        e.preventDefault()
        const ctx = this
        const { handOk, form } = this.props
        const source = form.getFieldsValue()

        this.preHandFormValues(source);

        form.validateFields((err: any) => {
            if (!err) {
                handOk(source, form, () => {
                    ctx.setState({
                        sourceType: ''
                    })
                })
            }
        });
    }

    testConnection = (e: any) => {
        const { testConnection, form } = this.props
        form.validateFields((err: any, source: any) => {
            if (!err) {
                this.preHandFormValues(source);
                testConnection(source)
            }
        });
    }

    preHandFormValues (source: any) {
        if (source.dataJson.jdbcUrl) {
            source.dataJson.jdbcUrl = utils.trim(source.dataJson.jdbcUrl)
        }
        if (source.dataJson.defaultFS) {
            source.dataJson.defaultFS = utils.trim(source.dataJson.defaultFS)
        }

        // 端口转为整型
        if (source.dataJson.port) {
            source.dataJson.port = parseInt(source.dataJson.port, 10)
        }
    }

    cancle = () => {
        const { form, handCancel } = this.props
        form.resetFields()
        handCancel()
    }

    sourceChange = (value: any) => {
        this.setState({ sourceType: parseInt(value, 10) })
        this.props.form.resetFields();
    }

    getJDBCRule = (type: any) => {
        switch (type) {
            case ENGINE_SOURCE_TYPE.LIBRA:
            case ENGINE_SOURCE_TYPE.HADOOP:
                return /jdbc:(\w)+:\/\/(\w)+/;
            default:
                return undefined;
        }
    }

    renderDynamic () {
        const { form, sourceData } = this.props;
        const { sourceType } = this.state;

        const { getFieldDecorator } = form;
        const config = sourceData.dataJson || {};

        const jdbcRulePattern: any = {
            pattern: this.getJDBCRule(sourceType),
            message: '请检查您的JDBC地址格式！'
        }

        switch (sourceType) {
            case ENGINE_SOURCE_TYPE.HADOOP: {
                const formItems: any = [
                    <FormItem
                        {...formItemLayout}
                        label="JDBC URL"
                        hasFeedback
                        key="jdbcUrl"
                    >
                        {getFieldDecorator('dataJson.jdbcUrl', {
                            rules: [{
                                required: true, message: 'jdbcUrl不可为空！'
                            }, jdbcRulePattern],
                            initialValue: config.jdbcUrl || ''
                        })(
                            <Input autoComplete="off" />
                        )}
                        <Tooltip title={'示例：' + jdbcUrlExample[sourceType]} arrowPointAtCenter>
                            <Icon className="help-doc" type="question-circle-o" />
                        </Tooltip>
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="用户名"
                        key="username"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.username', {
                            rules: [{
                                required: false, message: ''
                            }],
                            initialValue: config.username || ''
                        })(
                            <Input autoComplete="off" />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        key="password"
                        label="密码"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.password', {
                            rules: [{
                                required: false, message: ''
                            }],
                            initialValue: ''
                        })(
                            <Input type="password" autoComplete="off" />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="defaultFS"
                        key="defaultFS"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.defaultFS', {
                            rules: [{
                                required: true, message: 'defaultFS不可为空！'
                            }],
                            initialValue: config.defaultFS || ''
                        })(
                            <Input placeholder="hdfs://host:port" />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="高可用配置"
                        key="hadoopConfig"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.hadoopConfig', {
                            initialValue: config.hadoopConfig ? typeof config.hadoopConfig == 'string'
                                ? JSON.stringify(JSON.parse(config.hadoopConfig), null, 4) : JSON.stringify(config.hadoopConfig, null, 4) : ''
                        })(
                            <Input
                                className="no-scroll-bar"
                                type="textarea" rows={5}

                                placeholder={hdfsConf}
                            />
                        )}
                        <HelpDoc doc="hdfsConfig" />
                        <CopyIcon
                            style={{ position: 'absolute', right: '-20px', bottom: '0px' }}
                            copyText={hdfsConf}
                        />
                    </FormItem>
                ];
                return formItems
            }
            case ENGINE_SOURCE_TYPE.LIBRA: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="JDBC URL"
                        hasFeedback
                        key="jdbcUrl"

                    >
                        {getFieldDecorator('dataJson.jdbcUrl', {
                            rules: [{
                                required: true, message: 'jdbcUrl不可为空！'
                            }, jdbcRulePattern
                            ],
                            initialValue: config.jdbcUrl || ''
                        })(
                            <Input autoComplete="off" />
                        )}
                        <Tooltip overlayClassName="big-tooltip" title={'示例：' + jdbcUrlExample[sourceType]}>
                            <Icon className="help-doc" type="question-circle-o" />
                        </Tooltip>
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="用户名"
                        hasFeedback
                        key="username"
                    >
                        {getFieldDecorator('dataJson.username', {
                            rules: [{
                                required: true, message: '用户名不可为空！'
                            }],
                            initialValue: config.username || ''
                        })(
                            <Input autoComplete="off" />
                        )}
                    </FormItem>,
                    <FormItem
                        key="password"
                        {...formItemLayout}
                        label="密码"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.password', {
                            rules: [{
                                required: true, message: '密码不可为空！'
                            }],
                            initialValue: ''
                        })(
                            <Input type="password" autoComplete="off" />
                        )}
                    </FormItem>
                ]
            }
            default: return []
        }
    }

    render () {
        const { form, sourceData, formMode } = this.props;
        const { getFieldDecorator } = form;
        const { sourceType } = this.state;

        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="引擎类型"
                >
                    {getFieldDecorator('type', {
                        rules: [{
                            required: true, message: '引擎类型不可为空！'
                        }],
                        initialValue: sourceData.type ? sourceData.type.toString() : `${sourceType}`
                    })(
                        <EngineSelect
                            onChange={this.sourceChange}
                            disabled={formMode === 'edit'}
                        />
                    )}
                </FormItem>
                {this.renderDynamic()}
                <FormItem
                    {...tailFormItemLayout}
                    label=""
                >
                    <Button
                        icon="sync"
                        type="primary"
                        data-target="test"
                        onClick={this.testConnection}
                        style={{ marginRight: '10px' }}>测试连通性
                    </Button>
                    <Button
                        type="primary"
                        style={{ marginRight: '10px' }}
                        onClick={this.submit}>保存
                    </Button>
                    {/* <Button onClick={this.cancle}>删除</Button> */}
                </FormItem>
            </Form>
        )
    }
}

const FromWrapper = Form.create<any>()(EngineSourceForm);

export default FromWrapper;
