import React, { Component } from 'react'

import {
    Input, Button,
    Select, Form, Checkbox,
    Radio, Modal, Tooltip,
    Icon
} from 'antd'

import { isEmpty } from 'lodash';

import utils from 'utils';

import {
    formItemLayout,
    tailFormItemLayout,
    DATA_SOURCE,
} from '../../../comm/const';
import {
    jdbcUrlExample
} from '../../../comm/JDBCCommon';


import Api from '../../../api';
import HelpDoc from '../../helpDoc';
import CopyIcon from "main/components/copy-icon";

const FormItem = Form.Item
const Option = Select.Option
const RadioGroup = Radio.Group

const defaultConf =
    `{
"jdbcUrl": "", 
"username": "", 
"password": ""
}`
const hdfsConf =
    `{
"dfs.nameservices": "defaultDfs", 
"dfs.ha.namenodes.defaultDfs": "namenode1", 
"dfs.namenode.rpc-address.defaultDfs.namenode1": "", 
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider" 
}`
const hBaseConf =
    `{
"zookeeper.cluster": "ip1:port,ip2:port/子目录",
"hbase.rootdir": "hdfs: //ip:9000/hbase",
"hbase.cluster.distributed": "true",
"hbase.zookeeper.quorum": "***"
}`

function getConnectionConfig(sourceType) {
    switch (sourceType) {
        case DATA_SOURCE.HDFS:
            return hdfsConf
        case DATA_SOURCE.HBASE:
            return hBaseConf
        case DATA_SOURCE.MYSQL:
        case DATA_SOURCE.ORACLE:
        case DATA_SOURCE.SQLSERVER:
        case DATA_SOURCE.HIVE:
        default:
            return defaultConf
    }
}

class BaseForm extends Component {

    state = {
        sourceType: 1,
        hasHdfsConfig: false,
        hadoopConfig: 'defaultDfs',
        hadoopConfigStr: hdfsConf,
        ftpProtocal: 'ftp',
    }

    componentDidMount() {
        const sourceData = this.props.sourceData;
        if (!isEmpty(sourceData)) {
            if (sourceData.dataJson && sourceData.dataJson.hadoopConfig) {
                this.setState({ sourceType: sourceData.type, hasHdfsConfig: true })
            } else {
                this.setState({ sourceType: sourceData.type })
            }
        } 
    }

    submit = (e) => {
        e.preventDefault()
        const ctx = this
        const { handOk, form } = this.props
        const source = form.getFieldsValue()
        const { sourceType } = this.state

        this.preHandFormValues(source);

        form.validateFields((err) => {
            if (!err) {
                handOk(source, form, () => {
                    ctx.setState({
                        sourceType: '',
                    })
                })
            }
        });
    }

    testConnection = (e) => {
        const { testConnection, form } = this.props
        
        form.validateFields((err, source) => {
            if (!err) {
                this.preHandFormValues(source);
                testConnection(source)
            }
        });
    }

