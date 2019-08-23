import * as React from 'react';
import { connect } from 'react-redux';
import { Modal, Input, Button, Select, Form, message, Tooltip, Icon } from 'antd';

import { hidePasswordInDom } from 'funcs';

import HelpDoc from '../helpDoc';
import { formItemLayout, tailFormItemLayout, DATA_SOURCE } from '../../consts';
import { dataSourceActions } from '../../actions/dataSource';
import Api from '../../api/dataSource';
import { jdbcUrlExample } from '../../consts/JDBCCommon';

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

const mapStateToProps = (state: any) => {
    const { dataSource } = state;
    return { dataSource }
}

const mapDispatchToProps = (dispatch: any) => ({
    getDataSourcesType (params: any) {
        dispatch(dataSourceActions.getDataSourcesType(params));
    }
})

@(connect(mapStateToProps, mapDispatchToProps) as any)
class DataSourceModal extends React.Component<any, any> {
    state: any = {
        sourceType: 1,
        hadoopConfig: 'defaultDfs'

    }

    componentDidMount () {
        this.props.getDataSourcesType();
    }
    componentDidUpdate () {
        hidePasswordInDom();
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const oldData = this.props.sourceData;
        const newData = nextProps.sourceData;

        if (newData.id !== oldData.id) {
            if (newData.type === 7) {
                this.setState({ sourceType: newData.type || 1 });
            } else {
                this.setState({ sourceType: newData.type || 1 });
            }
        }
    }

    onOk = (e: any) => {
        const { editDataSource, form } = this.props;
        console.log(form.getFieldsValue());

        form.validateFields((err: any) => {
            if (!err) {
                editDataSource(form.getFieldsValue(), form)
            }
        });
    }

    testConnection = (e: any) => {
        const { sourceType } = this.state;
        const { form } = this.props;
        let field: any = [
            'type',
            'dataJson.jdbcUrl',
            'dataJson.username',
            'dataJson.password'
        ];

        if (sourceType === 10) {
            field = [...field, 'dataJson.project', 'dataJson.endPoint', 'dataJson.accessKey', 'dataJson.accessId'];
        }

        form.validateFields(field, (err: any, values: any) => {
            if (!err) {
                Api.testDSConnection(values).then((res: any) => {
                    if (res.data) {
                        message.success('数据源连接正常！');
                    } else {
                        message.error('数据库连接失败！');
                    }
                })
            }
        });
    }

    cancel = () => {
        const { form, handCancel } = this.props;
        this.setState({ sourceType: 1 });
        form.resetFields()
        handCancel()
    }

    sourceChange = (value: any) => {
        const group: any = [DATA_SOURCE.MYSQL, DATA_SOURCE.SQLSERVER, DATA_SOURCE.ORACLE];
        const prevValue = this.state.sourceType;

        this.setState({ sourceType: parseInt(value) })
        if (group.indexOf(prevValue) == -1 || group.indexOf(parseInt(value)) == -1) {
            this.props.form.resetFields();
        }
    }

