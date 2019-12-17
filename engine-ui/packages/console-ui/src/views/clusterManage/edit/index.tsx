/* eslint-disable @typescript-eslint/camelcase */
import * as React from 'react';
import { cloneDeep, mapValues, findKey } from 'lodash';
import { connect } from 'react-redux';
import { Form, Input, Row, Col, Icon, Button, message, Card, Tabs,
    Modal, Upload, Tooltip, Switch, Select } from 'antd';
import Api from '../../../api/console'
import moment from 'moment'

import { exChangeComponentConf,
    showTestResult, validateAllRequired,
    myUpperCase, myLowerCase, toChsKeys,
    isHadoopEngine } from '../../../consts/clusterFunc';

import { formItemLayout, ENGINE_TYPE, COMPONENT_TYPE_VALUE, SPARK_KEY_MAP, COMPONEMT_CONFIG_KEYS, COMPONEMT_CONFIG_KEY_ENUM,
    SPARK_KEY_MAP_DOTS, FLINK_KEY_MAP, FLINK_KEY_MAP_DOTS,
    DEFAULT_COMP_TEST, DEFAULT_COMP_REQUIRED,
    DTYARNSHELL_KEY_MAP, DTYARNSHELL_KEY_MAP_DOTS,
    notExtKeysFlink, notExtKeysSpark, notExtKeysHiveServer,
    notExtKeysLearning, notExtKeysDtyarnShell,
    notExtKeysSparkThrift, notExtKeysLibraSql } from '../../../consts';

import { updateTestStatus, updateRequiredStatus } from '../../../reducers/modules/cluster';
import GoBack from 'dt-common/src/components/go-back';
import SparkConfig from './sparkConfig'
import FlinkConfig from './flinkConfig';
import LearningConfig from './learningConfig';
import DtyarnShellConfig from './dtYarnshellConfig';
import HiveServerConfig from './hiveServerConfig';
import LibraSqlConfig from './libraSqlConfig';
import ZipConfig from './zipConfig';
import ImpalaSQLConfig from './impalaSQLConfig';
import { SparkThriftConfig, CarbonDataConfig } from './sparkThriftAndCarbonData';
import SftpConfig from './sftpConfig';
import AddCommModal from '../../../components/addCommModal';
import RequiredIcon from '../../../components/requiredIcon';
import TestRestIcon from '../../../components/testResultIcon';

const Option = Select.Option;
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const confirm = Modal.confirm;
function giveMeAKey () {
    return (new Date().getTime() + '' + ~~(Math.random() * 100000))
}
let timer: any = null;
@(connect((state: any) => {
    return {
        testStatus: state.testStatus,
        showRequireStatus: state.showRequireStatus
    }
}, (dispatch: any) => {
    return {
        updateTestStatus: (data: any) => {
            dispatch(updateTestStatus(data))
        },
        updateRequiredStatus: (data: any) => {
            dispatch(updateRequiredStatus(data))
        }
    }
}) as any)
class EditCluster extends React.Component<any, any> {
    kfile: any;
    state: any = {
        clusterData: {},
        selectUserMap: {},
        selectUser: '', // select输入value
        file: '', // 上传的文件
        kfile: '', // 上传的kerberos文件
        zipConfig: '{}', // 解析的配置文件信息
        securityStatus: false, // 根据配置文件是否显示spark， flink等其他参数
        uploadLoading: false, // 上传loading
        uploadKLoading: false, // 上传kerberos loading
        flink_params: [],
        spark_params: [],
        sparkThrif_params: [],
        hiveServer_params: [],
        learning_params: [],
        dtyarnshell_params: [],
        libraSql_params: [],
        sftp_params: [],
        core: null,
        nodeNumber: null,
        memory: null,
        extDefaultValue: {},
        fileHaveChange: false,
        checked: false,
        isZookeeper: false,
        allComponentConf: {},
        engineTypeKey: ENGINE_TYPE.HADOOP, // 默认hadoop engineType
        flinkPrometheus: undefined, // 配置Prometheus参数
        flinkData: undefined, // 获取Prometheus参数
        addEngineVisible: false, // 新增引擎modal
        addComponentVisible: false,
        editModalKey: '',
        modalKey: null, // 初始化key不要一样
        allTestLoading: false // 测试连通性loading
    }
    container: any;

    componentDidMount () {
        this.getDataList();
        this.props.updateTestStatus(DEFAULT_COMP_TEST)
        this.props.updateRequiredStatus(DEFAULT_COMP_REQUIRED)
        this.getKerberosFile();
    }

