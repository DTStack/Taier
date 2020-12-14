import * as React from 'react'
import { Form, Breadcrumb, Tabs, Button, message } from 'antd'
import { hashHistory } from 'react-router';
import * as _ from 'lodash'

import Api from '../../../api/console'
import { getActionType, initialScheduling, giveMeAKey, isViewMode,
    isNeedTemp, getModifyComp } from './help'
import { TABS_TITLE, COMPONENT_CONFIG_NAME, DEFAULT_COMP_VERSION } from './const'

import FileConfig from './fileConfig'
import FormConfig from './formConfig'
import ToolBar from './buttons/toolbar'
import ComponentButton from './buttons/componentbBtn'
import TestRestIcon from '../../../components/testResultIcon';

const TabPane = Tabs.TabPane;
interface IState {
    initialCompData: any[];
    versionData: any;
    clusterName: string;
    activeKey: number;
    saveCompsData: any[];
    testLoading: boolean;
    testStatus: any;
}
class EditCluster extends React.Component<any, IState> {
    state: IState = {
        initialCompData: initialScheduling(), // 初始各组件的存储值
        versionData: {},
        clusterName: '',
        activeKey: 0,
        saveCompsData: [],
        testLoading: false,
        testStatus: {}
    }

    componentDidMount () {
        this.getDataList();
        this.getVersionData();
    }

