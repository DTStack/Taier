import * as React from 'react';
import { cloneDeep } from 'lodash';
import { hashHistory } from 'react-router';
import {
    Form, Input, Card, Tabs, Button, message,
    notification, Popconfirm } from 'antd';
import Api from '../../../api/console';

import req from '../../../consts/reqUrls';
import {
    TABS_TITLE, TABS_TITLE_KEY, COMPONEMT_CONFIG_NAME_ENUM,
    COMPONEMT_CONFIG_KEY_ENUM, COMPONENT_TYPE_VALUE } from '../../../consts';

import ModifyComponentModal from '../../../components/modifyCompModal';
import SelectPopver from '../../../components/selectPopover';
import TestRestIcon from '../../../components/testResultIcon';
import DisplayResource from './displayResource';
import ComponentsConfig from './componentsConfig';
import dealData from './dealData';

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
function giveMeAKey () {
    return (new Date().getTime() + '' + ~~(Math.random() * 100000))
}

// 渲染组件icon
function renderCompIcon (scheduling: any) {
    switch (scheduling.schedulingCode) {
        case TABS_TITLE_KEY.STORE:
            return (<i className="iconfont iconcunchuzujian" style={{ marginRight: 2 }}></i>)
        case TABS_TITLE_KEY.COMMON:
            return (<i className="iconfont icongonggongzujian" style={{ marginRight: 2 }}></i>)
        case TABS_TITLE_KEY.COMPUTE:
            return (<i className="iconfont iconjisuanzujian" style={{ marginRight: 2 }}></i>)
        case TABS_TITLE_KEY.SOURCE:
            return (<i className="iconfont iconziyuantiaodu" style={{ marginRight: 2 }}></i>)
        default:
            return '';
    }
}

class EditCluster extends React.Component<any, any> {
    state: any = {
        clusterId: '',
        clusterName: '', // 集群名称
        compTypeKey: 0,
        componentConfig: {}, // 各组件配置信息
        cloneComponentConfig: {}, // 备份各组件配置信息
        testStatus: {},
        saveCompsData: [],
        tabCompData: [
            {
                schedulingCode: TABS_TITLE_KEY.COMMON,
                components: []
            },
            {
                schedulingCode: TABS_TITLE_KEY.SOURCE,
                components: []
            },
            {
                schedulingCode: TABS_TITLE_KEY.STORE,
                components: []
            },
            {
                schedulingCode: TABS_TITLE_KEY.COMPUTE,
                components: []
            }
        ], // 集群结构信息
        defaultValue: [], // 组件配置默认选中组件值
        selectValue: [], // 选中组件存值
        deleteComps: [], // 删除组件
        addComps: [], // 新增组件
        versionData: {},
        commonVersion: '',
        popoverVisible: false,
        uploadLoading: false,
        modify: false,
        testLoading: false
    }
    container: any;

    componentDidMount () {
        this.getDataList();
        this.getVersionData();
    }