    preHandFormValues(source) {
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

    sourceChange = (value) => {
        this.setState({ sourceType: parseInt(value, 10), hasHdfsConfig: false })
        this.props.form.resetFields();
    }

    enableHdfsConfig = (e) => {
        this.setState({
            hasHdfsConfig: !e.target.value,
        })
    }

    ftpProtocalChange = (e) => {
        this.setState({
            ftpProtocal: e.target.value,
        })
    }

    hadoopConfigChange = (e) => {
        const { hadoopConfig, hasHdfsConfig, hadoopConfigStr } = this.state
        const value = e.target.value.split('//')[1]
        if (hasHdfsConfig && value) {
            const reg = new RegExp(`${hadoopConfig}`, 'g')
            const newStr = hadoopConfigStr.replace(reg, value)
            this.setState({
                hadoopConfig: value,
                hadoopConfigStr: newStr
            })
            this.props.form.setFieldsValue({
                hadoopConfig: newStr,
            })
        }
    }

    getJDBCRule = (type) => {
        switch (type) {
            case DATA_SOURCE.HIVE:
                return /jdbc:(\w)+:\/\/(\w)+(\:\d|\w)+\/(\w)+/;
            case DATA_SOURCE.MYSQL:
                return /jdbc:mysql:\/\/(\w)+/;
            case DATA_SOURCE.ORACLE:
                return /jdbc:oracle:thin:@(\/\/)?(\w)+/;
            case DATA_SOURCE.SQLSERVER:
                return /jdbc:sqlserver:\/\/(\w)+/;
            case DATA_SOURCE.POSTGRESQL:
                return /jdbc:postgresql:\/\/(\w)+/;
            default:
                return null;
        }
    }

    renderDynamic() {
        const { form, sourceData,  } = this.props;
        const { hasHdfsConfig, sourceType, ftpProtocal } = this.state;
        
        const { getFieldDecorator } = form;
        const config = sourceData.dataJson || {};
        const jdbcRulePattern = {
            pattern: this.getJDBCRule(sourceType),
            message: '请检查您的JDBC地址格式！',
        }

        switch (sourceType) {
            case DATA_SOURCE.HDFS: {
                const formItems = [
                    <FormItem
                        {...formItemLayout}
                        label="DefaultFS"
                        key="defaultFS"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.defaultFS', {
                            rules: [{
                                required: true, message: 'defaultFS不可为空！',
                            }],
                            initialValue: config.defaultFS || '',
                        })(
                            <Input placeholder="hdfs://host:port" />,
                        )}
                    </FormItem>,
                    <FormItem
                        key="hasHdfsConfig"
                        {...tailFormItemLayout}
                    >
                        {getFieldDecorator('hasHdfsConfig', {
                            initialValue: hasHdfsConfig,
                        })(
                            <Checkbox
                                checked={hasHdfsConfig}
                                onChange={this.enableHdfsConfig}>
                                高可用配置
                            </Checkbox>,
                        )}
                    </FormItem>,
                ]
                if (hasHdfsConfig) {

                    formItems.push(
                        <FormItem
                            {...formItemLayout}
                            label="高可用配置"
                            key="hadoopConfig"
                            hasFeedback
                            style={{ display: hasHdfsConfig ? 'block' : 'none' }}
                        >
                            {getFieldDecorator('dataJson.hadoopConfig', {
                                rules: [{
                                    required: true, message: 'Hadoop配置不可为空！',
                                }],
                                initialValue: config.hadoopConfig ?   typeof config.hadoopConfig == "string" ? 
                                    JSON.stringify(JSON.parse(config.hadoopConfig),null,4): JSON.stringify(config.hadoopConfig,null,4) : ''
                            })(
                                <Input
                                    rows={5}
                                    className="no-scroll-bar"
                                    type="textarea"
                                    placeholder={hdfsConf}
                                />,
                            )}
                            <HelpDoc doc="hdfsConfig" />
                            <CopyIcon 
                                style={{position:"absolute",right:"-20px",bottom:"0px"}} 
                                copyText={hdfsConf} 
                            />
                        </FormItem>
                    )
                }
                return formItems;
            }
            case DATA_SOURCE.HIVE: {
                const formItems = [
                    <FormItem
                        {...formItemLayout}
                        label="JDBC URL"
                        hasFeedback
                        key="jdbcUrl"
                    >
                        {getFieldDecorator('dataJson.jdbcUrl', {
                            rules: [{
                                required: true, message: 'jdbcUrl不可为空！',
                            }, 
                                jdbcRulePattern
                            ],
                            initialValue: config.jdbcUrl || '',
                        })(
                            <Input autoComplete="off" />,
                        )}
                        <Tooltip title={'示例：' + jdbcUrlExample[sourceType]}>
                            <Icon className="help-doc" type="question-circle-o" />
                        </Tooltip>
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="用户名"
                        key="username"
                    >
                        {getFieldDecorator('dataJson.username', {
                            rules: [],
                            initialValue: config.username || '',
                        })(
                            <Input autoComplete="off" />,
                        )}
                    </FormItem>,
                    <FormItem
                        key="password"
                        {...formItemLayout}
                        label="密码"
                    >
                        {getFieldDecorator('dataJson.password', {
                            rules: [],
                            initialValue: '',
                        })(
                            <Input type="password" autoComplete="off"/>,
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
                                required: true, message: 'defaultFS不可为空！',
                            }],
                            initialValue: config.defaultFS || '',
                        })(
                            <Input placeholder="hdfs://host:port" />,
                        )}
                    </FormItem>,
                    <FormItem
                        key="hasHdfsConfig"
                        {...tailFormItemLayout}
                    >
                        {getFieldDecorator('hasHdfsConfig', {
                            initialValue: hasHdfsConfig,
                        })(
                            <Checkbox
                                checked={hasHdfsConfig}
                                onChange={this.enableHdfsConfig}>
                                高可用配置
                          </Checkbox>,
                        )}
                    </FormItem>,
                ]
                if (hasHdfsConfig) {
                    formItems.push(
                        <FormItem
                            {...formItemLayout}
                            label="高可用配置"
                            key="hadoopConfig"
                            hasFeedback
                            style={{ display: hasHdfsConfig ? 'block' : 'none' }}
                        >
                            {getFieldDecorator('dataJson.hadoopConfig', {
                                rules: [{
                                    required: true, message: 'Hadoop配置不可为空！',
                                }],
                                initialValue: config.hadoopConfig ?   typeof config.hadoopConfig == "string" ? 
                                    JSON.stringify(JSON.parse(config.hadoopConfig),null,4): JSON.stringify(config.hadoopConfig,null,4) : ''
                            })(
                                <Input
                                    className="no-scroll-bar"
                                    type="textarea" rows={5}
                                    
                                    placeholder={hdfsConf}
                                />,
                            )}
                            <HelpDoc doc="hdfsConfig" />
                            <CopyIcon 
                                style={{position:"absolute",right:"-20px",bottom:"0px"}} 
                                copyText={hdfsConf} 
                            />
                        </FormItem>
                    )
                }
                return formItems
            }
            case DATA_SOURCE.HBASE: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="Zookeeper集群地址"
                        key="hbase_quorum"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.hbase_quorum', {
                            rules: [{
                                required: true, message: 'Zookeeper配置不可为空！',
                            }],
                            initialValue: config.hbase_quorum || ''
                        })(
                            <Input
                                type="textarea"
                                rows={5}
                                placeholder="Zookeeper集群地址，例如：IP1:Port,IP2:Port,IP3:Port/子目录"
                            />,
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="其他参数"
                        key="hbase_other"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.hbase_other', {
                            rules: [],
                            initialValue: config.hbase_other || ''
                        })(
                            <Input type="textarea" rows={5} placeholder={`hbase.rootdir": "hdfs: //ip:9000/hbase`} />,
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.FTP: {
                const ftpFormItems = [
                    <FormItem
                        {...formItemLayout}
                        label="主机名/IP"
                        hasFeedback
                        key="host"
                    >
                        {getFieldDecorator('dataJson.host', {
                            rules: [{
                                required: true, message: '主机名/IP不可为空！',
                            }],
                            initialValue: config.host || '',
                        })(
                            <Input autoComplete="off" />,
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="端口"
                        hasFeedback
                        key="port"
                    >
                        {getFieldDecorator('dataJson.port', {
                            rules: [{
                                required: true, message: '端口不可为空！',
                            }],
                            initialValue: config.port || '',
                        })(
                            <Input type="number" placeholder="FTP默认21，SFTP默认22" autoComplete="off" />,
                        )}
                    </FormItem>,
                    <FormItem
                        key="username"
                        {...formItemLayout}
                        label="用户名"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.username', {
                            rules: [{
                                required: true, message: '用户名不可为空！',
                            }],
                            initialValue: '',
                        })(
                            <Input autoComplete="off" />,
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
                                required: true, message: '密码不可为空！',
                            }],
                            initialValue: '',
                        })(
                            <Input type="password" autoComplete="off" />,
                        )}
                    </FormItem>,
                    <FormItem
                        key="protocol"
                        {...formItemLayout}
                        label="协议"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.protocol', {
                            rules: [{
                                required: true, message: '协议不可为空！',
                            }],
                            initialValue: config.protocol || "ftp",
                        })(
                            <RadioGroup onChange={this.ftpProtocalChange}>
                                <Radio value="ftp">Standard</Radio>
                                <Radio value="sftp">SFTP</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>,
                ];

                if (ftpProtocal === 'ftp') {
                    ftpFormItems.push(
                        <FormItem
                            key="connectMode"
                            {...formItemLayout}
                            label="连接模式"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.connectMode', {
                                rules: [{
                                    required: true, message: '连接模式不可为空！',
                                }],
                                initialValue: config.connectMode || "PORT",
                            })(
                                <RadioGroup>
                                    <Radio value="PORT">Port (主动)</Radio>
                                    <Radio value="PASV">Pasv（被动）</Radio>
                                </RadioGroup>
                            )}
                        </FormItem>
                    )
                }

                return ftpFormItems;
            }
            case DATA_SOURCE.MAXCOMPUTE: {
                return [
                    <FormItem {...formItemLayout} label="AccessId" key="accessId" hasFeedback>
                        {
                            getFieldDecorator('dataJson.accessId', {
                                rules: [{
                                    required: true, message: 'access ID不可为空！',
                                }],
                                initialValue: config.accessId || '',
                            })(
                                <Input autoComplete="off" />,
                            )
                        }
                    </FormItem>,
                    <FormItem {...formItemLayout} label="AccessKey" key="accessKey" hasFeedback>
                        {
                            getFieldDecorator('dataJson.accessKey', {
                                rules: [{
                                    required: true, message: 'access Key不可为空！',
                                }],
                                initialValue: config.accessKey || '',
                            })(
                                <Input type="password" autoComplete="off" />,
                            )
                        }
                    </FormItem>,
                    <FormItem {...formItemLayout} label="Project Name" key="project" hasFeedback>
                        {
                            getFieldDecorator('dataJson.project', {
                                rules: [{
                                    required: true, message: 'Project Name不可为空！',
                                }],
                                initialValue: config.project || '',
                            })(
                                <Input autoComplete="off" />,
                            )
                        }
                    </FormItem>,
                    <FormItem {...formItemLayout} label="End Point" key="endPoint" hasFeedback>
                        {
                            getFieldDecorator('dataJson.endPoint', {
                                rules: [],
                                initialValue: config.endPoint || '',
                            })(
                                <Input autoComplete="off" />,
                            )
                        }
                    </FormItem>
                ]
            }
            case DATA_SOURCE.ES: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="Address"
                        key="Address"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.address', {
                            rules: [{
                                required: true, message: 'Address不可为空！',
                            }],
                            initialValue: config.address || '',
                        })(
                            <Input
                                type="textarea" rows={4}
                                placeholder="Elasticsearch地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接"
                            />,
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.KAFKA: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="Address"
                        key="Address"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.address', {
                            rules: [{
                                required: true, message: 'Address不可为空！',
                            }],
                            initialValue: config.address || '',
                        })(
                            <Input
                                type="textarea" rows={4}
                                placeholder="Zookeeper集群地址，例如：IP1:Port,IP2:Port,IP3:Port/子目录"
                            />,
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.REDIS: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="地址"
                        key="hostport"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.hostPort', {
                            rules: [{
                                required: true, message: '地址不可为空！',
                            }],
                            initialValue: config.hostPort || '',
                        })(
                            <Input
                                placeholder="Redis地址，例如：IP1:Port，暂不支持集群模式"
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        key="database"
                        {...formItemLayout}
                        label="数据库"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.database', {
                            rules: [{
                                required: true, message: '数据库不可为空！',
                            }],
                            initialValue: '',
                        })(
                            <Input autoComplete="off" />,
                        )}
                    </FormItem>,
                    <FormItem
                        key="password"
                        {...formItemLayout}
                        label="密码"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.password', {
                            initialValue: '',
                        })(
                            <Input type="password" autoComplete="off" />,
                        )}
                    </FormItem>,
                ]
            }
            case DATA_SOURCE.MONGODB: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="集群地址"
                        key="hostports"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.hostPorts', {
                            rules: [{
                                required: true, message: '集群地址不可为空！',
                            }],
                            initialValue: config.hostPorts || '',
                        })(
                            <Input
                                type="textarea" rows={4}
                                placeholder="MongoDB集群地址，例如：IP1:Port,IP2:Port,IP3:Port"
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        key="username"
                        {...formItemLayout}
                        label="用户名"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.username', {
                            initialValue: '',
                        })(
                            <Input autoComplete="off" />,
                        )}
                    </FormItem>,
                    <FormItem
                        key="password"
                        {...formItemLayout}
                        label="密码"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.password', {
                            initialValue: '',
                        })(
                            <Input type="password" autoComplete="off" />,
                        )}
                    </FormItem>,
                    <FormItem
                        key="database"
                        {...formItemLayout}
                        label="数据库"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.database', {
                            rules: [{
                                required: true, message: '数据库不可为空！',
                            }],
                            initialValue: '',
                        })(
                            <Input autoComplete="off" />,
                        )}
                    </FormItem>,
                ]
            }
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.SQLSERVER: 
            case DATA_SOURCE.POSTGRESQL: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="JDBC URL"
                        hasFeedback
                        key="jdbcUrl"

                    >
                        {getFieldDecorator('dataJson.jdbcUrl', {
                            rules: [{
                                required: true, message: 'jdbcUrl不可为空！',
                            },
                            jdbcRulePattern,
                        ],
                            initialValue: config.jdbcUrl || '',
                        })(
                            <Input autoComplete="off" />,
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
                                required: true, message: '用户名不可为空！',
                            }],
                            initialValue: config.username || '',
                        })(
                            <Input autoComplete="off" />,
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
                                required: true, message: '密码不可为空！',
                            }],
                            initialValue: '',
                        })(
                            <Input type="password" autoComplete="off"/>,
                        )}
                    </FormItem>
                ]
            }
            default: return []
        }
    }

    render() {
        
        const { form, sourceData, status } = this.props;
        const { getFieldDecorator } = form;
        const types = [
            {name: "MySQL", value: 1},
            {name: "HBase", value: 8},
            {name: "ElasticSearch", value: 11},
            {name: "Kafka", value: 14}
        ]
        const sourceTypeList = types.map(
            item => (
                <Option
                    key={item.value}
                    value={item.value.toString()}
                >
                    {item.name}
                </Option>
            )
        )
        const sourceType = this.state.sourceType || types[0] && types[0].value

        return ( 
            <Form autoComplete="off">
                <FormItem
                    {...formItemLayout}
                    label="数据源类型"
                    hasFeedback
                >
                    {getFieldDecorator('type', {
                        rules: [{
                            required: true, message: '数据源类型不可为空！',
                        }],
                        initialValue: sourceData.type ? sourceData.type.toString() : sourceType.toString(),
                    })(
                        <Select
                            onChange={this.sourceChange}
                            disabled={status === 'edit'}>
                            {sourceTypeList}
                        </Select>,
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="数据源名称"
                    hasFeedback
                >
                    {getFieldDecorator('dataName', {
                        rules: [{
                            required: true, message: '数据源名称不可为空！',
                        }, {
                            max: 128,
                            message: '数据源名称不得超过128个字符！',
                        }, {
                            pattern: /^[A-Za-z0-9_]+$/,
                            message: '名称只能由字母与数字、下划线组成',
                        }],
                        initialValue: sourceData.dataName || '',
                    })(
                        <Input autoComplete="off" disabled={status === 'edit'} />,
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="数据源描述"
                    hasFeedback
                >
                    {getFieldDecorator('dataDesc', {
                        rules: [{
                            max: 200,
                            message: '描述请控制在200个字符以内！',
                        }],
                        initialValue: sourceData.dataDesc || '',
                    })(
                        <Input type="textarea" rows={4} />,
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
                        onClick={this.submit}>确定
                    </Button>
                    <Button onClick={this.cancle}>取消</Button>
                </FormItem>
            </Form>
        )
    }

}


class DataSourceForm extends Component {

    state = {
        types: [],
    }

    componentDidMount() {
        this.loadSourceTypes()
    }

    loadSourceTypes = () => {
        Api.getDataSourceTypes().then(res => {
            this.setState({
                types: res.data || [],
            })
        })
    }

    cancle = () => {
        const { handCancel } = this.props
        this.myFrom.resetFields()
        handCancel()
    }

    render() {
        const { visible, form, title, status } = this.props
       
        const FormWrapper = Form.create()(BaseForm)

        return (
            <Modal
                title={title}
                wrapClassName="vertical-center-modal"
                visible={visible}
                onCancel={this.cancle}
                footer={false}
                maskClosable={false}
            >
                <FormWrapper 
                    types={this.state.types} 
                    ref={el => this.myFrom = el} 
                    {...this.props} 
                />
            </Modal>
        )
    }
}

export default DataSourceForm