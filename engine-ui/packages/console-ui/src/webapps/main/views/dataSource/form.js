import React, { Component } from 'react'

import {
    Input, Button,
    Select, Form, Checkbox,
    Radio, Modal
} from 'antd'

import {
    formItemLayout,
    tailFormItemLayout,
    DATA_SOURCE
} from '../../comm/const'

import Api from '../../api'
import HelpDoc from '../helpDoc';

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
"defaultFS": "",
"hadoopConfig": {
    "dfs.nameservices": "defaultDfs", 
    "dfs.ha.namenodes.defaultDfs": "namenode1", 
    "dfs.namenode.rpc-address.defaultDfs.namenode1": "", 
    "dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider" 
}
}`
const hBaseConf =
`{
"zookeeper.cluster": "ip1:port,ip2:port/子目录",
"hbase.rootdir": "hdfs: //ip:9000/hbase",
"hbase.cluster.distributed": "true",
"hbase.zookeeper.quorum": "***"
}`

function getConnectionConfig (sourceType) {
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

class DataSourceForm extends Component {
    state = {
        sourceType: '',
        types: [],
        hasHdfsConfig: false,
        hadoopConfig: 'defaultDfs',
        hadoopConfigStr: hdfsConf
    }

    componentDidMount () {
        this.loadSourceTypes()
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const newData = nextProps.sourceData

        if (newData && newData.id !== this.props.sourceData.id) {
            if (newData.dataJson && newData.dataJson.hadoopConfig) {
                this.setState({ sourceType: newData.type || 1, hasHdfsConfig: true })
            } else {
                this.setState({ sourceType: newData.type || 1 })
            }
        }
    }

    loadSourceTypes = () => {
        Api.getDataSourceTypes().then(res => {
            this.setState({
                types: res.data || []
            })
        })
    }

    submit = (e) => {
        e.preventDefault()
        const { handOk, form } = this.props
        const source = form.getFieldsValue()
        const { sourceType } = this.state

        // let fields = fields = ['dataName', 'type', 'dataJson']
        form.validateFields((err) => {
            if (!err) {
                handOk(source, form)
            }
        });
    }

    testConnection = (e) => {
        const { testConnection } = this.props

        this.props.form.validateFields((err, source) => {
            if (!err) {
                testConnection(source)
            }
        });
    }

    cancle = () => {
        const { form, handCancel } = this.props

        form.resetFields()
        handCancel()
    }

    sourceChange = (value) => {
        this.setState({ sourceType: value })
        this.props.form.resetFields();
    }

    enableHdfsConfig = (e) => {
        this.setState({
            hasHdfsConfig: !e.target.value
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
                hadoopConfig: newStr
            })
        }
    }

    setHadoopConf = (value) => {
        const editor = this.editor.self
        const doc = editor.doc

        doc.setValue(value)
    }

    getHelpDoc (type) {
        switch (type) {
            case DATA_SOURCE.HDFS:
                return 'hdfsConfig'
            case DATA_SOURCE.HBASE:
                return 'hBaseConfig'
            default:
                return 'rdbConfig'
        }
    }

    renderDynamic () {
        const { hasHdfsConfig, sourceType } = this.state
        const { form, sourceData } = this.props;
        const { getFieldDecorator } = form;
        const config = sourceData.dataJson || {};

        switch (sourceType) {
            case DATA_SOURCE.HDFS: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="DefaultFS"
                        key="defaultFS"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.defaultFS', {
                            rules: [{
                                required: true, message: 'defaultFS不可为空！'
                            }],
                            initialValue: config.defaultFS || ''
                        })(
                            <Input placeholder="hdfs://host:port"/>
                        )}
                    </FormItem>,
                    <FormItem
                        key="hasHdfsConfig"
                        {...tailFormItemLayout}
                    >
                        {getFieldDecorator('hasHdfsConfig', {
                            initialValue: false
                        })(
                            <Checkbox
                                checked={hasHdfsConfig}
                                onChange={this.enableHdfsConfig}>
                                高可用配置
                            </Checkbox>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="高可用配置"
                        key="hadoopConfig"
                        hasFeedback
                        style={{ display: hasHdfsConfig ? 'block' : 'none' }}
                    >
                        {getFieldDecorator('dataJson.hadoopConfig', {
                            rules: [{
                                required: true, message: 'Hadoop配置不可为空！'
                            }],
                            initialValue: config.hadoopConfig || ''
                        })(
                            <Input type="textarea" rows={5} placeholder={hdfsConf} />
                        )}
                        <HelpDoc doc="hdfsConfig" />
                    </FormItem>
                ]
            }
            case DATA_SOURCE.HIVE: {
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
                            }],
                            initialValue: config.jdbcUrl || ''
                        })(
                            <Input autoComplete="off" />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="用户名"
                        key="username"
                    >
                        {getFieldDecorator('dataJson.username', {
                            rules: [],
                            initialValue: config.username || ''
                        })(
                            <Input autoComplete="off" />
                        )}
                    </FormItem>,
                    <FormItem
                        key="password"
                        {...formItemLayout}
                        label="密码"
                    >
                        {getFieldDecorator('dataJson.password', {
                            rules: [],
                            initialValue: ''
                        })(
                            <Input type="password"/>
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
                            <Input placeholder="hdfs://host:port"/>
                        )}
                    </FormItem>,
                    <FormItem
                        key="hasHdfsConfig"
                        {...tailFormItemLayout}
                    >
                        {getFieldDecorator('hasHdfsConfig', {
                            initialValue: false
                        })(
                            <Checkbox
                                checked={hasHdfsConfig}
                                onChange={this.enableHdfsConfig}>
                                高可用配置
                            </Checkbox>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="高可用配置"
                        key="hadoopConfig"
                        style={{ display: hasHdfsConfig ? 'block' : 'none' }}
                    >
                        {getFieldDecorator('dataJson.hadoopConfig', {
                            rules: [],
                            initialValue: config.hadoopConfig || ''
                        })(
                            <Input type="textarea" rows={5} placeholder={hdfsConf} />
                        )}
                        <HelpDoc doc="hdfsConfig" />
                    </FormItem>
                ]
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
                                required: true, message: 'Zookeeper配置不可为空！'
                            }],
                            initialValue: config.hbase_quorum || ''
                        })(
                            <Input
                                type="textarea"
                                rows={5}
                                placeholder="Zookeeper集群地址，例如：IP1:Port,IP2:Port,IP3:Port/子目录"
                            />
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
                            <Input type="textarea" rows={5} placeholder={`hbase.rootdir": "hdfs: //ip:9000/hbase`}/>
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.FTP: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="主机名/IP"
                        hasFeedback
                        key="host"
                    >
                        {getFieldDecorator('dataJson.host', {
                            rules: [{
                                required: true, message: '主机名/IP不可为空！'
                            }],
                            initialValue: config.host || ''
                        })(
                            <Input autoComplete="off" />
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
                                required: true, message: '端口不可为空！'
                            }],
                            initialValue: config.port || ''
                        })(
                            <Input autoComplete="off" />
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
                                required: true, message: '用户名不可为空！'
                            }],
                            initialValue: ''
                        })(
                            <Input/>
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
                            <Input type="password"/>
                        )}
                    </FormItem>,
                    <FormItem
                        key="connectMode"
                        {...formItemLayout}
                        label="连接模式"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.connectMode', {
                            rules: [{
                                required: true, message: '连接模式不可为空！'
                            }],
                            initialValue: config.connectMode || ''
                        })(
                            <RadioGroup>
                                <Radio value={1}>PORT (主动)</Radio>
                                <Radio value={2}>PASV（被动）</Radio>
                            </RadioGroup>
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
                                required: true, message: '协议不可为空！'
                            }],
                            initialValue: config.protocol || ''
                        })(
                            <RadioGroup>
                                <Radio value={1}>Standard</Radio>
                                <Radio value={2}>SFTP</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.SQLSERVER:
            default: {
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
                            }],
                            initialValue: config.jdbcUrl || ''
                        })(
                            <Input autoComplete="off" />
                        )}
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
                            <Input type="password"/>
                        )}
                    </FormItem>
                ]
            }
        }
    }

    render () {
        const { visible, form, title, sourceData, status } = this.props
        const { types } = this.state
        const { getFieldDecorator } = form;
        const sourceTypeList = types.map(
            item => (
                <Option
                    key={item.value}
                    value={item.value}
                >
                    {item.name}
                </Option>
            )
        )
        const sourceType = types[0] && types[0].value

        return (
            <Modal
                title={title}
                wrapClassName="vertical-center-modal"
                visible={visible}
                onCancel={this.cancle}
                footer={false}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="数据源名称"
                        hasFeedback
                    >
                        {getFieldDecorator('dataName', {
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
                            <Input autoComplete="off" disabled={status === 'edit'} />
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
                                message: '描述请控制在200个字符以内！'
                            }],
                            initialValue: sourceData.dataDesc || ''
                        })(
                            <Input type="textarea" rows={4} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="数据源类型"
                        hasFeedback
                    >
                        {getFieldDecorator('type', {
                            rules: [{
                                required: true, message: '数据源类型不可为空！'
                            }],
                            initialValue: sourceData.type || sourceType
                        })(
                            <Select
                                onChange={this.sourceChange}
                                disabled={status === 'edit'}>
                                { sourceTypeList }
                            </Select>
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
            </Modal>
        )
    }
}

const FormWrapper = Form.create()(DataSourceForm)

export default FormWrapper
