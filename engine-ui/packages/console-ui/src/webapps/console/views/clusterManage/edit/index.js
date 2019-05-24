/* eslint-disable */
import React from 'react';
import utils from 'utils';
import { Form, Input, Row, Col, Select, Icon, Tooltip, Button, Tag, message, Card, Radio, Tabs, Modal } from 'antd';
import { cloneDeep } from 'lodash';
import { connect } from 'react-redux'
import { hashHistory } from 'react-router'

import { getUser, updateEngineList } from '../../../actions/console'
import Api from '../../../api/console'
import { engineTypeConfig, validateEngine } from '../../../consts/clusterFunc';
import { formItemLayout, ENGINE_TYPES, validateFlinkParams, validateHiveParams,
    validateCarbonDataParams, validateSparkParams, validateDtYarnShellParams, validateLearningParams } from '../../../consts'
import GoBack from 'main/components/go-back';
import ZipConfig from './zipConfig';
import SparkConfig from './sparkConfig'
import FlinkConfig from './flinkConfig';
import { HiveConfig, CarbonDataConfig } from './hiveAndCarbonData'
import AddEngineModal from '../addEngineModal';
const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const TabPane = Tabs.TabPane;
const confirm = Modal.confirm;
const TEST_STATUS = {
    NOTHING: 0,
    SUCCESS: 1,
    FAIL: 2
}
const formItemLayout1 = { // 表单常用布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 2 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
};
/* eslint-disable */
function giveMeAKey () {
    return (new Date().getTime() + '' + ~~(Math.random() * 100000))
}
function mapStateToProps (state) {
    return {
        consoleUser: state.consoleUser
    }
}
function mapDispatchToProps (dispatch) {
    return {
        getTenantList () {
            dispatch(getUser())
        },
        updateEngineList (params) {
            dispatch(updateEngineList(params))
        }
    }
}
@connect(mapStateToProps, mapDispatchToProps)
class EditCluster extends React.Component {
    state = {
        selectUserMap: {},
        selectUser: '', // select输入value
        file: '', // 上传的文件
        zipConfig: '', // 解析的配置文件信息
        securityStatus: false, // 根据配置文件是否显示spark， flink等其他参数
        uploadLoading: false, // 上传loading
        // testLoading: false,
        testStatus: TEST_STATUS.NOTHING,
        flink_params: [],
        spark_params: [],
        hive_params: [],
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
        gatewayHostValue: undefined,
        gatewayPortValue: undefined,
        gatewayJobNameValue: undefined,
        deleteOnShutdownOption: 'FALSE',
        randomJobNameSuffixOption: 'TRUE',
        flinkPrometheus: undefined, // 配置Prometheus参数
        flinkData: undefined, // 获取Prometheus参数
        addEngineVisible: false, // 新增引擎modal
        editModalKey: '',
        clusterType: '', // 集群类型 apache_hadoop,cloudera,huawei
        // 引擎testLoading
        flinkTestLoading: false,
        sparkTestLoading: false,
        dtYarnShellTestLoading: false,
        learningTestLoading: false,
        hdfsTestLoading: false,
        yarnTestLoading: false,
        hiveTestLoading: false,
        carbonTestLoading: false,
        libraTestLoading: false,
        allTestLoading: false,
        // 引擎testStatus
        flinkTestStatus: TEST_STATUS.NOTHING,
        sparkTestStatus: TEST_STATUS.NOTHING,
        dtYarnShellTestStatus: TEST_STATUS.NOTHING,
        learningTestStatus: TEST_STATUS.NOTHING,
        hdfsTestStatus: TEST_STATUS.NOTHING,
        yarnTestStatus: TEST_STATUS.NOTHING,
        hiveTestStatus: TEST_STATUS.NOTHING,
        carbonTestStatus: TEST_STATUS.NOTHING,
        libraTestStatus: TEST_STATUS.NOTHING,
        // 控制引擎必填项为全部填写时 出现红色*
        flinkShowRequired: false,
        sparkShowRequired: false,
        dtYarnShellShowRequired: false,
        learningShowRequired: false,
        hdfsShowRequired: false,
        yarnShowRequired: false,
        hiveShowRequired: false,
        carbonShowRequired: false,
        libraShowRequired: false
    }

