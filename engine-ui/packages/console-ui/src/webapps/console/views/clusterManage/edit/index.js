import React from "react";
import { Table, Form, Input, Row, Col, Select, Icon, Tooltip, Button, Tag, message, Card, Checkbox, Collapse } from "antd";
import { cloneDeep } from "lodash";
import { connect } from "react-redux"
import { hashHistory } from "react-router"

import { getUser } from "../../../actions/console"
import Api from "../../../api/console"
import { longLabelFormLayout, formItemLayout } from "../../../consts"
import GoBack from "main/components/go-back";
import ZipConfig from "./zipConfig";
import SparkConfig from "./sparkConfig"
import FlinkConfig from "./flinkConfig";
import { HiveConfig, CarbonDataConfig } from "./hiveAndCarbonData"
import utils from "utils";

const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;
const Panel = Collapse.Panel;
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
        // learning和dtyarnshell
        learning_params: [],
        dtyarnshell_params: [],
        core: null,
        nodeNumber: null,
        memory: null,
        extDefaultValue: {},
        fileHaveChange: false,
        checked: false,
        // 以下字段为填补关闭复选框数据无法获取输入数据情况
<<<<<<< HEAD
        gatewayHostValue: undefined,
        gatewayPortValue: undefined,
        gatewayJobNameValue: undefined,
        deleteOnShutdownOption: "FALSE",
        randomJobNameSuffixOption: "TRUE",
=======
        firstIptValue: undefined,
        secondIptValue: undefined,
        thirdIptValue: undefined,
        firstOption: "FALSE",
        secondOption: "TRUE",