    /**
     * set formData （转化服务端数据）
     * @param engineType 引擎类型
     * @param compConf 组件配置
     */
    setFormDataConf = (engineType: any, compConf: any) => {
        const isHadoop = isHadoopEngine(engineType);
        const { setFieldsValue } = this.props.form;
        let copyComp = cloneDeep(compConf);
        for (let key in copyComp) {
            if (key == COMPONEMT_CONFIG_KEYS.SPARK) {
                const { typeName } = copyComp[key];
                const typeArr = (typeName && typeName.split('-')) || [];
                copyComp[key] = Object.assign({}, toChsKeys(copyComp[key] || {}, SPARK_KEY_MAP), {
                    typeName: typeArr.length > 1 ? `${typeArr[0]}-${typeArr[1]}` : 'spark-yarn'
                })
            }
            if (key == COMPONEMT_CONFIG_KEYS.FLINK) {
                const { typeName } = copyComp[key];
                const typeArr = (typeName && typeName.split('-')) || [];
                copyComp[key] = Object.assign({}, toChsKeys(copyComp[key] || {}, FLINK_KEY_MAP), {
                    typeName: typeArr[0] || 'flink140'
                })
            }
            if (key == COMPONEMT_CONFIG_KEYS.LEARNING) {
                copyComp[key] = myUpperCase(copyComp[key])
            }
            if (key == COMPONEMT_CONFIG_KEYS.DTYARNSHELL) {
                copyComp[key] = toChsKeys(copyComp[key] || {}, DTYARNSHELL_KEY_MAP)
            }
            if (key == COMPONEMT_CONFIG_KEYS.HDFS) { // 由于上传文件的hdfs yarn不是form数据，不做set
                delete copyComp[key]
            }
            if (key == COMPONEMT_CONFIG_KEYS.YARN) {
                delete copyComp[key]
            }
            if (key == COMPONEMT_CONFIG_KEYS.LIBRASQL) {
                delete copyComp[key]
            }
        }
        console.log(copyComp, compConf);
        if (isHadoop) {
            setFieldsValue(copyComp);
            for (let i in copyComp) {
                if (copyComp[i].kerberosFile) {
                    setFieldsValue({
                        [i]: {
                            ...copyComp[i]
                            // openKerberos: copyComp[i].openKerberos,
                            // kerberosFile: copyComp[i].kerberosFile
                        }
                    })
                }
                if (i === 'sftpConf' || copyComp[i].rsaPath) {
                    timer = setTimeout(() => {
                        setFieldsValue({
                            [i]: {
                                ...copyComp[i]
                            }
                        })
                        clearTimeout(timer);
                        timer = null;
                    })
                }
            }
        } else {
            setFieldsValue({
                libraConf: compConf.libraConf
            })
        }
    }
    /**
     * 组件数据
     */
    getComponetData (clusterData: any, type: any) {
        const enginesData = clusterData.engines || [];
        const hadoopConf = enginesData.find((item: any) => item.engineType == ENGINE_TYPE.HADOOP) || {}; // hadoop engine 总数据
        const libraConf = enginesData.find((item: any) => item.engineType == ENGINE_TYPE.LIBRA) || {}; // libra engine 总数据
        switch (type) {
            case ENGINE_TYPE.HADOOP: {
                return hadoopConf.components || []
            }
            case ENGINE_TYPE.LIBRA: {
                return libraConf.components || []
            }
        }
    }
    // 获取kerberos文件信息
    deleteKerberosFile () {
        const { location } = this.props;
        const params = location.state || {};
        const clusterId = params.cluster.id || params.cluster.clusterId;
        Api.deleteKerberos({
            clusterId
        })
            .then(
                (res: any) => {
                    console.log(res);
                    if (res.code === 1) {
                        message.success(`删除Kerberos认证文件成功`)
                        this.getKerberosFile();
                    }
                }
            )
    }
    // 获取kerberos文件信息
    getKerberosFile () {
        const { location } = this.props;
        const params = location.state || {};
        const clusterId = params.cluster.id || params.cluster.clusterId;
        Api.getKerberosFile({
            clusterId
        })
            .then(
                (res: any) => {
                    console.log(res);
                    const data = res.data;
                    if (data && data.name) {
                        this.setState({
                            kfile: {
                                files: [res.data]
                            }
                        })
                    } else {
                        this.setState({
                            kfile: ''
                        })
                    }
                }
            )
    }

