import React from 'react';
import utils from 'utils';
/* eslint-disable */
import { Form, Input, Row, Col, Select, Icon, Tooltip, Button, message, Card, Radio, Tabs, Modal } from 'antd';
import { cloneDeep } from 'lodash';
import { connect } from 'react-redux'
// import { hashHistory } from 'react-router'

import { getUser, updateEngineList, updateHadoopComponentList, updateLibraComponentList } from '../../../actions/console'
import Api from '../../../api/console'
import { getComponentConfKey, validateEngine, myUpperCase, myLowerCase, toChsKeys } from '../../../consts/clusterFunc';
import { formItemLayout, ENGINE_TYPE, COMPONENT_TYPE_VALUE, SPARK_KEY_MAP, DTYARNSHELL_KEY_MAP,
    notExtKeysFlink, notExtKeysSpark, notExtKeysLearning, notExtKeysDtyarnShell, notExtKeysSparkThrift, notExtKeysLibraSql } from '../../../consts'
import GoBack from 'main/components/go-back';
import SparkConfig from './sparkConfig'
import FlinkConfig from './flinkConfig';
import { SparkThriftConfig, CarbonDataConfig } from './sparkThriftAndCarbonData'
import AddEngineModal from '../addEngineModal';
import AddComponentModal from '../addComponentModal';
const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const TabPane = Tabs.TabPane;
const confirm = Modal.confirm;
const TEST_STATUS = {
    NOTHING: 0,
    SUCCESS: true,
    FAIL: false
}
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
        },
        updateHadoopComponentList (params) {
            dispatch(updateHadoopComponentList(params))
        },
        updateLibraComponentList (params) {
            dispatch(updateLibraComponentList(params))
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
        // testStatus: TEST_STATUS.NOTHING,
        testResults: [], // 测试连通性返回数据
        flink_params: [],
        spark_params: [],
        sparkThrif_params: [],
        learning_params: [],
        dtyarnshell_params: [],
        libraSql_params: [],
        core: null,
        nodeNumber: null,
        memory: null,
        extDefaultValue: {},
        fileHaveChange: false,
        checked: false,
        allComponentConf: {},
        engineId: '',
        // 以下字段为填补关闭复选框数据无法获取输入数据情况
        gatewayHostValue: undefined,
        gatewayPortValue: undefined,
        gatewayJobNameValue: undefined,
        deleteOnShutdownOption: 'FALSE',
        randomJobNameSuffixOption: 'TRUE',
        flinkPrometheus: undefined, // 配置Prometheus参数
        flinkData: undefined, // 获取Prometheus参数
        addEngineVisible: false, // 新增引擎modal
        addComponentVisible: false,
        editModalKey: '',
        modalKey: '',
        // 组件testLoading
        allTestLoading: false,
        // 组件testStatus
        flinkTestStatus: TEST_STATUS.NOTHING,
        sparkTestStatus: TEST_STATUS.NOTHING,
        dtYarnShellTestStatus: TEST_STATUS.NOTHING,
        learningTestStatus: TEST_STATUS.NOTHING,
        hdfsTestStatus: TEST_STATUS.NOTHING,
        yarnTestStatus: TEST_STATUS.NOTHING,
        hiveTestStatus: TEST_STATUS.NOTHING,
        carbonTestStatus: TEST_STATUS.NOTHING,
        libraTestStatus: TEST_STATUS.NOTHING,
        // 控制组件必填项为全部填写时 出现红色*
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
        // this.refreshSaveEngine();
    }
    // 新增入口刷新无数据
    refreshSaveEngine () {
        const { mode, enginelist } = this.props.location.state;
        if (mode === 'new') {
            this.props.updateEngineList(enginelist)
        }
    }
    getEngineData = (data) => {
        let engineConf = {
            hadoopConf: {},
            libraConf: {}
        };
        data.map(item => {
            if (item.engineTypeCode === ENGINE_TYPE.HADOOP) {
                engineConf.hadoopConf = item
            }
            if (item.engineTypeCode === ENGINE_TYPE.LIBRASQL) {
                engineConf.libraConf = item
            }
        })
        return engineConf
    }
    exChangeComponentConf = (hadoopComp, libraComp) => {
        const comp = hadoopComp.concat(libraComp);
        let componentConf = {
            flinkConf: {},
            sparkConf: {},
            learningConf: {},
            dtyarnshellConf: {},
            hdfsConf: {}, // 对应hadoopConf
            yarnConf: {},
            sparkThriftConf: {}, // 对应hiveConf
            carbonConf: {},
            libraSqlConf: {}
        };
        comp.map(item => {
            switch (item.componentTypeCode) {
                case COMPONENT_TYPE_VALUE.FLINK: {
                    componentConf = Object.assign(componentConf, {
                        flinkConf: item.config
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.SPARK: {
                    componentConf = Object.assign(componentConf, {
                        sparkConf: item.config
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.LEARNING: {
                    componentConf = Object.assign(componentConf, {
                        learningConf: item.config
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                    componentConf = Object.assign(componentConf, {
                        dtyarnshellConf: item.config
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.HDFS: {
                    componentConf = Object.assign(componentConf, {
                        hdfsConf: item.config
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.YARN: {
                    componentConf = Object.assign(componentConf, {
                        yarnConf: item.config
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: {
                    componentConf = Object.assign(componentConf, {
                        sparkThriftConf: item.config
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.CARBONDATA: {
                    componentConf = Object.assign(componentConf, {
                        carbonConf: item.config
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.LIBRASQLSQL: {
                    componentConf = Object.assign(componentConf, {
                        libraSqlConf: item.config
                    })
                    break;
                }
            }
        })
        return componentConf
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
                            const data = res.data;
                            const enginesData = data.engines || [];
                            const engineConf = this.getEngineData(enginesData);
                            const hadoopConf = engineConf.hadoopConf || {}; // hadoop engine 总数据
                            const libraConf = engineConf.libraConf || {}; // libra engine 总数据
                            const hadoopComponentData = hadoopConf.components || []; // 组件信息
                            const libraComponentData = libraConf.components || [];
                            let componentConf = this.exChangeComponentConf(hadoopComponentData, libraComponentData);
                            componentConf = JSON.parse(componentConf)
                            const flinkData = componentConf.flinkConf;
                            const extParams = this.exchangeServerParams(componentConf)
                            const flinkConf = componentConf.flinkConf;
                            myUpperCase(flinkConf);
                            this.props.updateHadoopComponentList(hadoopComponentData) // dispatch engines
                            this.setState({
                                // checked: true,
                                allComponentConf: componentConf,
                                securityStatus: hadoopConf.security,
                                core: hadoopConf.resource.totalCore,
                                memory: hadoopConf.resource.totalMemory,
                                nodeNumber: hadoopConf.resource.totalNode,
                                zipConfig: JSON.stringify({
                                    yarnConf: componentConf.yarnConf,
                                    hdfsConf: componentConf.hdfsConf,
                                    hiveMeta: componentConf.hiveMeta // (暂无用数据)
                                }),
                                flink_params: extParams.flinkKeys,
                                spark_params: extParams.sparkKeys,
                                sparkThrif_params: extParams.sparkThriftKeys,
                                learning_params: extParams.learningKeys,
                                dtyarnshell_params: extParams.dtyarnshellKeys,
                                libraSql_params: extParams.libraSqlKeys,
                                extDefaultValue: extParams.default,
                                flinkPrometheus: componentConf.flinkConf,
                                flinkData: flinkData
                            })
                            // 判断是有Prometheus参数
                            if (flinkData && flinkData.hasOwnProperty('gatewayHost')) {
                                this.setState({
                                    checked: true
                                })
                            }
                            form.setFieldsValue({
                                clusterName: data.clusterName,
                                sparkThriftConf: componentConf.sparkThriftConf,
                                carbonConf: componentConf.carbonConf,
                                sparkConf: toChsKeys(componentConf.sparkConf || {}, SPARK_KEY_MAP),
                                flinkConf: componentConf.flinkConf,
                                learningConf: myUpperCase(componentConf.learningConf),
                                dtyarnshellConf: toChsKeys(componentConf.dtyarnshellConf || {}, DTYARNSHELL_KEY_MAP),
                                libraSqlConf: componentConf.libraSqlConf
                            })
                        } else {
                            this.props.updateEngineList([])
                        }
                    }
                )
        } else { // 新增集群填充集群名称、节点数、资源数
            this.setState({
                core: params.totalCore,
                memory: params.totalMemory,
                nodeNumber: params.totalNode
            })
            form.setFieldsValue({
                clusterName: params.clusterName
            })
        }
    }
    /* eslint-disable */
    /**
     * 从服务端配置中抽取出自定义参数
     * hdfs、yarn、carbondata不可自定义参数
     * @param {Map} config 服务端接收到的配置
     */
    exchangeServerParams (config) {
        let result = {
            flinkKeys: [],
            sparkKeys: [],
            sparkThriftKeys: [],
            learningKeys: [],
            dtyarnshellKeys: [],
            libraSqlKeys: [],
            default: {}
        };
        let flinkConfig = config.flinkConf || {};
        let sparkConfig = config.sparkConf || {};
        let sparkThriftConfig = config.sparkThriftConf || {};
        let learningConfig = config.learningConf || {};
        let dtyarnshellConfig = config.dtyarnshellConf || {};
        let libraSqlConfig = config.libraSqlConf || {};
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

        setDefault(flinkConfig, notExtKeysFlink, 'flink', result.flinkKeys)
        setDefault(sparkConfig, notExtKeysSpark, 'spark', result.sparkKeys)
        setDefault(sparkThriftConfig, notExtKeysSparkThrift, 'hive', result.sparkThriftKeys)
        setDefault(learningConfig, notExtKeysLearning, 'learning', result.learningKeys)
        setDefault(dtyarnshellConfig, notExtKeysDtyarnShell, 'dtyarnshell', result.dtyarnshellKeys)
        setDefault(libraSqlConfig, notExtKeysLibraSql, 'libra', result.libraSqlKeys)
        return result;
    }
    validateFileType (rule, value, callback) {
        const reg = /\.(zip)$/

        if (value && !reg.test(value.toLocaleLowerCase())) {
            const message = '配置文件只能是zip文件!';
            callback(message);
        }
        callback();
    }
    fileChange (e) {
        const { clusterId, cluster, mode } = this.props.location.state || {};
        const isNew = mode === 'new';
        const file = e.target;
        this.setState({ file: {}, uploadLoading: true, zipConfig: '', fileHaveChange: true });
        Api.uploadClusterResource({
            resources: file.files[0],
            clusterId : isNew ? clusterId : cluster.id,
            useDefaultConfig: false
        })
            .then(
                (res) => {
                    if (res.code == 1) {
                        this.setState({
                            uploadLoading: false,
                            file: file,
                            securityStatus: res.data.security,
                            zipConfig: res.data.componentConfig
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
    onChangeRadio = (e) => {
        const checkValue = e.target.value; // true则默认配置不上传文件
        const { clusterId, cluster, mode } = this.props.location.state || {};
        const isNew = mode === 'new';
        if(checkValue) {
            Api.uploadClusterResource({
                resources: '',
                clusterId : isNew ? clusterId : cluster.id,
                useDefaultConfig: true
            }).then(res => {
                if (res.code === 1) {
                    this.setState({
                        file: null,
                        securityStatus: res.data.security,
                        zipConfig: res.data.zipConfig
                    })
                }
            })
        }
    }
    // 控制engine前是否出现为填小图标 *
    showRequireIcon = (componentValue, isShow) => {
        let isShowRequiredIcon = {};
        switch (componentValue) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                isShowRequiredIcon = {
                    flinkShowRequired: isShow
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                isShowRequiredIcon = {
                    hiveShowRequired: isShow
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                isShowRequiredIcon = {
                    carbonShowRequired: isShow
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                isShowRequiredIcon = {
                    sparkShowRequired: isShow
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                isShowRequiredIcon = {
                    dtYarnShellShowRequired: isShow
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                isShowRequiredIcon = {
                    learningShowRequired: isShow
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                isShowRequiredIcon = {
                    hdfsShowRequired: isShow
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                isShowRequiredIcon = {
                    yarnShowRequired: isShow
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                isShowRequiredIcon = {
                    libraShowRequired: isShow
                }
                break;
            }
            //  全部校验
            case null : {
                return this.validateAllRequired()
            }
        }
    }
    // 全部校验表单是否必填
    validateAllRequired = () => {
        const { engineList } = this.props.consoleUser;
        let obj = {}
        engineList && engineList.map(item => {
            this.props.form.validateFields(validateEngine(item), {}, (err, values) => {
                if (item === COMPONENT_TYPE_VALUE.FLINK) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            flinkShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            flinkShowRequired: true
                        })
                    }
                } else if (item === COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            hiveShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            hiveShowRequired: true
                        })
                    }
                } else if (item === COMPONENT_TYPE_VALUE.CARBONDATA) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            carbonShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            carbonShowRequired: true
                        })
                    }
                } else if (item === COMPONENT_TYPE_VALUE.SPARK) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            sparkShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            sparkShowRequired: true
                        })
                    }
                } else if (item === COMPONENT_TYPE_VALUE.DTYARNSHELL) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            dtYarnShellShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            dtYarnShellShowRequired: true
                        })
                    }
                } else if (item === COMPONENT_TYPE_VALUE.LEARNING) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            learningShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            learningShowRequired: true
                        })
                    }
                } else if (item === COMPONENT_TYPE_VALUE.HDFS) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            hdfsShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            hdfsShowRequired: true
                        })
                    }
                } else if (item === COMPONENT_TYPE_VALUE.YARN) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            yarnShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            yarnShowRequired: true
                        })
                    }
                } else if (item === COMPONENT_TYPE_VALUE.LIBRASQL) {
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
    renderRequiredIcon = (componentValue) => {
        const { flinkShowRequired,
            hiveShowRequired,
            carbonShowRequired,
            sparkShowRequired,
            dtYarnShellShowRequired,
            learningShowRequired,
            hdfsShowRequired,
            yarnShowRequired,
            libraShowRequired } = this.state;
        switch (componentValue) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                if (flinkShowRequired) {
                    return <span className='icon_required'>*</span>
                }
                return null
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                if (hiveShowRequired) {
                    return <span className='icon_required'>*</span>
                }
                return null
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                if (carbonShowRequired) {
                    return <span className='icon_required'>*</span>
                }
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                if (sparkShowRequired) {
                    return <span className='icon_required'>*</span>
                }
                return null
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                if (dtYarnShellShowRequired) {
                    return <span className='icon_required'>*</span>
                }
                return null
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                if (learningShowRequired) {
                    return <span className='icon_required'>*</span>
                }
                return null
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                if (hdfsShowRequired) {
                    return <span className='icon_required'>*</span>
                }
                return null
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                if (yarnShowRequired) {
                    return <span className='icon_required'>*</span>
                }
                return null
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
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
    // show err message
    showDetailErrMessage (engine) {
        Modal.error({
            title: `${engine.engineName} 错误信息`,
            content: `${engine.errorMsg}`
        })
    }

    matchEngineTest (engineResult) {
        switch (engineResult.result) {
            case TEST_STATUS.NOTHING: {
                return null
            }
            case TEST_STATUS.SUCCESS: {
                return <Icon className='success-icon' type="check-circle" />
            }
            case TEST_STATUS.FAIL: {
                return <Tooltip
                    title={
                        <a
                            style={{ color: '#fff' }}
                            onClick={ this.showDetailErrMessage.bind(this, engineResult)}
                        >
                            {engineResult.errorMsg}
                        </a>
                    }
                    placement='right'

                >
                    <Icon className='err-icon' type="close-circle" />
                </Tooltip>
            }
        }
    }

    // 控制engine测试结果图标
    renderTestIcon = (componentValue) => {
        const { flinkTestStatus,
            hiveTestStatus,
            carbonTestStatus,
            sparkTestStatus,
            dtYarnShellTestStatus,
            learningTestStatus,
            hdfsTestStatus,
            yarnTestStatus,
            libraTestStatus } = this.state;
        switch (componentValue) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                return this.matchEngineTest(flinkTestStatus)
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                return this.matchEngineTest(hiveTestStatus)
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                return this.matchEngineTest(carbonTestStatus)
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                return this.matchEngineTest(sparkTestStatus)
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                return this.matchEngineTest(dtYarnShellTestStatus)
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                return this.matchEngineTest(learningTestStatus)
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                return this.matchEngineTest(hdfsTestStatus)
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                return this.matchEngineTest(yarnTestStatus)
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                return this.matchEngineTest(libraTestStatus)
            }
            default: {
                return null
            }
        }
    }
    addParam (type) {
        const { flink_params, spark_params, sparkThrif_params, learning_params, dtyarnshell_params, libraSql_params } = this.state;
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
        } else if (type == 'sparkThrift') {
            this.setState({
                sparkThrif_params: [...sparkThrif_params, {
                    id: giveMeAKey()
                }]
            })
        } else if (type == 'learning') {
            this.setState({
                learning_params: [...learning_params, {
                    id: giveMeAKey()
                }]
            })
        } else if (type == 'libra') {
            this.setState({
                libraSql_params: [...libraSql_params, {
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
        const { flink_params, spark_params, sparkThrif_params, learning_params, dtyarnshell_params, libraSql_params } = this.state;
        let tmpParams;
        let tmpStateName;
        if (type == 'flink') {
            tmpStateName = 'flink_params';
            tmpParams = flink_params;
        } else if (type == 'spark') {
            tmpStateName = 'spark_params';
            tmpParams = spark_params;
        } else if (type == 'sparkThrift') {
            tmpStateName = 'sparkThrif_params';
            tmpParams = sparkThrif_params;
        } else if (type == 'learning') {
            tmpStateName = 'learning_params';
            tmpParams = learning_params;
        } else if (type == 'libra') {
            tmpStateName = 'libraSql_params';
            tmpParams = libraSql_params;
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
        const { flink_params, spark_params, sparkThrif_params, learning_params, dtyarnshell_params, libraSql_params, extDefaultValue } = this.state;
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
        } else if (type == 'sparkThrift') {
            tmpParams = sparkThrif_params
        } else if (type == 'libra') {
            tmpParams = libraSql_params
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
    renderTestStatus = (testResults) =>{
        let testStatus = {}
        testResults && testResults.map(engine => {
            switch (engine.engineName) {
                case COMPONENT_TYPE_VALUE.FLINK: {
                    testStatus = Object.assign(testStatus, {
                        flinkTestStatus: engine
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                    testStatus = Object.assign(testStatus, {
                        hiveTestStatus: engine
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.CARBONDATA: {
                    testStatus = Object.assign(testStatus, {
                        carbonTestStatus: engine
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.SPARK: {
                    testStatus = Object.assign(testStatus, {
                        sparkTestStatus: engine
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                    testStatus = Object.assign(testStatus, {
                        dtYarnShellTestStatus: engine
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.LEARNING: {
                    testStatus = Object.assign(testStatus, {
                        learningTestStatus: engine
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.HDFS: {
                    testStatus = Object.assign(testStatus, {
                        hdfsTestStatus: engine
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.YARN: {
                    testStatus = Object.assign(testStatus, {
                        yarnTestStatus: engine
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.LIBRASQL: {
                    testStatus = Object.assign(testStatus, {
                        libraTestStatus: engine
                    })
                    break;
                }
                default: {
                    testStatus = Object.assign(testStatus, {})
                }
            }
        })
        console.log(testStatus)
        return testStatus
    }
    /**
     * 保存组件
     */
    saveComponent (componentValue) {
        const { clusterId, mode, cluster } = this.props.location.state || {};
        const { getFieldsValue } = this.props.form;
        const isNew = mode === 'new' // clusterId
        const componentConf = this.getComponentConf(getFieldsValue());
        Api.saveComponent({
            engineId: this.state.engineId,
            configString: JSON.stringify(componentConf[getComponentConfKey(componentValue)])
        }).then(res => {
            if (res.code ===1) {
                this.renderTestIcon()
                this.setState({
                    ...this.renderTestStatus(res.data.testResults)
                })
                message.success(`${componentValue}保存成功`)
            }
        })
    }
    addComponent (componentTypeCodeList, callback) {
        const hasSub = callback()
        if (hasSub) {
            Api.addComponent({
                engineId: this.state.engineId,
                componentTypeCodeList
            }).then(res => {
                if (res.code === 1) {
                    // 添加dispatch updateEngineList
                    // componentTypeCodeList获取value, 无法获取新增加的组件详细信息，需后端返回
                    let hadoopComponentList = this.props.consoleUser.hadoopComponentList;
                    const compList = hadoopComponentList.concat(componentTypeCodeList)
                    this.props.updateHadoopComponentList(compList)
                    this.closeAddModal()
                    message.success('添加组件成功!')
                }
            })
        }
    }
    addEngine (componentValue, callback) {
        callback()
        const { clusterId, mode, cluster } = this.props.location.state || {};
        const isNew = mode === 'new' // clusterId
        let hadoopComponentList = this.props.consoleUser.hadoopComponentList;
        const newEngineList = hadoopComponentList.concat(componentValue)
        if (callback()) {
            Api.saveOrAddEngine({
                clusterId: !isNew ? cluster.id : clusterId,
                componentValue,
                engineConfig: ''
            }).then(res => {
                if (res.code === 1) {
                    // 添加dispatch updateEngineList
                    this.props.updateEngineList(newEngineList)
                    this.onCancel()
                    message.success('添加引擎成功!')
                }
            })
        }
    }
    /**
     * 测试全部连通性
     * @param componentValue 组件类型 为null则全部测试
     */
    test (componentValue) {
        this.props.form.validateFields(null, {}, (err, values) => {
            if (!err) {
                this.setState({
                    ...this.showRequireIcon(componentValue, false), // 不出现红标
                    allTestLoading: true
                })
                const componentConf = this.getComponentConf(values);
                Api.testCluster({
                    clusterConf: JSON.stringify(componentConf)
                })
                    .then(
                        (res) => {
                            if (res.code == 1) {
                                const { description, testResults } = res.data;
                                this.setState({
                                    nodeNumber: description ? description.totalNode : 0,
                                    core: description ? description.totalCores : 0,
                                    memory: description ? description.totalMemory : 0,
                                    testResults: testResults,
                                    ...this.renderTestStatus(testResults),
                                    allTestLoading: false
                                })
                                // message.success('连通成功')
                            } else {
                                this.setState({
                                    allTestLoading: false
                                })
                            }
                        }
                    )
            } else {
                this.setState({
                    ...this.showRequireIcon(componentValue, true) // 出现红标
                })
                message.error('你有必填配置项未填写！')
            }
        })
    }
    // 取消操作
    handleCancel (componentValue) {
        const { form } = this.props;
        const { allComponentConf } = this.state;
        switch (componentValue) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                form.setFieldsValue({
                    flinkConf: allComponentConf.flinkConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                form.setFieldsValue({
                    sparkThriftConf: allComponentConf.sparkThriftConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                form.setFieldsValue({
                    carbonConf: allComponentConf.carbonConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                form.setFieldsValue({
                    sparkConf: toChsKeys(allComponentConf.sparkConf || {}, SPARK_KEY_MAP)
                })
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                form.setFieldsValue({
                    dtyarnshellConf: toChsKeys(allComponentConf.dtyarnshellConf || {}, DTYARNSHELL_KEY_MAP)
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                form.setFieldsValue({
                    learningConf: myUpperCase(allComponentConf.learningConf)
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                this.setState({
                    zipConfig: JSON.stringify({
                        hdfsConf: allComponentConf.hdfsConf
                    })
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                this.setState({
                    zipConfig: JSON.stringify({
                        yarnConf: allComponentConf.yarnConf
                    })
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                form.setFieldsValue({
                    libraSqlConf: allComponentConf.libraSqlConf
                })
                break;
            }
        }
    }
    showDeleteConfirm (componentValue) {
        confirm({
            title: '是否确定删除该组件？',
            okText: '是',
            okType: 'danger',
            cancelText: '否',
            onOk: () => {
                this.deleteComponent(componentValue)
            },
            onCancel () {
                console.log('cancel')
            }
        })
    }

    deleteComponent (componentValue) {
        const { clusterId, mode, cluster } = this.props.location.state || {};
        const isNew = mode === 'new' // clusterId
        const { consoleUser } = this.props;
        let hadoopComponentList = consoleUser.hadoopComponentList
        Api.deleteComponent({
            componentId: componentValue
        }).then(res => {
            if (res.code === 1) {
                const newComponentList = hadoopComponentList.filter(currentComp => { return currentComp.componentTypeCode != componentValue })
                this.props.updateHadoopComponentList(newComponentList) // 更新 Hadoop Component
                message.success('删除组件成功！')
            }
        })
    }
    onTabChange = (key) => {
        this.setState({
            engineId: key
        })
    }
    /**
     * 引擎配置模块底部 测试连通性、取消、保存、删除 Button
     * @param isView 是否显示 
     * @param componentValue 组件 
     */
    renderExtFooter = (isView, componentValue) => {
        return (
            <div>
                {isView ? null : (
                    <div className='config-bottom'>
                        <Row>
                            <Col span={4}></Col>
                            <Col span={formItemLayout.wrapperCol.sm.span}>
                                <span>
                                    <Button onClick={this.saveComponent.bind(this, componentValue, false)} style={{ marginLeft: '5px' }} type="primary">保存</Button>
                                    <Button onClick={this.handleCancel.bind(this, componentValue)} style={{ marginLeft: '5px' }}>取消</Button>
                                    <Button type="danger" style={{ marginLeft: '5px' }} onClick={this.showDeleteConfirm.bind(this, componentValue)}>删除</Button>
                                </span>
                            </Col>
                        </Row>
                    </div>
                )}
            </div>
        )
    }
    // 转化数据
    getComponentConf (formValues) {
        let { zipConfig } = this.state;
        zipConfig = JSON.parse(zipConfig || '{}');
        let componentConf = {};
        const sparkExtParams = this.getCustomParams(formValues, 'spark')
        const flinkExtParams = this.getCustomParams(formValues, 'flink')
        const sparkThriftExtParams = this.getCustomParams(formValues, 'sparkThrift')
        const learningExtParams = this.getCustomParams(formValues, 'learning');
        const dtyarnshellExtParams = this.getCustomParams(formValues, 'dtyarnshell');
        const libraExtParams = this.getCustomParams(formValues, 'libra')
        const learningTypeName = {
            typeName: 'learning'
        }
        const dtyarnshellTypeName = {
            typeName: 'dtyarnshell'
        }
        componentConf['hdfsConf'] = zipConfig.hdfsConf;
        componentConf['yarnConf'] = zipConfig.yarnConf;
        componentConf['hiveMeta'] = zipConfig.hiveMeta;
        componentConf['sparkThriftConf'] = { ...formValues.sparkThriftConf, ...sparkThriftExtParams } || {};
        componentConf['carbonConf'] = formValues.carbonConf || {};
        componentConf['sparkConf'] = { ...toChsKeys(formValues.sparkConf || {}, SPARK_KEY_MAP), ...sparkExtParams };
        componentConf['flinkConf'] = { ...formValues.flinkConf, ...flinkExtParams };
        componentConf['learningConf'] = { ...learningTypeName, ...myLowerCase(formValues.learningConf), ...learningExtParams };
        componentConf['dtyarnshellConf'] = { ...dtyarnshellTypeName, ...toChsKeys(formValues.dtyarnshellConf || {}, DTYARNSHELL_KEY_MAP), ...dtyarnshellExtParams };
        componentConf['libraSqlConf'] = { ...formValues.libraSqlConf, ...libraExtParams };
        // 服务端兼容，不允许null
        componentConf['sparkThriftConf'].username = componentConf['sparkThriftConf'].username || '';
        componentConf['sparkThriftConf'].password = componentConf['sparkThriftConf'].password || '';
        componentConf['carbonConf'].username = componentConf['carbonConf'].username || '';
        componentConf['carbonConf'].password = componentConf['carbonConf'].password || '';
        return componentConf;
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
        zipConfig = JSON.parse(zipConfig || '{}');
        let keyAndValue;
        if (type == 'hdfs') {
            keyAndValue = Object.entries(zipConfig.hdfsConf || {})
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
    closeAddModal () {
        this.setState({
            addComponentVisible: false
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
    // 渲染 Component Config
    renderComponentConf = (componentValue) => {
        const { checked, securityStatus, zipConfig } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view';
        const { gatewayHostValue, gatewayPortValue, gatewayJobNameValue, deleteOnShutdownOption, randomJobNameSuffixOption } = this.state;
        switch (componentValue) {
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: {
                return (
                    <SparkThriftConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        customView={(
                            <div>
                                {this.renderExtraParam('sparkThrift')}
                                {isView ? null : (
                                    <Row>
                                        <Col span={formItemLayout.labelCol.sm.span}></Col>
                                        <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                            <a onClick={this.addParam.bind(this, 'sparkThrift')}>添加自定义参数</a>
                                        </Col>
                                    </Row>
                                )}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER)}
                    />
                )
            }
            // hdfs 和 yarn 配置从配置文件中获取
            case COMPONENT_TYPE_VALUE.HDFS: {
                return (
                    zipConfig ? (
                        <div>
                            <div className="engine-config-content" style={{ width: '800px' }}>
                                {this.renderZipConfig('hdfs')}
                            </div>
                            {this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.HDFS)}
                        </div>
                    ) : null
                )
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                return (
                    zipConfig ? (
                        <div>
                            <div className="engine-config-content" style={{ width: '800px' }}>
                                {this.renderZipConfig('yarn')}
                            </div>
                            {this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.YARN)}
                        </div>
                    ) : null
                )
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                return (
                    <CarbonDataConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.CARBONDATA)}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.FLINK: {
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
                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.FLINK)}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
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
                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.SPARK)}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                return (
                    <div>
                        <div className="engine-config-content" style={{ width: '680px' }}>
                            <FormItem
                                label="learning.python3.path"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('learningConf.learningPython3Path', {
                                })(
                                    <Input disabled={isView} placeholder="/root/anaconda3/bin/python3" />
                                )}
                            </FormItem>
                            <FormItem
                                label="learning.python2.path"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('learningConf.learningPython2Path', {
                                })(
                                    <Input disabled={isView} placeholder="/root/anaconda2/bin/python2" />
                                )}
                            </FormItem>
                            <FormItem
                                label="learning.history.address"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('learningConf.learningHistoryAddress', {
                                })(
                                    <Input disabled={isView} placeholder="rdos1:10021" />
                                )}
                            </FormItem>
                            <FormItem
                                label={<Tooltip title="learning.history.webapp.address">learning.history.webapp.address</Tooltip>}
                                {...formItemLayout}
                            >
                                {getFieldDecorator('learningConf.learningHistoryWebappAddress', {
                                })(
                                    <Input disabled={isView} placeholder="rdos1:19886" />
                                )}
                            </FormItem>
                            <FormItem
                                label={<Tooltip title="learning.history.webapp.https.address">learning.history.webapp.https.address</Tooltip>}
                                {...formItemLayout}
                            >
                                {getFieldDecorator('learningConf.learningHistoryWebappHttpsAddress', {
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
                        {this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.LEARNING)}
                    </div>
                )
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
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
                                })(
                                    <Input disabled={isView} placeholder="/root/anaconda3/bin/python2" />
                                )}
                            </FormItem>
                            <FormItem
                                label={<Tooltip title="python3.path">python3.path</Tooltip>}
                                {...formItemLayout}
                            >
                                {getFieldDecorator('dtyarnshellConf.python3Path', {
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
                        {this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.DTYARNSHELL)}
                    </div>
                )
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                return (
                    <div>
                        <div className="engine-config-content" style={{ width: '680px' }}>
                            {this.renderExtraParam('libra')}
                            {isView ? null : (
                                <Row>
                                    <Col span={formItemLayout.labelCol.sm.span}></Col>
                                    <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                                        <a onClick={this.addParam.bind(this, 'libra')}>添加自定义参数</a>
                                    </Col>
                                </Row>
                            )}
                        </div>
                        {this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.LIBRASQL)}
                    </div>
                )
            }
            default:
                return (
                    <div>目前暂无该组件配置</div>
                )
        }
    }
    render () {
        const { file, uploadLoading, core, nodeNumber, memory, fileHaveChange, allTestLoading } = this.state;
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view';
        const isNew = mode == 'new';
        const { engineList, hadoopComponentList, libraComponentList } = this.props.consoleUser;
        return (
            <div className='console-wrapper'>
                <div>
                    <p className='back-icon'><GoBack size="default" type="textButton" style={{ fontSize: '14px', color: '#333333' }}></GoBack></p>
                    <div className='config-title'>集群信息</div>
                </div>
                <Tabs
                    defaultActiveKey={engineList && engineList[0].engineId}
                    tabPosition='top'
                    onChange={this.onTabChange}
                >
                    {
                        engineList && engineList.map(item => {
                            return (
                                <TabPane
                                    tab={item.engineName}
                                    key={`${item.engineId}`}
                                >
                                   {item.engineId == ENGINE_TYPE.HADOOP ? <React.Fragment>
                                    <Card
                                        className='shadow'
                                        style={{ margin: '20 20 10 20' }}
                                        noHovering
                                    >
                                        <div style={{ marginTop: '20px', borderBottom: '1px solid #DDDDDD' }}>
                                            <Row>
                                                <Col span={14} pull={2}>
                                                    <FormItem
                                                        label="集群标识"
                                                        {...formItemLayout}
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
                                                </Col>
                                            </Row>
                                        </div>
                                        {
                                            isView ? null : (
                                                <div>
                                                    <div className='config-title'>配置方式</div>
                                                    <Row>
                                                        <Col span={14} pull={2}>
                                                            <FormItem
                                                                label="配置方式"
                                                                {...formItemLayout}
                                                            >
                                                                {getFieldDecorator('useDefaultConfig', {
                                                                    rules: [{
                                                                        required: true,
                                                                        message: '请选择配置方式'
                                                                    }],
                                                                    initialValue: true
                                                                })(
                                                                    <RadioGroup onChange={this.onChangeRadio}>
                                                                        <Radio value={true}>使用默认配置</Radio>
                                                                        <Radio value={false}>上传配置文件</Radio>
                                                                    </RadioGroup>
                                                                )}
                                                            </FormItem>
                                                        </Col>
                                                    </Row>
                                                    {/* 上传配置文件 */}
                                                    {
                                                        getFieldValue('useDefaultConfig') ? null : (
                                                            <div>
                                                                <div className="upload-config" style={{ width: '750px' }}>
                                                                    <p style={{ marginBottom: '24px' }}>您需要获取Hadoop、Spark、Flink集群的配置文件，至少包括：<strong>core-site.xml、hdfs-site.xml、hive-site.xml、yarn-site.xml</strong>文件</p>

                                                                    <FormItem
                                                                        label="配置文件"
                                                                        {...formItemLayout}
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
                                                        modalKey: Math.random(),
                                                        addComponentVisible: true
                                                    })
                                                }} type="primary" style={{ marginLeft: '5px' }}>增加组件</Button>
                                                <Button  onClick={() => {
                                                    this.setState({
                                                        editModalKey: Math.random(),
                                                        addEngineVisible: true
                                                    })
                                                }} type="primary" style={{ marginLeft: '5px' }}>增加引擎</Button>
                                                <Button  onClick={this.test.bind(this, null)} loading={allTestLoading} type="primary" style={{ marginLeft: '5px' }}>测试全部连通性</Button>
                                            </div>
                                        )
                                    }

                                    {/* 组件配置 */}
                                    <Card
                                        className='shadow console-tabs cluster-tab-width'
                                        style={{ margin: '0 20 20 20' }}
                                        noHovering
                                    >
                                        <Tabs
                                            defaultActiveKey={hadoopComponentList && hadoopComponentList[0]}
                                            tabPosition='left'
                                            style={{ height: '550' }}
                                        >
                                            {/* 循环出tabPane */}
                                            {
                                                hadoopComponentList && hadoopComponentList.map(item => {
                                                    return (
                                                        <TabPane
                                                            tab={
                                                                <span>
                                                                    {this.renderRequiredIcon(item.componentTypeCode)}
                                                                    <span className='tab-title'>{item.componentName}</span>
                                                                    {this.renderTestIcon(item.componentTypeCode)}
                                                                </span>
                                                            }
                                                            forceRender={true}
                                                            key={`${item.componentTypeCode}`}
                                                        >
                                                            <div style={{ height: '550', paddingBottom: '150px', overflow: 'auto' }}>
                                                                {this.renderComponentConf(item.componentTypeCode)}
                                                            </div>
                                                        </TabPane>
                                                    )
                                                })
                                            }
                                        </Tabs>
                                    </Card>
                                    </React.Fragment> : <CarbonDataConfig
                                                        isView={isView}
                                                        getFieldDecorator={getFieldDecorator}
                                                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.CARBONDATA)}
                                                    />
                                    }
                                </TabPane>
                            )
                        })
                    }
                </Tabs>
                <AddEngineModal
                    key={this.state.editModalKey}
                    visible={this.state.addEngineVisible}
                    engineSelectedLists={engineList}
                    clusterType={!isNew ? this.state.clusterType : clusterType} // 集群类型
                    onCancel={() => this.onCancel()}
                    onOk={this.addEngine.bind(this)}
                />
                <AddComponentModal
                    key={this.state.modalKey}
                    visible={this.state.addComponentVisible}
                    onCancel={() => { this.closeAddModal() }}
                    addComponent={this.addComponent.bind(this)}
                />
            </div>
        )
    }
}
export default Form.create()(EditCluster);