>>>>>>> master
        flinkPrometheus: undefined, //配置Prometheus参数
        flinkData: undefined, //获取Prometheus参数
    }

    componentDidMount() {
        this.getDataList();
        // this.props.getTenantList();
    }

    // 填充表单数据
    getDataList() {
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
                            const flinkData = clusterConf.flinkConf;
                            const extParams = this.exchangeServerParams(clusterConf)
                            const flinkConf = clusterConf.flinkConf;
                            this.myUpperCase(flinkConf);
                            this.setState({
                                // checked: true,
                                core: cluster.totalCore,
                                memory: cluster.totalMemory,
                                nodeNumber: cluster.totalNode,
                                zipConfig: JSON.stringify({
                                    yarnConf: clusterConf.yarnConf,
                                    hadoopConf: clusterConf.hadoopConf,
                                    hiveMeta: clusterConf.hiveMeta
                                }),
                                flink_params: extParams.flinkKeys,
                                spark_params: extParams.sparkKeys,
                                learning_params: extParams.learningKeys,
                                dtyarnshell_params: extParams.dtyarnshellKeys,
                                extDefaultValue: extParams.default,
                                flinkPrometheus: clusterConf.flinkConf,
                                flinkData: flinkData
                            })
                            // 判断是有Prometheus参数
                            if (flinkData.hasOwnProperty('gatewayHost')) {
                                this.setState({
                                    checked: true
                                })
                            }
                            form.setFieldsValue({
                                clusterName: cluster.clusterName,
                                hiveConf: clusterConf.hiveConf,
                                carbonConf: clusterConf.carbonConf,
                                sparkConf: clusterConf.sparkConf,
                                flinkConf: clusterConf.flinkConf,
                                learningConf: this.myUpperCase(clusterConf.learningConf),
                                dtyarnshellConf: this.myUpperCase(clusterConf.dtyarnshellConf)
                            })
                        }
                    }
                )
        }
    }


    /**
     * 从服务端配置中抽取出自定义参数
     * @param {Map} config 服务端接收到的配置
     */
    exchangeServerParams(config) {
        let result = {
            flinkKeys: [],
            sparkKeys: [],
            learningKeys: [],
            dtyarnshellKeys: [],
            default: {}
        };
        let notExtKeys_flink = [
            "typeName", "flinkZkAddress",
            "flinkHighAvailabilityStorageDir",
            "flinkZkNamespace",
            "flinkYarnMode", "reporterClass",
            "gatewayHost", "gatewayPort",
            "gatewayJobName", "deleteOnShutdown",
            "randomJobNameSuffix", "jarTmpDir",
            "flinkPluginRoot", "remotePluginRootDir",
            "clusterMode", "flinkJarPath",
            "flinkJobHistory"
        ];
        let notExtKeys_spark = [
            "typeName", "sparkYarnArchive",
            "sparkSqlProxyPath", "sparkPythonExtLibPath"
        ];
        // let notExtKeys_learning = ["learningPython3Path", "learningPython2Path", 
        // "learningHistoryAddress", "learningHistoryWebappAddress", "learningHistoryWebappHttpsAddress"];
        // let notExtKeys_dtyarnshell = ["jlogstashRoot", "javaHome", "python2Path", "python3Path"]

        let notExtKeys_learning = [
            "typeName", "learning.python3.path",
            "learning.python2.path",
            "learning.history.address", "learning.history.webapp.address",
            "learning.history.webapp.https.address"
        ];
        let notExtKeys_dtyarnshell = [
            "typeName", "jlogstash.root",
            "java.home", "python2.path",
            "python3.path"
        ]

        let sparkConfig = config.sparkConf || {};
        let flinkConfig = config.flinkConf || {};
        let learningConfig = config.learningConf || {};
        let dtyarnshellConfig = config.dtyarnshellConf || {};
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
        setDefault(learningConfig, notExtKeys_learning, "learning", result.learningKeys)
        setDefault(dtyarnshellConfig, notExtKeys_dtyarnshell, "dtyarnshell", result.dtyarnshellKeys)
        return result;
    }
    // 表单字段. => 驼峰转化
    myUpperCase(obj) {
        var after = {},
            keys = [],
            values = [],
            newKeys = [];
        // . --> 驼峰
        for (let i in obj) {
            if (obj.hasOwnProperty(i)) {
                keys.push(i);
                values.push(obj[i]);
            }
        }
        keys.forEach(function (item, index) {
            var itemSplit = item.split('.');
            var newItem = itemSplit[0];
            for (let i = 1; i < itemSplit.length; i++) {
                var letters = itemSplit[i].split('');
                var firstLetter = letters.shift();
                firstLetter = firstLetter.toUpperCase();
                letters.unshift(firstLetter);
                newItem += letters.join("")
            }
            newKeys[index] = newItem;
        })
        for (let i = 0; i < values.length; i++) {
            after[newKeys[i]] = values[i]
        }
        //   console.log(after)
        return after;
    }

    // 驼峰 => .转化
    myLowerCase(obj) {
        var after = {},
            keys = [],
            newKeys = [],
            alphabet = 'QWERTYUIOPLKJHGFDSAZXCVBNM';
        for (let i in obj) {
            if (obj.hasOwnProperty(i)) {
                let keySplit = "";
                keySplit = i.split('');
                for (var j = 0; j < keySplit.length; j++) {
                    if (keySplit[j] == ".") {
                        keySplit.splice(j, 1);
                        keySplit[j] = keySplit[j].toUpperCase();
                    } else if (alphabet.indexOf(keySplit[j]) != -1) {
                        keySplit[j] = keySplit[j].toLowerCase();
                        keySplit.splice(j, 0, '.');
                        j++;
                    }
                }
                keySplit = keySplit.join('');
                after[keySplit] = obj[i];
            }
        }
        // console.log(after)
        return after;
    }


    getUserOptions() {
        const { consoleUser } = this.props;
        const { selectUserMap } = this.state;
        const userList = consoleUser.userList;
        const result = [];
        for (let i = 0; i < userList.length; i++) {
            const user = userList[i];
            if (!selectUserMap[user.tenantId]) {
                result.push(<Option key={user.tenantId + '$$' + user.tenantName} value={user.tenantId + '$$' + user.tenantName}>{user.tenantName}</Option>)
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
        const { flink_params, spark_params, learning_params, dtyarnshell_params } = this.state;
        if (type == "flink") {
            this.setState({
                flink_params: [...flink_params, {
                    id: giveMeAKey()
                }]
            })
        } else if (type == "spark") {
            this.setState({
                spark_params: [...spark_params, {
                    id: giveMeAKey()
                }]
            })
        } else if (type == "learning") {
            this.setState({
                learning_params: [...learning_params, {
                    id: giveMeAKey()
                }]
            })
        } else {
            this.setState({
                dtyarnshell_params: [...dtyarnshell_params, {
                    id: giveMeAKey()
                }]
            })
        }
    }
    deleteParam(id, type) {
        const { flink_params, spark_params, learning_params, dtyarnshell_params } = this.state;
        let tmpParams;
        let tmpStateName;
        if (type == "flink") {
            tmpStateName = "flink_params";
            tmpParams = flink_params;
        } else if (type == "spark") {
            tmpStateName = "spark_params";
            tmpParams = spark_params;
        } else if (type == "learning") {
            tmpStateName = "learning_params";
            tmpParams = learning_params;
        } else {
            tmpStateName = "dtyarnshell_params";
            tmpParams = dtyarnshell_params;
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
        const { flink_params, spark_params, learning_params, dtyarnshell_params, extDefaultValue } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == "view"
        let tmpParams;
        if (type == "flink") {
            tmpParams = flink_params;
        } else if (type == "spark") {
            tmpParams = spark_params;
        } else if (type == "learning") {
            tmpParams = learning_params;
        } else {
            tmpParams = dtyarnshell_params;
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
        // console.log(clusterConf);
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
        const learningExtParams = this.getCustomParams(formValues, "learning");
        const dtyarnshellExtParams = this.getCustomParams(formValues, "dtyarnshell")
        const learningTypeName = {
            typeName: "learning"
        }
        const dtyarnshellTypeName = {
            typeName: "dtyarnshell"
        }
        clusterConf["hadoopConf"] = zipConfig.hadoopConf;
        clusterConf["yarnConf"] = zipConfig.yarnConf;
        clusterConf["hiveMeta"] = zipConfig.hiveMeta;
        clusterConf["hiveConf"] = formValues.hiveConf;
        clusterConf["carbonConf"] = formValues.carbonConf;
        clusterConf["sparkConf"] = { ...formValues.sparkConf, ...sparkExtParams };
        clusterConf["flinkConf"] = { ...formValues.flinkConf, ...flinkExtParams };
        clusterConf["learningConf"] = { ...learningTypeName, ...this.myLowerCase(formValues.learningConf), ...learningExtParams };
        clusterConf["dtyarnshellConf"] = { ...dtyarnshellTypeName, ...this.myLowerCase(formValues.dtyarnshellConf), ...dtyarnshellExtParams };
        //服务端兼容，不允许null
        clusterConf["hiveConf"].username = clusterConf["hiveConf"].username || '';
        clusterConf["hiveConf"].password = clusterConf["hiveConf"].password || '';

        clusterConf["carbonConf"].username = clusterConf["carbonConf"].username || '';
        clusterConf["carbonConf"].password = clusterConf["carbonConf"].password || '';
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

    getPrometheusValue = () => {
        const { flinkPrometheus, flinkData } = this.state;
        const { form } = this.props;
        const { mode } = this.props.location.state || {};
        if (mode == "edit" && flinkData.hasOwnProperty('gatewayHost')) {
            form.setFieldsValue({
                'flinkConf.gatewayHost': flinkPrometheus.gatewayHost,
                'flinkConf.gatewayPort': flinkPrometheus.gatewayPort,
                'flinkConf.gatewayJobName': flinkPrometheus.gatewayJobName,
                'flinkConf.deleteOnShutdown': flinkPrometheus.deleteOnShutdown,
                'flinkConf.randomJobNameSuffix': flinkPrometheus.randomJobNameSuffix,
            });
        }
    }

    changeCheckbox(e) {
        this.setState({
            checked: e.target.checked
        }, () => {
            if (this.state.checked) {
                this.getPrometheusValue()
            }
        })
    }


    flinkYarnModes(flinkVersion) {
        const flinkYarnMode14 = ["PER_JOB", "LEGACY"];
        const flinkYarnMode15 = ["PER_JOB", "LEGACY", "NEW"];
        console.log(flinkVersion)  // finlk140
        if (flinkVersion == "flink140") {
            return flinkYarnMode14.map((item, index) => {
                return <Option key={item} value={item}>{item}</Option>
            })
        } else if (flinkVersion == "flink150") {
            return flinkYarnMode15.map((item, index) => {
                return <Option key={item} value={item}>{item}</Option>
            })
        }
    }

    // 获取每项Input的值
    getGatewayHostValue(e) {
        this.setState({
            gatewayHostValue: e.target.value
        })
    }
    getGatewayPortValue(e) {
        this.setState({
            gatewayPortValue: e.target.value
        })
    }
    getGatewayJobNameValue(e) {
        this.setState({
            gatewayJobNameValue: e.target.value
        })
    }
    changeDeleteOnShutdownOption(value) {
        this.setState({
            deleteOnShutdownOption: value
        })
    }
    changeRandomJobNameSuffixOption(value) {
        this.setState({
            randomJobNameSuffixOption: value
        })
    }

    render() {
        const { selectUser, file, zipConfig, uploadLoading, core, nodeNumber, memory, testLoading, fileHaveChange, checked } = this.state;
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == "view";
        const isNew = !(mode == "view" || mode == "edit");
        const columns = this.initColumns();
        // 获取flink版本
        const flinkVersion = getFieldValue("flinkConf.typeName") || "flink140";
        const { gatewayHostValue, gatewayPortValue, gatewayJobNameValue, deleteOnShutdownOption, randomJobNameSuffixOption } = this.state;
        // 获取flinkYarnMode
        const flinkYarnMode = getFieldValue("flinkConf.flinkYarnMode") || "PER_JOB"
        // const havedata = this.getFieldValue()
        return (
            <div className="contentBox">
                <p className="box-title" style={{ height: "auto", marginTop: "10px", paddingLeft: "20px" }}><GoBack size="default" type="textButton"></GoBack></p>
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
                            <span style={{ marginLeft: "20px" }}>节点数：{nodeNumber || '--'} </span>
                            <span style={{ marginLeft: "5px" }}>资源数：{core || '--'}VCore {this.exchangeMemory(memory)} </span>
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

                    <ZipConfig zipConfig={zipConfig} />
                    <p className="config-title">Hive JDBC信息</p>
                    <HiveConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                    />

                    <p className="config-title">CarbonData JDBC信息</p>
                    <CarbonDataConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                    />

                    <p className="config-title">Spark</p>
                    <SparkConfig
                        getFieldDecorator={getFieldDecorator}
                        isView={isView}
                        customView={(
                            <div>
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
                        )}
                    />
                    <p className="config-title">Flink</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FlinkConfig
                            isView={isView}
                            getFieldDecorator={getFieldDecorator}
                            checked={checked}
                            changeCheckbox={this.changeCheckbox.bind(this)}
                            gatewayHostValue={gatewayHostValue}
                            gatewayPortValue={gatewayPortValue}
                            gatewayJobNameValue={gatewayJobNameValue}
                            deleteOnShutdownOption={deleteOnShutdownOption}
                            randomJobNameSuffixOption={randomJobNameSuffixOption}
                            getGatewayHostValue={this.getGatewayHostValue.bind(this)}
                            getGatewayPortValue={this.getGatewayPortValue.bind(this)}
                            getGatewayJobNameValue={this.getGatewayJobNameValue.bind(this)}
                            changeDeleteOnShutdownOption={this.changeDeleteOnShutdownOption.bind(this)}
                            changeRandomJobNameSuffixOption={this.changeRandomJobNameSuffixOption.bind(this)}
                            customView={(
                                <div>
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
                            )}
                        />
                    </div>


                    <p className="config-title">Flink Engine</p>
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
                            label="flinkYarnMode"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkYarnMode', {
                                rules: [{
                                    required: true,
                                    message: "flinkYarnMode"
                                }],
                                initialValue: "PER_JOB"
                            })(
                                <Select disabled={isView} style={{ width: "100px" }}>
                                    {this.flinkYarnModes(flinkVersion)}
                                </Select>
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

                        {flinkYarnMode == "PER_JOB" ? <FormItem
                            label="flinkJarPath"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkJarPath', {
                                rules: [{
                                    required: true,
                                    message: "请输入flinkJarPath"
                                }],
                                // initialValue: "/opt/dtstack/flinkplugin"
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem> : null}

                        <FormItem
                            label="flinkJobHistory"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkJobHistory', {
                                rules: [{
                                    required: true,
                                    message: "请输入flinkJobHistory"
                                }],
                                // initialValue: "/opt/dtstack/flinkplugin"
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                    </div>


                    {/* Learning */}
                    <p className="config-title">Learning</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            label="learning.python3.path"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('learningConf.learningPython3Path', {
                                // rules: [{
                                //     message: "请输入learning.python3.path"
                                // }],
                                // initialValue: "/root/anaconda3/bin/python3"
                            })(
                                <Input disabled={isView} placeholder="/root/anaconda3/bin/python3" />
                            )}
                        </FormItem>
                        <FormItem
                            label="learning.python2.path"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('learningConf.learningPython2Path', {
                                // rules: [{
                                //     message: "请输入learning.python2.path"
                                // }],
                                // initialValue: "/root/anaconda2/bin/python2"
                            })(
                                <Input disabled={isView} placeholder="/root/anaconda2/bin/python2" />
                            )}
                        </FormItem>
                        <FormItem
                            label="learning.history.address"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('learningConf.learningHistoryAddress', {
                                // rules: [{
                                //     message: "请输入learning.history.address"
                                // }],
                                // initialValue: "rdos1:10021"
                            })(
                                <Input disabled={isView} placeholder="rdos1:10021" />
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="learning.history.webapp.address">learning.history.webapp.address</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('learningConf.learningHistoryWebappAddress', {
                                // rules: [{
                                //     message: "请输入learning.history.webapp.address"
                                // }],
                                // initialValue: "rdos1:19886"
                            })(
                                <Input disabled={isView} placeholder="rdos1:19886" />
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="learning.history.webapp.https.address">learning.history.webapp.https.address</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('learningConf.learningHistoryWebappHttpsAddress', {
                                // rules: [{
                                //     message: "请输入learning.history.webapp.https.address"
                                // }],
                                // initialValue: "rdos1:19885"
                            })(
                                <Input disabled={isView} placeholder="rdos1:19885" />
                            )}
                        </FormItem>
                        {this.renderExtraParam("learning")}
                        {isView ? null : (
                            <Row>
                                <Col span={formItemLayout.labelCol.sm.span}></Col>
                                <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                    <a onClick={this.addParam.bind(this, "learning")}>添加自定义参数</a>
                                </Col>
                            </Row>
                        )}
                    </div>


                    {/* DTYarnShell */}
                    <p className="config-title">DTYarnShell</p>
                    <div className="config-content" style={{ width: "680px" }}>
                        <FormItem
                            label="jlogstash.root"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('dtyarnshellConf.jlogstashRoot', {
                                rules: [{
                                    required: true,
                                    message: "请输入jlogstash.root"
                                }],
                                // initialValue: "/opt/dtstack/jlogstash"
                            })(
                                <Input disabled={isView} placeholder="/opt/dtstack/jlogstash" />
                            )}
                        </FormItem>
                        <FormItem
                            label="java.home"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('dtyarnshellConf.javaHome', {
                                rules: [{
                                    required: true,
                                    message: "请输入java.home"
                                }],
                                // initialValue: "/opt/java/bin"
                            })(
                                <Input disabled={isView} placeholder="/opt/java/bin" />
                            )}
                        </FormItem>
                        <FormItem
                            label="python2.path"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('dtyarnshellConf.python2Path', {
                                // rules: [{
                                //     message: "请输入python2.path"
                                // }],
                                // initialValue: "/root/anaconda3/bin/python3"
                            })(
                                <Input disabled={isView} placeholder="/root/anaconda3/bin/python2" />
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="python3.path">python3.path</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('dtyarnshellConf.python3Path', {
                                // rules: [{
                                //     message: "请输入python3.path"
                                // }],
                                // initialValue: "/root/anaconda3/bin/python3"
                            })(
                                <Input disabled={isView} placeholder="/root/anaconda3/bin/python3" />
                            )}
                        </FormItem>
                        {this.renderExtraParam("dtyarnshell")}
                        {isView ? null : (
                            <Row>
                                <Col span={formItemLayout.labelCol.sm.span}></Col>
                                <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                    <a onClick={this.addParam.bind(this, "dtyarnshell")}>添加自定义参数</a>
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