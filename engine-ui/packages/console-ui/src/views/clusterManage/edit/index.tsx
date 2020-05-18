import * as React from 'react';
import { cloneDeep } from 'lodash';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router';
import {
    Form, Input, Card, Tabs, Button, Popover, Checkbox,
    Row, Col, Radio, message } from 'antd';
import Api from '../../../api/console';

import { updateTestStatus, updateRequiredStatus } from '../../../reducers/modules/cluster';

import req from '../../../consts/reqUrls'
import {
    SOURCE_COMPONENTS, STORE_COMPONENTS, COMPUTE_COMPONENTS, COMMON_COMPONENTS,
    TABS_TITLE, TABS_TITLE_KEY, COMPONEMT_CONFIG_NAME_ENUM, COMPONEMT_CONFIG_KEY_ENUM,
    COMPONENT_TYPE_VALUE } from '../../../consts';

import ModifyComponentModal from '../../../components/modifyCompModal';
import DisplayResource from './displayResource';
import ComponentsConfig from './componentsConfig';

const TabPane = Tabs.TabPane;
const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;
const FormItem = Form.Item;

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
    state: any = {
        clusterId: '', // 集群id
        clusterName: '', // 集群名称
        compTypeKey: 0, // 组件默认选中
        selectComp: [], // 存储组件勾选状态
        componentConfig: {}, // 各组件配置文件信息
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
        modifyComps: [], // 变更组件
        modifySource: '', // 资源调度组件变更组件
        defaultValue: [], // 组件配置默认选中组件值
        selectValue: [], // 选中组件存值
        popoverVisible: false,
        uploadLoading: false,
        kerUploadLoading: false,
        modify: false
    }
    container: any;

    componentDidMount () {
        this.getDataList();
    }

    getDataList = () => {
        const { clusterId } = this.state;
        const { cluster = {} } = this.props.location.state || {} as any;
        // 是否存在组件id
        const isRequest = clusterId || cluster.clusterId;
        isRequest && Api.getClusterInfo({
            clusterId: clusterId || cluster.clusterId
        }).then((res: any) => {
            if (res.code === 1) {
                // 存入组件信息
                let newCompConfig: any = {};
                newCompConfig.clusterName = res.data.clusterName;
                res.data.scheduling.map((item: any) => {
                    item.components.map((comps: any) => {
                        newCompConfig[COMPONEMT_CONFIG_KEY_ENUM[comps.componentTypeCode]] = {
                            configInfo: JSON.parse(comps.componentConfig) || {},
                            loadTemplate: JSON.parse(comps.componentTemplate) || [],
                            fileName: comps.uploadFileName || '',
                            kerFileName: comps.kerberosFileName || '',
                            id: comps.id || ''
                        }
                    })
                })
                this.setState({
                    tabCompData: res.data.scheduling,
                    clusterName: res.data.clusterName,
                    componentConfig: newCompConfig,
                    clusterId: clusterId
                }, () => { this.selectDefaultValue(); console.log('componentUpdate-----', this.state.tabCompData, this.state.componentConfig) })
            }
        })
    }

    onTabChange = (key: any) => {
        this.setState({
            compTypeKey: Number(key),
            popoverVisible: false,
            modifyComps: []
        }, () => this.selectDefaultValue())
    }

    turnBack = () => {
        hashHistory.push({ pathname: '/console/clusterManage' })
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

    // 获取组件模板
    getLoadTemplate = () => {
        const { compTypeKey, tabCompData, componentConfig, clusterName } = this.state;
        const component = tabCompData.find((item: any) => item.schedulingCode === compTypeKey) || { components: [] };
        if (component.components.length > 0) {
            const componentTypeCode = component.components[0].componentTypeCode;
            const isNeedLoadTemp = componentTypeCode !== COMPONENT_TYPE_VALUE.YARN && componentTypeCode !== COMPONENT_TYPE_VALUE.KUBERNETES &&
                componentTypeCode !== COMPONENT_TYPE_VALUE.KUBERNETES && componentTypeCode !== COMPONENT_TYPE_VALUE.HDFS;
            // console.log('isNeedLoadTemp------', isNeedLoadTemp);
            if (isNeedLoadTemp) {
                Api.getLoadTemplate({
                    clusterName,
                    componentType: componentTypeCode
                }).then((res: any) => {
                    if (res.code === 1) {
                        this.setState({
                            componentConfig: {
                                ...this.state.componentConfig,
                                [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                                    ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                                    loadTemplate: res.data
                                }
                            }
                        })
                    }
                })
            }
        }
    }

    // 组件配置选中的组件，选中组件的值
    selectDefaultValue = () => {
        const { tabCompData, compTypeKey } = this.state;
        const cloneCompData = cloneDeep(tabCompData);
        let defaultValue: any = [];
        cloneCompData.length > 0 && cloneCompData.map((item: any) => {
            if (item.schedulingCode === compTypeKey) {
                item.components.map((comps: any) => {
                    defaultValue.push(comps.componentTypeCode);
                })
            }
        })
        this.setState({
            defaultValue,
            selectValue: defaultValue
        })
    }

    // 对单选框数据格式进行处理
    setRadioCompData = (e: any) => {
        this.setState({
            selectValue: [e.target.value]
        })
    }

    // 对复选框数据格式进行处理
    setCheckboxCompData = (value) => {
        this.setState({
            selectValue: [...value]
        })
    }

    // 点击确认后对选中数据进行处理
    modifyTabCompData = () => {
        const { tabCompData, selectValue, compTypeKey } = this.state;
        let modifyComps: any = [];
        const scheduling = tabCompData.find((sche: any) => sche.schedulingCode === compTypeKey)
        // 资源调度组件切换
        if (compTypeKey === TABS_TITLE_KEY.SOURCE && scheduling.components.length > 0) {
            this.modifySourceComponent();
            return;
        }
        scheduling.components.forEach((comps: any) => {
            // console.log('comps-----', selectValue, selectValue.findIndex((val: any) => val === comps.componentTypeCode), comps.id)
            // 不存在组件中且id存在(即保存过)
            if (selectValue.findIndex((val: any) => val === comps.componentTypeCode) === -1 && comps.id) {
                modifyComps.push(comps)
            }
        })
        if (modifyComps.length !== 0) {
            this.setState({
                modify: true,
                popoverVisible: false,
                modifyComps
            })
        } else {
            this.setTabCompData();
        }
    }

    // 存入选中组件数据
    setTabCompData = () => {
        const { selectValue, tabCompData, compTypeKey } = this.state;
        const cloneCompData = cloneDeep(tabCompData);
        let components: any = [];
        selectValue.map((val: any) => {
            components.push({ componentTypeCode: val, componentName: COMPONEMT_CONFIG_NAME_ENUM[val] });
            this.setState({
                componentConfig: {
                    ...this.state.componentConfig,
                    [COMPONEMT_CONFIG_KEY_ENUM[val]]: {}
                }
            })
        })
        cloneCompData.forEach((item: any) => {
            if (item.schedulingCode === compTypeKey) item.components = components
        })
        this.setState({
            tabCompData: cloneCompData,
            popoverVisible: false
        }, () => { this.getLoadTemplate(); this.selectDefaultValue() });
    }

    modifySourceComponent = () => {
        const { selectValue } = this.state;
        this.setState({
            modify: true,
            popoverVisible: false,
            modifySource: selectValue[0] === COMPONENT_TYPE_VALUE.KUBERNETES ? COMPONENT_TYPE_VALUE.YARN : COMPONENT_TYPE_VALUE.KUBERNETES
        })
    }

    // 已变更组件处理
    modifyComponent = () => {
        const { modifyComps, compTypeKey, modifySource } = this.state;
        let componentIds: any = [];
        if (compTypeKey === TABS_TITLE_KEY.SOURCE) {
            this.setState({
                componentConfig: {
                    ...this.state.componentConfig,
                    [COMPONEMT_CONFIG_KEY_ENUM[modifySource]]: {}
                },
                modify: false
            })
            this.setTabCompData();
            return;
        }
        modifyComps.map((comps: any) => {
            componentIds.push(comps.id)
        })
        Api.deleteComponent({
            componentIds: componentIds
        }).then((res: any) => {
            if (res.code === 1) {
                this.getDataList();
            }
            this.setState({
                modify: false
            })
        })
    }

    handleCancleModify = () => {
        this.setState({ modify: false, popoverVisible: false }, () => this.selectDefaultValue())
    }

    handleCanclePopover = () => {
        this.setState({ popoverVisible: false }, () => this.selectDefaultValue())
    }

    // Tab页组件配置按钮
    componentBtn = () => {
        let content: any;
        const { compTypeKey, popoverVisible, defaultValue } = this.state;
        // console.log('defaultValue------selectValue', defaultValue, selectValue)
        const popoverFooter = (
            <div style={{ marginTop: 37 }}>
                <Row>
                    <Col span={8}></Col>
                    <Col>
                        <Button size="small" onClick={this.handleCanclePopover}>取消</Button>
                        <Button size="small" type="primary" style={{ marginLeft: 8 }} onClick={this.modifyTabCompData}>确认</Button>
                    </Col>
                </Row>
            </div>
        )
        switch (compTypeKey) {
            case TABS_TITLE_KEY.COMMON:
                content = (
                    <CheckboxGroup onChange={this.setCheckboxCompData} defaultValue={defaultValue}>
                        <Row>
                            {COMMON_COMPONENTS.map((item: any) => {
                                return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                            })}
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            case TABS_TITLE_KEY.SOURCE:
                content = (
                    <RadioGroup style={{ width: '100%' }} onChange={this.setRadioCompData} defaultValue={defaultValue[0]}>
                        <Row>
                            {SOURCE_COMPONENTS.map((item: any) => {
                                return <Col key={`${item.componentTypeCode}`}><Radio value={item.componentTypeCode}>{item.componentName}</Radio></Col>
                            })}
                        </Row>
                        {popoverFooter}
                    </RadioGroup>
                )
                break;
            case TABS_TITLE_KEY.STORE:
                content = (
                    <CheckboxGroup onChange={this.setCheckboxCompData} defaultValue={defaultValue}>
                        <Row>
                            {STORE_COMPONENTS.map((item: any) => {
                                return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                            })}
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            case TABS_TITLE_KEY.COMPUTE:
                content = (
                    <CheckboxGroup onChange={this.setCheckboxCompData} defaultValue={defaultValue}>
                        <Row>
                            {COMPUTE_COMPONENTS.map((item: any) => {
                                return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                            })}
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            default:
                content = ''
                break;
        }
        return (
            <Popover
                key={popoverVisible}
                title="组件配置"
                placement="topRight"
                trigger="click"
                visible={popoverVisible}
                content={content}
                style={{ width: 240 }}
            >
                <Button className="c-editCluster__componentButton" onClick={() => this.setState({ popoverVisible: true })}><i className="iconfont iconzujianpeizhi" style={{ marginRight: 2 }}></i>组件配置</Button>
            </Popover>
        )
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

    // 配置文件Change事件
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
                    console.log(res.data[0])
                    this.setState({
                        componentConfig: {
                            ...componentConfig,
                            [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                                ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                                uploadFileName: file,
                                fileName: file.files[0].name,
                                configInfo: res.data[0]
                            }
                        },
                        uploadLoading: false
                    })
                } else {
                    this.setState({
                        uploadLoading: false
                    })
                }
            })
        }
    }

    // 批量上传参数
    paramsfileChange = (e: any, componentTypeCode: any) => {
        const paramsFile = e.target;
        const { componentConfig } = this.state;
        // console.log('changefile---------', paramsFile.files);
        const { form } = this.props;
        if (paramsFile.files.length > 0) {
            this.setState({ uploadLoading: true });
            Api.uploadResource({
                fileName: paramsFile.files[0],
                componentType: componentTypeCode
            }).then((res: any) => {
                if (res.code === 1) {
                    form.setFieldsValue({
                        [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                            configInfo: { ...res.data[0] }
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
    }

    // 下载配置文件
    downloadFile = (components: any, type: any) => {
        const config = this.getComponentConfig(components);
        const a = document.createElement('a');
        const param = `?componentId=${config.id}&type=${type}`;
        a.href = `${req.DOWNLOAD_RESOURCE}${param}`;
        a.click();
    }

    // Hadoop Kerberos认证文件Change事件
    kerFileChange = (e: any, componentTypeCode: any) => {
        const kerFile = e.target;
        const { componentConfig } = this.state;
        // console.log('changekerFile---------', kerFile.files[0]);
        if (kerFile.files.length > 0) {
            this.setState({ kerUploadLoading: true });
            this.setState({
                componentConfig: {
                    ...componentConfig,
                    [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                        ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                        kerberosFileName: kerFile,
                        kerFileName: kerFile.files[0].name
                    }
                },
                kerUploadLoading: false
            });
        }
    }

    deleteKerFile = (componentTypeCode: any) => {
        const { componentConfig } = this.state;
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

    /**
     * 处理添加、更新组件数据参数
     * @values 表单变更值
     * @components 组件
     */
    getComponentConfigPrames (values: any, components: any) {
        const componentTypeCode = components.componentTypeCode;

        // 组件配置相关 配置文件、组件id、组件模板、
        const config = this.getComponentConfig(components);
        const {
            uploadFileName = {}, configInfo = {}, loadTemplate = [], kerberosFileName = {},
            kerFileName = '' } = config;
        const files = uploadFileName.files && uploadFileName.files[0] ? uploadFileName.files[0] : {};
        const kerFiles = kerberosFileName.files && kerberosFileName.files[0] ? kerberosFileName.files[0] : {};

        // 各组件表单对应更改值
        let saveConfig = values[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]];
        const { hadoopVersion = '' } = saveConfig;
        const { clusterName } = values;

        // 返回模板信息以及相关输入值
        const componentTemplate = cloneDeep(loadTemplate)
        componentTemplate.forEach((item: any) => {
            if (saveConfig.configInfo[item.key]) {
                item.value = saveConfig.configInfo[item.key]
            }
        })

        /**
         * 配置信息或者配置表单键值
         * saveConfig.configInfo 表单键值
         * configInfo 组件配置信息
         */
        const paramsConfig = saveConfig.configInfo || configInfo;
        // console.log('paramsConfig-----------', paramsConfig, configInfo, saveConfig)
        return {
            resources: [files, kerFiles],
            clusterName: clusterName,
            componentConfig: JSON.stringify({ ...paramsConfig }),
            kerberosFileName: kerFileName,
            hadoopVersion: hadoopVersion,
            componentCode: componentTypeCode,
            componentTemplate: JSON.stringify(componentTemplate)
        }
    }

    saveComponent = (components: any) => {
        const { validateFieldsAndScroll } = this.props.form;
        const componentTypeCode = components.componentTypeCode;
        const config = this.getComponentConfig(components);
        validateFieldsAndScroll((err: any, values: any) => {
            console.log(err, values)
            const result = /^[a-z0-9_]{1,64}$/i.test(values.clusterName);
            if (err) {
                let paramName = COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode];
                if (Object.keys(err).includes(paramName) || !config.fileName) {
                    message.error('请检查配置');
                    return;
                }
            }
            if (!values.clusterName || !result) {
                const notice = values.clusterName ? '集群标识不能超过64字符，支持英文、数字、下划线' : '集群名称不能为空';
                message.error(`${notice}`);
                return;
            }
            const params = this.getComponentConfigPrames(values, components);
            Api.saveComponent({
                ...params
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        clusterId: res.data.clusterId
                    }, () => { this.getDataList() })
                    message.success('保存成功');
                }
            })
        })
    }

    handleCancel = (components: any) => {
        const { form } = this.props;
        const componentTypeCode = components.componentTypeCode;
        const config = this.getComponentConfig(components);
        const { configInfo = {} } = config;

        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.SFTP:
                form.setFieldsValue({
                    [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                        configInfo: { ...configInfo }
                    }
                })
                break;
            default:
                break;
        }
    }

    // 返回各个模块下的组件
    renderCompTabs = (item: any) => {
        const { tabCompData } = this.state;
        if (tabCompData.length === 0) return {};
        return tabCompData.find((comps: any) => comps.schedulingCode === item.schedulingCode) || {};
    }

    render () {
        const { compTypeKey, clusterName, modify, modifyComps, modifySource, selectValue } = this.state;
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const componentBtn = this.componentBtn();
        const { mode } = this.props.location.state || {} as any;
        const isView = mode === 'view';

        return (
            <div className="c-editCluster__containerWrap" ref={(el) => { this.container = el; }}>
                <div style={{ height: 20 }}>
                    <span className="c-editCluster__turnBack" onClick={this.turnBack}>多集群管理 / </span>
                    <span className="c-editCluster__title">新增集群</span>
                </div>
                <React.Fragment>
                    <div className="c-editCluster__header">
                        <FormItem label={null}>
                            {getFieldDecorator('clusterName', { initialValue: clusterName || '' })(
                                <Input style={{ width: 340, height: 32 }} placeholder="请输入集群标识" disabled={isView} />
                            )}
                        </FormItem>
                        {
                            isView
                                ? <Button type="primary" className="c-editCluster__header__btn" onClick={this.turnEditComp}>编辑</Button>
                                : <Button type="primary" className="c-editCluster__header__btn">测试全部连通性</Button>
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
                            {
                                TABS_TITLE.map((scheduling: any, index: any) => {
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
                                            <Card
                                                className="c-editCluster__container__card console-tabs cluster-tab-width"
                                                noHovering
                                            >
                                                <Tabs
                                                    tabPosition="left"
                                                    tabBarExtraContent={!isView && componentBtn}
                                                    className="c-editCluster__container__componentTabs"
                                                    onChange={(key: any) => console.log('renderkey-----------------', key)}
                                                >
                                                    {
                                                        tabCompDataList.map((comps: any, index: any) => {
                                                            return (
                                                                <TabPane tab={<span>{comps.componentName}</span>} key={`${comps.componentTypeCode}`}>
                                                                    <div className="c-editCluster__container__componentWrap">
                                                                        <div className="c-editCluster__container__componentWrap__resource" style={{ width: 200 }}>
                                                                            <DisplayResource
                                                                                {...this.state}
                                                                                isView={isView}
                                                                                components={comps}
                                                                                getFieldDecorator={getFieldDecorator}
                                                                                downloadFile={this.downloadFile}
                                                                                paramsfileChange={this.paramsfileChange}
                                                                                kerFileChange={this.kerFileChange}
                                                                                deleteKerFile={this.deleteKerFile}
                                                                                fileChange={this.fileChange} />
                                                                        </div>
                                                                        <div className="c-editCluster__container__componentWrap__config">
                                                                            <ComponentsConfig
                                                                                {...this.state}
                                                                                isView={isView}
                                                                                components={comps}
                                                                                getFieldValue={getFieldValue}
                                                                                getFieldDecorator={getFieldDecorator} />
                                                                        </div>
                                                                    </div>
                                                                    {
                                                                        !isView && <div className="c-editCluster__container__componentFooter">
                                                                            <Button className="c-editCluster__container__componentFooter__btn" onClick={this.handleCancel.bind(this, comps)}>取消</Button>
                                                                            <Button className="c-editCluster__container__componentFooter__btn" type="primary" style={{ marginLeft: 8 }} onClick={this.saveComponent.bind(this, comps)} >保存</Button>
                                                                        </div>
                                                                    }
                                                                </TabPane>
                                                            )
                                                        })
                                                    }
                                                </Tabs>
                                                {tabCompDataList.length === 0 && <div style={{ position: 'absolute', top: '50%', left: '60%' }}>空白页</div>}
                                            </Card>
                                        </TabPane>
                                    )
                                })
                            }
                        </Tabs>
                    </div>
                </React.Fragment>
                <ModifyComponentModal
                    modify={modify}
                    selectValue={selectValue}
                    modifySource={modifySource}
                    modifyComps={modifyComps}
                    modifyComponent={this.modifyComponent}
                    handleCancleModify={this.handleCancleModify} />
            </div>
        )
    }
}
export default Form.create<any>()(EditCluster);
