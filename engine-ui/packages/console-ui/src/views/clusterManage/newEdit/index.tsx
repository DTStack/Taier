import * as React from 'react'
import { Form, Breadcrumb, Tabs, Button,
    message, Modal, Icon } from 'antd'
import { hashHistory } from 'react-router'
import * as _ from 'lodash'

import Api from '../../../api/console'
import { initialScheduling, isViewMode, isNeedTemp,
    getModifyComp, isSameVersion, getCompsId,
    isMulitiVersion } from './help'
import { TABS_TITLE, COMPONENT_CONFIG_NAME, DEFAULT_COMP_VERSION,
    COMPONENT_TYPE_VALUE, TABS_POP_VISIBLE, COMP_ACTION } from './const'

import FileConfig from './fileConfig'
import FormConfig from './formConfig'
import ToolBar from './components/toolbar'
import ComponentButton from './components/compsBtn'
import TestRestIcon from '../../../components/testResultIcon'
import MulitiVersionComp from './components/multiVerComp'

const TabPane = Tabs.TabPane
const confirm = Modal.confirm
interface IState {
    testLoading: boolean;
    activeKey: number;
    clusterName: string;
    commVersion: string;
    versionData: any;
    testStatus: any;
    popVisible: any;
    saveCompsData: any[];
    initialCompData: any[];
}

class EditCluster extends React.Component<any, IState> {
    state: IState = {
        testLoading: false,
        activeKey: 0,
        clusterName: '',
        commVersion: '',
        versionData: {},
        testStatus: {},
        popVisible: TABS_POP_VISIBLE,
        saveCompsData: [],
        initialCompData: initialScheduling() // 初始各组件的存储值
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
        const typeCode = key ?? initialCompData[activeKey][0]?.componentTypeCode
        const comp = initialCompData[activeKey].find(comp => comp.componentTypeCode == typeCode)

        if ((!isNeedTemp(Number(typeCode)) && !comp?.componentTemplate && initialCompData[activeKey]?.length) ||
            params?.compVersion || params?.storeType) {
            const res = await Api.getLoadTemplate({
                clusterName,
                componentType: typeCode,
                version: params?.compVersion ?? DEFAULT_COMP_VERSION[typeCode] ?? '',
                storeType: params?.storeType ?? getFieldValue(`${typeCode}.storeType`) ?? ''
            })
            if (res.code == 1) {
                this.saveComp({
                    componentTemplate: JSON.stringify(res.data),
                    componentTypeCode: Number(typeCode)
                })
            }
            this.getSaveComponentList()
        }
    }

    handleCompVersion = (typeCode: string, version: string) => {
        if (isSameVersion(Number(typeCode))) {
            this.setState({
                commVersion: version[version.length - 1]
            })
            this.props.form.setFieldsValue({
                [COMPONENT_TYPE_VALUE.YARN]: {
                    hadoopVersion: version[version.length - 1],
                    hadoopVersionSelect: version
                },
                [COMPONENT_TYPE_VALUE.HDFS]: {
                    hadoopVersion: version[version.length - 1],
                    hadoopVersionSelect: version
                }
            })
            return
        }
        this.getLoadTemplate(typeCode, { compVersion: version })
    }

    onTabChange = (key: string) => {
        this.setState((preState) => ({
            activeKey: Number(key),
            popVisible: {
                ...preState.popVisible,
                [preState.activeKey]: false,
                [Number(key)]: false
            }
        }))
    }

    handlePopVisible = (visible?: boolean) => {
        this.setState((preState) => ({
            popVisible: {
                ...preState.popVisible,
                [preState.activeKey]: visible ?? true
            }
        }))
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

    handleConfirm = async (action: string, comps: any | any[]) => {
        console.log(comps)
        // 先删除组件，再添加
        const { initialCompData, activeKey, testStatus } = this.state
        let newCompData = initialCompData
        let newTestStatus = testStatus
        let currentCompArr = newCompData[activeKey]
        if (action == COMP_ACTION.DELETE) {
            const { componentTypeCode, id = '' } = comps
            const componentIds = getCompsId(currentCompArr, [id])
            let res: any
            if (componentIds.length) {
                res = await Api.deleteComponent({ componentIds })
            }

            if (res?.code == 1 || !componentIds.length) {
                currentCompArr = currentCompArr.filter(comp => comp.componentTypeCode != componentTypeCode)
                newTestStatus = {
                    ...newTestStatus,
                    [componentTypeCode]: null
                }
                this.props.form.setFieldsValue({
                    [componentTypeCode]: {
                        componentConfig: {},
                        specialConfig: {}
                    }
                })
            }
        } else {
            if (comps.length) {
                comps.forEach(code => {
                    currentCompArr.push({
                        componentTypeCode: code,
                        componentName: COMPONENT_CONFIG_NAME[code]
                    })
                })
            }
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
            if (comp.componentTypeCode == params.componentTypeCode) {
                return { ...comp, ...params }
            }
            return comp
        })
        newCompData[activeKey] = newComp
        this.setState({
            initialCompData: newCompData
        })
    }

