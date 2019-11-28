import * as React from 'react'

import {
    Input, Button,
    Select, Form, Checkbox,
    Radio, Modal, Tooltip,
    Icon, Alert, Switch, Upload, Row, Col
} from 'antd'

import { isEmpty } from 'lodash';

import utils from 'utils';
import { hidePasswordInDom } from 'funcs';

import {
    formItemLayout,
    tailFormItemLayout,
    DATA_SOURCE
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
const hdfsConf =
    `{
"dfs.nameservices": "defaultDfs", 
"dfs.ha.namenodes.defaultDfs": "namenode1", 
"dfs.namenode.rpc-address.defaultDfs.namenode1": "", 
"dfs.client.failover.proxy.provider.defaultDfs": "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider" 
}`

const kylinConf = `{
    "socketTimeout":10000,
    "connectTimeout":10000
}`;

const kuduConf = `{
    "openKerberos":false,
    "user":"",
    "keytabPath":"",
    "workerCount":4,
    "bossCount":1,
    "operationTimeout":30000,
    "adminOperationTimeout":30000
}`;

const kuduOthersPh = `输入JSON格式的参数，示例及默认参数如下：
{
    "openKerberos":false,
    "user":"",
    "keytabPath":"",
    "workerCount":4,
    "bossCount":1,
    "operationTimeout":30000,
    "adminOperationTimeout":30000
}`;

class BaseForm extends React.Component<any, any> {
    state: any = {
        sourceType: 1,
        hadoopConfig: 'defaultDfs',
        hadoopConfigStr: hdfsConf,
        hasCarbonDataConfig: false,
        ftpProtocal: 'ftp'
    }
    componentDidMount () {
        const sourceData = this.props.sourceData;
        if (!isEmpty(sourceData)) {
            let initialState: any = {
                sourceType: sourceData.type,
                ftpProtocal: (sourceData.dataJson && sourceData.dataJson.protocol) || 'ftp'
            }
            if (sourceData.dataJson && sourceData.type === DATA_SOURCE.CARBONDATA) {
                const hdfsConf = sourceData.dataJson.hdfsCustomConfig;
                initialState.hasCarbonDataConfig = hdfsConf !== 'default';
            }
            this.setState(initialState);
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
                testConnection(source)
            }
        });
    }

    preHandFormValues = (source: any) => {
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

    carbonDataConfigChange = (e: any) => {
        this.setState({
            hasCarbonDataConfig: e.target.value !== 'default'
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
            case DATA_SOURCE.KYLIN:
                return /http:\/\/([\w, .])+:(\w)+/;
            case DATA_SOURCE.HIVE_1:
            case DATA_SOURCE.HIVE_2:
            case DATA_SOURCE.POLAR_DB:
            case DATA_SOURCE.CARBONDATA:
                return /jdbc:(\w)+:\/\/(\w)+/;
            case DATA_SOURCE.MYSQL:
                return /jdbc:mysql:\/\/(\w)+/;
            case DATA_SOURCE.CLICK_HOUSE:
                return /jdbc:clickhouse:\/\/(\w)+/;
            case DATA_SOURCE.DB2:
                return /jdbc:db2:\/\/(\w)+/;
            case DATA_SOURCE.ORACLE:
                return /jdbc:oracle:thin:@(\/\/)?(\w)+/;
            case DATA_SOURCE.SQLSERVER:
                return undefined;
            case DATA_SOURCE.LIBRASQL:
            case DATA_SOURCE.POSTGRESQL:
                return /jdbc:postgresql:\/\/(\w)+/;
            case DATA_SOURCE.GBASE:
                return /jdbc:gbase:\/\/(\w)+/;
            default:
                return undefined;
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
                file.modifyTime = moment()
                console.log(file, moment());
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
                                href={`/api/rdos/download/batch/batchDownload/downloadKerberosXML?sourceType=${getFieldValue('type')}`}
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
        const { sourceType, ftpProtocal, hasCarbonDataConfig } = this.state;

        const { getFieldDecorator, getFieldValue } = form;
        const config = sourceData.dataJson || {};
        console.log(config);
        const jdbcRulePattern: any = {
            pattern: this.getJDBCRule(sourceType),
            message: '请检查您的JDBC地址格式！'
        }
        switch (sourceType) { // sourceType
            case DATA_SOURCE.KYLIN: {
                const formItems: any = [
                    <FormItem
                        {...formItemLayout}
                        label="RESTful URL:"
                        hasFeedback
                        key="authURL"
                    >
                        {getFieldDecorator('dataJson.authURL', {
                            rules: [{
                                required: true, message: 'RESTful URL 不可为空！'
                            }, jdbcRulePattern
                            ],
                            initialValue: config.authURL || ''
                        })(
                            <Input autoComplete="off" placeholder={ 'http://ip:port' } />
                        )}
                        <HelpDoc doc="kylinRestfulHelp" />
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="用户名"
                        key="username"
                        hasFeedback
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
                        {...formItemLayout}
                        key="password"
                        label="密码"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.password', {
                            rules: [{
                                required: true, message: '密码不可为空！'
                            }]
                        })(
                            <Input type="password" onChange={hidePasswordInDom} autoComplete="off" />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        key="project"
                        label="Project"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.project', {
                            rules: [{
                                required: true, message: 'Project不可为空！'
                            }],
                            initialValue: config.project || ''
                        })(
                            <Input autoComplete="off" placeholder={ 'DEFAULT' } />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="高可用配置"
                        key="config"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.config', {
                            initialValue: config.config ? typeof config.config == 'string'
                                ? JSON.stringify(JSON.parse(config.config), null, 4) : JSON.stringify(config.config, null, 4) : ''
                        })(
                            <Input.TextArea
                                className="no-scroll-bar"
                                rows={5}
                                placeholder={kylinConf}
                            />
                        )}
                        <CopyIcon
                            style={{ position: 'absolute', right: '-20px', bottom: '0px' }}
                            copyText={kylinConf}
                        />
                    </FormItem>
                ];
                return formItems;
            }
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
                            <Input.TextArea
                                rows={5}
                                className="no-scroll-bar"
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
                        key="dataJson.openKerberos"
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
                // const uploadForm = this.uploadForm()
                formItems.push(uploadForm)
                return formItems;
            }
            case DATA_SOURCE.CARBONDATA: {
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
                        hasFeedback
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
                        {...formItemLayout}
                        key="password"
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
                        key="hdfsCustomConfig"
                        {...formItemLayout}
                        label="HDFS配置"
                    >
                        {getFieldDecorator('dataJson.hdfsCustomConfig', {
                            rules: [],
                            initialValue: config.hdfsCustomConfig || 'default'
                        })(
                            <RadioGroup onChange={this.carbonDataConfigChange}>
                                <Radio value="default">默认</Radio>
                                <Radio value="custom">自定义</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ]

                if (hasCarbonDataConfig) {
                    formItems.push(
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
                    )
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
                                <Input.TextArea
                                    className="no-scroll-bar"
                                    rows={5}
                                    placeholder={hdfsConf}
                                />
                            )}
                            <HelpDoc doc="hdfsConfig" />
                            <CopyIcon
                                style={{ position: 'absolute', right: '-20px', bottom: '0px' }}
                                copyText={hdfsConf}
                            />
                        </FormItem>
                    )
                }
                return formItems
            }
            case DATA_SOURCE.HIVE_1:
            case DATA_SOURCE.HIVE_2: {
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
                            <Input.TextArea
                                className="no-scroll-bar"
                                placeholder={hdfsConf}
                            />
                        )}
                        <HelpDoc doc="hdfsConfig" />
                        <CopyIcon
                            style={{ position: 'absolute', right: '-20px', bottom: '0px' }}
                            copyText={hdfsConf}
                        />
                    </FormItem>
                ]
                if (sourceType === DATA_SOURCE.HIVE_2) {
                    formItems.push(
                        <FormItem
                            {...formItemLayout}
                            label="开启Kerberos认证"
                            key="dataJson.openKerberos"
                        >
                            {getFieldDecorator('dataJson.openKerberos', {
                                valuePropName: 'checked',
                                initialValue: config.openKerberos || false
                            })(
                                <Switch />
                            )}
                        </FormItem>
                    )
                }
                const uploadForm: any = getFieldValue('dataJson.openKerberos') ? this.uploadForm() : [];
                formItems.push(uploadForm)
                return formItems;
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
                            <Input.TextArea
                                rows={5}
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
                            <Input.TextArea rows={5} placeholder={`hbase.rootdir": "hdfs: //ip:9000/hbase`} />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="开启Kerberos认证"
                        key="dataJson.openKerberos"
                    >
                        {getFieldDecorator('dataJson.openKerberos', {
                            valuePropName: 'checked',
                            initialValue: config.openKerberos || false
                        })(
                            <Switch/>
                        )}
                    </FormItem>
                ]
                const uploadForm: any = getFieldValue('dataJson.openKerberos') ? this.uploadForm() : [];
                // const uploadForm = this.uploadForm()
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
                                <Radio value="ftp">FTP</Radio>
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
                            <Input.TextArea
                                rows={4}
                                placeholder="集群地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接"
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="集群名称"
                        key="clusterName"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.clusterName', {
                            rules: [{
                                required: true, message: '集群名称不可为空！'
                            }],
                            initialValue: config.clusterName || ''
                        })(
                            <Input
                                placeholder="请输入集群名称"
                            />
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
                                required: true, message: '地址不可为空！'
                            }],
                            initialValue: config.hostPort || ''
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
                                required: true, message: '数据库不可为空！'
                            }],
                            initialValue: config.database || ''
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
                                required: false, message: ''
                            }],
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
                            <Input.TextArea
                                rows={4}
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
                            rules: [{
                                required: false, message: ''
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
                                required: false, message: ''
                            }],
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
                                <span style={{ wordBreak: 'break-all' }}>
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
            case DATA_SOURCE.KUDU: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="集群地址"
                        key="hostPorts"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.hostPorts', {
                            rules: [{
                                required: true, message: '集群地址不可为空！'
                            }],
                            initialValue: config.hostPorts || ''
                        })(
                            <Input.TextArea
                                rows={5}
                                placeholder="集群地址，例如：IP1:Port,IP2:Port,IP3:Port3，多个IP地址用英文逗号隔开"
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="其他参数"
                        key="others"
                        hasFeedback
                    >
                        {getFieldDecorator('dataJson.others', {
                            initialValue: config.others ? typeof config.others == 'string'
                                ? JSON.stringify(JSON.parse(config.others), null, 4) : JSON.stringify(config.others, null, 4) : ''
                        })(
                            <Input.TextArea
                                className="no-scroll-bar"
                                rows={5}
                                placeholder={kuduOthersPh}
                            />
                        )}
                        <CopyIcon
                            style={{ position: 'absolute', right: '-20px', bottom: '0px' }}
                            copyText={kuduConf}
                        />
                    </FormItem>
                ];
            }
            case DATA_SOURCE.GBASE:
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.POLAR_DB:
            case DATA_SOURCE.DB2:
            case DATA_SOURCE.SQLSERVER:
            case DATA_SOURCE.LIBRASQL:
            case DATA_SOURCE.CLICK_HOUSE:
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
        const { active } = sourceData;

        return (
            <Form>
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
                            showSearch
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
                            max: 128,
                            message: '数据源名称不得超过128个字符！'
                        }, {
                            pattern: /^[A-Za-z0-9_\u4e00-\u9fa5]+$/,
                            message: '名称只能由中文、字母、数字和下划线组成'
                        }],
                        initialValue: sourceData.dataName || ''
                    })(
                        <Input autoComplete="off" disabled={status === 'edit' && active === 1} />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="数据源描述"
                >
                    {getFieldDecorator('dataDesc', {
                        rules: [{
                            max: 200,
                            message: '描述请控制在200个字符以内！'
                        }],
                        initialValue: sourceData.dataDesc || ''
                    })(
                        <Input.TextArea rows={4} />
                    )}
                </FormItem>
                {this.renderDynamic()}
                { this.state.sourceType !== DATA_SOURCE.KYLIN && sourceType !== DATA_SOURCE.GBASE && isTest && showSync && !isEdit && (
                    <FormItem
                        {...tailFormItemLayout}
                    >
                        {getFieldDecorator('isCopyToProduceProject', {
                            initialValue: sourceData.isCopyToProduceProject
                        })(
                            <Checkbox>
                                复制到目标项目
                            </Checkbox>
                        )}
                    </FormItem>
                )}
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

class DataSourceForm extends React.Component<any, any> {
    myFrom: any;
    cancle = () => {
        const { handCancel } = this.props
        this.myFrom.resetFields()
        handCancel()
    }
    isShowWarning () {
        const { project, sourceData = {} } = this.props;
        const projectName = project.projectName;
        if (sourceData) {
            if (sourceData.dataName == projectName) {
                return true;
            }
        }
        return false;
    }
    render () {
        const { visible, title, sourceTypes } = this.props;
        const isShowWarning = this.isShowWarning();
        const FormWrapper = Form.create<any>()(BaseForm)
        return (
            <Modal
                title={title}
                wrapClassName="vertical-center-modal"
                visible={visible} // visible
                onCancel={this.cancle}
                footer={false}
                maskClosable={false}
            >
                {isShowWarning && (<Alert style={{ marginBottom: '15px' }} message="项目自身数据源，建议不要修改" type="warning" showIcon />)}
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
