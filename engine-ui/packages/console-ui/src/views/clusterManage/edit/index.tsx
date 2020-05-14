import * as React from 'react';
import { cloneDeep, omit } from 'lodash';
import { connect } from 'react-redux';
import { browserHistory, hashHistory } from 'react-router';
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

import DisplayResource from './displayResource';
import ComponentsConfig from './componentsConfig';

const TabPane = Tabs.TabPane;
const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;
const FormItem = Form.Item;
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
        compTypeKey: 0, // 组件默认选中
        popoverVisible: false, // 气泡框可视
        selectComp: [], // 存储组件勾选状态
        componentConfig: {}, // 组件配置文件信息
        uploadLoading: false,
        clusterName: '', // 组件名称
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
        clusterId: '' // 集群id
    }
    container: any;

    onTabChange = (key: any) => {
        this.setState({
            compTypeKey: Number(key),
            selectComp: {},
            popoverVisible: false
        })
    }

    componentDidMount () {
        this.getDataList();
    }

    getDataList = () => {
        const { clusterId } = this.state;
        const params = this.props.location.state || {};
        const { cluster = {}, mode = '' } = params;
        const { componentConfig } = this.state;
        const isRequest = (mode === 'new' && clusterId) || mode !== 'new';
        isRequest && Api.getClusterInfo({
            clusterId: cluster.clusterId || clusterId
        }).then((res: any) => {
            if (res.code === 1) {
                // 存入组件信息
                const cloneCompConfig = cloneDeep(componentConfig);
                cloneCompConfig.clusterName = res.data.clusterName;
                res.data.scheduling.map((item: any) => {
                    item.components.map((comps: any) => {
                        cloneCompConfig[COMPONEMT_CONFIG_KEY_ENUM[comps.componentTypeCode]] = {
                            ...cloneCompConfig[COMPONEMT_CONFIG_KEY_ENUM[comps.componentTypeCode]],
                            configInfo: JSON.parse(comps.componentConfig),
                            loadTemplate: JSON.parse(comps.componentTemplate),
                            defaultFileName: comps.uploadFileName,
                            fileName: comps.uploadFileName,
                            id: comps.id
                        }
                    })
                })
                this.setState({
                    tabCompData: res.data.scheduling,
                    clusterName: res.data.clusterName,
                    componentConfig: cloneCompConfig
                }, () => console.log('componentUpdate-----', this.state.tabCompData))
            }
        })
    }

    getComponentConfig = (components: any) => {
        const { componentConfig } = this.state;
        const componentTypeCode = components.componentTypeCode;
        return componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
    }

    // 对单选框数据格式进行处理
    setRadioCompData = (e: any) => {
        const { compTypeKey, tabCompData } = this.state;
        const cloneCompData = cloneDeep(tabCompData);
        const components = { componentTypeCode: e.target.value, componentName: COMPONEMT_CONFIG_NAME_ENUM[e.target.value] }
        cloneCompData.forEach((item: any) => {
            if (item.schedulingCode === compTypeKey) item.components = [components]
        })
        this.setState({ selectComp: cloneCompData }, () => console.log('selectComp----', this.state.selectComp))
    }

    // 对复选框数据格式进行处理
    setCheckboxCompData = (value) => {
        console.log('value-----------', value)
        const { compTypeKey, tabCompData } = this.state;
        const cloneCompData = cloneDeep(tabCompData);
        let components: any = [];
        value.map((val: any) => {
            components.push({ componentTypeCode: val, componentName: COMPONEMT_CONFIG_NAME_ENUM[val] });
        })
        cloneCompData.forEach((item: any) => {
            if (item.schedulingCode === compTypeKey) item.components = components
        })
        this.setState({ selectComp: cloneCompData })
    }

    // 点击确认后存入数据
    setTabCompData = () => {
        const { selectComp } = this.state;
        const cloneSelectComp = cloneDeep(selectComp)
        if (selectComp.length > 0) {
            this.setState({
                tabCompData: cloneSelectComp
            }, () => { this.getLoadTemplate(); console.log('cloneSelectComp-------', cloneSelectComp, this.state.tabCompData) })
        }
        this.setState({ popoverVisible: false });
    }

    // 获取各模块中选中组件的值
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
        return defaultValue;
    }

    getLoadTemplate = () => {
        const { compTypeKey, tabCompData, componentConfig } = this.state;
        const component = tabCompData.find((item: any) => item.schedulingCode === compTypeKey) || { components: [] };
        if (component.components.length > 0) {
            const componentTypeCode = component.components[0].componentTypeCode;
            if (componentTypeCode !== COMPONENT_TYPE_VALUE.YARN && componentTypeCode !== COMPONENT_TYPE_VALUE.KUBERNETES) {
                Api.getLoadTemplate({
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

    // Tab页组件配置按钮
    componentBtn = () => {
        let content: any;
        const { compTypeKey, popoverVisible } = this.state;
        const value = this.selectDefaultValue();
        const popoverFooter = (
            <div style={{ marginTop: 37 }}>
                <Row>
                    <Col span={8}></Col>
                    <Col>
                        <Button size="small" onClick={() => this.setState({ popoverVisible: false })}>取消</Button>
                        <Button size="small" type="primary" style={{ marginLeft: 8 }} onClick={this.setTabCompData}>确认</Button>
                    </Col>
                </Row>
            </div>
        )
        switch (compTypeKey) {
            case TABS_TITLE_KEY.COMMON:
                content = (
                    <CheckboxGroup onChange={this.setCheckboxCompData} defaultValue={value}>
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
                    <RadioGroup style={{ width: '100%' }} onChange={this.setRadioCompData} defaultValue={value[0]}>
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
                    <CheckboxGroup onChange={this.setCheckboxCompData} defaultValue={value}>
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
                    <CheckboxGroup onChange={this.setCheckboxCompData} defaultValue={value}>
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
                break;
        }
        return (
            <Popover
                title="组件配置"
                placement="topRight"
                trigger="click"
                visible={popoverVisible}
                content={content}
                style={{ width: 240 }}
            >
                <Button className="c-clusterManage__componentButton" onClick={() => this.setState({ popoverVisible: true })}><i className="iconfont iconzujianpeizhi" style={{ marginRight: 2 }}></i>组件配置</Button>
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
                            ...this.state.componentConfig,
                            [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                                ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                                uploadFileName: file,
                                fileName: file.files[0].name,
                                configInfo: res.data[0]
                            }
                        },
                        uploadLoading: false
                    }, () => console.log('this.state.componentConfig---', this.state.componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]))
                } else {
                    this.setState({
                        uploadLoading: false
                    })
                }
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

    /**
     * 添加、更新组件数据参数
     */
    getComponentConfigPrames (values: any, components: any) {
        // const { componentConfig } = this.state;
        const componentTypeCode = components.componentTypeCode;

        // 组件配置相关 配置文件、组件id、组件模板、
        const config = this.getComponentConfig(components);
        const {
            uploadFileName = {}, configInfo = '', loadTemplate = [] } = config;
        const files = uploadFileName.files || '';

        // file文件剔除
        let saveConfig = values[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]];
        saveConfig = omit(saveConfig, ['file']);
        const { hadoopVersion = '', kerberosFileName = '' } = saveConfig;
        const { clusterName } = values;

        // 返回模板信息以及相关输入值
        const componentTemplate = cloneDeep(loadTemplate)
        componentTemplate.forEach((item: any) => {
            if (saveConfig[item.key]) {
                item.value = saveConfig[item.key]
            }
        })

        // 上传配置信息或者配置表单键值
        const paramsConfig = configInfo || saveConfig;
        return {
            resources: files[0] || '',
            clusterName: clusterName,
            componentConfig: JSON.stringify({ ...paramsConfig }),
            kerberosFileName: kerberosFileName,
            hadoopVersion: hadoopVersion,
            componentCode: componentTypeCode,
            componentTemplate: JSON.stringify(componentTemplate)
        }
    }

    saveComponent = (components: any) => {
        const { validateFieldsAndScroll } = this.props.form;
        const componentTypeCode = components.componentTypeCode;
        validateFieldsAndScroll((err: any, values: any) => {
            console.log(err, values)
            if (err) {
                let paramName = COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode];
                if (Object.keys(err).includes(paramName)) {
                    message.error('请检查配置')
                    return;
                }
            }
            if (!values.clusterName) {
                message.error('集群名称不能为空')
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
        const { componentConfig } = this.state;
        const componentTypeCode = components.componentTypeCode;
        const config = this.getComponentConfig(components);
        const { loadTemplate = [], defaultFileName = '' } = config;

        // 提取模板的对应值
        const componentTemplate = cloneDeep(loadTemplate)
        let templateValues: any = {};
        componentTemplate.map((item: any) => {
            templateValues[item.key] = item.value;
        })
        form.setFieldsValue({
            [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                ...templateValues,
                file: defaultFileName
            }
        })
        // this.setState({
        //     componentConfig: {
        //         ...this.state.componentConfig,
        //         [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
        //             ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
        //             fileName: defaultFileName
        //         }
        //     }
        // });
        // console.log('handleCancel--------', { ...templateValues, fileName: defaultFileName })
    }

    turnBack = () => {
        const { url, history, autoClose } = this.props
        if (url) {
            if (history) { browserHistory.push(url) } else { hashHistory.push(url) }
        } else {
            if (window.history.length == 1) {
                if (autoClose) {
                    window.close();
                }
            } else {
                hashHistory.go(-1);
            }
        }
    }

    // 返回各个模块下的组件
    renderCompTabs = (item: any) => {
        const { tabCompData } = this.state;
        if (tabCompData.length === 0) return {};
        return tabCompData.find((comps: any) => comps.schedulingCode === item.schedulingCode) || {};
    }

    // 渲染文件信息
    renderDisplayResource = (components: any) => {
        const { getFieldDecorator } = this.props.form;
        const { location } = this.props;
        const params = location.state || {};
        return (
            <DisplayResource
                {...this.state}
                params={params}
                components={components}
                getFieldDecorator={getFieldDecorator}
                downloadFile={this.downloadFile}
                fileChange={this.fileChange} />
        )
    }

    // 渲染配置信息
    renderComponentsConfig = (components: any) => {
        const { getFieldDecorator, getFieldValue } = this.props.form
        return (
            <ComponentsConfig
                {...this.state}
                components={components}
                getFieldValue={getFieldValue}
                getFieldDecorator={getFieldDecorator} />
        )
    }

    renderIcon = (scheduling: any) => {
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

    render () {
        const { compTypeKey, clusterName } = this.state;
        const { getFieldDecorator } = this.props.form;
        const componentBtn = this.componentBtn();
        const { mode } = this.props.location.state || {} as any;
        const isView = mode === 'view';

        return (
            <div className="c-clusterManage__containerWrap" ref={(el) => { this.container = el; }}>
                <div style={{ height: 20 }}>
                    <span className="c-clusterManage__turnBack" onClick={this.turnBack}>多集群管理 / </span>
                    <span className="c-clusterManage__title">新增集群</span>
                </div>
                <React.Fragment>
                    <div style={{ marginBottom: 12, marginTop: 12, height: 32 }}>
                        <FormItem label={null}>
                            {getFieldDecorator('clusterName', { initialValue: clusterName || '' })(
                                <Input style={{ width: 340, height: 32 }} placeholder="请输入集群标识" />
                            )}
                        </FormItem>
                    </div>
                    <div className="c-clusterManage__container shadow">
                        <Tabs
                            tabPosition="top"
                            onChange={this.onTabChange}
                            activeKey={`${compTypeKey}`}
                            className="c-clusterManage__container__commonTabs"
                            tabBarExtraContent={<div className="c-clusterManage__commonTabs__title">集群配置</div>}
                        >
                            {
                                TABS_TITLE.map((scheduling: any, index: any) => {
                                    // tabCompData.map((scheduling: any, index: any) => {
                                    const tabCompDataList = this.renderCompTabs(scheduling).components || [];
                                    return (
                                        <TabPane
                                            tab={
                                                <div style={{ height: 19, display: 'flex', alignItems: 'center' }}>
                                                    {this.renderIcon(scheduling)}
                                                    {scheduling.schedulingName}
                                                </div>
                                            }
                                            key={scheduling.schedulingCode}
                                        >
                                            <Card
                                                className="c-clusterManage__container__card console-tabs cluster-tab-width"
                                                noHovering
                                            >
                                                <Tabs
                                                    tabPosition="left"
                                                    tabBarExtraContent={componentBtn}
                                                    className="c-clusterManage__container__componentTabs"
                                                    onChange={(key: any) => console.log('renderkey-----------------', key)}
                                                >
                                                    {
                                                        tabCompDataList.map((comps: any, index: any) => {
                                                            return (
                                                                <TabPane tab={<span>{comps.componentName}</span>} key={`${comps.componentTypeCode}`}>
                                                                    <div className="c-clusterManage__container__componentWrap">
                                                                        <div className="c-clusterManage__container__componentWrap__resource" style={{ width: 200 }}>
                                                                            {this.renderDisplayResource(comps)}
                                                                        </div>
                                                                        <div className="c-clusterManage__container__componentWrap__config">
                                                                            {this.renderComponentsConfig(comps)}
                                                                        </div>
                                                                    </div>
                                                                    <div className="c-clusterManage__container__componentFooter">
                                                                        <Button className="c-clusterManage__container__componentFooter__btn" onClick={this.handleCancel.bind(this, comps)}>取消</Button>
                                                                        <Button className="c-clusterManage__container__componentFooter__btn" type="primary" style={{ marginLeft: 8 }} onClick={this.saveComponent.bind(this, comps)} >保存</Button>
                                                                    </div>
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
            </div>
        )
    }
}
export default Form.create<any>()(EditCluster);