    /** 完成操作交互后续可能会有变更 */
    handleComplete = () => {
        const { validateFieldsAndScroll } = this.props.form;
        const { initialCompData } = this.state
        const showConfirm = (arr: any[]) => {
            const compsName = Array.from(arr).map((code: number) => `"${COMPONENT_CONFIG_NAME[code]}"`)
            confirm({
                title: `${compsName.join('、')}尚未保存，是否需要保存？`,
                content: null,
                icon: <Icon style={{ color: '#FAAD14' }} type="exclamation-circle" theme="filled" />,
                okText: '保存',
                cancelText: '取消',
                onOk: () => {
                },
                onCancel: () => {
                    this.props.router.push('/console/clusterManage')
                }
            })
        }
        validateFieldsAndScroll((err: any, values: any) => {
            console.log(err, values)
            let modifyCompsArr = getModifyComp(values, initialCompData);
            if (!modifyCompsArr.size) {
                this.props.router.push('/console/clusterManage')
                return
            }
            showConfirm(modifyCompsArr)
        })
    }

    setTestStatus = (status: any, isSingle?: boolean) => {
        if (isSingle) {
            this.setState((preState) => ({
                testStatus: {
                    ...preState.testStatus,
                    [status.componentTypeCode]: {
                        ...status
                    }
                }
            }))
            return
        }
        let testStatus: any = {}
        status.forEach((temp: any) => {
            testStatus[temp.componentTypeCode] = { ...temp }
        })
        this.setState({
            testStatus: testStatus
        })
    }

    testConnects = (typeCode?: number, callBack?: Function) => {
        const { form } = this.props
        const { initialCompData, clusterName } = this.state
        form.validateFields(null, {}, (err: any, values: any) => {
            console.log(err, values)
            if ((err && !typeCode) || (err && Object.keys(err).includes(String(typeCode)))) {
                message.error('请检查配置')
                return
            }
            const modifyComps = getModifyComp(values, initialCompData)
            if (typeCode || typeCode == 0) {
                if (modifyComps.size > 0 && Array.from(modifyComps).includes(String(typeCode))) {
                    message.error(`组件 ${COMPONENT_CONFIG_NAME[typeCode]} 参数变更未保存，请先保存再测试组件连通性`)
                    return
                }
                callBack && callBack(true)
                Api.testConnect({
                    clusterName,
                    componentType: typeCode
                }).then((res: any) => {
                    if (res.code === 1) {
                        this.setTestStatus(res.data, true)
                    }
                    callBack && callBack(false)
                })
            } else {
                if (modifyComps.size > 0) {
                    const modifyCompsName = Array.from(modifyComps).map((code: number) => COMPONENT_CONFIG_NAME[code])
                    message.error(`组件 ${modifyCompsName.join('、')} 参数变更未保存，请先保存再测试组件连通性`)
                    return
                }
                this.setState({ testLoading: true });
                Api.testConnects({
                    clusterName
                }).then((res: any) => {
                    if (res.code === 1) {
                        this.setTestStatus(res.data)
                    }
                    this.setState({ testLoading: false })
                })
            }
        })
    }

    render () {
        const { mode, cluster } = this.props.location.state || {} as any
        const { clusterName, activeKey, initialCompData, versionData,
            saveCompsData, testLoading, testStatus, commVersion, popVisible } = this.state

        return (
            <div className="c-editCluster__containerWrap">
                <div className="c-editCluster__header">
                    <Breadcrumb>
                        <Breadcrumb.Item>
                            <a onClick={() => { this.props.router.push('/console/clusterManage') }}>多集群管理</a>
                        </Breadcrumb.Item>
                        <Breadcrumb.Item>{clusterName}</Breadcrumb.Item>
                    </Breadcrumb>
                    {isViewMode(mode) ? <span>
                        <Button className="cluster-btn" type="primary" onClick={this.turnCompMode.bind(this, 'edit')}>编辑</Button>
                    </span>
                        : <span>
                            <Button className="cluster-btn" ghost loading={testLoading} onClick={() => this.testConnects()} >测试所有组件连通性</Button>
                            <Button className="cluster-btn" type="primary" onClick={this.handleComplete}>完成</Button>
                        </span>}
                </div>
                <div className="c-editCluster__container">
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
                                        comps={comps}
                                        popVisible={popVisible[activeKey]}
                                        activeKey={activeKey}
                                        handleConfirm={this.handleConfirm}
                                        handlePopVisible={this.handlePopVisible}
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
                                            <>
                                                {isMulitiVersion(comp.componentTypeCode)
                                                    ? <MulitiVersionComp
                                                        comp={comp}
                                                        versionData={versionData}
                                                    />
                                                    : <>
                                                        <FileConfig
                                                            comp={comp}
                                                            view={isViewMode(mode)}
                                                            form={this.props.form}
                                                            versionData={versionData}
                                                            commVersion={commVersion}
                                                            saveCompsData={saveCompsData}
                                                            clusterInfo={{ clusterName, clusterId: cluster.clusterId }}
                                                            handleCompVersion={this.handleCompVersion}
                                                        />
                                                        <FormConfig
                                                            comp={comp}
                                                            view={isViewMode(mode)}
                                                            form={this.props.form}
                                                        />
                                                    </>}
                                                    {!isViewMode(mode) && <ToolBar
                                                        comp={comp}
                                                        clusterInfo={{ clusterName, clusterId: cluster.clusterId }}
                                                        initialCompData={initialCompData[activeKey]}
                                                        form={this.props.form}
                                                        saveComp={this.saveComp}
                                                        testConnects={this.testConnects}
                                                        handleConfirm={this.handleConfirm}
                                                    />}
                                                </>
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