    getDataList = () => {
        const { tabCompData } = this.state;
        const { cluster } = this.props.location.state || {} as any;
        Api.getClusterInfo({
            clusterId: cluster.clusterId
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tabCompData: res.data.scheduling || tabCompData,
                    clusterName: res.data.clusterName,
                    componentConfig: dealData.handleCompsData(res),
                    cloneComponentConfig: dealData.handleCompsData(res),
                    clusterId: res.clusterId
                })
                this.getSaveComponentList(res?.data?.clusterName)
            }
        })
    }

    getVersionData = () => {
        Api.getVersionData().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    versionData: res.data
                })
            }
        })
    }

    onTabChange = (key: any) => {
        this.setState({
            compTypeKey: Number(key),
            popoverVisible: false
        })
    }

    turnClusteManage = () => {
        hashHistory.push({ pathname: '/console/clusterManage' })
    }

    turnEditComp = () => {
        const { cluster } = this.props.location.state || {} as any;
        this.setState({ testLoading: false })
        hashHistory.push({
            pathname: '/console/clusterManage/editCluster',
            state: {
                mode: 'edit',
                cluster
            }
        })
    }

    getComponentConfig = (components: any) => {
        const { componentConfig } = this.state;
        const componentTypeCode = components.componentTypeCode;
        return componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
    }

    handleCompsVersion = (compVersion: any, componentTypeCode: number) => {
        const { componentConfig } = this.state;
        const { getFieldValue } = this.props?.form;
        const { storeType } = getFieldValue(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])

        this.setState({
            componentConfig: {
                ...componentConfig,
                [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                    ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                    hadoopVersion: compVersion
                }
            }
        })
        this.getLoadTemplate(componentTypeCode, { storeType, compVersion });
    }

    handleCompsCompsData = (storeType: any, componentTypeCode: number) => {
        const { componentConfig } = this.state;
        const { getFieldValue } = this.props.form;
        const values = getFieldValue(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])
        const { hadoopVersion: compVersion } = values

        this.setState({
            componentConfig: {
                ...componentConfig,
                [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                    ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                    storeType: storeType
                }
            }
        })
        this.getLoadTemplate(componentTypeCode, { storeType, compVersion });
    }

    // 获取组件模板
    getLoadTemplate = async (key: any = '', { storeType, compVersion }: any = { storeType: undefined, compVersion: undefined }) => {
        const { compTypeKey, tabCompData, componentConfig, clusterName } = this.state;
        const component = tabCompData?.find((item: any) => item.schedulingCode === compTypeKey) || { components: [] };
        if (component.components.length === 0) return;
        if (typeof storeType === 'undefined') {
            await this.getSaveComponentList(clusterName)
        }
        const { saveCompsData } = this.state
        const length = saveCompsData?.length
        let componentTypeCode = key === '' ? component.components[0].componentTypeCode : key;
        const isNeedLoadTemp = dealData.checkUplaodFileComps(componentTypeCode);
        const isChangeVersion = dealData.changeVersion(componentTypeCode, compVersion);
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {}
        const { loadTemplate = {} } = config;
        const stateStoreType = length === 0 ? undefined : length === 1 ? saveCompsData[0]?.key : 4
        const version = dealData.getCompsVersion(Number(componentTypeCode), compVersion)
        if ((!isNeedLoadTemp && (Object.keys(loadTemplate).length === 0 || isChangeVersion || storeType))) {
            Api.getLoadTemplate({
                clusterName,
                version: compVersion || version,
                storeType: storeType || config.storeType || stateStoreType,
                componentType: componentTypeCode
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        componentConfig: {
                            ...componentConfig,
                            [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                                ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                                loadTemplate: res.data,
                                params: dealData.getLoadTemplateParams(res.data),
                                configInfo: { ...dealData.getCompoentsConfigInfo(res.data) }
                            }
                        }
                    })
                }
            })
        }
    }
    getSaveComponentList= async (clusterName) => {
        const res = await Api.getComponentStore({ clusterName })
        if (!res) return
        const { data = [] } = res
        let saveCompsData = []
        data.forEach(item => {
            saveCompsData.push({
                key: item?.componentTypeCode,
                value: item?.componentName
            })
        })
        await this.setState({
            saveCompsData
        })
        return saveCompsData
    }
    // 组件配置选中的组件，选中组件的值
    selectDefaultValue = () => {
        const { tabCompData, compTypeKey } = this.state;
        const cloneCompData = cloneDeep(tabCompData);
        let defaultValue: any = [];
        cloneCompData.length > 0 && cloneCompData.map((item: any) => {
            if (item.schedulingCode === compTypeKey) {
                item.components.length > 0 && item.components.map((comps: any) => {
                    defaultValue.push(comps.componentTypeCode);
                })
            }
        })
        this.setState({
            defaultValue,
            selectValue: defaultValue
        })
    }
    setRadioCompData = (e: any) => {
        this.setState({
            selectValue: [e.target.value]
        })
    }
    setCheckboxCompData = (value) => {
        this.setState({
            selectValue: [...value]
        })
    }
    // 点击确认后对选中数据进行处理
    modifyTabCompData = () => {
        let deleteComps: any = [];
        let addComps: any = [];
        const { tabCompData, selectValue, compTypeKey, defaultValue } = this.state;
        const cloneSelectValue = cloneDeep(selectValue);
        const components = tabCompData.find((sche: any) => sche.schedulingCode === compTypeKey).components;
        if (cloneSelectValue.sort().toString() === defaultValue.sort().toString()) {
            this.setState({
                popoverVisible: false
            })
            return;
        }
        components.forEach((comps: any) => {
            if (selectValue.findIndex((val: any) => val === comps.componentTypeCode) === -1) {
                const config = this.getComponentConfig(comps);
                deleteComps.push({ ...comps, id: config.id || '' });
            }
        })

        if (components.length === 0) {
            addComps = selectValue;
        } else {
            selectValue.forEach((val: any) => {
                if (components.findIndex((comps: any) => comps.componentTypeCode === val) === -1) {
                    addComps.push(val);
                }
            })
        }
        console.log('delete comps---------add comps', deleteComps, addComps)
        if (deleteComps.length > 0) {
            this.setState({
                popoverVisible: false,
                modify: true,
                deleteComps: deleteComps,
                addComps: addComps
            })
        } else {
            this.handleAddComps(addComps);
        }
    }
    // 清除存储组件数据
    clearCompsConfig = (componentTypeCode: any) => {
        const { componentConfig } = this.state;
        this.setState({
            componentConfig: {
                ...componentConfig,
                [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {}
            },
            testStatus: {
                ...this.state.testStatus,
                [componentTypeCode]: null
            }
        })
    }
    // 删除tabCompData组件和组件存储的配置信息
    clearTabCompData = () => {
        const { tabCompData, compTypeKey, deleteComps, addComps } = this.state;
        let cloneComps = cloneDeep(tabCompData);
        const components = cloneComps.find((sche: any) => sche.schedulingCode === compTypeKey).components;
        deleteComps.forEach((compconent: any) => {
            components.splice(components.findIndex((comps: any) => comps.componentTypeCode === compconent.componentTypeCode), 1);
            this.clearCompsConfig(compconent.componentTypeCode)
        })
        this.setState({
            tabCompData: cloneComps
        }, () => this.handleAddComps(addComps));
    }
    // 处理删除数据
    handleDeleteComps = () => {
        const { deleteComps } = this.state;
        let componentIds: any = [];
        deleteComps.forEach((comps: any) => {
            if (comps.id) { componentIds.push(comps.id) }
        });
        if (componentIds.length === 0) {
            this.setState({
                modify: false
            }, () => this.clearTabCompData());
            return;
        }
        Api.deleteComponent({
            componentIds
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    modify: false
                }, () => this.clearTabCompData());
            } else {
                this.setState({
                    modify: false
                })
            }
        })
    }
    // 处理增加数据
    handleAddComps = (addComps: any) => {
        const { tabCompData, compTypeKey } = this.state;
        const cloneCompData = cloneDeep(tabCompData);
        let components: any = cloneCompData.find((sche: any) => sche.schedulingCode === compTypeKey).components;
        if (addComps.length > 0) {
            addComps.forEach((val: any) => {
                components.push({ componentTypeCode: val, componentName: COMPONEMT_CONFIG_NAME_ENUM[val] });
            })
            cloneCompData.forEach((item: any) => {
                if (item.schedulingCode === compTypeKey) item.components = components
            })
            this.setState({
                tabCompData: cloneCompData,
                popoverVisible: false
            }, () => { this.getLoadTemplate() });
        }
    }

    handleCancleModify = () => {
        this.setState({ modify: false });
    }

    handleCanclePopover = () => {
        this.setState({ popoverVisible: false })
    }

    setPopverVisible = () => {
        this.selectDefaultValue();
        this.setState({ popoverVisible: true });
    }

    validateFileType = (val: string) => {
        let flag = false;
        const reg = /\.(zip)$/
        if (val && !reg.test(val.toLocaleLowerCase())) {
            message.warning('配置文件只能是zip文件!');
        } else {
            flag = true;
        }
        return flag
    }

    fileChange = (e: any, componentTypeCode: any) => {
        const file = e.target;
        // console.log('changefile---------', file.files[0]);
        const isCanUpload = this.validateFileType(file && file.files && file.files[0].name)
        if (isCanUpload) {
            this.setState({ uploadLoading: true });
            Api.uploadResource({
                fileName: file.files[0],
                componentType: componentTypeCode
            }).then((res: any) => {
                if (res.code === 1) {
                    const { componentConfig } = this.state;
                    this.setState({
                        componentConfig: {
                            ...componentConfig,
                            [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                                ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                                uploadFileName: file,
                                fileName: file.files[0].name,
                                configInfo: res.data[0]
                            }
                        }
                    })
                }
                this.setState({
                    uploadLoading: false
                })
            })
        }
    }

    paramsfileChange = (e: any, componentTypeCode: any) => {
        const paramsFile = e.target;
        const { componentConfig } = this.state;
        // console.log('changefile---------', paramsFile.files);
        const { form } = this.props;
        if (paramsFile.files.length === 0) return;
        this.setState({ uploadLoading: true });
        Api.uploadResource({
            fileName: paramsFile.files[0],
            componentType: componentTypeCode
        }).then((res: any) => {
            if (res.code === 1) {
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                        configInfo: { ...dealData.handleBatchParams(res.data[0]) }
                    }
                })
                this.setState({
                    componentConfig: {
                        ...componentConfig,
                        [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                            ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                            paramsFileName: paramsFile.files[0].name
                        }
                    }
                })
            }
            this.setState({
                uploadLoading: false
            });
        })
    }

    // Hadoop Kerberos认证文件Change事件
    kerFileChange = (e: any, componentTypeCode: any) => {
        const kerFile = e.target;
        const { componentConfig } = this.state;
        const isCanUpload = this.validateFileType(kerFile && kerFile.files && kerFile.files[0].name)
        if (isCanUpload) {
            this.setState({
                componentConfig: {
                    ...componentConfig,
                    [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                        ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                        kerberosFileName: kerFile,
                        kerFileName: kerFile.files[0].name
                    }
                }
            });
        }
    }

    // 下载配置文件
    downloadFile = (components: any, type: any) => {
        const { clusterName } = this.state;
        const config = this.getComponentConfig(components);
        const hadoopVersion = this.props.form.getFieldValue(`${COMPONEMT_CONFIG_KEY_ENUM[components.componentTypeCode]}.hadoopVersion`) || '';
        const a = document.createElement('a');
        let param = config.id ? `?componentId=${config.id}&` : '?';
        param = param + `type=${type}&componentType=${components.componentTypeCode}&hadoopVersion=${hadoopVersion}&clusterName=${clusterName}`;
        a.href = `${req.DOWNLOAD_RESOURCE}${param}`;
        a.click();
    }

    deleteKerFile = (componentTypeCode: any) => {
        const { componentConfig } = this.state;
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
        config.id && Api.closeKerberos({
            componentId: config.id
        });
        this.setState({
            componentConfig: {
                ...componentConfig,
                [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                    ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                    kerberosFileName: {},
                    kerFileName: ''
                }
            }
        });
    }

    saveComponent = (components: any) => {
        const { validateFields } = this.props.form;
        const { componentConfig, cloneComponentConfig } = this.state;
        const { cluster } = this.props.location.state || {} as any;
        const componentTypeCode = components.componentTypeCode;
        const config = this.getComponentConfig(components);
        const isFileNameRequire = dealData.checkUplaodFileComps(componentTypeCode);
        validateFields(null, {}, (err: any, values: any) => {
            if (err) {
                let paramName = COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode];
                if (Object.keys(err).includes(paramName)) {
                    message.error('请检查配置');
                    return;
                }
            }
            if (isFileNameRequire && !config.fileName) {
                message.error('请上传配置文件');
                return;
            }
            const params = dealData.getComponentConfigPrames(values, components, config);
            console.log('this is params-----------', params)
            Api.saveComponent({
                ...params,
                clusterId: cluster.clusterId
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        clusterId: res.data.clusterId,
                        clusterName: res.data.clusterName,
                        componentConfig: dealData.updateCompsConfig(componentConfig, componentTypeCode, res),
                        cloneComponentConfig: dealData.updateCompsConfig(cloneComponentConfig, componentTypeCode, res)
                    });
                    message.success('保存成功');
                }
            })
        })
    }

    handleCancel = (components: any) => {
        const { form } = this.props;
        const { componentConfig } = this.state;
        const componentTypeCode = components.componentTypeCode;
        const config = this.getComponentConfig(components);
        const { configInfo = {}, params = [] } = config;
        const isUploadFileComps = dealData.checkUplaodFileComps(componentTypeCode);
        const handleCancelParams = dealData.handleCancleParams(params);
        if (!isUploadFileComps) {
            this.setState({
                componentConfig: {
                    ...componentConfig,
                    [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                        ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                        params: handleCancelParams
                    }
                }
            })
            form.setFieldsValue({
                [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                    configInfo: { ...configInfo }
                }
            })
            message.success('取消成功')
        }
    }

    addParams = (components: any, groupKey: any = '') => {
        const { componentConfig } = this.state;
        const componentTypeCode = components.componentTypeCode;
        const config = this.getComponentConfig(components);
        const { params = [] } = config;
        let newParams = cloneDeep(params)
        if (groupKey) {
            newParams.forEach((param: any) => {
                if (param.key === groupKey) {
                    param.groupParams = [...param.groupParams, { id: giveMeAKey() }]
                }
            })
        } else {
            newParams = [...newParams, { id: giveMeAKey() }]
        }
        // console.log('addParams-------config', config, newParams, params)
        this.setState({
            componentConfig: {
                ...componentConfig,
                [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                    ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                    params: newParams
                }
            }
        })
    }

    deleteParams = (components: any, id: any, groupKey: any = '') => {
        const { componentConfig } = this.state;
        const componentTypeCode = components.componentTypeCode;
        const config = this.getComponentConfig(components);
        const params = config.params || [];
        if (groupKey) {
            params.forEach((p: any) => {
                p.key === groupKey && p.groupParams.splice(p.groupParams.findIndex((param: any) => param.id === id), 1)
            })
        } else {
            params.splice(params.findIndex((param: any) => param.id === id), 1);
        }
        this.setState({
            componentConfig: {
                ...componentConfig,
                [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                    ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                    params: [...params]
                }
            }
        })
    }

    handleNotSaveComps = () => {
        const { validateFieldsAndScroll } = this.props.form;
        validateFieldsAndScroll((err: any, values: any) => {
            console.log(err, values)
            if (err) {
                message.error('请检查配置');
                return;
            }
            // console.log('componentTypeCodeArr=========', componentTypeCodeArr)
            const { cloneComponentConfig } = this.state;
            let modifyCompsArr = dealData.getMoadifyComps(values, cloneComponentConfig);
            if (modifyCompsArr.length === 0) {
                this.testConnects(values.clusterName)
                return;
            }
            let modifyCompsNames: any = [];
            modifyCompsArr.forEach((comp: number) => {
                modifyCompsNames.push(COMPONEMT_CONFIG_NAME_ENUM[comp])
            })
            message.error(`组件 ${modifyCompsNames.join('、')} 参数变更未保存，请先保存再测试组件连通性`)
        })
    }

    testConnects = (clusterName: string) => {
        this.setState({ testLoading: true });
        Api.testConnects({
            clusterName
        }).then((res: any) => {
            if (res.code === 1) {
                let testStatus: any = {}
                res.data.forEach((temp: any) => {
                    testStatus[temp.componentTypeCode] = { ...temp }
                })
                this.setState({
                    testStatus: testStatus
                })
            }
        }).finally(() => {
            this.setState({ testLoading: false })
        })
    }

    refreshYarnQueue (clusterName: string) {
        this.setState({ testLoading: true });
        Api.refreshQueue({ clusterName }).then((res: any) => {
            if (res.code == 1) {
                const target = res.data.find(v => v.componentTypeCode == COMPONENT_TYPE_VALUE.YARN)
                if (target?.result || res.data.length == 0) {
                    message.success('刷新成功')
                } else {
                    notification['error']({
                        message: '刷新失败',
                        description: `${target.errorMsg}`,
                        style: { wordBreak: 'break-word' }
                    });
                }
            }
        }).finally(() => {
            this.setState({ testLoading: false })
        })
    }

    handleCommonVersion = (val: string, componentTypeCode: number) => {
        if (componentTypeCode === COMPONENT_TYPE_VALUE.YARN || componentTypeCode === COMPONENT_TYPE_VALUE.HDFS) {
            this.setState({
                commonVersion: val
            })
            const { form } = this.props;
            val && form.setFieldsValue({
                [COMPONEMT_CONFIG_KEY_ENUM[COMPONENT_TYPE_VALUE.YARN]]: {
                    hadoopVersion: val
                },
                [COMPONEMT_CONFIG_KEY_ENUM[COMPONENT_TYPE_VALUE.HDFS]]: {
                    hadoopVersion: val
                }
            })
        }
    }

    handleSaveCompsData = (val: string, componentTypeCode: number) => {
        const { form } = this.props;
        this.setState({
            storeType: val
        })
        form.setFieldsValue({
            [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                storeType: val
            }
        })
    }

    renderCompTabs = (item: any) => {
        const { tabCompData } = this.state;
        if (tabCompData.length === 0) return {};
        return tabCompData.find((comps: any) => comps.schedulingCode === item.schedulingCode) || {};
    }

    render () {
        const { compTypeKey, popoverVisible, clusterName, modify, selectValue,
            deleteComps, defaultValue, componentConfig, testLoading, saveCompsData } = this.state;
        const { location: { state: { cluster: { clusterName: realClusterName } } } } = this.props
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { mode } = this.props.location.state || {} as any;
        const isView = mode === 'view';
        const componentBtn = !isView && (
            <SelectPopver
                key={popoverVisible}
                popoverVisible={popoverVisible}
                modify={modify}
                compTypeKey={compTypeKey}
                defaultValue={defaultValue}
                setPopverVisible={this.setPopverVisible}
                handleCanclePopover={this.handleCanclePopover}
                modifyTabCompData={this.modifyTabCompData}
                setCheckboxCompData={this.setCheckboxCompData}
                setRadioCompData={this.setRadioCompData} />
        );

        return (
            <div className="c-editCluster__containerWrap" ref={(el) => { this.container = el; }}>
                <div style={{ height: 20 }}>
                    <span className="c-editCluster__turnBack" onClick={this.turnClusteManage}>多集群管理 / </span>
                    <span className="c-editCluster__title">{dealData.getActionType(mode)}</span>
                </div>
                <React.Fragment>
                    <div className="c-editCluster__header">
                        <FormItem label={null}>
                            {getFieldDecorator('clusterName', { initialValue: clusterName || '' })(
                                <Input style={{ width: 340, height: 32 }} placeholder="请输入集群标识" disabled={true} />
                            )}
                        </FormItem>
                        {isView
                            ? <div>
                                <Button style={{ marginRight: 10 }} loading={testLoading} onClick={this.refreshYarnQueue.bind(this, clusterName)}>刷新</Button>
                                <Button type="primary" onClick={this.turnEditComp}>编辑</Button>
                            </div>
                            : <Button type="primary" loading={testLoading} onClick={this.handleNotSaveComps}>测试全部连通性</Button>
                        }
                    </div>
                    <div className="c-editCluster__container shadow">
                        <Tabs
                            tabPosition="top"
                            onChange={this.onTabChange}
                            activeKey={`${compTypeKey}`}
                            className="c-editCluster__container__commonTabs"
                            tabBarExtraContent={<div className="c-editCluster__commonTabs__title">集群配置</div>}
                        >
                            {TABS_TITLE.map((scheduling: any, index: any) => {
                                const tabCompDataList = this.renderCompTabs(scheduling).components || [];
                                return (
                                    <TabPane
                                        tab={
                                            <div style={{ height: 19, display: 'flex', alignItems: 'center' }}>
                                                {renderCompIcon(scheduling)}
                                                {scheduling.schedulingName}
                                            </div>
                                        }
                                        key={scheduling.schedulingCode}
                                    >
                                        {tabCompDataList.length === 0 && <div key={compTypeKey} className="c-editCluster__container__emptyLogo">
                                            <img src="public/img/emptyLogo.svg" />
                                        </div>}
                                        <Card
                                            className="c-editCluster__container__card console-tabs cluster-tab-width"
                                            hoverable
                                        >
                                            <Tabs
                                                tabPosition="left"
                                                tabBarExtraContent={componentBtn}
                                                className="c-editCluster__container__componentTabs"
                                                onChange={(key: any) => this.getLoadTemplate(key)}
                                            >
                                                {tabCompDataList.map((comps: any, index: any) => {
                                                    return (
                                                        <TabPane
                                                            tab={<span>{comps.componentName}<TestRestIcon testStatus={this.state.testStatus[comps.componentTypeCode] || {}}/></span>}
                                                            key={`${comps.componentTypeCode}`}
                                                        >
                                                            <div className="c-editCluster__container__componentWrap">
                                                                <div className="c-editCluster__container__componentWrap__resource" style={{ width: 210 }}>
                                                                    <DisplayResource
                                                                        {...this.state}
                                                                        isView={isView}
                                                                        components={comps}
                                                                        saveCompsData={saveCompsData}
                                                                        getFieldValue={getFieldValue}
                                                                        getFieldDecorator={getFieldDecorator}
                                                                        downloadFile={this.downloadFile}
                                                                        paramsfileChange={this.paramsfileChange}
                                                                        kerFileChange={this.kerFileChange}
                                                                        clusterName={realClusterName}
                                                                        handleCommonVersion={this.handleCommonVersion}
                                                                        handleSaveCompsData={this.handleSaveCompsData}
                                                                        handleCompsCompsData={this.handleCompsCompsData}
                                                                        handleCompsVersion={this.handleCompsVersion}
                                                                        deleteKerFile={this.deleteKerFile}
                                                                        fileChange={this.fileChange} />
                                                                </div>
                                                                <div className="c-editCluster__container__componentWrap__config">
                                                                    <ComponentsConfig
                                                                        {...this.state}
                                                                        isView={isView}
                                                                        components={comps}
                                                                        componentConfig={componentConfig}
                                                                        addParams={this.addParams}
                                                                        deleteParams={this.deleteParams}
                                                                        getFieldValue={getFieldValue}
                                                                        getFieldDecorator={getFieldDecorator} />
                                                                </div>
                                                            </div>
                                                            {!isView && <div className="c-editCluster__container__componentFooter">
                                                                <Popconfirm
                                                                    title="确认取消当前更改？"
                                                                    onConfirm={this.handleCancel.bind(this, comps)}
                                                                    okText="确认"
                                                                    cancelText="取消"
                                                                >
                                                                    <Button className="c-editCluster__container__componentFooter__btn">取消</Button>
                                                                </Popconfirm>
                                                                <Button className="c-editCluster__container__componentFooter__btn" type="primary" style={{ marginLeft: 8 }} onClick={this.saveComponent.bind(this, comps)} >保存</Button>
                                                            </div>}
                                                        </TabPane>
                                                    )
                                                })}
                                            </Tabs>
                                        </Card>
                                    </TabPane>
                                )
                            })}
                        </Tabs>
                    </div>
                </React.Fragment>
                <ModifyComponentModal
                    modify={modify}
                    selectValue={selectValue}
                    deleteComps={deleteComps}
                    handleDeleteComps={this.handleDeleteComps}
                    handleCancleModify={this.handleCancleModify} />
            </div>
        )
    }
}
export default Form.create<any>()(EditCluster);
