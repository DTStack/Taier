import * as React from 'react';
import { connect } from 'react-redux';
import {
    Modal, Input, Button, Select, Icon,
    Form, message, Tooltip,
    Switch, Row, Col, Upload
} from 'antd';
import { pickBy } from 'lodash'

import utils from 'utils';
import { hidePasswordInDom } from 'funcs';

import CopyIcon from 'main/components/copy-icon';
import HelpDoc from '../helpDoc';
import { formItemLayout, tailFormItemLayout, DATA_SOURCE } from '../../consts';
import { dataSourceActions } from '../../actions/dataSource';
import Api from '../../api/dataSource';

import {
    jdbcUrlExample
} from '../../consts/jdbcExample';
import moment from 'moment';

const FormItem = Form.Item;
const Option = Select.Option;

const hdfsConf = `{
    "defaultFS": "",
    "hadoopConfig": {
        "dfs.nameservices": "defaultDfs", 
        "dfs.ha.namenodes.defaultDfs": "namenode1", 
        "dfs.namenode.rpc-address.defaultDfs.namenode1": "", 
        "dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider" 
    }
}`;

const mapStateToProps = (state: any) => {
    const { dataSource } = state;
    return { dataSource };
};

const mapDispatchToProps = (dispatch: any) => ({
    getDataSourcesType (params: any) {
        dispatch(dataSourceActions.getDataSourcesType(params));
    }
});

@(connect(
    mapStateToProps,
    mapDispatchToProps
) as any)
class DataSourceModal extends React.Component<any, any> {
    state: any = {
        sourceType: undefined,
        hadoopConfig: 'defaultDfs'
    };

    componentDidMount () {
        // this.props.getDataSourcesType();
    }
    componentDidUpdate () {
        hidePasswordInDom();
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const oldData = this.props.sourceData;
        const newData = nextProps.sourceData;

        if (newData && newData !== oldData) {
            if (newData.type === 7) {
                this.setState({
                    sourceType: newData.type || 1
                });
            } else {
                this.setState({ sourceType: newData.type || 1 });
            }
        }
    }

    onOk = (e: any) => {
        const { editDataSource, form } = this.props;

        form.validateFields((err: any, values: any) => {
            console.log(err, values);
            if (!err) {
                if (values.dataJson.jdbcUrl) {
                    values.dataJson.jdbcUrl = utils.trim(
                        values.dataJson.jdbcUrl
                    );
                }
                if (values.dataJson.defaultFS) {
                    values.dataJson.defaultFS = utils.trim(
                        values.dataJson.defaultFS
                    );
                }
                editDataSource(values, form);
            }
        });
    };

    testConnection = () => {
        const { sourceType } = this.state;
        const { form, sourceData } = this.props;

        let field: any;
        console.log(sourceData)

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

        form.validateFields(field, (err: any, values: any) => {
            if (!err) {
                values.id = sourceData.id;
                if (values.dataJson.openKerberos) {
                    values.dataJsonString = JSON.stringify(values.dataJson)
                    delete values.dataJson;
                    values = pickBy(values, (item, key) => { // 过滤掉空字符串和值为null的属性，并且过滤掉编辑时的kerberos字段
                        if (key === 'kerberosFile' && (!item.type)) {
                            return false
                        }
                        return item != null
                    })
                    Api.testDSConnectionKerberos(values).then((res: any) => {
                        if (res.data) {
                            message.success('数据源连接正常！');
                        } else {
                            message.error('数据库连接失败！');
                        }
                    });
                } else {
                    Api.testDSConnection(values).then((res: any) => {
                        if (res.data) {
                            message.success('数据源连接正常！');
                        } else {
                            message.error('数据库连接失败！');
                        }
                    });
                }
            }
        });
    };

    cancel = () => {
        const { form, handCancel } = this.props;
        form.resetFields();
        handCancel();
    };

