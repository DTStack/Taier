import * as React from 'react';
import { cloneDeep, omit } from 'lodash';
import { connect } from 'react-redux';
import {
    Form, Input, Card, Tabs, Button,
    Popover, Checkbox, Row, Col, Radio,
    message
} from 'antd';
import Api from '../../../api/console'

import { updateTestStatus, updateRequiredStatus } from '../../../reducers/modules/cluster';

import {
    SOURCE_COMPONENTS, STORE_COMPONENTS, COMPUTE_COMPONENTS, COMMON_COMPONENTS,
    TABS_TITLE, TABS_TITLE_KEY, COMPONEMT_CONFIG_NAME_ENUM, COMPONEMT_CONFIG_KEY_ENUM,
    COMPONENT_TYPE_VALUE } from '../../../consts';

import DisplayResource from './displayResource';
import ComponentsConfig from './componentsConfig';
// import GoBack from 'dt-common/src/components/go-back';

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
        tabCompData: [], // 组件结构信息
        selectComp: [], // 存储勾选状态
        componentConfig: {}, // 组件配置文件信息
        uploadLoading: false,
        componentId: '', // 组件id
        clusterName: '' // 组件名称
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
        const { location } = this.props;
        const params = location.state || {};
        params.mode !== 'new' && Api.getClusterInfo({
            clusterId: params.cluster.id || params.cluster.clusterId
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tabCompData: res.data.scheduling,
                    clusterName: res.data.clusterName
                }, () => console.log('sssssss-----', this.state.tabCompData))
            }
        })
    }

    // 对单选框数据格式进行处理
    setSelectCompData = (e: any) => {
        const { compTypeKey } = this.state;
        let compData: any = [];
        let tabsInfo: any = {};
        let components = [];
        let compInfo: any = {};
        compInfo.componentTypeCode = e.target.value;
        compInfo.componentName = COMPONEMT_CONFIG_NAME_ENUM[e.target.value];
        components.push(compInfo)
        tabsInfo.schedulingCode = compTypeKey;
        tabsInfo.components = components;
        compData.push(tabsInfo);
        this.setState({
            selectComp: compData
        })
    }

    // 对复选框数据格式进行处理
    setSelectCheckboxCompData = (value) => {
        const { compTypeKey } = this.state;
        let compData: any = [];
        let tabsInfo: any = {};
        let components = [];
        value.map((item: any) => {
            let compInfo: any = {};
            compInfo.componentTypeCode = item;
            compInfo.componentName = COMPONEMT_CONFIG_NAME_ENUM[item];
            components.push(compInfo)
        })
        tabsInfo.schedulingCode = compTypeKey;
        tabsInfo.components = components;
        compData.push(tabsInfo);
        this.setState({
            selectComp: compData
        })
    }

    // 点击确认后存入数据
    setTabCompData = () => {
        const { selectComp, tabCompData } = this.state;
        let newTabCompData: any = [];
        let cloneTabCompData = cloneDeep(tabCompData)
        selectComp.map((item: any, index: any) => {
            const compIndex = cloneTabCompData.findIndex((comp: any) => comp.schedulingCode === item.schedulingCode);
            if (compIndex > -1) {
                cloneTabCompData.splice(compIndex, 1);
            }
            // console.log('rendersetTabCompData---------', compIndex, cloneTabCompData)
            return newTabCompData.push(item);
        })
        newTabCompData = newTabCompData.concat(cloneTabCompData)
        this.setState({
            tabCompData: newTabCompData,
            popoverVisible: false
        }, () => { this.getLoadTemplate(); console.log('tabCompData-------', this.state.tabCompData) })
    }

    getLoadTemplate = () => {
        const { compTypeKey, tabCompData, componentConfig } = this.state;
        const component = tabCompData.find((item: any) => item.schedulingCode === compTypeKey);
        const componentTypeCode = component.components[0].componentTypeCode;
        // console.log(component)
        if (component && componentTypeCode !== COMPONENT_TYPE_VALUE.YARN && componentTypeCode !== COMPONENT_TYPE_VALUE.KUBERNETES) {
            Api.getLoadTemplate({
                componentType: componentTypeCode
            }).then((res: any) => {
                if (res.code === 1) {
                    // console.log(res.data)
                    this.setState({
                        componentConfig: {
                            ...this.state.componentConfig,
                            [COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]: {
                                ...componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]],
                                loadTemplate: res.data
                            }
                        }
                    }, () => console.log('componentConfig-------', this.state.componentConfig))
                }
            })
        }
    }

    compValue = (schedulingCode: any) => {
        const { tabCompData } = this.state;
        let selectValue: any = [];
        let radiusSelectValue: any;
        if (tabCompData.length === 0) return [];
        if (schedulingCode === 1) {
            tabCompData.map((item: any) => {
                if (item.schedulingCode === schedulingCode) {
                    item.components.map((item: any) => {
                        radiusSelectValue = item.componentTypeCode;
                    })
                }
            })
            return radiusSelectValue;
        }
        tabCompData.map((item: any) => {
            if (item.schedulingCode === schedulingCode) {
                item.components.map((item: any) => {
                    selectValue.push(item.componentTypeCode);
                })
            }
        })
        return selectValue;
    }

    // Tab页组件配置按钮
    componentBtn = () => {
        let content: any;
        const { compTypeKey, popoverVisible } = this.state;
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
                // const commonValue = this.compValue(TABS_TITLE_KEY.COMMON);
                // console.log('rendercommon-----------', value)
                content = (
                    <CheckboxGroup onChange={this.setSelectCheckboxCompData}>
                        <Row>
                            {
                                COMMON_COMPONENTS.map((item: any) => {
                                    return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                                })
                            }
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            case TABS_TITLE_KEY.SOURCE:
                // const sourceValue = this.compValue(TABS_TITLE_KEY.SOURCE);
                content = (
                    <RadioGroup style={{ width: '100%' }} onChange={this.setSelectCompData}>
                        <Row>
                            {
                                SOURCE_COMPONENTS.map((item: any) => {
                                    return <Col key={`${item.componentTypeCode}`}><Radio value={item.componentTypeCode}>{item.componentName}</Radio></Col>
                                })
                            }
                        </Row>
                        {popoverFooter}
                    </RadioGroup>
                )
                break;
            case TABS_TITLE_KEY.STORE:
                content = (
                    <CheckboxGroup onChange={this.setSelectCheckboxCompData}>
                        <Row>
                            {
                                STORE_COMPONENTS.map((item: any) => {
                                    return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                                })
                            }
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            case TABS_TITLE_KEY.COMPUTE:
                content = (
                    <CheckboxGroup onChange={this.setSelectCheckboxCompData}>
                        <Row>
                            {
                                COMPUTE_COMPONENTS.map((item: any) => {
                                    return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                                })
                            }
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            default: content = '';
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
                <Button className="c-clusterManage__componentButton" onClick={() => this.setState({ popoverVisible: true })}>组件配置</Button>
            </Popover>
        )
    }

    // 配置文件Change事件
    fileChange = (e: any, componentTypeCode: any) => {
        // console.log('Yarnfile---------', e.target);
        const file = e.target;
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
                                configInfo: res.data[0]
                            }
                        },
                        uploadLoading: false
                    }, () => console.log(this.state.componentConfig, this.state.componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]]))
                } else {
                    this.setState({
                        uploadLoading: false
                    })
                }
            })
        }
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

    /**
     * 添加、更新组件数据参数
     */
    getComponentConf (values: any, component: any) {
        const { componentConfig } = this.state;
        const componentTypeCode = component.componentTypeCode;

        // 配置相关
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
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

    saveComponent = (component: any) => {
        const { validateFieldsAndScroll } = this.props.form;
        validateFieldsAndScroll((err: any, values: any) => {
            console.log(err, values)
            if (err) {
                let paramName = COMPONEMT_CONFIG_KEY_ENUM[component.componentTypeCode];
                if (Object.keys(err).includes(paramName)) {
                    message.error('请检查配置')
                    return;
                }
            }
            if (!values.clusterName) {
                message.error('集群名称不能为空')
                return;
            }
            const params = this.getComponentConf(values, component);
            console.log('shanshansshana----------', { ...params })
            Api.saveComponent({
                ...params
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        componentId: res.data.id
                    }, () => console.log('shshhsha------', this.state.componentId))
                }
            })
        })
    }

    // 返回各个模块下的组件
    renderCompTabs = (item: any) => {
        const { tabCompData } = this.state;
        if (tabCompData.length === 0) return {};
        return tabCompData.find((comps: any) => comps.schedulingCode === item.tabsKey) || {};
    }

    // 渲染文件信息
    renderDisplayResource = (component: any) => {
        const { getFieldDecorator } = this.props.form
        // const { YarnFile, uploadLoading } = this.state;
        return (
            <DisplayResource
                {...this.state}
                component={component}
                getFieldDecorator={getFieldDecorator}
                // YarnFile={YarnFile}
                // uploadLoading={uploadLoading}
                fileChange={this.fileChange} />
        )
    }

    // 渲染配置信息
    renderComponentsConfig = (component: any) => {
        const { getFieldDecorator, getFieldValue } = this.props.form
        return (
            <ComponentsConfig
                {...this.state}
                // YarnCofig={YarnCofig}
                component={component}
                getFieldValue={getFieldValue}
                getFieldDecorator={getFieldDecorator} />
        )
    }

    render () {
        const { compTypeKey, clusterName } = this.state;
        const { getFieldDecorator } = this.props.form;
        const componentBtn = this.componentBtn();

        return (
            <div className="c-clusterManage__containerWrap" ref={(el) => { this.container = el; }}>
                <div style={{ height: 20 }}>
                    <span style={{ fontSize: 14, fontWeight: 'bold', color: '#999' }}>多集群管理 / </span>
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
                                TABS_TITLE.map((item: any, index: any) => {
                                    const tabCompDataList = this.renderCompTabs(item).components || [];
                                    return (
                                        <TabPane
                                            tab={
                                                <span>
                                                    {/* {this.renderImage(item)} */}
                                                    {item.tabName}
                                                </span>
                                            }
                                            key={item.tabsKey}
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
                                                        tabCompDataList.map((item: any, index: any) => {
                                                            return (
                                                                <TabPane tab={<span>{item.componentName}</span>} key={`${item.componentTypeCode}`}>
                                                                    <div className="c-clusterManage__container__componentWrap">
                                                                        <div className="c-clusterManage__container__componentWrap__resource" style={{ width: 200 }}>
                                                                            {this.renderDisplayResource(item)}
                                                                        </div>
                                                                        <div className="c-clusterManage__container__componentWrap__config">
                                                                            {this.renderComponentsConfig(item)}
                                                                        </div>
                                                                    </div>
                                                                    <div className="c-clusterManage__container__componentFooter">
                                                                        <Button className="c-clusterManage__container__componentFooter__btn">取消</Button>
                                                                        <Button className="c-clusterManage__container__componentFooter__btn" type="primary" style={{ marginLeft: 8 }} onClick={this.saveComponent.bind(this, item)} >保存</Button>
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
