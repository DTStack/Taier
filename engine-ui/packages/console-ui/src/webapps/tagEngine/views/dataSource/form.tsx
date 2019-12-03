import * as React from 'react'

import {
    Input, Button,
    Select, Form, Checkbox,
    Radio, Modal, Tooltip,
    Icon, Switch, Upload, Row, Col
} from 'antd'

import { isEmpty } from 'lodash';

import utils from 'utils';
import { hidePasswordInDom } from 'funcs';

import {
    formItemLayout,
    tailFormItemLayout,
    DATA_SOURCE,
    REDIS_TYPE
} from '../../comm/const';
import {
    jdbcUrlExample
} from '../../comm/JDBCCommon';

import HelpDoc from '../helpDoc';
import CopyIcon from 'main/components/copy-icon';
import moment from 'moment';

const FormItem = Form.Item
const Option = Select.Option
const RadioGroup = Radio.Group
const TextArea = Input.TextArea;
const rowFix = {
    rows: 4
}
const rowFix5 = {
    rows: 5
}

const hdfsConf =
    `{
"dfs.nameservices": "defaultDfs", 
"dfs.ha.namenodes.defaultDfs": "namenode1", 
"dfs.namenode.rpc-address.defaultDfs.namenode1": "", 
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider" 
}`

class BaseForm extends React.Component<any, any> {
    state: any = {
        sourceType: 11,
        hadoopConfig: 'defaultDfs',
        hadoopConfigStr: hdfsConf,
        ftpProtocal: 'ftp',
        redisType: REDIS_TYPE.SINGLE,
        isTestConnect: false
    }