    sourceChange = (value: any) => {
        this.setState({ sourceType: parseInt(value) });
        this.props.form.resetFields();
    };
    uploadForm = () => {
        const { form, sourceData } = this.props;
        const formNewLayout = {
            labelCol: {
                xs: { span: 24 },
                sm: { span: 0 }
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 24 }
            }
        }
        const { getFieldDecorator, setFieldsValue, getFieldValue } = form;
        const nullArr: any[] = [];
        const upProps = {
            beforeUpload: (file: any) => {
                file.modifyTime = moment();
                console.log(file);
                setFieldsValue({
                    [`kerberosFile`]: file
                })
                return false;
            },
            fileList: nullArr,
            name: 'file',
            accept: '.zip'
        };
        return (
            <Row>
                <Col span={6}/>
                <Col span={14}>
                    <FormItem
                        {...formNewLayout}
                        key={`kerberosFile`}
                        label=""
                        // style={{
                        //     margin: 0
                        // }}
                    >
                        {getFieldDecorator(`kerberosFile`, {
                            rules: [{
                                required: true, message: '文件不可为空！'
                            }],
                            initialValue: (sourceData.dataJson && sourceData.dataJson.kerberosFile) || ''
                        })(<div/>)}
                        <div
                            style={{
                                display: 'flex'
                            }}
                        >
                            <Upload {...upProps}>
                                <Button style={{ color: '#999' }}>
                                    <Icon type="upload" /> 上传文件
                                </Button>
                            </Upload>
                            <Tooltip title="上传文件前，请在控制台开启SFTP服务。">
                                <Icon type="question-circle-o" style={{ fontSize: '14px', marginTop: '8px', marginLeft: '10px' }}/>
                            </Tooltip>
                            <a
                                href={`/api/dq/download/service/download/downloadKerberosXML?sourceType=${getFieldValue('type')}`}
                                download
                            >
                                <div
                                    style={{ color: '#0099ff', cursor: 'pointer', marginLeft: '10px' }}
                                >
                                    下载文件模板
                                </div>
                            </a>
                        </div>
                        <div
                            style={{ color: '#999' }}
                        >
                            上传单个文件，支持扩展格式：.zip
                        </div>
                        {
                            getFieldValue(`kerberosFile`)
                                ? (
                                    <div
                                        style={{
                                            width: '120%',
                                            position: 'relative'
                                        }}
                                    >
                                        <Icon
                                            type="close"
                                            style={{
                                                cursor: 'pointer',
                                                position: 'absolute',
                                                right: '5px',
                                                top: '11px',
                                                zIndex: 99
                                            }}
                                            onClick={() => {
                                                setFieldsValue({
                                                    [`kerberosFile`]: ''
                                                })
                                            }}
                                        />
                                        <Input value={(getFieldValue(`kerberosFile`)).name + '   ' + moment((getFieldValue(`kerberosFile`)).modifyTime).format('YYYY-MM-DD HH:mm:ss')}/>
                                    </div>
                                )
                                : null
                        }
                    </FormItem>
                </Col>
            </Row>
        );
    }

    hidePasswordInDom = (e: any) => {
        // 特殊处理密码在 dom 中的展示
        setTimeout(() => {
            document.getElementById('dataJson.password').setAttribute('value', '');
        }, 200)
    }

    renderDynamic () {
        const { sourceType } = this.state;
        const { form, sourceData } = this.props;
        const { getFieldDecorator, getFieldValue } = form;
        const config = sourceData.dataJson || {};

        switch (sourceType) {
            case DATA_SOURCE.HIVE: {
                return (
                    <div>
                        <FormItem
                            {...formItemLayout}
                            label="JDBC URL"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.jdbcUrl', {
                                rules: [
                                    {
                                        required: true,
                                        message: 'jdbcUrl不可为空！'
                                    }
                                ],
                                initialValue: config.jdbcUrl || ''
                            })(<Input autoComplete="off" />)}
                            <Tooltip title={'示例：' + jdbcUrlExample[sourceType]} arrowPointAtCenter>
                                <Icon className="help-doc" type="question-circle-o" />
                            </Tooltip>
                        </FormItem>
                        <FormItem {...formItemLayout} label="用户名">
                            {getFieldDecorator('dataJson.username', {
                                rules: [],
                                initialValue: config.username || ''
                            })(<Input autoComplete="off" />)}
                            <HelpDoc doc="usernameMsg" />
                        </FormItem>
                        <FormItem {...formItemLayout} label="密码">
                            {getFieldDecorator('dataJson.password', {
                                rules: [],
                                initialValue: ''
                            })(<Input onChange={hidePasswordInDom} type="password" />)}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="defaultFS"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.defaultFS', {
                                rules: [
                                    {
                                        required: true,
                                        message: 'defaultFS不可为空！'
                                    }
                                ],
                                initialValue: config.defaultFS || ''
                            })(<Input placeholder="hdfs://host:port" />)}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="高可用配置"
                        >
                            {getFieldDecorator('dataJson.hadoopConfig', {
                                rules: [],
                                initialValue: config.hadoopConfig || ''
                            })(
                                <Input
                                    {...{ rows: 5 }}
                                    type="textarea"
                                    placeholder={hdfsConf}
                                />
                            )}
                            <HelpDoc doc="hdfsConfig" />
                            <CopyIcon
                                style={{
                                    position: 'absolute',
                                    right: '-20px',
                                    bottom: '0px'
                                }}
                                copyText={hdfsConf}
                            />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="开启Kerberos认证"
                            key="openKerberos"
                        >
                            {getFieldDecorator('dataJson.openKerberos', {
                                valuePropName: 'checked',
                                initialValue: config.openKerberos || false
                            })(
                                <Switch />
                            )}
                        </FormItem>
                        {
                            getFieldValue('dataJson.openKerberos') ? this.uploadForm() : null
                        }
                    </div>
                );
            }

            case DATA_SOURCE.MAXCOMPUTE: {
                return (
                    <div>
                        <FormItem
                            {...formItemLayout}
                            label="AccessId"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.accessId', {
                                rules: [
                                    {
                                        required: true,
                                        message: 'access ID不可为空！'
                                    }
                                ],
                                initialValue: config.accessId || ''
                            })(<Input autoComplete="off" />)}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="AccessKey"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.accessKey', {
                                rules: [
                                    {
                                        required: true,
                                        message: 'access Key不可为空！'
                                    }
                                ],
                                initialValue: config.accessKey || ''
                            })(<Input type="password" autoComplete="off" />)}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="Project Name"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.project', {
                                rules: [
                                    {
                                        required: true,
                                        message: 'Project Name不可为空！'
                                    }
                                ],
                                initialValue: config.project || ''
                            })(<Input autoComplete="off" />)}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="End Point"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.endPoint', {
                                rules: [
                                    {
                                        required: false,
                                        message: 'End Point不可为空！'
                                    }
                                ],
                                initialValue: config.endPoint || ''
                            })(<Input autoComplete="off" />)}
                        </FormItem>
                    </div>
                );
            }

            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.SQLSERVER: {
                return (
                    <div>
                        <FormItem
                            {...formItemLayout}
                            label="JDBC URL"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.jdbcUrl', {
                                rules: [
                                    {
                                        required: true,
                                        message: 'jdbcUrl不可为空！'
                                    }
                                ],
                                initialValue: config.jdbcUrl || ''
                            })(<Input autoComplete="off" />)}
                            <Tooltip title={'示例：' + jdbcUrlExample[sourceType]} arrowPointAtCenter>
                                <Icon className="help-doc" type="question-circle-o" />
                            </Tooltip>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="用户名"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.username', {
                                rules: [
                                    {
                                        required: true,
                                        message: '用户名不可为空！'
                                    }
                                ],
                                initialValue: config.username || ''
                            })(<Input autoComplete="off" />)}
                            <HelpDoc doc="usernameMsg" />
                        </FormItem>
                        <FormItem {...formItemLayout} label="密码" hasFeedback>
                            {getFieldDecorator('dataJson.password', {
                                rules: [
                                    {
                                        required: true,
                                        message: '密码不可为空！'
                                    }
                                ],
                                initialValue: ''
                            })(<Input type="password" onChange={hidePasswordInDom}/>)}
                        </FormItem>
                    </div>
                );
            }

            default:
                break;
        }
    }

    renderSourceType = (data: any) => {
        return data.map((type: any) => (
            <Option key={type.value} value={type.value.toString()}>
                {type.name}
            </Option>
        ));
    };

    render () {
        const {
            visible,
            form,
            title,
            sourceData,
            status,
            dataSource
        } = this.props;
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
                    <FormItem
                        {...formItemLayout}
                        label="数据源类型"
                        hasFeedback
                    >
                        {getFieldDecorator('type', {
                            rules: [
                                {
                                    required: true,
                                    message: '数据源类型不可为空！'
                                }
                            ],
                            initialValue: sourceData.type
                                ? sourceData.type.toString()
                                : '1'
                        })(
                            <Select
                                onChange={this.sourceChange}
                                disabled={status === 'edit'}
                            >
                                {this.renderSourceType(dataSource.sourceType)}
                            </Select>
                        )}
                    </FormItem>

                    <FormItem
                        {...formItemLayout}
                        label="数据源名称"
                        hasFeedback
                    >
                        {getFieldDecorator('dataName', {
                            rules: [
                                {
                                    required: true,
                                    message: '数据源名称不可为空！'
                                },
                                {
                                    max: 128,
                                    message: '数据源名称不得超过128个字符！'
                                },
                                {
                                    pattern: /^[A-Za-z0-9_]+$/,
                                    message: '名称只能由字母与数字、下划线组成'
                                }
                            ],
                            initialValue: sourceData.dataName || ''
                        })(
                            <Input
                                autoComplete="off"
                                disabled={status === 'edit' && sourceData.active === 1}
                            />
                        )}
                    </FormItem>

                    <FormItem
                        {...formItemLayout}
                        label="数据源描述"
                        hasFeedback
                    >
                        {getFieldDecorator('dataDesc', {
                            rules: [
                                {
                                    max: 200,
                                    message: '描述请控制在200个字符以内！'
                                }
                            ],
                            initialValue: sourceData.dataDesc || ''
                        })(<Input type="textarea" {...{ rows: 4 }} />)}
                    </FormItem>

                    {this.renderDynamic()}

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
        );
    }
}

const DataSourceFormWrapper = Form.create<any>()(DataSourceModal);

export default DataSourceFormWrapper;