    getDataList = () => {
        const { cluster } = this.props.location.state || {} as any;
        Api.getClusterInfo({
            clusterId: cluster.clusterId
        }).then((res: any) => {
            if (res.code === 1) {
                let initData = initialScheduling()
                const { scheduling } = res.data
                scheduling && scheduling.forEach((comps: any) => {
                    initData[comps.schedulingCode] = comps.components
                })
                this.setState({
                    initialCompData: initData,
                    clusterName: res.data.clusterName
                }, this.getSaveComponentList)
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

    getSaveComponentList= async () => {
        const { clusterName } = this.state
        const res = await Api.getComponentStore({ clusterName })
        if (res.code == 1 && res.data) {
            let saveCompsData = []
            res.data.forEach((item: any) => {
                saveCompsData.push({
                    key: item?.componentTypeCode,
                    value: item?.componentName
                })
            })
            this.setState({
                saveCompsData
            })
        }
    }

    // 获取组件模板
    getLoadTemplate = async (key?: string, params?: any) => {
        const { getFieldValue } = this.props.form
        const { clusterName, initialCompData, activeKey } = this.state
        const typeCode = key ?? initialCompData[activeKey][0].componentTypeCode
        const comp = initialCompData[activeKey].find(comp => comp.componentTypeCode == typeCode)

        if (!isNeedTemp(typeCode) && !comp.componentTemplate) {
            Api.getLoadTemplate({
                clusterName,
                componentType: typeCode,
                version: params?.compVersion ?? DEFAULT_COMP_VERSION[typeCode] ?? '',
                storeType: params?.storeType ?? getFieldValue(`${typeCode}.storeType`) ?? ''
            }).then((res: any) => {
                if (res.code == 1) {
                    this.saveComp({ componentTemplate: JSON.stringify(res.data), typeCode })
                }
            })
        }
    }

    onTabChange = (key: string) => {
        this.setState({
            activeKey: Number(key)
        })
    }

    turnCompMode = (type: string) => {
        const { cluster } = this.props.location.state || {} as any;
        this.setState({ testLoading: false })
        hashHistory.push({
            pathname: '/console/clusterManage/editCluster',
            state: {
                mode: type,
                cluster
            }
        })
    }

    handleConfirm = (addComps: any[], deleteComps: any[]) => {
        console.log(addComps, deleteComps)
        // 先删除组件，再添加
        const { initialCompData, activeKey, testStatus } = this.state
        let newCompData = initialCompData
        let newTestStatus = testStatus
        let currentCompArr = newCompData[activeKey]
        if (deleteComps.length > 0) {
            deleteComps.forEach(code => {
                currentCompArr = currentCompArr.filter(comp => comp.componentCode == code)
                newTestStatus = {
                    ...newTestStatus,
                    [code]: null
                }
            })
        }
        if (addComps.length > 0) {
            addComps.forEach(code => {
                currentCompArr.push({
                    componentTypeCode: code,
                    componentName: COMPONENT_CONFIG_NAME[code]
                })
            })
        }
        newCompData[activeKey] = currentCompArr
        this.setState({
            initialCompData: newCompData,
            testStatus: newTestStatus
        }, this.getLoadTemplate)
    }

    saveComp = (params: any) => {
        const { activeKey, initialCompData } = this.state
        let newCompData = _.cloneDeep(initialCompData)
        let newComp = initialCompData[activeKey].map(comp => {
            if (comp.componentTypeCode == params.typeCode) {
                return { ...comp, ...params }
            }
            return comp
        })
        newCompData[activeKey] = newComp
        this.setState({
            initialCompData: newCompData
        }, () => console.log('initialCompData == ', initialCompData))
    }

    testConnects = () => {
        const { form } = this.props
        const { initialCompData, clusterName } = this.state
        form.validateFields(null, {}, (err: any, values: any) => {
            console.log(err, values)
            if (err) {
                message.error('请检查配置');
                return
            }
            if (!err) {
                let modifyCompsName = []
                const modifyComps = getModifyComp(values, initialCompData)
                if (modifyComps.size > 0) {
                    console.log(modifyComps)
                    modifyComps.forEach((code) => {
                        modifyCompsName.push(COMPONENT_CONFIG_NAME[code])
                    })
                    message.error(`组件 ${modifyCompsName.join('、')} 参数变更未保存，请先保存再测试组件连通性`)
                    return
                }
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
                    this.setState({ testLoading: false })
                })
            }
        })
    }

    render () {
        const { mode, cluster } = this.props.location.state || {} as any
        const { clusterName, activeKey, initialCompData, versionData,
            saveCompsData, testLoading, testStatus } = this.state
        console.log(testStatus)

        return (
            <div className="c-editCluster__containerWrap">
                <div className="c-editCluster__header">
                    <Breadcrumb>
                        <Breadcrumb.Item onClick={() => { this.props.router.push('/console/clusterManage') }}>
                            多集群管理
                        </Breadcrumb.Item>
                        <Breadcrumb.Item>{getActionType(mode)}</Breadcrumb.Item>
                        <Breadcrumb.Item>{clusterName}</Breadcrumb.Item>
                    </Breadcrumb>
                    {isViewMode(mode) ? <span>
                        <Button className="cluster-btn" type="primary" onClick={this.turnCompMode.bind(this, 'edit')}>编辑</Button>
                    </span>
                        : <span>
                            <Button className="cluster-btn" ghost loading={testLoading} onClick={this.testConnects} >测试所有组件连通性</Button>
                            <Button className="cluster-btn" type="primary" onClick={this.turnCompMode.bind(this, 'view')}>完成</Button>
                        </span>}
                </div>
                <div className="c-editCluster__container shadow">
                    <Tabs
                        tabPosition="top"
                        onChange={this.onTabChange}
                        activeKey={`${activeKey}`}
                        className="c-editCluster__container__commonTabs"
                        tabBarExtraContent={<div className="c-editCluster__commonTabs__title">集群配置</div>}
                    >
                        {initialCompData.map((comps: any, key: number) => {
                            return (<TabPane
                                tab={
                                    <div style={{ height: 19, display: 'flex', alignItems: 'center' }}>
                                        <i className={`iconfont ${TABS_TITLE[key].iconName}`} style={{ marginRight: 2 }} />
                                        {TABS_TITLE[key].name}
                                    </div>
                                }
                                key={String(key)}
                            >
                                {comps?.length == 0 && <div key={activeKey} className='empty-logo'>
                                    <img src="public/img/emptyLogo.svg" />
                                </div>}
                                <Tabs
                                    tabPosition="left"
                                    tabBarExtraContent={!isViewMode(mode) && <ComponentButton
                                        key={giveMeAKey()}
                                        activeKey={activeKey}
                                        comps={comps}
                                        handleConfirm={this.handleConfirm}
                                    />}
                                    className="c-editCluster__container__componentTabs"
                                    onChange={(key: any) => this.getLoadTemplate(key)}
                                >
                                    {comps?.length > 0 && comps.map((comp: any) => {
                                        return (<TabPane
                                            tab={<span>
                                                {comp.componentName}
                                                <TestRestIcon testStatus={testStatus[comp.componentTypeCode] ?? {}}/>
                                            </span>}
                                            key={`${comp.componentTypeCode}`}
                                        >
                                            <FileConfig
                                                comp={comp}
                                                view={isViewMode(mode)}
                                                form={this.props.form}
                                                versionData={versionData}
                                                clusterName={clusterName}
                                                saveCompsData={saveCompsData}
                                            />
                                            <FormConfig
                                                comp={comp}
                                                view={isViewMode(mode)}
                                                form={this.props.form}
                                            />
                                            {!isViewMode(mode) && <ToolBar
                                                comp={comp}
                                                clusterInfo={{ clusterName, clusterId: cluster.clusterId }}
                                                initialCompData={initialCompData[activeKey]}
                                                form={this.props.form}
                                                saveComp={this.saveComp}
                                            />}
                                        </TabPane>)
                                    })}
                                </Tabs>
                            </TabPane>)
                        })}
                    </Tabs>
                </div>
            </div>
        )
    }
}
export default Form.create<any>()(EditCluster);
