import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Input, Button, Select, Form, Checkbox, message } from 'antd';

import utils from 'utils'

import HelpDoc from '../helpDoc';
import { formItemLayout, tailFormItemLayout, DATA_SOURCE } from '../../consts';
import { dataSourceActions } from '../../actions/dataSource';
import Api from '../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;

const hdfsConf =
`{
    "defaultFS": "",
    "hadoopConfig": {
        "dfs.nameservices": "defaultDfs", 
        "dfs.ha.namenodes.defaultDfs": "namenode1", 
        "dfs.namenode.rpc-address.defaultDfs.namenode1": "", 
        "dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider" 
    }
}`

const mapStateToProps = state => {
    const { dataSource } = state;
    return { dataSource }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesType (params) {
        dispatch(dataSourceActions.getDataSourcesType(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
class DataSourceModal extends Component {
    state = {
        sourceType: undefined,
        hasHdfsConfig: false,
        hadoopConfig: 'defaultDfs'
    }

    componentDidMount () {
        // this.props.getDataSourcesType();
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const oldData = this.props.sourceData;
        const newData = nextProps.sourceData;

        if (newData && newData !== oldData) {
            if (newData.type === 7) {
                this.setState({ sourceType: newData.type || 1, hasHdfsConfig: true });
            } else {
                this.setState({ sourceType: newData.type || 1 });
            }
        }
    }
    onOk = (e) => {
        const { editDataSource, form } = this.props;

        form.validateFields((err, values) => {
            console.log(err, values)
            if (!err) {
                if (values.dataJson.jdbcUrl) {
                    values.dataJson.jdbcUrl = utils.trim(values.dataJson.jdbcUrl)
                }
                if (values.dataJson.defaultFS) {
                    values.dataJson.defaultFS = utils.trim(values.dataJson.defaultFS)
                }
                editDataSource(values, form);
            }
        });
    }

    testConnection = () => {
        const { sourceType } = this.state;
        const { form } = this.props;

        let field;

        if (sourceType === DATA_SOURCE.MAXCOMPUTE) {
            field = [
                'type',
                'dataName',
                'dataJson.project',
                'dataJson.endPoint',
                'dataJson.accessKey',
                'dataJson.accessId'
            ];
        }

        form.validateFields(field, (err, values) => {
            if (!err) {
                Api.testDSConnection(values).then((res) => {
                    if (res.data) {
                        message.success('数据源连接正常！');
                    } else {
                        message.error('数据库连接失败！');
                    }
                });
            }
        });
    }

    cancel = () => {
        const { form, handCancel } = this.props;
        form.resetFields();
        handCancel();
    }

    sourceChange = (value) => {
        this.setState({ sourceType: parseInt(value) });
        this.props.form.resetFields();
    }

    enableHdfsConfig = (e) => {
        this.setState({
            hasHdfsConfig: !e.target.value
        });
    }

    renderDynamic () {
        const { hasHdfsConfig, sourceType } = this.state
        const { form, sourceData } = this.props;
        const { getFieldDecorator } = form;
        const config = sourceData.dataJson || {};

        switch (sourceType) {
            case DATA_SOURCE.HIVE: {
                return <div>
                    <FormItem {...formItemLayout} label="JDBC URL" hasFeedback>
                        {
                            getFieldDecorator('dataJson.jdbcUrl', {
                                rules: [{
                                    required: true, message: 'jdbcUrl不可为空！'
                                }],
                                initialValue: config.jdbcUrl || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="用户名">
                        {
                            getFieldDecorator('dataJson.username', {
                                rules: [],
                                initialValue: config.username || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="密码">
                        {
                            getFieldDecorator('dataJson.password', {
                                rules: [],
                                initialValue: ''
                            })(
                                <Input type="password"/>
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="defaultFS" hasFeedback>
                        {
                            getFieldDecorator('dataJson.defaultFS', {
                                rules: [{
                                    required: true, message: 'defaultFS不可为空！'
                                }],
                                initialValue: config.defaultFS || ''
                            })(
                                <Input placeholder="hdfs://host:port"/>
                            )
                        }
                    </FormItem>
                    <FormItem {...tailFormItemLayout}>
                        {
                            getFieldDecorator('hasHdfsConfig', {
                                initialValue: false
                            })(
                                <Checkbox checked={hasHdfsConfig} onChange={this.enableHdfsConfig}>
                                        高可用配置
                                </Checkbox>
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="高可用配置" style={{ display: hasHdfsConfig ? 'block' : 'none' }}>
                        {
                            getFieldDecorator('dataJson.hadoopConfig', {
                                rules: [],
                                initialValue: config.hadoopConfig || ''
                            })(
                                <Input type="textarea" rows={5} placeholder={hdfsConf} />
                            )
                        }
                        <HelpDoc doc="hdfsConfig" />
                    </FormItem>
                </div>
            }

            case DATA_SOURCE.MAXCOMPUTE: {
                return <div>
                    <FormItem {...formItemLayout} label="AccessId" hasFeedback>
                        {
                            getFieldDecorator('dataJson.accessId', {
                                rules: [{
                                    required: true, message: 'access ID不可为空！'
                                }],
                                initialValue: config.accessId || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="AccessKey" hasFeedback>
                        {
                            getFieldDecorator('dataJson.accessKey', {
                                rules: [{
                                    required: true, message: 'access Key不可为空！'
                                }],
                                initialValue: config.accessKey || ''
                            })(
                                <Input type="password" autoComplete="off" />
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="Project Name" hasFeedback>
                        {
                            getFieldDecorator('dataJson.project', {
                                rules: [{
                                    required: true, message: 'Project Name不可为空！'
                                }],
                                initialValue: config.project || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="End Point" hasFeedback>
                        {
                            getFieldDecorator('dataJson.endPoint', {
                                rules: [{
                                    required: true, message: 'End Point不可为空！'
                                }],
                                initialValue: config.endPoint || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                </div>
            }

            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.SQLSERVER: {
                return <div>
                    <FormItem {...formItemLayout} label="JDBC URL" hasFeedback>
                        {
                            getFieldDecorator('dataJson.jdbcUrl', {
                                rules: [{
                                    required: true, message: 'jdbcUrl不可为空！'
                                }],
                                initialValue: config.jdbcUrl || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="用户名" hasFeedback>
                        {
                            getFieldDecorator('dataJson.username', {
                                rules: [{
                                    required: true, message: '用户名不可为空！'
                                }],
                                initialValue: config.username || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="密码" hasFeedback>
                        {
                            getFieldDecorator('dataJson.password', {
                                rules: [{
                                    required: true, message: '密码不可为空！'
                                }],
                                initialValue: ''
                            })(
                                <Input type="password"/>
                            )
                        }
                    </FormItem>
                </div>
            }
            default:
                break;
        }
    }

    renderSourceType = (data) => {
        return data.map(
            type => (
                <Option
                    key={type.value}
                    value={type.value.toString()}>
                    {type.name}
                </Option>
            )
        );
    }

    render () {
        const { visible, form, title, sourceData, status, dataSource } = this.props;
        const { getFieldDecorator } = form;

        return (
            <Modal
                title={title}
                maskClosable={false}
                wrapClassName="vertical-center-modal"
                visible={visible}
                onCancel={this.cancel}
                footer={false}
            >
                <Form>
                    <FormItem {...formItemLayout} label="数据源类型" hasFeedback>
                        {
                            getFieldDecorator('type', {
                                rules: [{
                                    required: true, message: '数据源类型不可为空！'
                                }],
                                initialValue: sourceData.type ? sourceData.type.toString() : '1'
                            })(
                                <Select
                                    onChange={this.sourceChange}
                                    disabled={status === 'edit'}>
                                    {
                                        this.renderSourceType(dataSource.sourceType)
                                    }
                                </Select>
                            )}
                    </FormItem>

                    <FormItem {...formItemLayout} label="数据源名称" hasFeedback>
                        {
                            getFieldDecorator('dataName', {
                                rules: [{
                                    required: true, message: '数据源名称不可为空！'
                                }, {
                                    max: 128,
                                    message: '数据源名称不得超过128个字符！'
                                }, {
                                    pattern: /^[A-Za-z0-9_]+$/,
                                    message: '名称只能由字母与数字、下划线组成'
                                }],
                                initialValue: sourceData.dataName || ''
                            })(
                                <Input autoComplete="off" disabled={status === 'edit'} />
                            )}
                    </FormItem>

                    <FormItem {...formItemLayout} label="数据源描述" hasFeedback>
                        {
                            getFieldDecorator('dataDesc', {
                                rules: [{
                                    max: 200,
                                    message: '描述请控制在200个字符以内！'
                                }],
                                initialValue: sourceData.dataDesc || ''
                            })(
                                <Input type="textarea" rows={4} />
                            )
                        }
                    </FormItem>

                    {
                        this.renderDynamic()
                    }

                    <FormItem {...tailFormItemLayout}>
                        <Button
                            icon="sync"
                            type="primary"
                            data-target="test"
                            onClick={this.testConnection}
                            style={{ marginRight: '10px' }}
                        >
                            测试连通性
                        </Button>
                        <Button
                            type="primary"
                            style={{ marginRight: '10px' }}
                            onClick={this.onOk}
                        >
                            确定
                        </Button>
                        <Button onClick={this.cancel}>取消</Button>
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}

const DataSourceFormWrapper = Form.create()(DataSourceModal)

export default DataSourceFormWrapper
