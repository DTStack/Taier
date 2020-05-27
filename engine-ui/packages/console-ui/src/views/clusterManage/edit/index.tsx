import * as React from 'react';
import { cloneDeep } from 'lodash';
import { hashHistory } from 'react-router';
import {
    Form, Input, Card, Tabs, Button, message, Modal, Popconfirm } from 'antd';
import Api from '../../../api/console';

import req from '../../../consts/reqUrls';
import {
    TABS_TITLE, TABS_TITLE_KEY, COMPONEMT_CONFIG_NAME_ENUM,
    COMPONEMT_CONFIG_KEY_ENUM } from '../../../consts';

import ModifyComponentModal from '../../../components/modifyCompModal';
import SelectPopver from '../../../components/selectPopover';
import TestRestIcon from '../../../components/testResultIcon';
import DisplayResource from './displayResource';
import ComponentsConfig from './componentsConfig';
import dealData from './dealData';

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const confirm = Modal.confirm
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
        popoverVisible: false,
        uploadLoading: false,
        modify: false,
        testLoading: false
    }
    container: any;

    componentDidMount () {
        this.getDataList();
    }

    getDataList = () => {
        const { clusterId } = this.state;
        const { cluster = {} } = this.props.location.state || {} as any;
        const isRequest = clusterId || cluster.clusterId;
        isRequest && Api.getClusterInfo({
            clusterId: clusterId || cluster.clusterId
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tabCompData: res.data.scheduling,
                    clusterName: res.data.clusterName,
                    componentConfig: dealData.handleCompsData(res),
                    cloneComponentConfig: dealData.handleCompsData(res),
                    clusterId: clusterId
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
        const { form } = this.props
        form.validateFields(null, {}, (err: any, values: any) => {
            console.log(err, values)
            const { cloneComponentConfig } = this.state;
            let modifyCompsArr = dealData.getMoadifyComps(values, cloneComponentConfig);
            if (modifyCompsArr.length === 0) {
                hashHistory.push({ pathname: '/console/clusterManage' })
            } else {
                confirm({
                    title: '当前变更是否保存？',
                    okText: '保存',
                    cancelText: '不保存',
                    onCancel () {
                        hashHistory.push({ pathname: '/console/clusterManage' })
                    }
                });
            }
        })
    }

    turnEditComp = () => {
        const params = this.props.location.state || {};
        const { cluster = {} } = params;
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

    handleFlinkSparkVersion = (key: number, compVersion: any) => {
        this.getLoadTemplate(key, compVersion);
        this.handleCompsVersion(key, compVersion);
    }

    handleCompsVersion = (key: number, compVersion: any) => {
        const { componentConfig } = this.state;
        this.setState({
            componentConfig: {
                ...componentConfig,
                [COMPONEMT_CONFIG_KEY_ENUM[key]]: {
                    ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[key]],
                    hadoopVersion: compVersion
                }
            }
        })
    }

    // 获取组件模板
    getLoadTemplate = (key: any = '', compVersion: any = '') => {
        const { compTypeKey, tabCompData, componentConfig, clusterName } = this.state;
        const component = tabCompData.find((item: any) => item.schedulingCode === compTypeKey) || { components: [] };
        if (component.components.length === 0) return;
        let componentTypeCode = key === '' ? component.components[0].componentTypeCode : key;
        const isNeedLoadTemp = dealData.checkUplaodFileComps(componentTypeCode)
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {}
        const { loadTemplate = {} } = config;
        const version = dealData.getCompsVersion(Number(componentTypeCode), compVersion)
        if (!isNeedLoadTemp && Object.keys(loadTemplate).length === 0) {
            Api.getLoadTemplate({
                clusterName,
                version,
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
        const components = tabCompData.find((sche: any) => sche.schedulingCode === compTypeKey).components;
        if (selectValue.sort().toString() === defaultValue.sort().toString()) {
            this.setState({
                popoverVisible: false
            })
            return;
        }
        components.map((comps: any) => {
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
            }
        })
    }
    // 删除tabCompData组件和组件存储的配置信息
    clearTabCompData = () => {
        const { tabCompData, compTypeKey, deleteComps, addComps } = this.state;
        let cloneComps = cloneDeep(tabCompData);
        const components = cloneComps.find((sche: any) => sche.schedulingCode === compTypeKey).components;
        deleteComps.map((compconent: any) => {
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
        deleteComps.map((comps: any) => {
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
            addComps.map((val: any) => {
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
        console.log('changefile---------', file.files[0]);
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
                console.log('res.data[0]=======', res.data[0])
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
            })
        })
    }

    // Hadoop Kerberos认证文件Change事件
    kerFileChange = (e: any, componentTypeCode: any) => {
        const kerFile = e.target;
        const { componentConfig } = this.state;
        if (kerFile.files.length > 0) {
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
        const config = this.getComponentConfig(components);
        const a = document.createElement('a');
        const param = `?componentId=${config.id}&type=${type}`;
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
        const { validateFieldsAndScroll } = this.props.form;
        const { componentConfig, cloneComponentConfig } = this.state;
        const componentTypeCode = components.componentTypeCode;
        const config = this.getComponentConfig(components);
        const isFileNameRequire = dealData.checkUplaodFileComps(componentTypeCode);
        validateFieldsAndScroll((err: any, values: any) => {
            console.log(err, values)
            const result = /^[a-z0-9_]{1,64}$/i.test(values.clusterName);
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
            if (!values.clusterName || !result) {
                const notice = values.clusterName ? '集群标识不能超过64字符，支持英文、数字、下划线' : '集群名称不能为空';
                message.error(`${notice}`);
                return;
            }
            const params = dealData.getComponentConfigPrames(values, components, config);
            console.log('this is params-----------', params)
            Api.saveComponent({
                ...params
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        clusterId: res.data.clusterId,
                        clusterName: res.data.clusterName,
                        componentConfig: dealData.updateCompsConfig(componentConfig, componentTypeCode, res),
                        cloneComponentConfig: dealData.updateCompsConfig(cloneComponentConfig, componentTypeCode, res)
                    }, () => console.log('componentConfig------componentConfig', this.state.componentConfig));
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
            params.map((p: any) => {
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
            modifyCompsArr.map((comp: number) => {
                modifyCompsNames.push(COMPONEMT_CONFIG_NAME_ENUM[comp])
            })
            message.error(`组件 ${modifyCompsNames.join('、')} 参数变更未保存，请先保存再测试组件连通性`)
        })
    }

    testConnects = (clusterName: string) => {
        this.setState({
            testLoading: true
        });
        Api.testConnects({
            clusterName
        }).then((res: any) => {
            if (res.code === 1) {
                let testStatus: any = {}
                res.data.map((temp: any) => {
                    testStatus[temp.componentTypeCode] = { ...temp }
                })
                this.setState({
                    testStatus: testStatus
                })
            }
            this.setState({
                testLoading: false
            })
        }).finally(() => {
            this.setState({
                testLoading: false
            })
        })
    }

    renderCompTabs = (item: any) => {
        const { tabCompData } = this.state;
        if (tabCompData.length === 0) return {};
        return tabCompData.find((comps: any) => comps.schedulingCode === item.schedulingCode) || {};
    }

    render () {
        const { compTypeKey, popoverVisible, clusterName, modify, selectValue,
            deleteComps, defaultValue, componentConfig, testLoading } = this.state;
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
                    <span className="c-editCluster__title">新增集群</span>
                </div>
                <React.Fragment>
                    <div className="c-editCluster__header">
                        <FormItem label={null}>
                            {getFieldDecorator('clusterName', { initialValue: clusterName || '' })(
                                <Input style={{ width: 340, height: 32 }} placeholder="请输入集群标识" disabled={isView || !(mode === 'new')} />
                            )}
                        </FormItem>
                        {isView ? <Button type="primary" className="c-editCluster__header__btn" onClick={this.turnEditComp}>编辑</Button>
                            : <Button type="primary" className="c-editCluster__header__btn" loading={testLoading} onClick={this.handleNotSaveComps}>测试全部连通性</Button>}
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
                                            <img src="public/img/emptyLogo.png" />
                                        </div>}
                                        <Card
                                            className="c-editCluster__container__card console-tabs cluster-tab-width"
                                            noHovering
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
                                                                <div className="c-editCluster__container__componentWrap__resource" style={{ width: 200 }}>
                                                                    <DisplayResource
                                                                        {...this.state}
                                                                        isView={isView}
                                                                        components={comps}
                                                                        getFieldValue={getFieldValue}
                                                                        getFieldDecorator={getFieldDecorator}
                                                                        downloadFile={this.downloadFile}
                                                                        paramsfileChange={this.paramsfileChange}
                                                                        kerFileChange={this.kerFileChange}
                                                                        handleFlinkSparkVersion={this.handleFlinkSparkVersion}
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
