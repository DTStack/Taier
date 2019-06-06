import React from 'react';
import utils from 'utils';
import { Form, Input, Row, Col, Icon, Tooltip, Button, message, Card, Radio, Tabs, Modal } from 'antd';
import { connect } from 'react-redux'

import { getTenantList, updateEngineList, updateHadoopComponentList, updateLibraComponentList } from '../../../actions/console'
import Api from '../../../api/console'
import { getComponentConfKey, validateCompParams, myUpperCase, myLowerCase, toChsKeys } from '../../../consts/clusterFunc';
import { formItemLayout, ENGINE_TYPE, COMPONENT_TYPE_VALUE, SPARK_KEY_MAP, DTYARNSHELL_KEY_MAP,
    notExtKeysFlink, notExtKeysSpark, notExtKeysLearning, notExtKeysDtyarnShell, notExtKeysSparkThrift, notExtKeysLibraSql } from '../../../consts'
import GoBack from 'main/components/go-back';
import SparkConfig from './sparkConfig'
import FlinkConfig from './flinkConfig';
import LearningConfig from './learningConfig';
import DtyarnShellConfig from './dtYarnshellConfig';
import LibraSqlConfig from './libraSqlConfig';
import { SparkThriftConfig, CarbonDataConfig } from './sparkThriftAndCarbonData';
import AddCommModal from '../../../components/addCommModal';
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const TabPane = Tabs.TabPane;
const confirm = Modal.confirm;
const TEST_STATUS = {
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
            dispatch(getTenantList())
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
        engineId: 1,
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
        // 组件testResult
        flinkTestResult: {},
        sparkTestResult: {},
        dtYarnShellTestResult: {},
        learningTestResult: {},
        hdfsTestResult: {},
        yarnTestResult: {},
        sparkThriftTestResult: {},
        carbonTestResult: {},
        libraSqlTestResult: {},
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
    // refreshSaveEngine () {
    //     const { mode } = this.props.location.state;
    //     if (mode === 'new') {
    //         this.props.updateEngineList(enginelist)
    //     }
    // }
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
                case COMPONENT_TYPE_VALUE.LIBRASQL: {
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
    // 新增入口刷调用？
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
                            const resource = hadoopConf.resource || {};
                            const hadoopComponentData = hadoopConf.components || []; // 组件信息
                            const libraComponentData = libraConf.components || [];
                            let componentConf = this.exChangeComponentConf(hadoopComponentData, libraComponentData);
                            componentConf = JSON.parse(componentConf)
                            console.log(componentConf)
                            const flinkData = componentConf.flinkConf;
                            const extParams = this.exchangeServerParams(componentConf)
                            const flinkConf = componentConf.flinkConf;
                            myUpperCase(flinkConf);
                            // this.props.updateHadoopComponentList(hadoopComponentData) // dispatch engines
                            this.setState({
                                // checked: true,
                                allComponentConf: componentConf,
                                securityStatus: hadoopConf.security,
                                core: resource.totalCore,
                                memory: resource.totalMemory,
                                nodeNumber: resource.totalNode,
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
                            // this.props.updateEngineList([])
                        }
                    }
                )
        } else {
            // 新增集群填充集群名称、节点数、资源数
        }
    }

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
        setDefault(sparkThriftConfig, notExtKeysSparkThrift, 'sparkThrift', result.sparkThriftKeys)
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
        const { cluster } = this.props.location.state || {};
        const file = e.target;
        this.setState({ file: {}, uploadLoading: true, zipConfig: '', fileHaveChange: true });
        Api.uploadResource({
            resources: file.files[0],
            clusterId: cluster.id || cluster.clusterId,
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
        const { cluster } = this.props.location.state || {};
        if (checkValue) {
            Api.uploadResource({
                resources: '',
                clusterId: cluster.id || cluster.clusterId,
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
    /**
     * 校验组件必填项未填标识 *
     */
    validateAllRequired = () => {
        const { hadoopComponentList, libraComponentList } = this.props.consoleUser;
        const tabCompData = this.state.engineId == ENGINE_TYPE.HADOOP ? hadoopComponentList : libraComponentList; // 不同engine的组件数据
        let obj = {}
        tabCompData && tabCompData.map(item => {
            this.props.form.validateFields(validateCompParams(item.componentTypeCode), {}, (err, values) => {
                if (item.componentTypeCode === COMPONENT_TYPE_VALUE.FLINK) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            flinkShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            flinkShowRequired: true
                        })
                    }
                } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            hiveShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            hiveShowRequired: true
                        })
                    }
                } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.CARBONDATA) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            carbonShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            carbonShowRequired: true
                        })
                    }
                } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.SPARK) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            sparkShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            sparkShowRequired: true
                        })
                    }
                } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.DTYARNSHELL) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            dtYarnShellShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            dtYarnShellShowRequired: true
                        })
                    }
                } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.LEARNING) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            learningShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            learningShowRequired: true
                        })
                    }
                } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.HDFS) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            hdfsShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            hdfsShowRequired: true
                        })
                    }
                } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.YARN) {
                    if (!err) {
                        obj = Object.assign(obj, {
                            yarnShowRequired: false
                        })
                    } else {
                        obj = Object.assign(obj, {
                            yarnShowRequired: true
                        })
                    }
                } else if (item.componentTypeCode === COMPONENT_TYPE_VALUE.LIBRASQL) {
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
                return null
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
    matchCompTest (testResult) {
        switch (testResult.result) {
            case TEST_STATUS.SUCCESS: {
                return <Icon className='success-icon' type="check-circle" />
            }
            case TEST_STATUS.FAIL: {
                return <Tooltip
                    title={
                        <a
                            style={{ color: '#fff' }}
                            onClick={ this.showDetailErrMessage.bind(this, testResult)}
                        >
                            {testResult.errorMsg}
                        </a>
                    }
                    placement='right'

                >
                    <Icon className='err-icon' type="close-circle" />
                </Tooltip>
            }
            default: {
                return null
            }
        }
    }

    // 组件成功失败图标
    renderTestIcon = (componentValue) => {
        const { flinkTestResult,
            sparkThriftTestResult,
            carbonTestResult,
            sparkTestResult,
            dtYarnShellTestResult,
            learningTestResult,
            hdfsTestResult,
            yarnTestResult,
            libraSqlTestResult } = this.state;
        switch (componentValue) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                return this.matchCompTest(flinkTestResult)
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                return this.matchCompTest(sparkThriftTestResult)
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                return this.matchCompTest(carbonTestResult)
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                return this.matchCompTest(sparkTestResult)
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                return this.matchCompTest(dtYarnShellTestResult)
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                return this.matchCompTest(learningTestResult)
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                return this.matchCompTest(hdfsTestResult)
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                return this.matchCompTest(yarnTestResult)
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                return this.matchCompTest(libraSqlTestResult)
            }
            default: {
                return null
            }
        }
    }
    /* eslint-disable */
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
    /* eslint-enable */
    // 添加自定义参数
    showAddCustomParam = (isView, type) => {
        return (
            isView ? null : (
                <Row>
                    <Col span={formItemLayout.labelCol.sm.span}></Col>
                    <Col className="m-card" span={formItemLayout.wrapperCol.sm.span}>
                        <a onClick={this.addParam.bind(this, type)}>添加自定义参数</a>
                    </Col>
                </Row>
            )
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
    showTestResult = (testResults) => {
        let testStatus = {}
        testResults && testResults.map(comp => {
            switch (comp.componentTypeCode) {
                case COMPONENT_TYPE_VALUE.FLINK: {
                    testStatus = Object.assign(testStatus, {
                        flinkTestResult: comp
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: {
                    testStatus = Object.assign(testStatus, {
                        sparkThriftTestResult: comp
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.CARBONDATA: {
                    testStatus = Object.assign(testStatus, {
                        carbonTestResult: comp
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.SPARK: {
                    testStatus = Object.assign(testStatus, {
                        sparkTestResult: comp
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                    testStatus = Object.assign(testStatus, {
                        dtYarnShellTestResult: comp
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.LEARNING: {
                    testStatus = Object.assign(testStatus, {
                        learningTestResult: comp
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.HDFS: {
                    testStatus = Object.assign(testStatus, {
                        hdfsTestResult: comp
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.YARN: {
                    testStatus = Object.assign(testStatus, {
                        yarnTestResult: comp
                    })
                    break;
                }
                case COMPONENT_TYPE_VALUE.LIBRASQL: {
                    testStatus = Object.assign(testStatus, {
                        libraSqlTestResult: comp
                    })
                    break;
                }
                default: {
                    testStatus = Object.assign(testStatus, {})
                }
            }
        })
        return testStatus
    }
    /**
     * 保存组件
     */
    saveComponent (componentValue) {
        const { getFieldsValue } = this.props.form;
        const componentConf = this.getComponentConf(getFieldsValue());
        Api.saveComponent({
            engineId: this.state.engineId,
            configString: JSON.stringify(componentConf[getComponentConfKey(componentValue)])
        }).then(res => {
            if (res.code === 1) {
                this.renderTestIcon()
                this.setState({
                    ...this.showTestResult(res.data.testResults)
                })
                message.success(`${componentValue}保存成功`)
            }
        })
    }
    addComponent (params) {
        const { canSubmit, reqParams } = params
        if (canSubmit) {
            Api.addComponent({
                engineId: reqParams.engineId,
                componentTypeCodeList: reqParams.componentTypeCodeList
            }).then(res => {
                if (res.code === 1) {
                    // 添加dispatch updateEngineList
                    // componentTypeCodeList获取value, 无法获取新增加的组件详细信息，需后端返回
                    let { hadoopComponentList } = this.props.consoleUser;
                    const compList = hadoopComponentList.concat(res.data)
                    this.props.updateHadoopComponentList(compList)
                    this.closeAddModal()
                    message.success('添加组件成功!')
                }
            })
        }
    }
    addEngine (params) {
        const { cluster } = this.props.location.state || {};
        const { canSubmit, reqParams } = params;
        if (canSubmit) {
            Api.addEngine({
                clusterId: cluster.id || cluster.clusterId,
                engineName: reqParams.engineName,
                componentTypeCodeList: reqParams.componentTypeCodeList
            }).then(res => {
                if (res.code === 1) {
                    // 添加dispatch updateEngineList
                    this.props.updateEngineList(res.data)
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
    test () {
        this.props.form.validateFields(null, {}, (err, values) => {
            if (!err) {
                this.setState({
                    flinkShowRequired: false,
                    sparkShowRequired: false,
                    dtYarnShellShowRequired: false,
                    learningShowRequired: false,
                    hdfsShowRequired: false,
                    yarnShowRequired: false,
                    hiveShowRequired: false,
                    carbonShowRequired: false,
                    libraShowRequired: false,
                    allTestLoading: true
                })
                const componentConf = this.getComponentConf(values);
                Api.testComponent({
                    componentConfigs: JSON.stringify(componentConf)
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
                                    ...this.showTestResult(testResults),
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
                    ...this.validateAllRequired() // 出现红标
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
                break;
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
        const { engineId } = this.state;
        const isHadoop = engineId == ENGINE_TYPE.HADOOP;
        return (
            <React.Fragment>
                {isView ? null : (
                    <div className={ isHadoop ? 'config-bottom-long' : 'config-bottom-short' }>
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
            </React.Fragment>
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
    /**
     * LIBA不显示集群信息，hadoop查看不显示配置文件radio
     */
    displayResource = (engineType) => {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { file, uploadLoading, core, nodeNumber, memory, fileHaveChange } = this.state;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view';
        return engineType == ENGINE_TYPE.HADOOP ? <Card className='shadow' style={{ margin: '20 20 10 20' }} noHovering>
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
        </Card> : null
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
                            <>
                                {this.renderExtraParam('sparkThrift')}
                                {this.showAddCustomParam(isView, 'sparkThrift')}
                            </>
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
                                {this.showAddCustomParam(isView, 'flink')}
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
                                {this.showAddCustomParam(isView, 'spark')}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.SPARK)}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                return (
                    <LearningConfig
                        getFieldDecorator={getFieldDecorator}
                        isView={isView}
                        customView={(
                            <div>
                                {this.renderExtraParam('learning')}
                                {this.showAddCustomParam(isView, 'learning')}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.LEARNING)}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                return (
                    <DtyarnShellConfig
                        getFieldDecorator={getFieldDecorator}
                        isView={isView}
                        securityStatus={securityStatus}
                        customView={(
                            <div>
                                {this.renderExtraParam('dtyarnshell')}
                                {this.showAddCustomParam(isView, 'dtyarnshell')}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.DTYARNSHELL)}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                return (
                    <LibraSqlConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        customView={(
                            <div>
                                <div className="engine-config-content" style={{ width: '680px' }}>
                                    {this.renderExtraParam('libra')}
                                    {this.showAddCustomParam(isView, 'libra')}
                                </div>
                                {this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.LIBRASQL)}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, COMPONENT_TYPE_VALUE.LIBRASQL)}
                    />
                )
            }
            default:
                return <div>目前暂无该组件配置</div>
        }
    }
    render () {
        const { allTestLoading } = this.state;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view';
        const { engineList, hadoopComponentList, libraComponentList } = this.props.consoleUser;
        const tabCompData = this.state.engineId == ENGINE_TYPE.HADOOP ? hadoopComponentList : libraComponentList; // 不同engine的组件数据
        return (
            <div className='console-wrapper'>
                <div>
                    <p className='back-icon'><GoBack size="default" type="textButton" style={{ fontSize: '14px', color: '#333333' }}></GoBack></p>
                    <div className='config-title'>集群信息</div>
                </div>
                <Tabs
                    defaultActiveKey={engineList && `${engineList[0].engineId}`}
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
                                    <React.Fragment>
                                        {this.displayResource(item.engineId)}
                                        {
                                            isView ? null : (
                                                <div style={{ margin: '0 20 0 20', textAlign: 'right' }}>
                                                    <Button onClick={() => {
                                                        this.setState({
                                                            modalKey: Math.random(),
                                                            addComponentVisible: true
                                                        })
                                                    }} type="primary" style={{ marginLeft: '5px' }}>增加组件</Button>
                                                    <Button onClick={() => {
                                                        this.setState({
                                                            editModalKey: Math.random(),
                                                            addEngineVisible: true
                                                        })
                                                    }} type="primary" style={{ marginLeft: '5px' }}>增加引擎</Button>
                                                    <Button onClick={this.test.bind(this, null)} loading={allTestLoading} type="primary" style={{ marginLeft: '5px' }}>测试全部连通性</Button>
                                                </div>
                                            )
                                        }
                                        {/* 组件配置 */}
                                        <Card
                                            className='shadow console-tabs cluster-tab-width'
                                            style={{ margin: '10 20 20 20' }}
                                            noHovering
                                        >
                                            <Tabs
                                                defaultActiveKey={tabCompData && `${tabCompData[0].componentTypeCode}`}
                                                tabPosition='left'
                                            >
                                                {/* 循环组件tabPane */}
                                                {
                                                    tabCompData && tabCompData.map(item => {
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
                                                                <div style={{ height: '550', paddingBottom: '190px', overflow: 'auto' }}>
                                                                    {this.renderComponentConf(item.componentTypeCode)}
                                                                </div>
                                                            </TabPane>
                                                        )
                                                    })
                                                }
                                            </Tabs>
                                        </Card>
                                    </React.Fragment>
                                </TabPane>
                            )
                        })
                    }
                </Tabs>
                <AddCommModal
                    key={this.state.editModalKey}
                    title='增加引擎'
                    isAddCluster={false}
                    visible={this.state.addEngineVisible}
                    onCancel={() => this.onCancel()}
                    onOk={this.addEngine.bind(this)}
                />
                <AddCommModal
                    key={this.state.modalKey}
                    title='增加组件'
                    isRequired={true}
                    isAddCluster={false}
                    isAddComp={true}
                    visible={this.state.addComponentVisible}
                    onCancel={() => { this.closeAddModal() }}
                    onOk={this.addComponent.bind(this)}
                />
            </div>
        )
    }
}
export default Form.create()(EditCluster);