    // 更新hadoop版本
    updateHadoopVersion = () => {
        const { location } = this.props;
        const params = location.state || {};
        const hadoopVersion = this.props.form.getFieldValue('hadoopVersion');
        Api.updateHadoopVersion({
            clusterId: params.cluster.id || params.cluster.clusterId,
            hadoopVersion: hadoopVersion
        }).then(res => {
            if (res.code === 1) {
                message.success('集群版本保存成功！')
            }
        })
    }
    // 填充表单数据
    getDataList (engineType?: any) {
        const { location } = this.props;
        const params = location.state || {};
        Api.getClusterInfo({
            clusterId: params.cluster.id || params.cluster.clusterId
        })
            .then(
                (res: any) => {
                    if (res.code == 1) {
                        const data = res.data || {};
                        const enginesData = data.engines || [];
                        const activeKey = engineType || (enginesData[0] && enginesData[0].engineType)
                        const hadoopConf = enginesData.find((item: any) => item.engineType == ENGINE_TYPE.HADOOP) || {};
                        const resource = hadoopConf.resource || {}; // 资源信息
                        const hadoopComponentData = this.getComponetData(data, ENGINE_TYPE.HADOOP);
                        const libraComponentData = this.getComponetData(data, ENGINE_TYPE.LIBRA)
                        let componentConf = exChangeComponentConf(hadoopComponentData, libraComponentData) || {}; // 所有引擎数据组合
                        const flinkData = componentConf.flinkConf;
                        const extParams = this.exchangeServerParams(componentConf)
                        componentConf.flinkConf = myUpperCase(flinkData);
                        this.setState({
                            clusterData: data,
                            allComponentConf: componentConf,
                            engineTypeKey: activeKey,
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
                            hiveServer_params: extParams.hiveServerKeys,
                            learning_params: extParams.learningKeys,
                            dtyarnshell_params: extParams.dtyarnshellKeys,
                            libraSql_params: extParams.libraSqlKeys,
                            extDefaultValue: extParams.default,
                            flinkPrometheus: componentConf.flinkConf,
                            flinkData: flinkData
                        })
                        // 判断是有Prometheus参数
                        if (flinkData && flinkData.hasOwnProperty('metrics.reporter.promgateway.class')) {
                            this.setState({
                                checked: true
                            })
                        }
                        if (flinkData && flinkData.hasOwnProperty('high-availability.zookeeper.quorum')) {
                            this.setState({
                                isZookeeper: true
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
    exchangeServerParams (config: any) {
        let result: any = {
            flinkKeys: [],
            sparkKeys: [],
            sparkThriftKeys: [],
            hiveServerKeys: [],
            learningKeys: [],
            dtyarnshellKeys: [],
            libraSqlKeys: [],
            default: {}
        };
        let flinkConfig = config.flinkConf || {};
        let sparkConfig = config.sparkConf || {};
        let hiveConfig = config.hiveConf || {};
        let hiveServerConfig = config.hiveServerConf || {};
        let learningConfig = config.learningConf || {};
        let dtyarnshellConfig = config.dtscriptConf || {};
        let libraConfig = config.libraConf || {};
        function setDefault (config: any, notExtKeys: any, type: any, keys: any) {
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
        setDefault(hiveServerConfig, notExtKeysHiveServer, 'hiveServer', result.hiveServerKeys)
        setDefault(learningConfig, notExtKeysLearning, 'learning', result.learningKeys)
        setDefault(dtyarnshellConfig, notExtKeysDtyarnShell, 'dtyarnshell', result.dtyarnshellKeys)
        setDefault(libraConfig, notExtKeysLibraSql, 'libra', result.libraSqlKeys)
        return result;
    }
    validateFileType (val: string) {
        let flag = false;
        const reg = /\.(zip)$/
        if (val && !reg.test(val.toLocaleLowerCase())) {
            message.warning('配置文件只能是zip文件!');
        } else {
            flag = true;
        }
        return flag
    }
    fileChange (e: any) {
        const { cluster } = this.props.location.state || {} as any;
        const file = e.target;
        const isCanUpload = this.validateFileType(file.files && file.files[0].name)
        if (isCanUpload) {
            this.props.form.setFieldsValue({
                file: ''
            })
            this.setState({ uploadLoading: true, zipConfig: '{}', fileHaveChange: true });
            Api.uploadResource({
                resources: file.files[0],
                clusterId: cluster.id || cluster.clusterId,
                useDefaultConfig: false
            })
                .then(
                    (res: any) => {
                        if (res.code == 1) {
                            const conf = res.data.componentConfig;
                            this.setState({
                                uploadLoading: false,
                                file: file,
                                securityStatus: res.data.security,
                                zipConfig: {
                                    hadoopConf: Object.assign({}, conf.HDFS, {
                                        md5zip: conf.md5zip
                                    }),
                                    yarnConf: conf.YARN
                                }
                            })
                        } else {
                            // 清空 value
                            const ele: any = document.getElementById('myOfflinFile');
                            ele.value = '';
                            this.props.form.setFieldsValue({
                                file: ''
                            })
                            this.setState({
                                uploadLoading: false,
                                zipConfig: '{}'
                            })
                        }
                    }
                )
        }
    }
    kfileChange (e: any) {
        const { cluster } = this.props.location.state || {} as any;
        const kfile = e.target;
        console.log(kfile)
        if (kfile.files.length > 0) {
            this.setState({ uploadKLoading: true });
            Api.uploadKerberosFile({
                kerberosFile: kfile.files[0],
                clusterId: cluster.id || cluster.clusterId
            })
                .then(
                    (res: any) => {
                        console.log(res.code)
                        if (res.code == 1) {
                            // console.log('1111111')
                            this.setState({
                                uploadKLoading: false,
                                kfile: {
                                    files: [
                                        {
                                            name: kfile.files[0].name
                                        }
                                    ]
                                }
                            })
                        } else {
                            // console.log('2222222222')
                            this.setState({
                                uploadKLoading: false
                            })
                        }
                        this.kfile.value = '';
                    }
                )
        }
    }
    /* eslint-disable */
    addParam(type: any) {
        const { flink_params, spark_params, sparkThrif_params,
            hiveServer_params, learning_params,
            dtyarnshell_params, libraSql_params } = this.state;
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
        } else if (type == 'hiveServer') {
            this.setState({
                hiveServer_params: [...hiveServer_params, {
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
    deleteParam (id: any, type: any) {
        const { flink_params, spark_params, sparkThrif_params,
            hiveServer_params, learning_params,
            dtyarnshell_params, libraSql_params } = this.state;
        let tmpParams: any;
        let tmpStateName: any;
        if (type == 'flink') {
            tmpStateName = 'flink_params';
            tmpParams = flink_params;
        } else if (type == 'spark') {
            tmpStateName = 'spark_params';
            tmpParams = spark_params;
        } else if (type == 'sparkThrift') {
            tmpStateName = 'sparkThrif_params';
            tmpParams = sparkThrif_params;
        } else if (type == 'hiveServer') {
            tmpStateName = 'hiveServer_params';
            tmpParams = hiveServer_params;
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
            (param: any) => {
                return param.id != id;
            }
        );
        this.setState({
            [tmpStateName]: tmpParams
        })
    }
    /**
     * 渲染组件额外参数
     */
    renderExtraParam(type: any) {
        const { flink_params, spark_params, sparkThrif_params,
            hiveServer_params, learning_params, dtyarnshell_params,
            libraSql_params, extDefaultValue } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { mode } = this.props.location.state || {} as any;
        const isView = mode == 'view'
        let tmpParams: any;
        if (type == 'flink') {
            tmpParams = flink_params;
        } else if (type == 'spark') {
            tmpParams = spark_params;
        } else if (type == 'learning') {
            tmpParams = learning_params;
        } else if (type == 'sparkThrift') {
            tmpParams = sparkThrif_params
        } else if (type == 'hiveServer') {
            tmpParams = hiveServer_params
        } else if (type == 'libra') {
            tmpParams = libraSql_params
        } else if (type == 'dtyarnshell') {
            tmpParams = dtyarnshell_params
        } else {
            tmpParams = dtyarnshell_params;
        }
        return tmpParams.map(
            (param: any) => {
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
    showAddCustomParam = (isView: any, type: any) => {
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
    exchangeMemory (totalMemory: any) {
        if (!totalMemory) {
            return '--';
        }
        const memory = totalMemory / 1024;
        const haveDot = Math.floor(memory) != memory
        return `${haveDot ? memory.toFixed(2) : memory}GB`
    }
    saveComponent (component: any) {
        const { validateFieldsAndScroll } = this.props.form;
        const { cluster } = this.props.location.state || {} as any;
        validateFieldsAndScroll((err: any, values: any) => {
            if (err) {
                let paramName = COMPONEMT_CONFIG_KEY_ENUM[component.componentTypeCode];
                if (Object.keys(err).includes(paramName)) {
                    message.error('请检查配置')
                    return;
                }
            }
            const componentConf = this.getComponentConf(values);
            const saveConfig = componentConf[COMPONEMT_CONFIG_KEY_ENUM[component.componentTypeCode]];
            console.log(component, componentConf, saveConfig)
            if (saveConfig && saveConfig.openKerberos && saveConfig.kerberosFile) {
                const kerberosFile = saveConfig.kerberosFile
                delete saveConfig.kerberosFile;
                Api.saveComponentWithKerberos({
                    componentId: component.componentId,
                    configString: JSON.stringify(saveConfig),
                    clusterId: cluster.id || cluster.clusterId,
                    kerberosFile
                }).then((res: any) => {
                    if (res.code === 1) {
                        // 避免上传配置文件的组件hdfs、yarn保存之后会导致另一项组件数据清空，这里不请求数据
                        // this.getDataList(this.state.engineTypeKey);
                        message.success(`${component.componentName}保存成功`)
                    }
                })
            } else {
                if (saveConfig.openKerberos && !saveConfig.kerberosFile) {
                    message.error('开启kerberos认证之后，必须上传文件才能保存');
                } else {
                    saveConfig && delete saveConfig.kerberosFile;
                    Api.saveComponent({
                        clusterId: cluster.id || cluster.clusterId,
                        componentId: component.componentId,
                        configString: JSON.stringify(saveConfig)
                    }).then((res: any) => {
                        if (res.code === 1) {
                            // 避免上传配置文件的组件hdfs、yarn保存之后会导致另一项组件数据清空，这里不请求数据
                            // this.getDataList(this.state.engineTypeKey);
                            message.success(`${component.componentName}保存成功`)
                        }
                    });
                }
            }
        })
    }
    addComponent (params: any) {
        const { canSubmit, reqParams } = params;
        const { clusterData, engineTypeKey } = this.state;
        const enginesData = clusterData.engines || [];
        const hadoopConf = enginesData.find((item: any) => item.engineType == ENGINE_TYPE.HADOOP) || {};
        if (canSubmit) {
            Api.addComponent({
                engineId: hadoopConf.engineId,
                componentTypeCodeList: reqParams.componentTypeCodeList
            }).then((res: any) => {
                if (res.code === 1) {
                    this.getDataList(engineTypeKey);
                    this.closeAddModal()
                    message.success('添加组件成功!')
                }
            })
        }
    }
    addEngine (params: any) {
        const { cluster } = this.props.location.state || {} as any;
        const { canSubmit, reqParams } = params;
        const { engineTypeKey } = this.state;
        if (canSubmit) {
            Api.addEngine({
                clusterId: cluster.id || cluster.clusterId,
                engineName: reqParams.engineName,
                componentTypeCodeList: reqParams.componentTypeCodeList
            }).then((res: any) => {
                if (res.code === 1) {
                    this.onCancel()
                    this.getDataList(engineTypeKey);
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
        const { updateRequiredStatus, updateTestStatus, form } = this.props;
        const { cluster } = this.props.location.state || {} as any;
        const { engineTypeKey, clusterData } = this.state;
        // 页面滚动至底部
        // const scroll = () => {
        //     const ele = document.querySelector('#JS_console_container');
        //     ele.scrollTop = 0;
        //     ele.scrollTo({
        //         top: ele.scrollHeight,
        //         behavior: 'smooth'
        //     })
        // }
        // scroll();
        form.validateFields(null, {}, (err: any, values: any) => {
            console.log(values)
            if (!err) {
                updateRequiredStatus(DEFAULT_COMP_REQUIRED)
                this.setState({
                    allTestLoading: true
                })
                let componentConf = this.getComponentConf(values);
                let hasFile = false;
                let fileObj = {}
                componentConf = mapValues(componentConf, (item, key) => {
                    console.log(item, item && item.kerberosFile)
                    let newConfig = item
                    if (item && item.kerberosFile) {
                        hasFile = true;
                        fileObj = {
                            ...fileObj,
                            [`${key}KerberosFile`]: item.kerberosFile
                        };
                        delete newConfig.kerberosFile;
                    }
                    return newConfig;
                })
                console.log('startapi', componentConf, cluster)
                if (hasFile) {
                    Api.testComponentKerberos({
                        ...fileObj,
                        clusterId: cluster.id || cluster.clusterId,
                        componentConfigs: JSON.stringify(componentConf)
                    })
                        .then(
                            (res: any) => {
                                if (res.code == 1) {
                                    const { description, testResults } = res.data;
                                    const testCompResult = showTestResult(testResults, engineTypeKey);
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
                        ).finally(
                            () => {
                                this.setState({
                                    allTestLoading: false
                                })
                            }
                        )
                } else {
                    Api.testComponent({
                        clusterId: cluster.id || cluster.clusterId,
                        componentConfigs: JSON.stringify(componentConf)
                    })
                        .then(
                            (res: any) => {
                                if (res.code == 1) {
                                    const { description, testResults } = res.data;
                                    const testCompResult = showTestResult(testResults, engineTypeKey);
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
                        ).finally(
                            () => {
                                this.setState({
                                    allTestLoading: false
                                })
                            }
                        )
                }
            } else {
                const hadoopComponentData = this.getComponetData(clusterData, ENGINE_TYPE.HADOOP);
                const libraComponentData = this.getComponetData(clusterData, ENGINE_TYPE.LIBRA);
                const tabCompData = isHadoopEngine(engineTypeKey) ? hadoopComponentData : libraComponentData;
                const requiredStatus = validateAllRequired(form.validateFields, tabCompData);
                updateRequiredStatus(requiredStatus);
                message.error('你有必填配置项未填写！')
            }
        })
    }
    // 取消操作
    handleCancel (component: any) {
        const { form } = this.props;
        const { allComponentConf } = this.state;
        switch (component.componentTypeCode) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                const flinkObj = toChsKeys(allComponentConf.flinkConf || {}, FLINK_KEY_MAP);
                console.log(flinkObj)
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEYS.FLINK]: {
                        ...flinkObj
                    }
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER]: allComponentConf.hiveConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEYS.CARBONDATA]: allComponentConf.carbonConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.IMPALASQL: {
                form.setFieldsValue({
                    impalaSqlConf: allComponentConf.impalaSqlConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.HIVESERVER: {
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEYS.HIVESERVER]: allComponentConf.hiveServerConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEYS.SPARK]: toChsKeys(allComponentConf.sparkConf || {}, SPARK_KEY_MAP)
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEYS.DTYARNSHELL]: toChsKeys(allComponentConf.dtscriptConf || {}, DTYARNSHELL_KEY_MAP)
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEYS.LEARNING]: myUpperCase(allComponentConf.learningConf)
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
                    [COMPONEMT_CONFIG_KEYS.LIBRASQL]: allComponentConf.libraConf
                })
                break;
            }
            case COMPONENT_TYPE_VALUE.SFTP: {
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEYS.SFTP]: allComponentConf.sftpConf
                })
                break;
            }
        }
    }
    showDeleteConfirm (component: any) {
        console.log(this.state.clusterData, component)
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

    deleteComponent (component: any) {
        const { componentTypeCode, componentName, componentId } = component;
        if (componentTypeCode == COMPONENT_TYPE_VALUE.HDFS ||
            componentTypeCode == COMPONENT_TYPE_VALUE.YARN ||
            componentTypeCode == COMPONENT_TYPE_VALUE.LIBRASQL || COMPONENT_TYPE_VALUE.SFTP) {
            message.error(`${componentName}不允许删除！`)
        } else {
            Api.deleteComponent({
                componentId: componentId
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        engineTypeKey: ENGINE_TYPE.HADOOP
                    }, () => {
                        this.getDataList(ENGINE_TYPE.HADOOP)
                    })
                    message.success(`${componentName}删除组件成功！`)
                }
            })
        }
    }
    onTabChange = (key: any) => {
        const { allComponentConf } = this.state;
        this.setState({
            engineTypeKey: key
        }, () => {
            this.setFormDataConf(key, allComponentConf)
        })
    }
    /**
     * 引擎配置模块底部 测试连通性、取消、保存、删除 Button
     * @param isView 是否显示
     * @param componentValue 组件
     */
    renderExtFooter = (isView: any, component: any) => {
        return (
            <React.Fragment>
                {isView ? null : (
                    <div className='config-bottom-long'>
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
    /**
     * 转换成服务端可用数据
     */
    getComponentConf (formValues: any) {
        let { zipConfig } = this.state;
        zipConfig = typeof zipConfig == 'string' ? JSON.parse(zipConfig) : zipConfig
        let componentConf: any = {};
        console.log(formValues)
        const sparkExtParams = this.getCustomParams(formValues, 'spark')
        const flinkExtParams = this.getCustomParams(formValues, 'flink')
        const sparkThriftExtParams = this.getCustomParams(formValues, 'sparkThrift')
        const learningExtParams = this.getCustomParams(formValues, 'learning');
        const hiveServerExtParams = this.getCustomParams(formValues, 'hiveServer');
        const dtyarnshellExtParams = this.getCustomParams(formValues, 'dtyarnshell');
        const libraExtParams = this.getCustomParams(formValues, 'libra');
        const learningTypeName: any = {
            typeName: `learning`
        }
        const dtyarnshellTypeName: any = {
            typeName: `dtscript`
        }
        // md5zip 随hdfs组件一起保存
        componentConf['hadoopConf'] = zipConfig.hadoopConf;
        componentConf['yarnConf'] = zipConfig.yarnConf;
        componentConf['hiveMeta'] = zipConfig.hiveMeta;
        componentConf['hiveConf'] = { ...formValues.hiveConf, ...sparkThriftExtParams } || {};
        componentConf['carbonConf'] = formValues.carbonConf || {};
        componentConf['impalaSqlConf'] = formValues.impalaSqlConf || {};
        componentConf['hiveServerConf'] = { ...formValues.hiveServerConf, ...hiveServerExtParams } || {};
        componentConf['sparkConf'] = { ...toChsKeys(formValues.sparkConf || {}, SPARK_KEY_MAP_DOTS), ...sparkExtParams };
        componentConf['flinkConf'] = { ...toChsKeys({ ...formValues.flinkConf } || {}, FLINK_KEY_MAP_DOTS), ...flinkExtParams };
        componentConf['learningConf'] = { ...learningTypeName, ...myLowerCase(formValues.learningConf), ...learningExtParams };
        componentConf['dtscriptConf'] = { ...dtyarnshellTypeName, ...toChsKeys(formValues.dtscriptConf || {}, DTYARNSHELL_KEY_MAP_DOTS), ...dtyarnshellExtParams };
        componentConf['libraConf'] = { ...formValues.libraConf, ...libraExtParams };
        componentConf['sftpConf'] = formValues.sftpConf || {};
        // 服务端兼容，不允许null
        componentConf['hiveConf'].username = componentConf['hiveConf'].username || '';
        componentConf['hiveConf'].password = componentConf['hiveConf'].password || '';
        componentConf['carbonConf'].username = componentConf['carbonConf'].username || '';
        componentConf['carbonConf'].password = componentConf['carbonConf'].password || '';
        componentConf['impalaSqlConf'].username = componentConf['impalaSqlConf'].username || '';
        componentConf['impalaSqlConf'].password = componentConf['impalaSqlConf'].password || '';
        componentConf['hiveServerConf'].username = componentConf['hiveServerConf'].username || '';
        componentConf['hiveServerConf'].password = componentConf['hiveServerConf'].password || '';
        return componentConf;
    }
    getCustomParams (data: any, ParamKey: any) {
        let params: any = {};
        let tmpParam: any = {};
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
        console.log(params)
        return params;
    }
    // 解决切换 配置Prometheus Metric地址 数据消失
    getPrometheusValue = () => {
        const { flinkPrometheus, flinkData } = this.state;
        const { form } = this.props;
        const { mode } = this.props.location.state || {} as any;
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
    changeCheckbox (e: any) {
        this.setState({
            checked: e.target.checked
        }, () => {
            if (this.state.checked) {
                this.getPrometheusValue()
            }
        })
    }
    onChangeZookeeper (value: string) {
        /* eslint-disable-next-line */
        this.setState({ isZookeeper: value == 'NONE' ? false : true })
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
    /**
     * LIBA不显示集群信息
     */
    displayResource = (engineType: any) => {
        const { getFieldDecorator } = this.props.form;
        const { clusterData, file, uploadLoading, core, nodeNumber, memory, uploadKLoading, kfile } = this.state;
        const { mode } = this.props.location.state || {} as any;
        const isView = mode == 'view';
        const markStyle = {
            marginTop: '20px',
            borderBottom: !isView && '1px dashed #DDDDDD'
        }
        return isHadoopEngine(engineType) ? <Card className='shadow' style={{ margin: '20px 20px 10px 20px' }} noHovering>
            <div style={markStyle}>
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
                <Row>
                    <Col span={14} pull={2}>
                        <FormItem
                            label="集群版本"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('hadoopVersion', {
                                rules: [{
                                    required: true,
                                    message: '请选择集群版本'
                                }],
                                initialValue: clusterData.hadoopVersion || 'hadoop2'
                            })(
                                <Select style={{ width: '200px', marginRight: '10px' }} disabled={isView}>
                                    <Option value='hadoop2' key='hadoop2'>hadoop2</Option>
                                    <Option value='hadoop3' key='hadoop3'>hadoop3</Option>
                                    <Option value='HW' key='HW'>HW</Option>
                                </Select>

                            )}
                            <a onClick={() => {
                                this.updateHadoopVersion();
                            }} {...{ disabled: isView }}>保存</a>
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
                            <Row>
                                <Col span={24}>
                                    <FormItem
                                        label={null}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('file', null)(
                                            <div>
                                                {
                                                    uploadLoading
                                                        ? <label
                                                            style={{ lineHeight: '28px' }}
                                                            className="ant-btn disble"
                                                        >上传文件</label>
                                                        : <label
                                                            style={{ lineHeight: '28px', textIndent: 'initial' }}
                                                            className="ant-btn"
                                                            htmlFor="myOfflinFile">
                                                            <span><Icon type="upload" />上传文件</span>
                                                        </label>
                                                }
                                                {uploadLoading ? <Icon className="blue-loading" type="loading" /> : null}
                                                <span> {file.files && file.files[0] && file.files[0].name}</span>
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
                            <Row>
                                <Col span={24}>
                                    <FormItem
                                        label={null}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator('kerberosFile', {
                                            rules: [{
                                            }]
                                        })(
                                            <div
                                                style={{
                                                    display: 'flex',
                                                    alignItems: 'center'
                                                }}
                                            >
                                                <div
                                                    style={{
                                                        marginRight: '10px'
                                                    }}
                                                >
                                                    Haddoop Kerberos认证文件:
                                                </div>
                                                {
                                                    uploadKLoading
                                                        ? <label
                                                            style={{ lineHeight: '28px' }}
                                                            className="ant-btn disble"
                                                        >上传文件</label>
                                                        : <label
                                                            style={{ lineHeight: '28px', textIndent: 'initial' }}
                                                            className="ant-btn"
                                                            htmlFor="kerberosFiles">
                                                            <span><Icon type="upload" />上传文件</span>
                                                        </label>
                                                }
                                                {uploadKLoading ? <Icon className="blue-loading" type="loading" /> : null}
                                                {/* <span> {kfile.files && kfile.files.length > 0 && kfile.files[0].name}</span> */}
                                                <div
                                                    style={{
                                                        display: 'flex',
                                                        alignItems: 'center'
                                                    }}
                                                >
                                                    {kfile.files && kfile.files.length > 0 && kfile.files[0].name}
                                                    {
                                                        kfile.files && kfile.files.length > 0
                                                            ? (
                                                                <Icon
                                                                    type="close-circle"
                                                                    style={{
                                                                        cursor: 'pointer'
                                                                    }}
                                                                    onClick={() => {
                                                                        console.log('delete')
                                                                        this.deleteKerberosFile();
                                                                    }}
                                                                />
                                                            )
                                                            : null
                                                    }
                                                </div>
                                                <input
                                                    name="file"
                                                    type="file"
                                                    ref={(e) => { this.kfile = e }}
                                                    id="kerberosFiles"
                                                    onChange={this.kfileChange.bind(this)}
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

    // 开启kerberos认证
    uploadForm = (key: any) => {
        const { form } = this.props;
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
        const formValue = getFieldValue(`${key}.kerberosFile`)
        const keyNum = COMPONENT_TYPE_VALUE[findKey(COMPONEMT_CONFIG_KEYS, (item) => { return item === key })]
        // console.log(formValue, this.props.form.getFieldsValue())
        const upProps = {
            beforeUpload: (file: any) => {
                file.modifyTime = moment();
                console.log(file);
                setFieldsValue({
                    [`${key}.kerberosFile`]: file
                })
                return false;
            },
            fileList: nullArr,
            name: 'file',
            accept: '.zip'
        };
        return (
            <React.Fragment>
                <FormItem
                    {...formItemLayout}
                    label="开启Kerberos认证"
                    key={`${key}.openKerberos`}
                >
                    {getFieldDecorator(`${key}.openKerberos`, {
                        valuePropName: 'checked',
                        initialValue: false
                    })(
                        <Switch
                            onChange={(checked) => {
                                if (!checked) {
                                    setFieldsValue({
                                        [`${key}.kerberosFile`]: ''
                                    })
                                }
                            }}
                        />
                    )}
                </FormItem>
                {
                    getFieldValue(`${key}.openKerberos`)
                        ? (
                            <Row
                                style={{
                                    visibility: getFieldValue(`${key}.openKerberos`) ? null : 'hidden',
                                    height: getFieldValue(`${key}.openKerberos`) ? null : 0,
                                    overflow: 'hidden'
                                }}
                            >
                                <Col span={6}/>
                                <Col span={14}>
                                    <FormItem
                                        {...formNewLayout}
                                        key={`${key}.kerberosFile`}
                                        label=""
                                        // style={{
                                        //     margin: 0
                                        // }}
                                    >
                                        {getFieldDecorator(`${key}.kerberosFile`, {
                                            rules: [{
                                                required: getFieldValue(`${key}.openKerberos`), message: '文件不可为空！'
                                            }],
                                            initialValue: ''
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
                                                href={`/api/console/download/component/downloadKerberosXML?componentType=${keyNum}`}
                                                download
                                            >
                                                <div
                                                    style={{ color: '#0099ff', cursor: 'pointer', marginLeft: '10px' }}
                                                    onClick={() => {
                                                        console.log(key, keyNum)
                                                    }}
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
                                            formValue
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
                                                                    [`${key}.kerberosFile`]: ''
                                                                })
                                                            }}
                                                        />
                                                        <Input value={formValue.name + '   ' + moment(formValue.modifyTime).format('YYYY-MM-DD HH:mm:ss')}/>
                                                    </div>
                                                )
                                                : null
                                        }
                                    </FormItem>
                                </Col>
                            </Row>
                        )
                        : null
                }
            </React.Fragment>
        );
    }

    /**
     * 渲染 Component Config
     */
    renderComponentConf = (component: any) => {
        const { checked, securityStatus, zipConfig, isZookeeper } = this.state;
        const { getFieldDecorator, getFieldValue, setFieldsValue, resetFields } = this.props.form;
        const { mode } = this.props.location.state || {} as any;
        const isView = mode == 'view';
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
                        kerberosView={this.uploadForm('hiveConf')}
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
                        kerberosView={this.uploadForm('carbonConf')}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.IMPALASQL: {
                return (
                    <ImpalaSQLConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        singleButton={this.renderExtFooter(isView, component)}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.HIVESERVER: {
                return (
                    <HiveServerConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        customView={(
                            <>
                                {this.renderExtraParam('hiveServer')}
                                {this.showAddCustomParam(isView, 'hiveServer')}
                            </>
                        )}
                        singleButton={this.renderExtFooter(isView, component)}
                        kerberosView={this.uploadForm('hiveServerConf')}
                    />
                )
            }
            case COMPONENT_TYPE_VALUE.FLINK: {
                return (
                    <FlinkConfig
                        isView={isView}
                        getFieldDecorator={getFieldDecorator}
                        getFieldValue={getFieldValue}
                        setFieldsValue={setFieldsValue}
                        resetFields={resetFields}
                        securityStatus={securityStatus}
                        checked={checked}
                        isZookeeper={isZookeeper}
                        onChangeZookeeper={this.onChangeZookeeper.bind(this)}
                        changeCheckbox={this.changeCheckbox.bind(this)}
                        customView={(
                            <div>
                                {this.renderExtraParam('flink')}
                                {this.showAddCustomParam(isView, 'flink')}
                            </div>
                        )}
                        singleButton={this.renderExtFooter(isView, component)}
                        kerberosView={this.uploadForm('flinkConf')}
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
                        kerberosView={this.uploadForm('sparkConf')}
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
                        kerberosView={this.uploadForm('learningConf')}
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
                        kerberosView={this.uploadForm('dtscriptConf')}
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
            case COMPONENT_TYPE_VALUE.SFTP: {
                return (
                    <SftpConfig
                        isView={isView}
                        getFieldValue={getFieldValue}
                        setFieldsValue={setFieldsValue}
                        getFieldDecorator={getFieldDecorator}
                        singleButton={this.renderExtFooter(isView, component)}
                    />
                )
            }
            default:
                return <div>目前暂无该组件配置</div>
        }
    }
    render () {
        const { clusterData, allTestLoading,
            engineTypeKey, editModalKey, modalKey,
            addEngineVisible, addComponentVisible } = this.state;
        const { mode } = this.props.location.state || {} as any;
        const isView = mode == 'view';
        const hadoopComponentData = this.getComponetData(clusterData, ENGINE_TYPE.HADOOP);
        const libraComponentData = this.getComponetData(clusterData, ENGINE_TYPE.LIBRA);
        const tabCompData = isHadoopEngine(engineTypeKey) ? hadoopComponentData : libraComponentData; // 不同engine的组件数据
        // const tabCompData = isHadoopEngine(engineTypeKey) ? [...hadoopComponentData, { componentId: 2321, componentName: 'SFTP', componentTypeCode: 10, config: {} }] : libraComponentData; // 不同engine的组件数据
        const engineList = clusterData.engines || [];
        // console.log(this.state, tabCompData)
        return (
            <div className='console-wrapper' ref={(el) => { this.container = el; }}>
                <div>
                    <p className='back-icon'><GoBack size="default" type="textButton" style={{ fontSize: '14px', color: '#333333' }}></GoBack></p>
                    <div className='config-title'>集群信息</div>
                </div>
                <Tabs
                    activeKey={`${engineTypeKey}`}
                    tabPosition='top'
                    onChange={this.onTabChange}
                >
                    {
                        engineList && engineList.map((item: any, index: any) => {
                            const { engineType } = item;
                            const isHadoop = isHadoopEngine(engineType);
                            return (
                                <TabPane
                                    tab={item.engineName}
                                    key={`${engineType}`}
                                >
                                    <React.Fragment>
                                        {this.displayResource(engineType)}
                                        {
                                            isView ? null : (
                                                <div style={{ margin: '5px 20px 0px 20px', textAlign: 'right' }}>
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
                                            className='shadow console-tabs cluster-tab-width console-compontent'
                                            noHovering
                                        >
                                            <Tabs
                                                tabPosition='left'
                                            >
                                                {
                                                    tabCompData && tabCompData.map((item: any, index: any) => {
                                                        const { componentTypeCode } = item;
                                                        const componentName = item.componentName === 'SparkThrift'
                                                            ? 'Spark ThriftServer'
                                                            : (
                                                                item.componentName === 'CarbonData'
                                                                    ? 'CarbonData ThriftServer'
                                                                    : item.componentName
                                                            )
                                                        console.log(item.componentName, componentName);
                                                        return (
                                                            <TabPane
                                                                tab={
                                                                    <span>
                                                                        <RequiredIcon componentData={item} showRequireStatus={this.props.showRequireStatus}/>
                                                                        <span className='tab-title'>{componentName}</span>
                                                                        <TestRestIcon componentData={item} testStatus={this.props.testStatus}/>
                                                                    </span>
                                                                }
                                                                forceRender={true}
                                                                key={`${componentTypeCode}`}
                                                            >
                                                                <div className='tabpane-content-max'>
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
                    key={editModalKey}
                    title='增加引擎'
                    isAddCluster={false}
                    visible={addEngineVisible}
                    engineList={engineList}
                    onCancel={() => this.onCancel()}
                    onOk={this.addEngine.bind(this)}
                />
                <AddCommModal
                    key={modalKey}
                    title='增加组件'
                    isAddCluster={false}
                    isAddComp={true}
                    visible={addComponentVisible}
                    hadoopComponentData={hadoopComponentData}
                    onCancel={() => { this.closeAddModal() }}
                    onOk={this.addComponent.bind(this)}
                />
            </div>
        )
    }
}
export default Form.create<any>()(EditCluster);