    componentDidMount () {
        this.getDataList();
        // this.props.getTenantList();
        this.refreshSaveEngine();
    }
    // 新增入口刷新无数据
    refreshSaveEngine () {
        const { mode, enginelist } = this.props.location.state;
        if (mode === 'new') {
            this.props.updateEngineList(enginelist)
        }
    }
    // 填充表单数据
    getDataList () {
        const { location, form } = this.props;
        const params = location.state || {};
        if (params.mode == 'edit' || params.mode == 'view') {
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
                            const keyMap = {
                                'spark.yarn.appMasterEnv.PYSPARK_PYTHON': 'sparkYarnAppMasterEnvPYSPARK_PYTHON',
                                'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON': 'sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON'
                            }
                            const yarnShellkeyMap = {
                                'jlogstash.root': 'jlogstashRoot',
                                'java.home': 'javaHome',
                                'hadoop.home.dir': 'hadoopHomeDir',
                                'python2.path': 'python2Path',
                                'python3.path': 'python3Path'
                            }
                            this.myUpperCase(flinkConf);
                            this.setState({
                                // checked: true,
                                securityStatus: cluster.security,
                                core: cluster.totalCore,
                                memory: cluster.totalMemory,
                                nodeNumber: cluster.totalNode,
                                clusterType: cluster.clusterType,
                                zipConfig: JSON.stringify({
                                    yarnConf: clusterConf.yarnConf,
                                    hadoopConf: clusterConf.hadoopConf,
                                    hiveMeta: clusterConf.hiveMeta
                                }),
                                flink_params: extParams.flinkKeys,
                                spark_params: extParams.sparkKeys,
                                hive_params: extParams.hiveKeys,
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
                                sparkConf: this.toChsKeys(clusterConf.sparkConf, keyMap),
                                flinkConf: clusterConf.flinkConf,
                                learningConf: this.myUpperCase(clusterConf.learningConf),
                                dtyarnshellConf: this.toChsKeys(clusterConf.dtyarnshellConf, yarnShellkeyMap)
                            })
                        }
                        // 暂时先派发数据测试(getOne接口成功dispatch)
                        this.props.updateEngineList([
                            'Flink',
                            'Spark',
                            'DTYarnShell',
                            'Learning',
                            'HDFS',
                            'YARN',
                            'Spark Thrift Server',
                            'CarbonData',
                            'Libra'
                        ])
                    }
                )
        } else { // 新增集群填充集群名称、节点数、资源数
            this.setState({
                core: params.totalCore,
                memory: params.totalMemory,
                nodeNumber: params.totalNode,
                zipConfig: params.clusterConfig // 新增默认配置hdfs和yarn
            })
            form.setFieldsValue({
                clusterName: params.clusterName
            })
            
        }
    }

    /**
     * 从服务端配置中抽取出自定义参数
     * @param {Map} config 服务端接收到的配置
     */
    exchangeServerParams (config) {
        let result = {
            flinkKeys: [],
            sparkKeys: [],
            hiveKeys: [],
            learningKeys: [],
            dtyarnshellKeys: [],
            default: {}
        };
        let notExtKeys_flink = [
            'typeName', 'flinkZkAddress',
            'flinkHighAvailabilityStorageDir',
            'flinkZkNamespace', 'reporterClass',
            'gatewayHost', 'gatewayPort',
            'gatewayJobName', 'deleteOnShutdown',
            'randomJobNameSuffix', 'jarTmpDir',
            'flinkPluginRoot', 'remotePluginRootDir',
            'clusterMode', 'flinkJarPath',
            'flinkJobHistory', 'flinkPrincipal', 'flinkKeytabPath', 'flinkKrb5ConfPath',
            'zkPrincipal', 'zkKeytabPath', 'zkLoginName'
        ];
        let notExtKeys_spark = [
            'typeName', 'sparkYarnArchive',
            'sparkSqlProxyPath', 'sparkPythonExtLibPath', 'spark.yarn.appMasterEnv.PYSPARK_PYTHON',
            'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON', 'sparkPrincipal', 'sparkKeytabPath',
            'sparkKrb5ConfPath', 'zkPrincipal', 'zkKeytabPath', 'zkLoginName'
        ];
        // let notExtKeys_learning = ["learningPython3Path", "learningPython2Path",
        // "learningHistoryAddress", "learningHistoryWebappAddress", "learningHistoryWebappHttpsAddress"];
        // let notExtKeys_dtyarnshell = ["jlogstashRoot", "javaHome", "python2Path", "python3Path"]

        let notExtKeys_learning = [
            'typeName', 'learning.python3.path',
            'learning.python2.path',
            'learning.history.address', 'learning.history.webapp.address',
            'learning.history.webapp.https.address'
        ];
        let notExtKeys_dtyarnshell = [
            'typeName', 'jlogstash.root',
            'java.home', 'hadoop.home.dir', 'python2.path',
            'python3.path', 'hdfsPrincipal', 'hdfsKeytabPath', 'hdfsKrb5ConfPath'
        ]
        let notExtKeys_hive = [
            'jdbcUrl', 'username', 'password'
        ]
        let sparkConfig = config.sparkConf || {};
        let flinkConfig = config.flinkConf || {};
        let hiveConfig = config.hiveConf || {};
        let learningConfig = config.learningConf || {};
        let dtyarnshellConfig = config.dtyarnshellConf || {};
        function setDefault (config, notExtKeys, type, keys) {
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

        setDefault(sparkConfig, notExtKeys_spark, 'spark', result.sparkKeys)
        setDefault(flinkConfig, notExtKeys_flink, 'flink', result.flinkKeys)
        setDefault(hiveConfig, notExtKeys_hive, 'hive', result.hiveKeys)
        setDefault(learningConfig, notExtKeys_learning, 'learning', result.learningKeys)
        setDefault(dtyarnshellConfig, notExtKeys_dtyarnshell, 'dtyarnshell', result.dtyarnshellKeys)
        return result;
    }
    // 表单字段. => 驼峰转化
    myUpperCase (obj) {
        var after = {};

        var keys = [];

        var values = [];

        var newKeys = [];
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
                newItem += letters.join('')
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
    myLowerCase (obj) {
        var after = {};

        var keys = [];

        var newKeys = [];

        var alphabet = 'QWERTYUIOPLKJHGFDSAZXCVBNM';
        for (let i in obj) {
            if (obj.hasOwnProperty(i)) {
                let keySplit = '';
                keySplit = i.split('');
                for (var j = 0; j < keySplit.length; j++) {
                    if (keySplit[j] == '.') {
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

    /**
     * PYspark两字段需转化(spark.yarn.appMasterEnv.PYSPARK_PYTHON,
     * spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON)
     * @param obj 传入对象
     * @param keyMap key映射关系
     */
    toChsKeys (obj, keyMap) {
        return Object.keys(obj).reduce((newObj, key) => {
            let newKey = keyMap[key] || key;
            newObj[newKey] = obj[key];
            return newObj
        }, {})
    }
    getUserOptions () {
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
    changeUserValue (value) {
        this.setState({
            selectUser: value
        })
    }
    selectUser (value, option) {
        const { selectUserMap } = this.state;
        this.setState({
            selectUser: '',
            selectUserMap: {
                ...selectUserMap,
                [value.split('$$')[0]]: {
                    name: option.props.children
                }
            }
        })
    }
    getTableDataSource () {
        const { selectUserMap } = this.state;
        const keyAndValue = Object.entries(selectUserMap);
        return keyAndValue.map((item) => {
            return {
                id: item[0],
                name: item[1].name
            }
        })
    }
    removeUser (id) {
        let { selectUserMap } = this.state;
        selectUserMap = cloneDeep(selectUserMap);
        delete selectUserMap[id];
        this.setState({
            selectUserMap: selectUserMap
        })
    }
    initColumns () {
        return [
            {
                title: '租户名称',
                dataIndex: 'name',
                width: '150px'
            },
            {
                title: '操作',
                dataIndex: 'deal',
                render: (text, record) => {
                    return (<a onClick={this.removeUser.bind(this, record.id)}>删除</a>)
                }
            }
        ]
    }
    validateFileType (rule, value, callback) {
        const reg = /\.(zip)$/

        if (value && !reg.test(value.toLocaleLowerCase())) {
            callback('配置文件只能是zip文件!');
        }
        callback();
    }
    fileChange (e) {
        const { clusterId } = this.props.location.state || {};
        const file = e.target;
        this.setState({ file: {}, uploadLoading: true, zipConfig: '', fileHaveChange: true });
        Api.uploadClusterResource({
            resources: file.files[0],
            clusterId,
            useDefaultConfig: false
        })
            .then(
                (res) => {
                    if (res.code == 1) {
                        this.setState({
                            uploadLoading: false,
                            file: file,
                            securityStatus: res.data.security,
                            zipConfig: res.data.config
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
    renderTestResult (engineType) {
        const { flinkTestStatus,
                hiveTestStatus,
                carbonTestStatus,
                sparkTestStatus,
                dtYarnShellTestStatus,
                learningTestStatus,
                hdfsTestStatus,
                yarnTestStatus,
                libraTestStatus } = this.state;
        switch (engineType) {
            case ENGINE_TYPES.FLINK: {
                switch (flinkTestStatus) {
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
            case ENGINE_TYPES.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                switch (hiveTestStatus) {
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
            case ENGINE_TYPES.CARBONDATA: {
                switch (carbonTestStatus) {
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
            case ENGINE_TYPES.SPARK: {
                switch (sparkTestStatus) {
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
            case ENGINE_TYPES.DTYARNSHELL: {
                switch (dtYarnShellTestStatus) {
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
            case ENGINE_TYPES.LEARNING: {
                switch (learningTestStatus) {
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
            case ENGINE_TYPES.HDFS: {
                switch (hdfsTestStatus) {
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
            case ENGINE_TYPES.YARN: {
                switch (yarnTestStatus) {
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
            case ENGINE_TYPES.LIBRA: {
                switch (libraTestStatus) {
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
            case null : {
                return null
            }
            default: {
                return null
            }
        }
    }
    // 控制engine前是否出现为填小图标 *
    showRequireIcon = (engineType, isShow) => {
        let isShowRequiredIcon = {}
        switch (engineType) {
            case ENGINE_TYPES.FLINK: {
                return isShowRequiredIcon = {
                    flinkShowRequired: isShow
                }
            }
            case ENGINE_TYPES.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                return isShowRequiredIcon = {
                    hiveShowRequired: isShow
                }
            }
            case ENGINE_TYPES.CARBONDATA: {
                return isShowRequiredIcon = {
                    carbonShowRequired: isShow
                }
            }
            case ENGINE_TYPES.SPARK: {
                return isShowRequiredIcon = {
                    sparkShowRequired: isShow
                }
            }
            case ENGINE_TYPES.DTYARNSHELL: {
                return isShowRequiredIcon = {
                    dtYarnShellShowRequired: isShow
                }
            }
            case ENGINE_TYPES.LEARNING: {
                return isShowRequiredIcon = {
                    learningShowRequired: isShow
                }
            }
            case ENGINE_TYPES.HDFS: {
                return isShowRequiredIcon = {
                    hdfsShowRequired: isShow
                }
            }
            case ENGINE_TYPES.YARN: {
                return isShowRequiredIcon = {
                    yarnShowRequired: isShow
                }
            }
            case ENGINE_TYPES.LIBRA: {
                return isShowRequiredIcon = {
                    libraShowRequired: isShow
                }
            }
            //  全部校验
            case null : {
                return this.validateAllRequired()
            }
        }
    }
    // 全部校验表单是否必填
    validateAllRequired = () => {
        const { mode } = this.props.location.state || {};
        const { engineList } = this.props.consoleUser;
        const isNew = mode == 'new';
        // const engines = !isNew ? this.state.engineSelectedLists : engineLists;
        let obj = {}
        engineList && engineList.map(item => {
            this.props.form.validateFields(validateEngine(item), {}, (err, values) => {
                if (item === ENGINE_TYPES.FLINK) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            flinkShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            flinkShowRequired: true
                        })
                    }
                } else if (item === ENGINE_TYPES.SPARKTHRIFTSERVER) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            hiveShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            hiveShowRequired: true
                        })
                    }
                } else if (item === ENGINE_TYPES.CARBONDATA) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            carbonShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            carbonShowRequired: true
                        })
                    }
                } else if (item === ENGINE_TYPES.SPARK) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            sparkShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            sparkShowRequired: true
                        })
                    }
                } else if (item === ENGINE_TYPES.DTYARNSHELL) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            dtYarnShellShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            dtYarnShellShowRequired: true
                        })
                    }
                } else if (item === ENGINE_TYPES.LEARNING) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            learningShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            learningShowRequired: true
                        })
                    }
                } else if (item === ENGINE_TYPES.HDFS) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            hdfsShowRequired : false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            hdfsShowRequired: true
                        })
                    }
                } else if (item === ENGINE_TYPES.YARN) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            yarnShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            yarnShowRequired: true
                        })
                    }
                } else if (item === ENGINE_TYPES.LIBRA) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            libraShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            libraShowRequired: true
                        })
                    }
                } else {
                    console.log('error')
                }
            })
        })
        return obj
    }
    renderRequiredIcon = (engineType) => {
        const { flinkShowRequired,
            hiveShowRequired,
            carbonShowRequired,
            sparkShowRequired,
            dtYarnShellShowRequired,
            learningShowRequired,
            hdfsShowRequired,
            yarnShowRequired,
            libraShowRequired } = this.state;
    switch (engineType) {
        case ENGINE_TYPES.FLINK: {
            if (flinkShowRequired) {
                return <span className='icon_required'>*</span>
            }
            return null
        }
        case ENGINE_TYPES.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
            if (hiveShowRequired) {
                return <span className='icon_required'>*</span>
            }
            return null
        }
        case ENGINE_TYPES.CARBONDATA: {
            if (carbonShowRequired) {
                return <span className='icon_required'>*</span>
            }
        }
        case ENGINE_TYPES.SPARK: {
            if (sparkShowRequired) {
                return <span className='icon_required'>*</span>
            }
            return null
        }
        case ENGINE_TYPES.DTYARNSHELL: {
            if (dtYarnShellShowRequired) {
                return <span className='icon_required'>*</span>
            }
            return null
        }
        case ENGINE_TYPES.LEARNING: {
            if (learningShowRequired) {
                return <span className='icon_required'>*</span>
            }
            return null
        }
        case ENGINE_TYPES.HDFS: {
            if (hdfsShowRequired) {
                return <span className='icon_required'>*</span>
            }
            return null
        }
        case ENGINE_TYPES.YARN: {
            if (yarnShowRequired) {
                return <span className='icon_required'>*</span>
            }
            return null
        }
        case ENGINE_TYPES.LIBRA: {
            if (libraShowRequired) {
                return <span className='icon_required'>*</span>
            }
            return null
        }
        case null : {
            return null
        }
        default: {
            return null
        }
    }
    }
    // 控制engine测试结果图标
    renderTestIcon = (engineType) => {
        const { flinkTestStatus,
            hiveTestStatus,
            carbonTestStatus,
            sparkTestStatus,
            dtYarnShellTestStatus,
            learningTestStatus,
            hdfsTestStatus,
            yarnTestStatus,
            libraTestStatus } = this.state;
        switch (engineType) {
            case ENGINE_TYPES.FLINK: {
                switch (flinkTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case ENGINE_TYPES.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                switch (hiveTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case ENGINE_TYPES.CARBONDATA: {
                switch (carbonTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case ENGINE_TYPES.SPARK: {
                switch (sparkTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case ENGINE_TYPES.DTYARNSHELL: {
                switch (dtYarnShellTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case ENGINE_TYPES.LEARNING: {
                switch (learningTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case ENGINE_TYPES.HDFS: {
                switch (hdfsTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case ENGINE_TYPES.YARN: {
                switch (yarnTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case ENGINE_TYPES.LIBRA: {
                switch (libraTestStatus) {
                    case TEST_STATUS.NOTHING: {
                        return null
                    }
                    case TEST_STATUS.SUCCESS: {
                        return <Icon className='success-icon' type="check-circle" />
                    }
                    case TEST_STATUS.FAIL: {
                        return <Icon className='err-icon' type="close-circle" />
                    }
                }
            }
            case null : {
                return null
            }
            default: {
                return null
            }
        }
    }
    addParam (type) {
        const { flink_params, spark_params, hive_params, learning_params, dtyarnshell_params } = this.state;
        if (type == 'flink') {
            this.setState({
                flink_params: [...flink_params, {
                    id: giveMeAKey()
                }]
            })
        } else if (type == 'spark') {
            this.setState({
                spark_params: [...spark_params, {
                    id: giveMeAKey()
                }]
            })
        } else if (type == 'hive') {
            this.setState({
                hive_params: [...hive_params, {
                    id: giveMeAKey()
                }]
            })
        } else if (type == 'learning') {
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
    deleteParam (id, type) {
        const { flink_params, spark_params, hive_params, learning_params, dtyarnshell_params } = this.state;
        let tmpParams;
        let tmpStateName;
        if (type == 'flink') {
            tmpStateName = 'flink_params';
            tmpParams = flink_params;
        } else if (type == 'spark') {
            tmpStateName = 'spark_params';
            tmpParams = spark_params;
        } else if (type == 'hive') {
            tmpStateName = 'hive_params';
            tmpParams = hive_params;
        } else if (type == 'learning') {
            tmpStateName = 'learning_params';
            tmpParams = learning_params;
        } else {
            tmpStateName = 'dtyarnshell_params';
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
    renderExtraParam (type) {
        const { flink_params, spark_params, hive_params, learning_params, dtyarnshell_params, extDefaultValue } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view'
        let tmpParams;
        if (type == 'flink') {
            tmpParams = flink_params;
        } else if (type == 'spark') {
            tmpParams = spark_params;
        } else if (type == 'learning') {
            tmpParams = learning_params;
        } else if (type == 'hive') {
            tmpParams = hive_params
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
                                    message: '请输入参数属性名'
                                }],
                                initialValue: extDefaultValue[param.id] && extDefaultValue[param.id].name
                            })(
                                <Input disabled={isView} style={{ width: 'calc(100% - 12px)' }} />
                            )}
                            :
                        </FormItem>
                    </Col>
                    <Col span={formItemLayout.wrapperCol.sm.span}>
                        <FormItem key={param.id + '-value'}>
                            {getFieldDecorator(type + '%' + param.id + '-value', {
                                rules: [{
                                    required: true,
                                    message: '请输入参数属性值'
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
    exchangeMemory (totalMemory) {
        if (!totalMemory) {
            return '--';
        }
        const memory = totalMemory / 1024;
        const haveDot = Math.floor(memory) != memory
        return `${haveDot ? memory.toFixed(2) : memory}GB`
    }
    loadingStatus = (engineType, isShowLoading) => {
        switch (engineType) {
            case ENGINE_TYPES.FLINK: {
                return this.setState({
                    flinkTestLoading: isShowLoading
                })
            }
            case ENGINE_TYPES.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                return this.setState({
                    hiveTestLoading: isShowLoading
                })
            }
            case ENGINE_TYPES.CARBONDATA: {
                return this.setState({
                    carbonTestLoading: isShowLoading
                })
            }
            case ENGINE_TYPES.SPARK: {
                return this.setState({
                    sparkTestLoading: isShowLoading
                })
            }
            case ENGINE_TYPES.DTYARNSHELL: {
                return this.setState({
                    dtYarnShellTestLoading: isShowLoading
                })
            }
            case ENGINE_TYPES.LEARNING: {
                return this.setState({
                    learningTestLoading: isShowLoading
                })
            }
            case ENGINE_TYPES.HDFS: {
                return this.setState({
                    hdfsTestLoading: isShowLoading
                })
            }
            case ENGINE_TYPES.YARN: {
                return this.setState({
                    yarnTestLoading: isShowLoading
                })
            }
            case ENGINE_TYPES.LIBRA: {
                return this.setState({
                    libraTestLoading: isShowLoading
                })
            }
            case null : {
                this.setState({
                    flinkTestLoading: isShowLoading,
                    hiveTestLoading: isShowLoading,
                    carbonTestLoading: isShowLoading,
                    sparkTestLoading: isShowLoading,
                    dtYarnShellTestLoading: isShowLoading,
                    learningTestLoading: isShowLoading,
                    hdfsTestLoading: isShowLoading,
                    yarnTestLoading: isShowLoading,
                    libraTestLoading: isShowLoading,
                    allTestLoading: isShowLoading
                })
            }
        }
    }
    
    renderTestStatus = (engineType, isSuccess) =>{
        let testStatus = {}
        switch (engineType) {
            case ENGINE_TYPES.FLINK: {
                return testStatus = {
                    flinkTestStatus: isSuccess
                }
            }
            case ENGINE_TYPES.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                return testStatus = {
                    hiveTestStatus: isSuccess
                }
            }
            case ENGINE_TYPES.CARBONDATA: {
                return testStatus = {
                    carbonTestStatus: isSuccess
                }
            }
            case ENGINE_TYPES.SPARK: {
                return testStatus = {
                    sparkTestStatus: isSuccess
                }
            }
            case ENGINE_TYPES.DTYARNSHELL: {
                return testStatus = {
                    dtYarnShellTestStatus: isSuccess
                }
            }
            case ENGINE_TYPES.LEARNING: {
                return testStatus = {
                    learningTestStatus: isSuccess
                }
            }
            case ENGINE_TYPES.HDFS: {
                return testStatus = {
                    hdfsTestStatus: isSuccess
                }
            }
            case ENGINE_TYPES.YARN: {
                return testStatus = {
                    yarnTestStatus: isSuccess
                }
            }
            case ENGINE_TYPES.LIBRA: {
                return testStatus = {
                    libraTestStatus: isSuccess
                }
            }
            case null : {
                return testStatus = {
                    flinkTestStatus: isSuccess,
                    hiveTestStatus: isSuccess,
                    carbonTestStatus: isSuccess,
                    sparkTestStatus: isSuccess,
                    dtYarnShellTestStatus: isSuccess,
                    learningTestStatus: isSuccess,
                    hdfsTestStatus: isSuccess,
                    yarnTestStatus: isSuccess,
                    libraTestStatus: isSuccess
                }
            }
        }
    }
    // 全部保存
    saveOrUpdateCluster (engineType) {
        const { mode } = this.props.location.state || {};
        this.props.form.validateFields (null, {}, (err, values) => {
            if (!err) {
                // let functionName = 'createCluster'
                // if (mode == 'edit') {
                //     functionName = 'updateCluster'
                // }
                // Api[functionName](this.getServerParams(values, engineType, true))
                //     .then(
                //         (res) => {
                //             if (res.code == 1) {
                //                 message.success('保存成功')
                //                 this.props.router.goBack();
                //             }
                //         }
                //     )
                Api.saveOrUpdateCluster(this.getServerParams(values, engineType, true)).then(res => {
                    if (res.code ===1) {
                        message.success('保存成功')
                        this.props.router.goBack();
                    }
                })
            }
        })
    }
    /**
     * 保存单引擎
     */
    saveSingleEngine (engineType) {
        const { clusterId, mode, cluster } = this.props.location.state || {};
        const isNew = mode === 'new' // clusterId
        this.props.form.validateFields(validateEngine(engineType), {}, (err, values) => {
            if (!err) {
                const clusterConf = this.getClusterConf(values);
                Api.saveOrAddEngine({
                    clusterId: !isNew ? cluster.id : clusterId,
                    engineType,
                    engineConfig: JSON.stringify(clusterConf[engineTypeConfig(engineType)])
                }).then(res => {
                    if (res.code ===1) {
                        message.success(`${engineType}保存成功`)
                    }
                })
            }
        })
    }
    // add engine
    addEngine (engineType, callback) {
        callback()
        const { clusterId, mode, cluster } = this.props.location.state || {};
        const isNew = mode === 'new' // clusterId
        let engineList = this.props.consoleUser.engineList;
        const newEngineList = engineList.concat(engineType)
        if (callback()) {
            Api.saveOrAddEngine({
                clusterId: !isNew ? cluster.id : clusterId,
                engineType,
                engineConfig: ''
            }).then(res => {
                if (res.code ===1) {
                    // 添加dispatch updateEngineList
                    this.props.updateEngineList(newEngineList)
                    this.onCancel()
                    message.success('添加引擎成功!')
                }
            })
        }
    }
    /**
     * 测试连通性
     * @param engineType 引擎类型 为null则全部测试
     */
    test (engineType) {
        this.props.form.validateFields (validateEngine(engineType), {}, (err, values) => {
            if (!err) {
                this.setState({
                    ...this.showRequireIcon(engineType, false) // 不出现红标
                })
                this.loadingStatus(engineType, true)
                Api.testCluster(this.getServerParams(values, engineType))
                    .then(
                        (res) => {
                            if (res.code == 1) {
                                this.setState({
                                    nodeNumber: res.data.totalNode,
                                    core: res.data.totalCores,
                                    memory: res.data.totalMemory,
                                    ...this.renderTestStatus(engineType, TEST_STATUS.SUCCESS),
                                    ...this.loadingStatus(engineType, false)
                                })
                                message.success('连通成功')
                            } else {
                                this.setState({
                                    ...this.renderTestStatus(engineType, TEST_STATUS.FAIL),
                                    ...this.loadingStatus(engineType, false)
                                })
                            }
                        }
                    )
            } else {
                this.setState({
                    ...this.showRequireIcon(engineType, true) // 出现红标
                })
            }
        })
    }

    showDeleteConfirm (engineType) {
        confirm({
            title: '是否确定删除该引擎？',
            okText: '是',
            okType: 'danger',
            cancelText: '否',
            onOk: () => {
                this.deleteEngine(engineType)
            },
            onCancel() {
                console.log('cancel')
            }
        })
    }

    deleteEngine (engineType) {
        const { clusterId, mode, cluster } = this.props.location.state || {};
        const isNew = mode === 'new' // clusterId
        const { consoleUser } = this.props;
        let engineList = consoleUser.engineList
        Api.deleteEngine({
            clusterId: !isNew ? cluster.id : clusterId,
            engineType
        }).then(res => {
            if (res.code === 1) {
                const newEngineList = engineList.filter(currentValue => { return currentValue != engineType })
                this.props.updateEngineList(newEngineList) // 更新engineList
            }
            // test
            // const newEngineList = engineList.filter(currentValue => { return currentValue != engineType })
            // console.log(newEngineList)
            // this.props.updateEngineList(newEngineList) // 更新engineList
        })
    }
    /**
     * 引擎配置模块底部 测试连通性、取消、保存、删除 Button
     * @param isView 是否显示 
     * @param engineType 引擎类型 
     * @param engineTestLoading 测试loading
     */
    renderExtFooter = (isView, engineType, engineTestLoading) => {
        return (
            <div>
                {isView ? null : (
                    <div className='config-bottom'>
                        <Row>
                            <Col span={4}></Col>
                            <Col span={formItemLayout.wrapperCol.sm.span}>
                                <Button onClick={this.test.bind(this, engineType)} loading={engineTestLoading} type="primary">测试连通性</Button>
                                <span style={{ marginLeft: '18px' }}>
                                    {this.renderTestResult(engineType)}
                                </span>
                                <span>
                                    <Button onClick={hashHistory.goBack} style={{ marginLeft: '5px' }}>取消</Button>
                                    <Button onClick={this.saveSingleEngine.bind(this, engineType, false)} style={{ marginLeft: '5px' }} type="primary">保存</Button>
                                    <Button type="danger" style={{ marginLeft: '5px' }} onClick={this.showDeleteConfirm.bind(this, engineType)}>删除</Button>
                                </span>
                            </Col>
                        </Row>
                    </div>
                )}
            </div>
        )
    }

    // 获取各引擎配置项数据
    // 为null 则传全部参数
    getServerParams (formValues, engineType, haveFile) {
        const { mode, cluster, clusterId } = this.props.location.state || {};
        // const isEdit = mode === 'edit' // cluster.id
        const clusterConf = this.getClusterConf(formValues);
        const params = {
            clusterName: formValues.clusterName,
            clusterId, // 保存全部需要
            engineType: engineType ? engineType : '',
            clusterConf: engineType ? JSON.stringify(clusterConf[engineTypeConfig(engineType)]) : JSON.stringify(clusterConf)
        };
        if (haveFile) {
            let file = this.state.file;
            if (file) {
                params.config = file.files[0]
            }
        }
        if (mode == 'edit') {
            params.id = cluster.id
        }
        if (mode == 'new') {
            params.id = clusterId
        }
        return params;
    }
    // 转化数据
    getClusterConf (formValues) {
        let { zipConfig } = this.state;
        zipConfig = JSON.parse(zipConfig || '{}');
        let clusterConf = {};
        const sparkExtParams = this.getCustomParams(formValues, 'spark')
        const flinkExtParams = this.getCustomParams(formValues, 'flink')
        const hiveExtParams = this.getCustomParams(formValues, 'hive')
        const learningExtParams = this.getCustomParams(formValues, 'learning');
        const dtyarnshellExtParams = this.getCustomParams(formValues, 'dtyarnshell')
        const learningTypeName = {
            typeName: 'learning'
        }
        const dtyarnshellTypeName = {
            typeName: 'dtyarnshell'
        }
        const keyMap = {
            'sparkYarnAppMasterEnvPYSPARK_PYTHON': 'spark.yarn.appMasterEnv.PYSPARK_PYTHON',
            'sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON': 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON'
        }
        const yarnShellkeyMap = {
            'jlogstashRoot': 'jlogstash.root',
            'javaHome': 'java.home',
            'hadoopHomeDir': 'hadoop.home.dir',
            'python2Path': 'python2.path',
            'python3Path': 'python3.path'
        }
        clusterConf['hadoopConf'] = zipConfig.hadoopConf;
        clusterConf['yarnConf'] = zipConfig.yarnConf;
        clusterConf['hiveMeta'] = zipConfig.hiveMeta;
        clusterConf['hiveConf'] = { ...formValues.hiveConf, ...hiveExtParams } || {};
        clusterConf['carbonConf'] = formValues.carbonConf || {};
        clusterConf['sparkConf'] = { ...this.toChsKeys(formValues.sparkConf || {}, keyMap), ...sparkExtParams };
        clusterConf['flinkConf'] = { ...formValues.flinkConf, ...flinkExtParams };
        clusterConf['learningConf'] = { ...learningTypeName, ...this.myLowerCase(formValues.learningConf), ...learningExtParams };
        clusterConf['dtyarnshellConf'] = { ...dtyarnshellTypeName, ...this.toChsKeys(formValues.dtyarnshellConf || {}, yarnShellkeyMap), ...dtyarnshellExtParams };
        // 服务端兼容，不允许null
        clusterConf['hiveConf'].username = clusterConf['hiveConf'].username || '';
        clusterConf['hiveConf'].password = clusterConf['hiveConf'].password || '';
        clusterConf['carbonConf'].username = clusterConf['carbonConf'].username || '';
        clusterConf['carbonConf'].password = clusterConf['carbonConf'].password || '';
        return clusterConf;
    }
    getCustomParams (data, ParamKey) {
        let params = {};
        let tmpParam = {};
        for (let key in data) {
            // key的数据结构为flink%1532398855125918-name,flink%1532398855125918-value
            if (key.startsWith(ParamKey + '%')) {
                let tmpKeys = key.split('%')[1].split('-');
                let id = tmpKeys[0];// 自定义参数的id
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

    renderZipConfig (type) {
        let { zipConfig } = this.state;
        zipConfig = JSON.parse(zipConfig);
        let keyAndValue;
        if (type == 'hdfs') {
            keyAndValue = Object.entries(zipConfig.hadoopConf)
            utils.sortByCompareFunctions(keyAndValue,
                ([key, value], [compareKey, compareValue]) => {
                    if (key == 'fs.defaultFS') {
                        return -1;
                    }
                    if (compareKey == 'fs.defaultFS') {
                        return 1;
                    }
                    return 0;
                },
                ([key, value], [compareKey, compareValue]) => {
                    if (key == 'dfs.nameservices') {
                        return -1;
                    }
                    if (compareKey == 'dfs.nameservices') {
                        return 1;
                    }
                    return 0;
                },
                ([key, value], [compareKey, compareValue]) => {
                    if (key.indexOf('dfs.ha.namenodes') > -1) {
                        return -1;
                    }
                    if (compareKey.indexOf('dfs.ha.namenodes') > -1) {
                        return 1;
                    }
                    return 0;
                },
                ([key, value], [compareKey, compareValue]) => {
                    const checkKey = key.indexOf('dfs.namenode.rpc-address') > -1
                    const checkCompareKey = compareKey.indexOf('dfs.namenode.rpc-address') > -1
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
                    if (key == 'yarn.resourcemanager.ha.rm-ids') {
                        return -1;
                    }
                    if (compareKey == 'yarn.resourcemanager.ha.rm-ids') {
                        return 1;
                    }
                    return 0;
                },
                ([key, value], [compareKey, compareValue]) => {
                    const checkKey = key.indexOf('yarn.resourcemanager.address') > -1
                    const checkCompareKey = compareKey.indexOf('yarn.resourcemanager.address') > -1
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
                    const checkKey = key.indexOf('yarn.resourcemanager.webapp.address') > -1
                    const checkCompareKey = compareKey.indexOf('yarn.resourcemanager.webapp.address') > -1
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
                return (<Row key={key} className="zipConfig-item">
                    <Col className="formitem-textname" span={formItemLayout.labelCol.sm.span + 4}>
                        {key.length > 40
                            ? <Tooltip title={key}>{key.substr(0, 40) + '...'}</Tooltip>
                            : key}：
                    </Col>
                    <Col className="formitem-textvalue" span={formItemLayout.wrapperCol.sm.span - 1}>
                        {value}
                    </Col>
                </Row>)
            }
        )
    }

    getPrometheusValue = () => {
        const { flinkPrometheus, flinkData } = this.state;
        const { form } = this.props;
        const { mode } = this.props.location.state || {};
        if (mode == 'edit' && flinkData.hasOwnProperty('gatewayHost')) {
            form.setFieldsValue({
                'flinkConf.gatewayHost': flinkPrometheus.gatewayHost,
                'flinkConf.gatewayPort': flinkPrometheus.gatewayPort,
                'flinkConf.gatewayJobName': flinkPrometheus.gatewayJobName,
                'flinkConf.deleteOnShutdown': flinkPrometheus.deleteOnShutdown,
                'flinkConf.randomJobNameSuffix': flinkPrometheus.randomJobNameSuffix
            });
        }
    }

    changeCheckbox (e) {
        this.setState({
            checked: e.target.checked
        }, () => {
            if (this.state.checked) {
                this.getPrometheusValue()
            }
        })
    }
    onCancel () {
        this.setState({
            addEngineVisible: false
        })
    }
    // 获取每项Input的值
    getGatewayHostValue (e) {
        this.setState({
            gatewayHostValue: e.target.value
        })
    }
    getGatewayPortValue (e) {
        this.setState({
            gatewayPortValue: e.target.value
        })
    }
    getGatewayJobNameValue (e) {
        this.setState({
            gatewayJobNameValue: e.target.value
        })
    }
    changeDeleteOnShutdownOption (value) {
        this.setState({
            deleteOnShutdownOption: value
        })
    }
    changeRandomJobNameSuffixOption (value) {
        this.setState({
            randomJobNameSuffixOption: value
        })
    }
    // 渲染不同tab engineConfig
    renderEngineConfig = (engineType) => {
        const { checked, securityStatus, zipConfig, allTestLoading, flinkTestLoading, sparkTestLoading,
            dtYarnShellTestLoading, learningTestLoading, hdfsTestLoading,
            yarnTestLoading, hiveTestLoading, carbonTestLoading, libraTestLoading } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view';
        const { gatewayHostValue, gatewayPortValue, gatewayJobNameValue, deleteOnShutdownOption, randomJobNameSuffixOption } = this.state;
        switch (engineType) {
            // spark thrift server 对应之前的hive配置信息
            case ENGINE_TYPES.SPARKTHRIFTSERVER: {
                return (
                    <HiveConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        customView={(
                            <div>
                                {this.renderExtraParam('hive')}
                                {isView ? null : (
                                    <Row>
                                        <Col span={formItemLayout.labelCol.sm.span}></Col>
                                        <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                            <a onClick={this.addParam.bind(this, 'hive')}>添加自定义参数</a>
                                        </Col>
                                    </Row>
                                )}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, ENGINE_TYPES.SPARKTHRIFTSERVER, hiveTestLoading)}
                    />
                )
            }
            // hdfs 和 yarn 配置从配置文件中获取
            case ENGINE_TYPES.HDFS: {
                return (
                    zipConfig ? (
                        <div>
                            <div className="engine-config-content" style={{ width: '800px' }}>
                                {this.renderZipConfig('hdfs')}
                            </div>
                            {this.renderExtFooter(isView, ENGINE_TYPES.HDFS, hdfsTestLoading)}
                        </div>
                    ) : null
                )
            }
            case ENGINE_TYPES.YARN: {
                return (
                    zipConfig ? (
                        <div>
                            <div className="engine-config-content" style={{ width: '800px' }}>
                                {this.renderZipConfig('yarn')}
                            </div>
                            {this.renderExtFooter(isView, ENGINE_TYPES.YARN, yarnTestLoading)}
                        </div>
                    ) : null
                )
            }
            case ENGINE_TYPES.CARBONDATA: {
                return (
                    <CarbonDataConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        singleButton={this.renderExtFooter(isView, ENGINE_TYPES.CARBONDATA, carbonTestLoading)}
                    />
                )
            }
            case ENGINE_TYPES.FLINK: {
                return (
                    <FlinkConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        securityStatus={securityStatus}
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
                                {this.renderExtraParam('flink')}
                                {isView ? null : (
                                    <Row>
                                        <Col span={formItemLayout.labelCol.sm.span}></Col>
                                        <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                            <a onClick={this.addParam.bind(this, 'flink')}>添加自定义参数</a>
                                        </Col>
                                    </Row>
                                )}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, ENGINE_TYPES.FLINK, flinkTestLoading)}
                    />
                )
            }
            case ENGINE_TYPES.SPARK: {
                return (
                    <SparkConfig
                        getFieldDecorator={getFieldDecorator}
                        securityStatus={securityStatus}
                        isView={isView}
                        customView={(
                            <div>
                                {this.renderExtraParam('spark')}
                                {isView ? null : (
                                    <Row>
                                        <Col span={formItemLayout.labelCol.sm.span}></Col>
                                        <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                            <a onClick={this.addParam.bind(this, 'spark')}>添加自定义参数</a>
                                        </Col>
                                    </Row>
                                )}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, ENGINE_TYPES.SPARK, sparkTestLoading)}
                    />
                )
            }
            case ENGINE_TYPES.LEARNING: {
                return (
                    <div>
                        <div className="engine-config-content" style={{ width: '680px' }}>
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
                            {this.renderExtraParam('learning')}
                            {isView ? null : (
                                <Row>
                                    <Col span={formItemLayout.labelCol.sm.span}></Col>
                                    <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                        <a onClick={this.addParam.bind(this, 'learning')}>添加自定义参数</a>
                                    </Col>
                                </Row>
                            )}
                        </div>
                        {/* config底部功能按钮（测试连通性、取消、保存） */}
                        {this.renderExtFooter(isView, ENGINE_TYPES.LEARNING, learningTestLoading)}
                    </div>
                )
            }
            case ENGINE_TYPES.DTYARNSHELL: {
                return (
                    <div>
                        <div className="engine-config-content" style={{ width: '680px' }}>
                            <FormItem
                                label="jlogstash.root"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('dtyarnshellConf.jlogstashRoot', {
                                    rules: [{
                                        required: true,
                                        message: '请输入jlogstash.root'
                                    }]
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
                                        message: '请输入java.home'
                                    }]
                                })(
                                    <Input disabled={isView} placeholder="/opt/java/bin" />
                                )}
                            </FormItem>
                            <FormItem
                                label="hadoop.home.dir"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('dtyarnshellConf.hadoopHomeDir', {
                                    rules: [{
                                        required: true,
                                        message: '请输入hadoop.home.dir'
                                    }]
                                })(
                                    <Input disabled={isView} placeholder="/opt/dtstack/hadoop-2.7.3" />
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
                            {
                                securityStatus ? <div>
                                    <FormItem
                                        label="hdfsPrincipal"
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('dtyarnshellConf.hdfsPrincipal', {
                                            rules: [{
                                                required: true,
                                                message: '请输入hdfsPrincipal'
                                            }]
                                        })(
                                            <Input disabled={isView} />
                                        )}
                                    </FormItem>
                                    <FormItem
                                        label="hdfsKeytabPath"
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('dtyarnshellConf.hdfsKeytabPath', {
                                            rules: [{
                                                required: true,
                                                message: '请输入hdfsKeytabPath'
                                            }]
                                        })(
                                            <Input disabled={isView} />
                                        )}
                                    </FormItem>
                                    <FormItem
                                        label="hdfsKrb5ConfPath"
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('dtyarnshellConf.hdfsKrb5ConfPath', {
                                            rules: [{
                                                required: true,
                                                message: '请输入hdfsKrb5ConfPath'
                                            }]
                                        })(
                                            <Input disabled={isView} />
                                        )}
                                    </FormItem>
                                </div> : null
                            }
                            {this.renderExtraParam('dtyarnshell')}
                            {isView ? null : (
                                <Row>
                                    <Col span={formItemLayout.labelCol.sm.span}></Col>
                                    <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                        <a onClick={this.addParam.bind(this, 'dtyarnshell')}>添加自定义参数</a>
                                    </Col>
                                </Row>
                            )}
                        </div>
                        {this.renderExtFooter(isView, ENGINE_TYPES.DTYARNSHELL, dtYarnShellTestLoading)}
                    </div>
                )
            }
            case ENGINE_TYPES.LIBRA: {
                return (
                    <div className="engine-config-content" style={{ width: '680px' }}>
                        i am libra
                    </div>
                )
            }
            default:
                return (
                    <div>目前暂无该引擎配置</div>
                )
        }
    }
    render () {
        const { file, zipConfig, uploadLoading, core, nodeNumber, memory, fileHaveChange, checked, securityStatus, allTestLoading  } = this.state;
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { mode, clusterType } = this.props.location.state || {};
        const isView = mode == 'view';
        const isEdit = mode == 'edit';
        const isNew = mode == 'new';
        // const isNew = !(mode == 'view' || mode == 'edit');
        const columns = this.initColumns();
        const { gatewayHostValue, gatewayPortValue, gatewayJobNameValue, deleteOnShutdownOption, randomJobNameSuffixOption } = this.state;
        const { engineList } = this.props.consoleUser;
        return (
            <div className='console-wrapper'>
                
                <Card
                    className='shadow'
                    style={{ margin: '20 20 10 20' }}
                    noHovering
                >
                    <div>
                        <p className='back-icon'><GoBack size="default" type="textButton" style={{ fontSize: '14px', color: '#333333' }}></GoBack></p>
                        <div className='config-title'>集群信息</div>
                    </div>
                    <div style={{ borderBottom: '1px solid #DDDDDD' }}>
                        <FormItem
                            label="集群标识"
                            {...formItemLayout1}
                        >
                            {getFieldDecorator('clusterName', {
                                rules: [{
                                    required: true,
                                    message: '请输入集群标识'
                                }, {
                                    pattern: /^[a-z0-9_]{1,64}$/i,
                                    message: '集群标识不能超过64字符，支持英文、数字、下划线'
                                }]
                            })(
                                <Input disabled={true} placeholder="请输入集群标识" style={{ width: '200px' }} />
                            )}
                            <span style={{ marginLeft: '20px' }}>节点数：{nodeNumber || '--'} </span>
                            <span style={{ marginLeft: '5px' }}>资源数：{core || '--'}VCore {this.exchangeMemory(memory)} </span>
                        </FormItem>
                    </div>
                    {
                        isView ? null : (
                            <div>
                                <div className='config-title'>配置方式</div>
                                <FormItem
                                    label="配置方式"
                                    {...formItemLayout1}
                                >
                                    {getFieldDecorator('useDefaultConfig', {
                                        rules: [{
                                            required: true,
                                            message: '请选择配置方式'
                                        }],
                                        initialValue: true
                                    })(
                                        <RadioGroup>
                                            <Radio value={true}>使用默认配置</Radio>
                                            <Radio value={false}>上传配置文件</Radio>
                                        </RadioGroup>
                                    )}
                                </FormItem>
                                {/* 上传配置文件 */}
                                {
                                    getFieldValue('useDefaultConfig') ? null : (
                                        <div>
                                            <div className="upload-config" style={{ width: '750px' }}>
                                                <p style={{ marginBottom: '24px' }}>您需要获取Hadoop、Spark、Flink集群的配置文件，至少包括：<strong>core-site.xml、hdfs-site.xml、hive-site.xml、yarn-site.xml</strong>文件</p>

                                                <FormItem
                                                    label="配置文件"
                                                    {...formItemLayout1}
                                                >
                                                    {getFieldDecorator('file', {
                                                        rules: [{
                                                            required: !(!fileHaveChange && mode == 'edit'), message: '请选择上传文件'
                                                        }, {
                                                            validator: this.validateFileType
                                                        }]
                                                    })(
                                                        <div>
                                                            {
                                                                uploadLoading
                                                                    ? <label
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
                                                                <span style={{ marginLeft: '10px' }}>支持扩展名：.zip</span>
                                                        </div>
                                                    )}
                                                </FormItem>
                                                如何获取这些配置文件？请您参考<a>《帮助文档》</a>
                                            </div>
                                        </div>
                                    )
                                }

                            </div>
                        )
                    }
                   
                    
                    {/* <ZipConfig zipConfig={zipConfig} /> */}
                </Card>
                {
                    isView ? null : (
                        <div style={{ margin: '5 20 5 20', textAlign: 'right' }}>
                            <Button  onClick={() => {
                                this.setState({
                                    editModalKey: Math.random(),
                                    addEngineVisible: true
                                })
                            }} type="primary" style={{ marginLeft: '5px' }}>增加引擎</Button>
                            <Button  onClick={this.test.bind(this, null)} loading={allTestLoading} type="primary" style={{ marginLeft: '5px' }}>测试全部连通性</Button>
                            <Button onClick={this.saveOrUpdateCluster.bind(this, null)} type="primary" style={{ marginLeft: '5px' }}>保存全部</Button>
                        </div>
                    )
                }

                {/* 引擎配置 */}
                <Card
                    className='shadow console-tabs'
                    style={{ margin: '0 20 20 20' }}
                    noHovering
                >
                    <Tabs
                        defaultActiveKey={engineList && engineList[0]}
                        tabPosition='left'
                        style={{ height: '515' }}
                    >
                        {/* 循环出tabPane */}
                        {
                            engineList && engineList.map(item => {
                                return (
                                    <TabPane
                                        tab={
                                            <span>
                                                {/* <span className='icon_required'>*</span> */}
                                                {this.renderRequiredIcon(item)}
                                                <span className='tab-title'>{item}</span>
                                                {this.renderTestIcon(item)}
                                            </span>
                                        }
                                        forceRender={true}
                                        key={`${item}`}
                                    >
                                        <div style={{ height: '515', overflow: 'auto' }}>
                                            {this.renderEngineConfig(item)}
                                        </div>
                                    </TabPane>
                                )
                            })
                        }
                    </Tabs>
                </Card>
                <AddEngineModal
                    key={this.state.editModalKey}
                    visible={this.state.addEngineVisible}
                    engineSelectedLists={engineList}
                    clusterType={!isNew ? this.state.clusterType : clusterType} // 集群类型
                    onCancel={() => this.onCancel()}
                    onOk={this.addEngine.bind(this)}
                />
            </div>
        )
    }
}
export default Form.create()(EditCluster);
