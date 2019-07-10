import React from 'react';
import { cloneDeep } from 'lodash';
import { connect } from 'react-redux';
import { Form, Input, Row, Col, Icon, Button, message, Card, Tabs, Modal } from 'antd';
import Api from '../../../api/console'
import { getComponentConfKey, exChangeComponentConf, showTestResult, validateAllRequired,
    myUpperCase, myLowerCase, toChsKeys } from '../../../consts/clusterFunc';
import { formItemLayout, ENGINE_TYPE, COMPONENT_TYPE_VALUE, SPARK_KEY_MAP,
    SPARK_KEY_MAP_DOTS, DEFAULT_COMP_TEST, DEFAULT_COMP_REQUIRED,
    DTYARNSHELL_KEY_MAP, DTYARNSHELL_KEY_MAP_DOTS, notExtKeysFlink, notExtKeysSpark, notExtKeysLearning,
    notExtKeysDtyarnShell, notExtKeysSparkThrift, notExtKeysLibraSql } from '../../../consts';
import { updateTestStatus, updateRequiredStatus } from '../../../reducers/modules/cluster';
import GoBack from 'main/components/go-back';
import SparkConfig from './sparkConfig'
import FlinkConfig from './flinkConfig';
import LearningConfig from './learningConfig';
import DtyarnShellConfig from './dtYarnshellConfig';
import LibraSqlConfig from './libraSqlConfig';
import ZipConfig from './zipConfig';
import { SparkThriftConfig, CarbonDataConfig } from './sparkThriftAndCarbonData';
import AddCommModal from '../../../components/addCommModal';
import RequiredIcon from '../../../components/requiredIcon';
import TestRestIcon from '../../../components/testResultIcon';
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const confirm = Modal.confirm;
function giveMeAKey () {
    return (new Date().getTime() + '' + ~~(Math.random() * 100000))
}
@connect(state => {
    return {
        testStatus: state.testStatus,
        showRequireStatus: state.showRequireStatus
    }
}, dispatch => {
    return {
        updateTestStatus: (data) => {
            dispatch(updateTestStatus(data))
        },
        updateRequiredStatus: (data) => {
            dispatch(updateRequiredStatus(data))
        }
    }
})
class EditCluster extends React.Component {
    state = {
        clusterData: {},
        engineList: [],
        hadoopEngineData: {},
        libraEngineData: {},
        hadoopComponentData: [],
        libraComponentData: [],
        selectUserMap: {},
        selectUser: '', // select输入value
        file: '', // 上传的文件
        zipConfig: '', // 解析的配置文件信息
        securityStatus: false, // 根据配置文件是否显示spark， flink等其他参数
        uploadLoading: false, // 上传loading
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
        defaultEngineType: ENGINE_TYPE.HADOOP, // 默认hadoop engineType
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
        modalKey: null, // 初始化key不要一样
        allTestLoading: false // 测试连通性loading
    }