    enableDetailConfig = (e: any) => {
        this.setState({
            detailConfig: !e.target.value
        });
    }
    hidePasswordInDom = (e: any) => {
        // 特殊处理密码在 dom 中的展示
        setTimeout(() => {
            document.getElementById('dataJson.password').setAttribute('value', '');
        }, 200)
    }
    renderDynamic () {
        const { sourceType } = this.state
        const { form, sourceData } = this.props;
        const { getFieldDecorator } = form;
        const config = sourceData.dataJson || {};
        const jdbcHelpDoc = jdbcUrlExample[sourceType];
        console.log(sourceType);

        switch (sourceType) {
            case DATA_SOURCE.HIVE: {
                return [
                    <FormItem {...formItemLayout} label="JDBC URL" key="jdbcUrl" hasFeedback>
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
                        <Tooltip title={'示例：' + jdbcUrlExample[sourceType]}>
                            <Icon className="help-doc" type="question-circle-o" />
                        </Tooltip>
                    </FormItem>,
                    <FormItem {...formItemLayout} label="用户名" key="username">
                        {
                            getFieldDecorator('dataJson.username', {
                                rules: [],
                                initialValue: config.username || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>,
                    <FormItem {...formItemLayout} label="密码" key="password">
                        {
                            getFieldDecorator('dataJson.password', {
                                rules: [],
                                initialValue: ''
                            })(
                                <Input type="password" onChange={hidePasswordInDom} />
                            )
                        }
                    </FormItem>,
                    <FormItem {...formItemLayout} label="defaultFS" key="defaultFS" hasFeedback>
                        {
                            getFieldDecorator('dataJson.defaultFS', {
                                rules: [{
                                    required: true, message: 'defaultFS不可为空！'
                                }],
                                initialValue: config.defaultFS || ''
                            })(
                                <Input placeholder="hdfs://host:port" />
                            )
                        }
                    </FormItem>,
                    <FormItem {...formItemLayout} label="高可用配置" key="hadoopConfig" >
                        {
                            getFieldDecorator('dataJson.hadoopConfig', {
                                rules: [],
                                initialValue: config.hadoopConfig || ''
                            })(
                                <Input {...{ rows: 5 } } type="textarea" placeholder={hdfsConf} />
                            )
                        }
                        <HelpDoc doc="hdfsConfig" />
                    </FormItem>
                ]
            }

            case DATA_SOURCE.MAXCOMPUTE: {
                return [
                    <FormItem {...formItemLayout} label="AccessId" key="accessId" hasFeedback>
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
                    </FormItem>,
                    <FormItem {...formItemLayout} label="AccessKey" key="accessKey" hasFeedback>
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
                    </FormItem>,
                    <FormItem {...formItemLayout} label="Project Name" key="project" hasFeedback>
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
                    </FormItem>,
                    <FormItem {...formItemLayout} label="End Point" key="endPoint" hasFeedback>
                        {
                            getFieldDecorator('dataJson.endPoint', {
                                rules: [{
                                    required: false, message: 'End Point不可为空！'
                                }],
                                initialValue: config.endPoint || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                ]
            }

            case DATA_SOURCE.ORACLE: {
                return [
                    <FormItem {...formItemLayout} label="JDBC URL" key="jdbcUrl" hasFeedback>
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
                        <Tooltip overlayClassName="big-tooltip" title={
                            (
                                <span style={{ wordBreak: ' break-all' } as any}>
                                    SID示例：{jdbcUrlExample[sourceType][0]}
                                    <br />
                                    ServiceName示例：{jdbcUrlExample[sourceType][1]}
                                </span>
                            )
                        }>
                            <Icon className="help-doc" type="question-circle-o" />
                        </Tooltip>
                    </FormItem>,
                    <FormItem {...formItemLayout} label="用户名" key="username" hasFeedback>
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
                    </FormItem>,
                    <FormItem {...formItemLayout} label="密码" key="password" hasFeedback>
                        {
                            getFieldDecorator('dataJson.password', {
                                rules: [{
                                    required: true, message: '密码不可为空！'
                                }],
                                initialValue: ''
                            })(
                                <Input type="password" onChange={hidePasswordInDom} />
                            )
                        }
                    </FormItem>
                ]
            }
            // case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.SQLSERVER:
            case DATA_SOURCE.RDS:
            case DATA_SOURCE.DB2:
            default: {
                return [
                    <FormItem {...formItemLayout} label="JDBC URL" key="jdbcUrl" hasFeedback>
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
                        {jdbcHelpDoc && (
                            <Tooltip overlayClassName="big-tooltip" title={'示例：' + jdbcHelpDoc}>
                                <Icon className="help-doc" type="question-circle-o" />
                            </Tooltip>
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout} label="用户名" key="username" hasFeedback>
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
                    </FormItem>,
                    <FormItem {...formItemLayout} label="密码" key="password" hasFeedback>
                        {
                            getFieldDecorator('dataJson.password', {
                                rules: [{
                                    required: true, message: '密码不可为空！'
                                }],
                                initialValue: ''
                            })(
                                <Input type="password" onChange={hidePasswordInDom} />
                            )
                        }
                    </FormItem>
                ]
            }
        }
    }

    renderSourceType = (data: any) => {
        return data.map(
            (type: any) => (
                <Option key={type.value} value={type.value.toString()}>{type.name}</Option>
            )
        );
    }

    render () {
        const { visible, form, title, sourceData, status, dataSource } = this.props
        const { sourceType } = this.state
        const { getFieldDecorator } = form;
        // const sourceType = dataSource.sourceType[0] && dataSource.sourceType[0].value.toString();

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
                                initialValue: sourceData.type ? sourceData.type.toString() : sourceType.toString()
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
                                    pattern: /^[A-Za-z0-9_-]+$/,
                                    message: '名称只能由字母与数字、下划线组成'
                                }],
                                initialValue: sourceData.dataName || ''
                            })(
                                <Input autoComplete="off" disabled={status === 'edit' && sourceData.active === 1} />
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
                                <Input {...{ type: 'textarea', rows: 4 }} />
                            )}
                    </FormItem>

                    {
                        this.renderDynamic()
                    }

                    <FormItem {...tailFormItemLayout} label="">
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
const DataSourceModalForm = Form.create<any>()(DataSourceModal)

export default DataSourceModalForm;