    componentDidMount () {
        const sourceData = this.props.sourceData;
        if (!isEmpty(sourceData)) {
            if (sourceData.dataJson && sourceData.dataJson.hadoopConfig) {
                this.setState({
                    sourceType: sourceData.type,
                    redisType: sourceData.dataJson.redisType
                })
            } else {
                this.setState({
                    sourceType: sourceData.type,
                    redisType: sourceData.dataJson.redisType
                })
            }
        }
    }
    componentDidUpdate () {
        hidePasswordInDom();
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
                testConnection(source, () => {
                    this.setState({
                        isTestConnect: true
                    })
                })
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

    ftpProtocalChange = (e: any) => {
        this.setState({
            ftpProtocal: e.target.value
        })
    }
    redisTypeChange = (e: any) => {
        this.setState({
            redisType: e.target.value
        }, () => {
            this.props.form.resetFields(['dataJson.hostPort']);
        })
    }
    hadoopConfigChange = (e: any) => {
        const { hadoopConfig, hadoopConfigStr } = this.state
        const value = e.target.value.split('//')[1]
        if (value) {
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

    getJDBCRule = (type: any) => {
        switch (type) {
            case DATA_SOURCE.HIVE:
                return /jdbc:(\w)+:\/\/(\w)+/;
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
                                href={`/api/streamapp/download/streamDownload/downloadKerberosXML?sourceType=${getFieldValue('type')}`}
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

    renderDynamic () {
        const { form, sourceData, showUserNameWarning } = this.props;
        const { sourceType, ftpProtocal, redisType } = this.state;

        const { getFieldDecorator, getFieldValue } = form;
        const config = sourceData.dataJson || {};

        const jdbcRulePattern: any = {
            pattern: this.getJDBCRule(sourceType),
            message: '请检查您的JDBC地址格式！'
        }

        switch (sourceType) {
            case DATA_SOURCE.HDFS: {
                const formItems: any = [
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
                    </FormItem>
                ]
                formItems.push(
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
                                // rows={5}
                                className="no-scroll-bar"
                                type="textarea"
                                placeholder={hdfsConf}
                                {...rowFix5}
                            />
                        )}
                        <HelpDoc doc="hdfsConfig" />
                        <CopyIcon
                            style={{ position: 'absolute', right: '-20px', bottom: '0px' }}
                            copyText={hdfsConf}
                        />
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="开启Kerberos认证"
                        key="openKerberos"
                    >
                        {getFieldDecorator('dataJson.openKerberos', {
                            valuePropName: 'checked',
                            initialValue: config.openKerberos || false
                        })(
                            <Switch/>
                        )}
                    </FormItem>
                )
                const uploadForm: any = getFieldValue('dataJson.openKerberos') ? this.uploadForm() : [];
                formItems.push(uploadForm)
                return formItems;
            }
            case DATA_SOURCE.HIVE: {
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
                            }, jdbcRulePattern
                            ],
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
                            <Input type="password" onChange={hidePasswordInDom} autoComplete="off" />
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
                                type="textarea"
                                {...rowFix5}
                                placeholder={hdfsConf}
                            />
                        )}
                        <HelpDoc doc="hdfsConfig" />
                        <CopyIcon
                            style={{ position: 'absolute', right: '-20px', bottom: '0px' }}
                            copyText={hdfsConf}
                        />
                    </FormItem>,
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
                ]
                const uploadForm: any = getFieldValue('dataJson.openKerberos') ? this.uploadForm() : [];
                formItems.push(uploadForm)
                return formItems
            }
            case DATA_SOURCE.HBASE: {
                const formItems = [
                    <FormItem
                        {...formItemLayout}
                        label="集群地址"
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
                                // rows={5}
                                {...rowFix5}
                                placeholder="集群地址，例如：IP1:Port,IP2:Port,IP3:Port/子目录"
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        key="hbase_parent"
                        label="根目录"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.hbase_parent', {
                            rules: [],
                            initialValue: config.hbase_parent || ''
                        })(
                            <Input
                                placeholder="ZooKeeper中hbase创建的根目录，例如：/hbase"
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
                            initialValue: config.hbase_other ? typeof config.hbase_other == 'string'
                                ? JSON.stringify(JSON.parse(config.hbase_other), null, 4) : JSON.stringify(config.hbase_other, null, 4) : ''
                        })(
                            <Input type="textarea" {...rowFix5} placeholder={`hbase.rootdir": "hdfs: //ip:9000/hbase`} />
                        )}
                    </FormItem>,
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
                ]
                const uploadForm: any = getFieldValue('dataJson.openKerberos') ? this.uploadForm() : [];
                formItems.push(uploadForm)
                return formItems
            }
            case DATA_SOURCE.FTP: {
                const ftpFormItems: any = [
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
                            <Input type="number" placeholder="FTP默认21，SFTP默认22" autoComplete="off" />
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
                            <Input type="password" onChange={hidePasswordInDom} autoComplete="off" />
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
                            initialValue: config.protocol || 'ftp'
                        })(
                            <RadioGroup onChange={this.ftpProtocalChange}>
                                <Radio value="ftp">Standard</Radio>
                                <Radio value="sftp">SFTP</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
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
                                    required: true, message: '连接模式不可为空！'
                                }],
                                initialValue: config.connectMode || 'PORT'
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
                                rules: [],
                                initialValue: config.endPoint || ''
                            })(
                                <Input autoComplete="off" />
                            )
                        }
                    </FormItem>
                ]
            }
            case DATA_SOURCE.ES: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="URL"
                        hasFeedback
                        key="url"

                    >
                        {getFieldDecorator('dataJson.url', {
                            rules: [{
                                required: true, message: 'url不可为空！'
                            }, jdbcRulePattern
                            ],
                            initialValue: config.url || ''
                        })(
                            <Input onChange={() => { this.setState({ isTestConnect: false }) }} autoComplete="off" />
                        )}
                        <Tooltip overlayClassName="big-tooltip" title={'示例：172.16.8.177:9200'}>
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
                                required: false, message: '用户名不可为空！'
                            }],
                            initialValue: config.username || ''
                        })(
                            <Input onChange={() => { this.setState({ isTestConnect: false }) }} autoComplete="off" />
                        )}
                        {/* {showUserNameWarning && <Tooltip overlayClassName="big-tooltip" title={'若需要实时采集MySQL的数据，这里的用户需具有REPLICATION SLAVE权限，否则无法读取底层日志采集数据'}>
                            <Icon className="help-doc" type="question-circle-o" />
                        </Tooltip>} */}
                    </FormItem>,
                    <FormItem
                        key="password"
                        {...formItemLayout}
                        label="密码"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.password', {
                            rules: [{
                                required: false, message: '密码不可为空！'
                            }],
                            initialValue: ''
                        })(
                            <Input type="password" onChange={() => { this.setState({ isTestConnect: false }); hidePasswordInDom(); }} autoComplete="off" />
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.KAFKA:
            case DATA_SOURCE.KAFKA_09:
            case DATA_SOURCE.KAFKA_10: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="集群地址"
                        key="Address"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.address', {
                            rules: [{
                                required: true, message: '集群地址不可为空！'
                            }],
                            initialValue: config.address || ''
                        })(
                            <Input
                                type="textarea"
                                placeholder="集群地址，例如：IP1:Port,IP2:Port,IP3:Port/子目录"
                                {...rowFix}
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="broker地址"
                        key="brokerList"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.brokerList', {
                            initialValue: config.brokerList || ''
                        })(
                            <Input
                                type="textarea"
                                placeholder="Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录"
                                {...rowFix}
                            />
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.REDIS: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="模式"
                        key="redisType"
                    >
                        {getFieldDecorator('dataJson.redisType', {
                            rules: [{
                                required: true, message: '模式不能为空'
                            }],
                            initialValue: config.redisType || redisType
                        })(
                            <RadioGroup onChange={this.redisTypeChange.bind(this)}>
                                <Radio value={REDIS_TYPE.SINGLE}>单机</Radio>
                                <Radio value={REDIS_TYPE.CLUSTER}>集群</Radio>
                                <Radio value={REDIS_TYPE.SENTINEL}>哨兵</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="地址"
                        key="hostport"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.hostPort', {
                            rules: [{
                                required: true, message: '地址不可为空！'
                            }],
                            initialValue: config.hostPort || ''
                        })(
                            <TextArea
                                placeholder={
                                    redisType == REDIS_TYPE.SINGLE
                                        ? 'Redis地址，例如：IP1:Port'
                                        : 'Redis地址，例如：IP1:Port，多个地址以英文逗号分开'
                                }
                                rows={4}
                            />
                        )}
                    </FormItem>,
                    redisType == REDIS_TYPE.SENTINEL ? (
                        <FormItem
                            {...formItemLayout}
                            label="master名称"
                            key="masterName"
                            hasFeedback
                        >
                            {getFieldDecorator('dataJson.masterName', {
                                rules: [{
                                    required: true, message: 'master名称不可为空！'
                                }],
                                initialValue: config.masterName || ''
                            })(
                                <Input
                                    placeholder="请输入master名称"
                                />
                            )}
                        </FormItem>
                    ) : null,
                    redisType != REDIS_TYPE.CLUSTER ? <FormItem
                        key="database"
                        {...formItemLayout}
                        label="数据库"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.database', {
                            initialValue: config.database || ''
                        })(
                            <Input autoComplete="off" />
                        )}
                    </FormItem> : null,
                    <FormItem
                        key="password"
                        {...formItemLayout}
                        label="密码"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.password', {
                            initialValue: ''
                        })(
                            <Input type="password" onChange={hidePasswordInDom} autoComplete="off" />
                        )}
                    </FormItem>
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
                                required: true, message: '集群地址不可为空！'
                            }],
                            initialValue: config.hostPorts || ''
                        })(
                            <Input
                                type="textarea"
                                placeholder="MongoDB集群地址，例如：IP1:Port,IP2:Port,IP3:Port"
                                {...rowFix}
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
                            initialValue: ''
                        })(
                            <Input type="password" onChange={hidePasswordInDom} autoComplete="off" />
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
                                required: true, message: '数据库不可为空！'
                            }],
                            initialValue: config.database || ''
                        })(
                            <Input autoComplete="off" />
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.ORACLE: {
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
                            <Input type="password" onChange={hidePasswordInDom} autoComplete="off" />
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.MYSQL:
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
                        {showUserNameWarning && <Tooltip overlayClassName="big-tooltip" title={'若需要实时采集MySQL的数据，这里的用户需具有REPLICATION SLAVE权限，否则无法读取底层日志采集数据'}>
                            <Icon className="help-doc" type="question-circle-o" />
                        </Tooltip>}
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
                            <Input type="password" onChange={hidePasswordInDom} autoComplete="off" />
                        )}
                    </FormItem>
                ]
            }
            default: return []
        }
    }

    render () {
        const { form, sourceData, status, types, isTest, showSync } = this.props;
        const { isTestConnect } = this.state;
        const { getFieldDecorator } = form;
        const sourceTypeList = types.map(
            (item: any) => (
                <Option
                    key={item.value}
                    value={item.value.toString()}
                >
                    {item.name}
                </Option>
            )
        )
        const sourceType = this.state.sourceType || (types[0] && types[0].value)
        const isEdit = status == 'edit';
        const autoCompleteFix = {
            autoComplete: 'off'
        }

        return (
            <Form {...autoCompleteFix}>
                <FormItem
                    {...formItemLayout}
                    label="数据源类型"
                    hasFeedback
                >
                    {getFieldDecorator('type', {
                        rules: [{
                            required: true, message: '数据源类型不可为空！'
                        }],
                        initialValue: sourceData.type ? sourceData.type.toString() : sourceType.toString()
                    })(
                        <Select
                            onChange={this.sourceChange}
                            disabled={status === 'edit'}>
                            {sourceTypeList}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="数据源名称"
                    hasFeedback
                >
                    {getFieldDecorator('dataName', {
                        rules: [{
                            required: true, message: '数据源名称不可为空！'
                        }, {
                            max: 80,
                            message: '数据源名称不得超过80个字符！'
                        }, {
                            pattern: /^[A-Za-z0-9_\u4e00-\u9fa5]+$/,
                            message: '名称只能由中文、字母、数字和下划线组成'
                        }],
                        initialValue: sourceData.dataName || ''
                    })(
                        <Input autoComplete="off" disabled={status === 'edit' && sourceData.active === 1} />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="数据源描述"
                    hasFeedback
                >
                    {getFieldDecorator('dataDesc', {
                        rules: [{
                            max: 500,
                            message: '描述请控制在500个字符以内！'
                        }],
                        initialValue: sourceData.dataDesc || ''
                    })(
                        <Input type="textarea" {...rowFix}/>
                    )}
                </FormItem>
                {this.renderDynamic()}
                {isTest && showSync && !isEdit && (<FormItem
                    {...tailFormItemLayout}
                >
                    {getFieldDecorator('isCopyToProduceProject', {
                        initialValue: sourceData.isCopyToProduceProject
                    })(
                        <Checkbox>
                            复制到目标项目
                        </Checkbox>
                    )}
                </FormItem>)}
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
                        disabled={!isTestConnect}
                        style={{ marginRight: '10px' }}
                        onClick={this.submit}>确定
                    </Button>
                    <Button onClick={this.cancle}>取消</Button>
                </FormItem>
            </Form>
        )
    }
}
class DataSourceForm extends React.Component<any, any> {
    myFrom: any;
    cancle = () => {
        const { handCancel } = this.props
        this.myFrom.resetFields()
        handCancel()
    }

    render () {
        const { visible, title, sourceTypes } = this.props
        const FormWrapper = Form.create<any>()(BaseForm)
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
                    types={sourceTypes}
                    ref={(el: any) => this.myFrom = el}
                    {...this.props}
                />
            </Modal>
        )
    }
}

export default DataSourceForm