    componentDidMount () {
        this.getDataList();
        this.props.updateTestStatus(DEFAULT_COMP_TEST)
        this.props.updateRequiredStatus(DEFAULT_COMP_REQUIRED)
    }
    /**
     * set formData
     * @param engineType 引擎类型
     * @param compConf 组件配置
     */
    setFormDataConf = (engineType, compConf) => {
        const isHadoop = engineType == ENGINE_TYPE.HADOOP;
        const { setFieldsValue } = this.props.form;
        let copyComp = cloneDeep(compConf);
        for (let key in copyComp) {
            if (key == 'sparkConf') {
                copyComp[key] = toChsKeys(copyComp[key] || {}, SPARK_KEY_MAP)
            }
            if (key == 'learningConf') {
                copyComp[key] = myUpperCase(copyComp[key])
            }
            if (key == 'dtyarnshellConf') {
                copyComp[key] = toChsKeys(copyComp[key] || {}, DTYARNSHELL_KEY_MAP)
            }
            if (key == 'hadoopConf') { // 由于上传文件的hdfs yarn不是form数据，不做set
                delete copyComp[key]
            }
            if (key == 'yarnConf') {
                delete copyComp[key]
            }
            if (key == 'libraConf') {
                delete copyComp[key]
            }
        }
        if (isHadoop) {
            setFieldsValue(copyComp)
        } else {
            setFieldsValue({
                libraConf: compConf.libraConf
            })
        }
    }
    // 填充表单数据
    getDataList (engineType) {
        const { location } = this.props;
        const params = location.state || {};
        Api.getClusterInfo({
            clusterId: params.cluster.id || params.cluster.clusterId
        })
            .then(
                (res) => {
                    if (res.code == 1) {
                        const data = res.data;
                        const enginesData = data.engines || [];
                        const activeKey = engineType || enginesData[0].engineType;
                        const hadoopConf = enginesData.find(item => item.engineType == ENGINE_TYPE.HADOOP) || {}; // hadoop engine 总数据
                        const libraConf = enginesData.find(item => item.engineType == ENGINE_TYPE.LIBRA) || {}; // libra engine 总数据
                        const resource = hadoopConf.resource || {};
                        const hadoopComponentData = hadoopConf.components || []; // 组件信息
                        const libraComponentData = libraConf.components || [];
                        let componentConf = exChangeComponentConf(hadoopComponentData, libraComponentData);
                        const flinkData = componentConf.flinkConf;
                        const extParams = this.exchangeServerParams(componentConf)
                        const flinkConf = componentConf.flinkConf;
                        myUpperCase(flinkConf);
                        this.setState({
                            clusterData: data,
                            allComponentConf: componentConf,
                            engineList: enginesData,
                            hadoopEngineData: hadoopConf,
                            libraEngineData: libraConf,
                            hadoopComponentData,
                            libraComponentData,
                            defaultEngineType: activeKey,
                            securityStatus: hadoopConf.security,
                            core: resource.totalCore,
                            memory: resource.totalMemory,
                            nodeNumber: resource.totalNode,
                            zipConfig: JSON.stringify({
                                yarnConf: componentConf.yarnConf,
                                hadoopConf: componentConf.hadoopConf,
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
                        this.setFormDataConf(activeKey, componentConf);
                    }
                }
            )
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
        let hiveConfig = config.hiveConf || {};
        let learningConfig = config.learningConf || {};
        let dtyarnshellConfig = config.dtyarnshellConf || {};
        let libraConfig = config.libraConf || {};
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
        setDefault(hiveConfig, notExtKeysSparkThrift, 'sparkThrift', result.sparkThriftKeys)
        setDefault(learningConfig, notExtKeysLearning, 'learning', result.learningKeys)
        setDefault(dtyarnshellConfig, notExtKeysDtyarnShell, 'dtyarnshell', result.dtyarnshellKeys)
        setDefault(libraConfig, notExtKeysLibraSql, 'libra', result.libraSqlKeys)
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
                        const conf = res.data.componentConfig;
                        this.setState({
                            uploadLoading: false,
                            file: file,
                            securityStatus: res.data.security,
                            zipConfig: {
                                hadoopConf: conf.HDFS,
                                yarnConf: conf.YARN
                            }
                        })
                    } else {
                        this.props.form.setFieldsValue({
                            file: {}
                        })
                        this.setState({
                            uploadLoading: false
                        })
                    }
                }
            )
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
        } else if (type == 'dtyarnshell') {
            tmpParams = dtyarnshell_params
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
    saveComponent (component) {
        const { getFieldsValue } = this.props.form;
        const componentConf = this.getComponentConf(getFieldsValue());
        Api.saveComponent({
            componentId: component.componentId,
            configString: JSON.stringify(componentConf[getComponentConfKey(component.componentTypeCode)])
        }).then(res => {
            if (res.code === 1) {
                this.getDataList(this.state.defaultEngineType);
                message.success(`${component.componentName}保存成功`)
            }
        })
    }
    addComponent (params) {
        const { canSubmit, reqParams } = params
        if (canSubmit) {
            Api.addComponent({
                engineId: this.state.hadoopEngineData.engineId,
                componentTypeCodeList: reqParams.componentTypeCodeList
            }).then(res => {
                if (res.code === 1) {
                    this.getDataList(this.state.defaultEngineType);
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
                    this.onCancel()
                    this.getDataList(this.state.defaultEngineType);
                    message.success('添加引擎成功!')
                }
            })
        }
    }
    /**
     * 测试全部连通性
     * @param componentValue 组件类型值
     */
    test () {
        const { updateRequiredStatus, updateTestStatus } = this.props;
        this.props.form.validateFields(null, {}, (err, values) => {
            if (!err) {
                updateRequiredStatus(DEFAULT_COMP_REQUIRED)
                this.setState({
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
                                const testCompResult = showTestResult(testResults, this.state.defaultEngineType);
                                this.setState({
                                    nodeNumber: description ? description.totalNode : 0,
                                    core: description ? description.totalCores : 0,
                                    memory: description ? description.totalMemory : 0,
                                    allTestLoading: false
                                })
                                updateTestStatus(testCompResult)
                            } else {
                                this.setState({
                                    allTestLoading: false
                                })
                            }
                        }
                    )
            } else {
                const { hadoopComponentData, libraComponentData, defaultEngineType } = this.state;
                const tabCompData = defaultEngineType == ENGINE_TYPE.HADOOP ? hadoopComponentData : libraComponentData;
                const requiredStatus = validateAllRequired(this.props.form.validateFields, tabCompData);
                updateRequiredStatus(requiredStatus);
                message.error('你有必填配置项未填写！')
            }
        })
    }
    // 取消操作
    handleCancel (component) {
        const { form } = this.props;
        const { allComponentConf } = this.state;
        switch (component.componentTypeCode) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                form.setFieldsValue({
                    flinkConf: allComponentConf.flinkConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                form.setFieldsValue({
                    hiveConf: allComponentConf.hiveConf
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
                        hadoopConf: allComponentConf.hadoopConf,
                        yarnConf: allComponentConf.yarnConf
                    })
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                this.setState({
                    zipConfig: JSON.stringify({
                        hadoopConf: allComponentConf.hadoopConf,
                        yarnConf: allComponentConf.yarnConf
                    })
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.LIBRASQL: {
                form.setFieldsValue({
                    libraConf: allComponentConf.libraConf
                })
                break;
            }
        }
    }
    showDeleteConfirm (component) {
        const { componentName } = component
        confirm({
            title: `是否确定删除${componentName}组件？`,
            okText: '是',
            okType: 'danger',
            cancelText: '否',
            onOk: () => {
                this.deleteComponent(component)
            },
            onCancel () {
                console.log('cancel')
            }
        })
    }

    deleteComponent (component) {
        const { componentTypeCode, componentName, componentId } = component;
        if (componentTypeCode == COMPONENT_TYPE_VALUE.FLINK ||
            componentTypeCode == COMPONENT_TYPE_VALUE.SPARK ||
            componentTypeCode == COMPONENT_TYPE_VALUE.LEARNING ||
            componentTypeCode == COMPONENT_TYPE_VALUE.DTYARNSHELL ||
            componentTypeCode == COMPONENT_TYPE_VALUE.CARBONDATA) {
            Api.deleteComponent({
                componentId: componentId
            }).then(res => {
                if (res.code === 1) {
                    this.getDataList(this.state.defaultEngineType)
                    message.success(`${componentName}删除组件成功！`)
                }
            })
        } else {
            message.error(`${componentName}不允许删除！`)
        }
    }
    onTabChange = (key) => {
        const { allComponentConf } = this.state;
        this.setState({
            defaultEngineType: key
        }, () => {
            this.setFormDataConf(key, allComponentConf)
        })
    }
    /**
     * 引擎配置模块底部 测试连通性、取消、保存、删除 Button
     * @param isView 是否显示
     * @param componentValue 组件
     */
    renderExtFooter = (isView, component) => {
        const { defaultEngineType } = this.state;
        const isHadoop = defaultEngineType == ENGINE_TYPE.HADOOP;
        return (
            <React.Fragment>
                {isView ? null : (
                    <div className={ isHadoop ? 'config-bottom-long' : 'config-bottom-short' }>
                        <Row>
                            <Col span={4}></Col>
                            <Col span={formItemLayout.wrapperCol.sm.span}>
                                <span>
                                    <Button onClick={this.saveComponent.bind(this, component)} style={{ marginLeft: '5px' }} type="primary">保存</Button>
                                    <Button onClick={this.handleCancel.bind(this, component)} style={{ marginLeft: '5px' }}>取消</Button>
                                    <Button type="danger" style={{ marginLeft: '5px' }} onClick={this.showDeleteConfirm.bind(this, component)}>删除</Button>
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
        zipConfig = typeof zipConfig == 'string' ? JSON.parse(zipConfig) : zipConfig
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
        componentConf['hadoopConf'] = zipConfig.hadoopConf;
        componentConf['yarnConf'] = zipConfig.yarnConf;
        componentConf['hiveMeta'] = zipConfig.hiveMeta;
        componentConf['hiveConf'] = { ...formValues.hiveConf, ...sparkThriftExtParams } || {};
        componentConf['carbonConf'] = formValues.carbonConf || {};
        componentConf['sparkConf'] = { ...toChsKeys(formValues.sparkConf || {}, SPARK_KEY_MAP_DOTS), ...sparkExtParams };
        componentConf['flinkConf'] = { ...formValues.flinkConf, ...flinkExtParams };
        componentConf['learningConf'] = { ...learningTypeName, ...myLowerCase(formValues.learningConf), ...learningExtParams };
        componentConf['dtyarnshellConf'] = { ...dtyarnshellTypeName, ...toChsKeys(formValues.dtyarnshellConf || {}, DTYARNSHELL_KEY_MAP_DOTS), ...dtyarnshellExtParams };
        componentConf['libraConf'] = { ...formValues.libraConf, ...libraExtParams };
        // 服务端兼容，不允许null
        componentConf['hiveConf'].username = componentConf['hiveConf'].username || '';
        componentConf['hiveConf'].password = componentConf['hiveConf'].password || '';
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
        const { getFieldDecorator } = this.props.form;
        const { clusterData, file, uploadLoading, core, nodeNumber, memory } = this.state;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view';
        return engineType == ENGINE_TYPE.HADOOP ? <Card className='shadow' style={{ margin: '20 20 10 20' }} noHovering>
            <div style={{ marginTop: '20px', borderBottom: '1px dashed #DDDDDD' }}>
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
                                }],
                                initialValue: clusterData.clusterName
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
                    <React.Fragment>
                        <div className="upload-file">
                            <div className='upload-title'>上传配置文件</div>
                            <p style={{ marginBottom: '24px' }}>您需要获取Hadoop、Spark、Flink集群的配置文件，至少包括：<strong>core-site.xml、hdfs-site.xml、hive-site.xml、yarn-site.xml</strong>文件</p>
                            <Row style={{ marginLeft: '8px' }}>
                                <Col span={14} pull={2}>
                                    <FormItem
                                        label="配置文件"
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('file', {
                                            rules: [{
                                                // required: !(!fileHaveChange && mode == 'edit'), message: '请选择上传文件'
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
                                </Col>
                            </Row>
                            {/* 暂无控制台帮助文档 */}
                            <div className='upload-help'>如何获取这些配置文件？请您参考<span>《帮助文档》</span></div>
                        </div>
                    </React.Fragment>
                )
            }
        </Card> : null
    }
    // 渲染 Component Config
    renderComponentConf = (component) => {
        const { checked, securityStatus, zipConfig } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view';
        const { gatewayHostValue, gatewayPortValue, gatewayJobNameValue, deleteOnShutdownOption, randomJobNameSuffixOption } = this.state;
        switch (component.componentTypeCode) {
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
                        singleButton={this.renderExtFooter(isView, component)}
                    />
                )
            }
            // hdfs 和 yarn 配置从配置文件中获取
            case COMPONENT_TYPE_VALUE.HDFS: {
                return (
                    zipConfig ? (
                        <ZipConfig
                            zipConfig={this.state.zipConfig}
                            type='hdfs'
                            singleButton={this.renderExtFooter(isView, component)}
                        />
                    ) : null
                )
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                return (
                    zipConfig ? (
                        <ZipConfig
                            zipConfig={this.state.zipConfig}
                            type='yarn'
                            singleButton={this.renderExtFooter(isView, component)}
                        />
                    ) : null
                )
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                return (
                    <CarbonDataConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        singleButton={this.renderExtFooter(isView, component)}
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
                        singleButton={this.renderExtFooter(isView, component)}
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
                        singleButton={this.renderExtFooter(isView, component)}
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
                        singleButton={this.renderExtFooter(isView, component)}
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
                        singleButton={this.renderExtFooter(isView, component)}
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
                                <div className="engine-config-content">
                                    {this.renderExtraParam('libra')}
                                    {this.showAddCustomParam(isView, 'libra')}
                                </div>
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, component)}
                    />
                )
            }
            default:
                return <div>目前暂无该组件配置</div>
        }
    }
    render () {
        const { allTestLoading, hadoopComponentData, libraComponentData,
            engineList, defaultEngineType } = this.state;
        const { mode } = this.props.location.state || {};
        const isView = mode == 'view';
        const tabCompData = defaultEngineType == ENGINE_TYPE.HADOOP ? hadoopComponentData : libraComponentData; // 不同engine的组件数据
        return (
            <div className='console-wrapper'>
                <div>
                    <p className='back-icon'><GoBack size="default" type="textButton" style={{ fontSize: '14px', color: '#333333' }}></GoBack></p>
                    <div className='config-title'>集群信息</div>
                </div>
                <Tabs
                    // defaultActiveKey={`${defaultEngineType}`}
                    activeKey={`${defaultEngineType}`}
                    tabPosition='top'
                    onChange={this.onTabChange}
                >
                    {
                        engineList && engineList.map((item, index) => {
                            const { engineType } = item;
                            const isHadoop = engineType == ENGINE_TYPE.HADOOP;
                            return (
                                <TabPane
                                    tab={item.engineName}
                                    key={`${engineType}`}
                                >
                                    <React.Fragment>
                                        {this.displayResource(engineType)}
                                        {
                                            isView ? null : (
                                                <div style={{ margin: '5 20 0 20', textAlign: 'right' }}>
                                                    {isHadoop && <Button onClick={() => {
                                                        this.setState({
                                                            modalKey: Math.random(),
                                                            addComponentVisible: true
                                                        })
                                                    }} type="primary" style={{ marginLeft: '5px' }}>增加组件</Button>}
                                                    <Button onClick={() => {
                                                        this.setState({
                                                            editModalKey: Math.random(),
                                                            addEngineVisible: true
                                                        })
                                                    }} type="primary" style={{ marginLeft: '5px' }}>增加引擎</Button>
                                                    <Button onClick={this.test.bind(this)} loading={allTestLoading} type="primary" style={{ float: 'left' }}>测试全部连通性</Button>
                                                </div>
                                            )
                                        }
                                        {/* 组件配置 */}
                                        <Card
                                            className='shadow console-tabs cluster-tab-width'
                                            style={{ margin: '10 20 20 20', height: isHadoop ? '500' : 'calc(100% - 50px)' }}
                                            noHovering
                                        >
                                            <Tabs
                                                // defaultActiveKey={tabCompData && `${tabCompData[0].componentTypeCode}`}
                                                tabPosition='left'
                                            >
                                                {
                                                    tabCompData && tabCompData.map((item, index) => {
                                                        const { componentTypeCode } = item;
                                                        return (
                                                            <TabPane
                                                                tab={
                                                                    <span>
                                                                        <RequiredIcon componentData={item} showRequireStatus={this.props.showRequireStatus}/>
                                                                        <span className='tab-title'>{item.componentName}</span>
                                                                        <TestRestIcon componentData={item} testStatus={this.props.testStatus}/>
                                                                    </span>
                                                                }
                                                                forceRender={true}
                                                                key={`${componentTypeCode}`}
                                                            >
                                                                <div className={isHadoop ? 'tabpane-content-max' : 'tabpane-content-min'}>
                                                                    {this.renderComponentConf(item)}
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
                    engineList={engineList}
                    onCancel={() => this.onCancel()}
                    onOk={this.addEngine.bind(this)}
                />
                <AddCommModal
                    key={this.state.modalKey}
                    title='增加组件'
                    isAddCluster={false}
                    isAddComp={true}
                    visible={this.state.addComponentVisible}
                    hadoopComponentData={hadoopComponentData}
                    onCancel={() => { this.closeAddModal() }}
                    onOk={this.addComponent.bind(this)}
                />
            </div>
        )
    }
}
export default Form.create()(EditCluster);
