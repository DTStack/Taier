import React from "react";
import { Table, Form, Input, Row, Col, Select, Icon, Tooltip, Button, Tag, message, Card } from "antd";
import { cloneDeep } from "lodash";
import { connect } from "react-redux"
import {hashHistory} from "react-router"

import { getUser } from "../../../actions/console"
import Api from "../../../api/console"
import { longLabelFormLayout, formItemLayout } from "../../../consts"
import GoBack from "main/components/go-back";
import utils from "utils";

const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;
const TEST_STATUS = {
    NOTHING: 0,
    SUCCESS: 1,
    FAIL: 2
}
function giveMeAKey() {
    return (new Date().getTime() + '' + ~~(Math.random() * 100000))
}
function mapStateToProps(state) {
    return {
        consoleUser: state.consoleUser
    }
}
function mapDispatchToProps(dispatch) {
    return {
        getTenantList() {
            dispatch(getUser())
        }
    }
}
@connect(mapStateToProps, mapDispatchToProps)
class EditCluster extends React.Component {
    state = {
        selectUserMap: {},
        selectUser: "",//select输入value
        file: "",//上传的文件
        zipConfig: "",//解析的配置文件信息
        uploadLoading: false,//上传loading
        testLoading: false,
        testStatus: TEST_STATUS.NOTHING,
        flink_params: [],
        spark_params: [],
        core: null,
        nodeNumber: null,
        memory: null,
        extDefaultValue: {},
        fileHaveChange: false
    }
    componentDidMount() {
        const { location, form } = this.props;
        const params = location.state || {};
        if (params.mode == "edit" || params.mode == "view") {
            Api.getClusterInfo({
                clusterId: params.cluster.id
            })
                .then(
                    (res) => {
                        if (res.code == 1) {
                            const cluster = res.data;
                            let clusterConf = cluster.clusterConf;
                            clusterConf = JSON.parse(clusterConf);
                            const extParams = this.exchangeServerParams(clusterConf)

                            this.setState({
                                core: cluster.totalCore,
                                memory: cluster.totalMemory,
                                nodeNumber: cluster.totalNode,
                                zipConfig: JSON.stringify({
                                    yarnConf: clusterConf.yarnConf,
                                    hadoopConf: clusterConf.hadoopConf,
                                    hiveMeta:clusterConf.hiveMeta
                                }),
                                flink_params: extParams.flinkKeys,
                                spark_params: extParams.sparkKeys,
                                extDefaultValue: extParams.default
                            })
                            form.setFieldsValue({
                                clusterName: cluster.clusterName,
                                hiveConf: clusterConf.hiveConf,
                                sparkConf: clusterConf.sparkConf,
                                flinkConf: clusterConf.flinkConf,
                            })
                        }
                    }
                )
        }
        // this.props.getTenantList();
    }
    /**
     * 从服务端配置中抽取出自定义参数
     * @param {Map} config 服务端接收到的配置
     */
    exchangeServerParams(config) {
        let result = {
            flinkKeys: [],
            sparkKeys: [],
            default: {}
        };
        let notExtKeys_flink = ["typeName", "flinkZkAddress",
            "flinkHighAvailabilityStorageDir", "flinkZkNamespace",
            "jarTmpDir", "flinkPluginRoot", "remotePluginRootDir", "clusterMode"];
        let notExtKeys_spark = ["typeName", "sparkYarnArchive",
            "sparkSqlProxyPath", "sparkPythonExtLibPath"];
        let sparkConfig = config.sparkConf;
        let flinkConfig = config.flinkConf;

        function setDefault(config, notExtKeys, type, keys) {
            const keyAndValue = Object.entries(config);
            keyAndValue.map(
                ([key, value]) => {
                    if (!notExtKeys.includes(key)) {
                        let formItemId = giveMeAKey();
                        keys.push({ id: formItemId });
                        result.default[formItemId] = {
                            name: key,
                            value: value
                        }
                    }
                }
            )
        }

        setDefault(sparkConfig, notExtKeys_spark, "spark", result.sparkKeys)
        setDefault(flinkConfig, notExtKeys_flink, "flink", result.flinkKeys)
        return result;
    }
    getUserOptions() {
        const { consoleUser } = this.props;
        const { selectUserMap } = this.state;
        const userList = consoleUser.userList;
        const result = [];
        for (let i = 0; i < userList.length; i++) {
            const user = userList[i];
            if (!selectUserMap[user.tenantId]) {
                result.push(<Option value={user.tenantId + '$$' + user.tenantName}>{user.tenantName}</Option>)
            }
        }
        return result;
    }
    changeUserValue(value) {
        this.setState({
            selectUser: value
        })
    }
    selectUser(value, option) {
        const { selectUserMap } = this.state;
        this.setState({
            selectUser: "",
            selectUserMap: {
                ...selectUserMap,
                [value.split("$$")[0]]: {
                    name: option.props.children
                }
            }
        })
    }
    getTableDataSource() {
        const { selectUserMap } = this.state;
        const keyAndValue = Object.entries(selectUserMap);
        return keyAndValue.map((item) => {
            return {
                id: item[0],
                name: item[1].name
            }
        })

    }
    removeUser(id) {
        let { selectUserMap } = this.state;
        selectUserMap = cloneDeep(selectUserMap);
        delete selectUserMap[id];
        this.setState({
            selectUserMap: selectUserMap
        })
    }
    initColumns() {
        return [
            {
                title: "租户名称",
                dataIndex: "name",
                width: "150px"
            },
            {
                title: "操作",
                dataIndex: "deal",
                render: (text, record) => {
                    return (<a onClick={this.removeUser.bind(this, record.id)}>删除</a>)
                }
            }
        ]
    }
    validateFileType(rule, value, callback) {
        const reg = /\.(zip)$/

        if (value && !reg.test(value.toLocaleLowerCase())) {
            callback('配置文件只能是zip文件!');
        }
        callback();
    }
    fileChange(e) {
        const file = e.target;
        this.setState({ file: {}, uploadLoading: true, zipConfig: "", fileHaveChange: true });
        Api.uploadClusterResource({
            config: file.files[0]
        })
            .then(
                (res) => {
                    if (res.code == 1) {
                        this.setState({
                            uploadLoading: false,
                            file: file,
                            zipConfig: res.data
                        })
                    } else {
                        this.props.form.setFieldsValue({
                            file: null
                        })
                        this.setState({
                            uploadLoading: false
                        })
                    }
                }
            )

        // this.props.handleFileChange(file);
    }
    renderTestResult() {
        const { testStatus } = this.state;
        switch (testStatus) {
            case TEST_STATUS.NOTHING: {
                return null
            }
            case TEST_STATUS.SUCCESS: {
                return <Tag color="#87d068">连通成功</Tag>
            }
            case TEST_STATUS.FAIL: {
                return <span>
                    <Tag color="red">连通失败</Tag>
                </span>
            }
        }
    }
    addParam(type, ) {
        const { flink_params, spark_params } = this.state;
        if (type == "flink") {
            this.setState({
                flink_params: [...flink_params, {
                    id: giveMeAKey()
                }]
            })
        } else {
            this.setState({
                spark_params: [...spark_params, {
                    id: giveMeAKey()
                }]
            })
        }
    }
    deleteParam(id, type) {
        const { flink_params, spark_params } = this.state;
        let tmpParams;
        let tmpStateName;
        if (type == "flink") {
            tmpStateName = "flink_params";
            tmpParams = flink_params;
        } else {
            tmpStateName = "spark_params";
            tmpParams = spark_params;
        }
        tmpParams = tmpParams.filter(
            (param) => {
                return param.id != id;
            }
        );
        this.setState({
            [tmpStateName]: tmpParams
        })
    }
    renderExtraParam(type) {
        const { flink_params, spark_params, extDefaultValue } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == "view"
        let tmpParams;
        if (type == "flink") {
            tmpParams = flink_params;
        } else {
            tmpParams = spark_params;
        }
        return tmpParams.map(
            (param) => {
                return (<Row key={param.id}>
                    <Col span={formItemLayout.labelCol.sm.span}>
                        <FormItem key={param.id + '-name'}>
                            {getFieldDecorator(type + '%' + param.id + '-name', {
                                rules: [{
                                    required: true,
                                    message: "请输入参数属性名"
                                }],
                                initialValue: extDefaultValue[param.id] && extDefaultValue[param.id].name
                            })(
                                <Input disabled={isView} style={{ width: "calc(100% - 12px)" }} />
                            )}
                            :
                        </FormItem>
                    </Col>
                    <Col span={formItemLayout.wrapperCol.sm.span}>
                        <FormItem key={param.id + '-value'}>
                            {getFieldDecorator(type + '%' + param.id + '-value', {
                                rules: [{
                                    required: true,
                                    message: "请输入参数属性值"
                                }],
                                initialValue: extDefaultValue[param.id] && extDefaultValue[param.id].value
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>

                    </Col>
                    {isView ? null : (<a className="formItem-right-text" onClick={this.deleteParam.bind(this, param.id, type)}>删除</a>)}
                </Row>)
            }
        )
    }
    renderZipConfig(type) {
        let { zipConfig } = this.state;
        zipConfig = JSON.parse(zipConfig);
        let keyAndValue;
        if (type == "hdfs") {
            keyAndValue = Object.entries(zipConfig.hadoopConf)
            utils.sortByCompareFunctions(keyAndValue,
                ([key, value], [compareKey, compareValue]) => {
                    if (key == "fs.defaultFS") {
                        return -1;
                    }
                    if (compareKey == "fs.defaultFS") {
                        return 1;
                    }
                    return 0;
                },
                ([key, value], [compareKey, compareValue]) => {
                    if (key == "dfs.nameservices") {
                        return -1;
                    }
                    if (compareKey == "dfs.nameservices") {
                        return 1;
                    }
                    return 0;
                },
                ([key, value], [compareKey, compareValue]) => {
                    if (key.indexOf("dfs.ha.namenodes") > -1) {
                        return -1;
                    }
                    if (compareKey.indexOf("dfs.ha.namenodes") > -1) {
                        return 1;
                    }
                    return 0;
                },
                ([key, value], [compareKey, compareValue]) => {
                    const checkKey = key.indexOf("dfs.namenode.rpc-address") > -1
                    const checkCompareKey = compareKey.indexOf("dfs.namenode.rpc-address") > -1
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
        } else {
            keyAndValue = Object.entries(zipConfig.yarnConf)
            utils.sortByCompareFunctions(keyAndValue,
                ([key, value], [compareKey, compareValue]) => {
                    if (key == "yarn.resourcemanager.ha.rm-ids") {
                        return -1;
                    }
                    if (compareKey == "yarn.resourcemanager.ha.rm-ids") {
                        return 1;
                    }
                    return 0;
                },
                ([key, value], [compareKey, compareValue]) => {
                    
                    const checkKey = key.indexOf("yarn.resourcemanager.address") > -1
                    const checkCompareKey = compareKey.indexOf("yarn.resourcemanager.address") > -1
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
                ([key, value], [compareKey, compareValue]) => {
                    const checkKey = key.indexOf("yarn.resourcemanager.webapp.address") > -1
                    const checkCompareKey = compareKey.indexOf("yarn.resourcemanager.webapp.address") > -1
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
        }

        return keyAndValue.map(
            ([key, value]) => {
                return (<Row className="zipConfig-item">
                    <Col className="formitem-textname" span={formItemLayout.labelCol.sm.span + 4}>
                        {key.length > 40 ?
                            <Tooltip title={key}>{key.substr(0, 40) + "..."}</Tooltip>
                            : key}：
                    </Col>
                    <Col className="formitem-textvalue" span={formItemLayout.wrapperCol.sm.span - 1}>
                        {value}
                    </Col>
                </Row>)
            }
        )
    }
    exchangeMemory(totalMemory) {
        if (!totalMemory) {
            return '--';
        }
        const memory = totalMemory / 1024;
        const haveDot = Math.floor(memory) != memory
        return `${haveDot ? memory.toFixed(2) : memory}GB`
    }
    save() {
        const { mode } = this.props.location.state || {};
        this.props.form.validateFieldsAndScroll(null, {}, (err, values) => {
            if (!err) {
                let functionName = "createCluster"
                if (mode == "edit") {
                    functionName = "updateCluster"
                }
                Api[functionName](this.getServerParams(values, true))
                    .then(
                        (res) => {
                            if (res.code == 1) {
                                message.success("保存成功")
                                this.props.router.goBack();
                            }
                        }
                    )
            }
        })
    }
    test() {
        this.props.form.validateFieldsAndScroll(null, {}, (err, values) => {
            if (!err) {
                this.setState({
                    testLoading: true,
                })
                Api.testCluster(this.getServerParams(values))
                    .then(
                        (res) => {
                            if (res.code == 1) {
                                this.setState({
                                    testLoading: false,
                                    testStatus: TEST_STATUS.SUCCESS,
                                    nodeNumber: res.data.totalNode,
                                    core: res.data.totalCores,
                                    memory: res.data.totalMemory
                                })
                                message.success("连通成功")
                            } else {
                                this.setState({
                                    testLoading: false,
                                    testStatus: TEST_STATUS.FAIL
                                })
                            }
                        }
                    )
            }
        })
    }
    getServerParams(formValues, haveFile) {
        const { mode, cluster } = this.props.location.state || {};
        const clusterConf = this.getClusterConf(formValues);
        const params = {
            clusterName: formValues.clusterName,
            clusterConf: JSON.stringify(clusterConf)
        };
        if (haveFile) {
            let file = this.state.file;
            if (file) {
                params.config = file.files[0]
            }
        }
        if (mode == "edit") {
            params.id = cluster.id
        }
        return params;
    }
    getClusterConf(formValues) {
        let { zipConfig } = this.state;
        zipConfig = JSON.parse(zipConfig);
        let clusterConf = {};
        const sparkExtParams = this.getCustomParams(formValues, "spark")
        const flinkExtParams = this.getCustomParams(formValues, "flink")
        clusterConf["hadoopConf"] = zipConfig.hadoopConf;
        clusterConf["yarnConf"] = zipConfig.yarnConf;
        clusterConf["hiveMeta"] = zipConfig.hiveMeta;
        clusterConf["hiveConf"] = formValues.hiveConf;
        clusterConf["sparkConf"] = { ...formValues.sparkConf, ...sparkExtParams };
        clusterConf["flinkConf"] = { ...formValues.flinkConf, ...flinkExtParams };
        //服务端兼容，不允许null
        clusterConf["hiveConf"].username = clusterConf["hiveConf"].username || '';
        clusterConf["hiveConf"].password = clusterConf["hiveConf"].password || '';
        return clusterConf;
    }
    getCustomParams(data, ParamKey) {
        let params = {};
        let tmpParam = {};
        for (let key in data) {
            //key的数据结构为flink%1532398855125918-name,flink%1532398855125918-value
            if (key.startsWith(ParamKey + "%")) {
                let tmpKeys = key.split("%")[1].split("-");
                let id = tmpKeys[0];//自定义参数的id
                let idParam = tmpKeys[1];
                if (!tmpParam[id]) {
                    tmpParam[id] = {};
                }
                tmpParam[id][idParam] = data[key];
            }
        }
        for (let key in tmpParam) {
            let item = tmpParam[key];
            params[item.name] = item.value;
        }
        return params;
    }
    render() {
        const { selectUser, file, zipConfig, uploadLoading, core, nodeNumber, memory, testLoading, fileHaveChange } = this.state;
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == "view";
        const isNew= !(mode=="view"||mode=="edit");
        const columns = this.initColumns();

        return (
            <div className="contentBox">
                <p className="box-title" style={{ height: "auto", paddingLeft: "20px" }}><GoBack size="default" type="textButton"></GoBack></p>
                <Card
                    noHovering
                    className="contentBox shadow">
                    <p className="config-title">集群信息</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            label="集群标识"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('clusterName', {
                                rules: [{
                                    required: true,
                                    message: "请输入集群标识"
                                }, {
                                    pattern: /^[a-z0-9_]{1,64}$/i,
                                    message: "集群标识不能超过64字符，支持英文、数字、下划线"
                                }]
                            })(
                                <Input disabled={!isNew} placeholder="请输入集群标识" style={{ width: "40%" }} />
                            )}
                            <span style={{ marginLeft: "30px" }}>节点数：{nodeNumber || '--'} </span>
                            <span style={{ marginLeft: "10px" }}>资源数：{core || '--'}VCore {this.exchangeMemory(memory)} </span>
                        </FormItem>
                        {/* <FormItem
                            label="绑定租户"
                            {...formItemLayout}
                        >
                            <Select
                                mode="combobox"
                                style={{ width: "150px" }}
                                placeholder="请选择租户"
                                onSelect={this.selectUser.bind(this)}
                                onSearch={this.changeUserValue.bind(this)}
                                value={selectUser}
                            >
                                {this.getUserOptions()}
                            </Select>
                        </FormItem> */}
                        {/* <Row>
                            <Col span={formItemLayout.labelCol.sm.span}></Col>
                            <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                <Table
                                    className="m-table"
                                    style={{ width: "300px" }}
                                    columns={columns}
                                    pagination={false}
                                    dataSource={this.getTableDataSource()}
                                    scroll={{ y: 200 }}
                                />
                            </Col>
                        </Row> */}
                    </div>
                    {isView ? null : (
                        <div>
                            <p className="config-title">上传配置文件</p>
                            <div className="config-content" style={{ width: "750px" }}>
                                <p style={{ marginBottom: "24px" }}>您需要获取Hadoop、Spark、Flink集群的配置文件，至少包括：<strong>core-site.xml、hdfs-site.xml、hive-site.xml、yarn-site.xml</strong>文件</p>

                                <FormItem
                                    label="配置文件"
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator('file', {
                                        rules: [{
                                            required: !fileHaveChange && mode == "edit" ? false : true, message: '请选择上传文件',
                                        }, {
                                            validator: this.validateFileType,
                                        }],
                                    })(
                                        <div>
                                            {
                                                uploadLoading ?
                                                    <label
                                                        style={{ lineHeight: '28px' }}
                                                        className="ant-btn disble"
                                                    >选择文件</label>
                                                    : <label
                                                        style={{ lineHeight: '28px' }}
                                                        className="ant-btn"
                                                        htmlFor="myOfflinFile">选择文件</label>
                                            }
                                            {uploadLoading ? <Icon className="blue-loading" type="loading" /> : null}
                                            <span> {file.files && file.files[0].name}</span>
                                            <input
                                                name="file"
                                                type="file"
                                                id="myOfflinFile"
                                                onChange={this.fileChange.bind(this)}
                                                accept=".zip"
                                                style={{ display: 'none' }}
                                            />
                                        </div>
                                    )}
                                    <span>支持扩展名：.zip</span>
                                </FormItem>
                                如何获取这些配置文件？请您参考<a>《帮助文档》</a>
                            </div>
                        </div>
                    )}
                    {
                        zipConfig ?
                            <div><p className="config-title">HDFS</p>
                                <div className="config-content" style={{ width: "800px" }}>
                                    {this.renderZipConfig("hdfs")}
                                </div>
                                <p className="config-title">YARN</p>
                                <div className="config-content" style={{ width: "800px" }}>
                                    {this.renderZipConfig("yarn")}
                                </div>
                            </div>
                            :
                            null
                    }
                    <p className="config-title">Hive JDBC信息</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            label="JDBC URL"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('hiveConf.jdbcUrl', {
                                rules: [{
                                    required: true,
                                    message: "请输入jdbcUrl"
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="用户名"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('hiveConf.username')(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="密码"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('hiveConf.password')(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                    </div>
                    <p className="config-title">Spark</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            label="版本选择"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('sparkConf.typeName', {
                                rules: [{
                                    required: true,
                                    message: "请选择Spark版本"
                                }],
                                initialValue: "spark_yarn"
                            })(
                                <Select disabled={isView} style={{ width: "100px" }}>
                                    <Option value="spark_yarn">2.X</Option>
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            label="sparkYarnArchive"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('sparkConf.sparkYarnArchive', {
                                rules: [{
                                    required: true,
                                    message: "请输入sparkYarnArchive"
                                }],
                                initialValue: "/sparkjars/jars"
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="sparkSqlProxyPath"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('sparkConf.sparkSqlProxyPath', {
                                rules: [{
                                    required: true,
                                    message: "请输入sparkSqlProxyPath"
                                }],
                                initialValue: "/user/spark/spark-0.0.1-SNAPSHOT.jar"
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="sparkPythonExtLibPath"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('sparkConf.sparkPythonExtLibPath', {
                                rules: [{
                                    required: true,
                                    message: "请输入sparkPythonExtLibPath"
                                }],
                                initialValue: "/pythons/pyspark.zip,hdfs://ns1/pythons/py4j-0.10.4-src.zip"
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        {this.renderExtraParam("spark")}
                        {isView ? null : (
                            <Row>
                                <Col span={formItemLayout.labelCol.sm.span}></Col>
                                <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                    <a onClick={this.addParam.bind(this, "spark")}>添加自定义参数</a>
                                </Col>
                            </Row>
                        )}
                    </div>
                    <p className="config-title">Flink</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            label="版本选择"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.typeName', {
                                rules: [{
                                    required: true,
                                    message: "请选择flink版本"
                                }],
                                initialValue: "flink140"
                            })(
                                <Select disabled={isView} style={{ width: "100px" }}>
                                    <Option value="flink140">1.4</Option>
                                    <Option value="flink150">1.5</Option>
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            label="clusterMode"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.clusterMode', {
                                rules: [{
                                    required: true,
                                    message: "请选择clusterMode"
                                }],
                                initialValue: "yarn"
                            })(
                                <Select disabled={isView} style={{ width: "100px" }}>
                                    <Option value="standalone">standalone</Option>
                                    <Option value="yarn">yarn</Option>
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            label="flinkZkAddress"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkZkAddress', {
                                rules: [{
                                    required: true,
                                    message: "请输入flinkZkAddress"
                                }],

                            })(
                                <Input disabled={isView} placeholder="hostname1:port,hostname2:port，多个地址用英文逗号隔开" />
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="flinkHighAvailabilityStorageDir">flinkHighAvailabilityStorageDir</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkHighAvailabilityStorageDir', {
                                rules: [{
                                    required: true,
                                    message: "请输入flinkHighAvailabilityStorageDir"
                                }]
                            })(
                                <Input disabled={isView} placeholder="Flink高可用存储地址，例如：/flink140/ha" />
                            )}
                        </FormItem>
                        <FormItem
                            label="flinkZkNamespace"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkZkNamespace', {
                                rules: [{
                                    required: true,
                                    message: "请输入flinkZkNamespace"
                                }]
                            })(
                                <Input disabled={isView} placeholder="Flink在Zookeeper的namespace，例如：/flink140" />
                            )}
                        </FormItem>
                        <FormItem
                            label="jarTmpDir"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.jarTmpDir', {
                                initialValue: "../tmp140"
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="flinkPluginRoot"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkPluginRoot', {
                                initialValue: "/opt/dtstack/flinkplugin"
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="remotePluginRootDir"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.remotePluginRootDir', {
                                initialValue: "/opt/dtstack/flinkplugin"
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        {this.renderExtraParam("flink")}
                        {isView ? null : (
                            <Row>
                                <Col span={formItemLayout.labelCol.sm.span}></Col>
                                <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                    <a onClick={this.addParam.bind(this, "flink")}>添加自定义参数</a>
                                </Col>
                            </Row>
                        )}
                    </div>
                    <p className="config-title"></p>
                    {isView ? null : (
                        <div className="config-content" style={{ width: "100%" }}>
                            <Button onClick={this.test.bind(this)} loading={testLoading} type="primary">测试连通性</Button>
                            <span style={{ marginLeft: "18px" }}>
                                {this.renderTestResult()}
                            </span>

                            <span style={{ float: "right", marginRight: "18px" }}>
                                <Button onClick={this.save.bind(this)} type="primary">保存</Button>
                                <Button onClick={hashHistory.goBack} style={{ marginLeft: "8px" }}>取消</Button>
                            </span>
                        </div>
                    )}
                </Card>
            </div>
        )
    }
}
export default Form.create()(EditCluster);