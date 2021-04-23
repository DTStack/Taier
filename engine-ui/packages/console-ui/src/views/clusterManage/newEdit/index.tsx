import * as React from 'react'
import { Form, Breadcrumb, Tabs, Button,
    message, Modal, Icon } from 'antd'
import { hashHistory } from 'react-router'
import * as _ from 'lodash'

import Api from '../../../api/console'
import { initialScheduling, isViewMode, isNeedTemp,
    getModifyComp, isSameVersion, getCompsId,
    isMultiVersion, getCurrentComp, includesCurrentComp,
    getSingleTestStatus, isDataCheckBoxs } from './help'
import { TABS_TITLE, COMPONENT_CONFIG_NAME, DEFAULT_COMP_VERSION,
    COMPONENT_TYPE_VALUE, TABS_POP_VISIBLE, COMP_ACTION } from './const'

import FileConfig from './fileConfig'
import FormConfig from './formConfig'
import ToolBar from './components/toolbar'
import ComponentButton from './components/compsBtn'
import MetaIcon from './components/metaIcon'
import TestRestIcon from '../../../components/testResultIcon'
import MultiVersionComp from './components/multiVerComp'

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
        const comp = getCurrentComp(initialCompData[activeKey], { typeCode })
        const saveParams: any = {
            componentTypeCode: Number(typeCode),
            hadoopVersion: params?.compVersion ?? ''
        }

        if (isNeedTemp(Number(typeCode))) {
            this.saveComp(saveParams)
            return
        }

        if (isMultiVersion(typeCode) && !params?.compVersion) return

        if ((!comp?.componentTemplate && initialCompData[activeKey]?.length) ||
            params?.compVersion || params?.storeType) {
            const res = await Api.getLoadTemplate({
                clusterName,
                componentType: typeCode,
                version: params?.compVersion ?? DEFAULT_COMP_VERSION[typeCode] ?? '',
                storeType: params?.storeType ?? getFieldValue(`${typeCode}.storeType`) ?? ''
            })
            if (res.code == 1) saveParams.componentTemplate = JSON.stringify(res.data)
            this.saveComp(saveParams)
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

    handleConfirm = async (action: string, comps: any | any[], mulitple?: boolean) => {
        const { initialCompData, activeKey, testStatus } = this.state
        let newCompData = initialCompData
        let currentCompArr = newCompData[activeKey]
        if (comps.length && action !== COMP_ACTION.DELETE) {
            const initialComp = comps.map(code => {
                if (!isMultiVersion(code)) return { componentTypeCode: code, multiVersion: [undefined] }
                return { componentTypeCode: code, multiVersion: [] }
            })
            currentCompArr = currentCompArr.concat(initialComp)
        }

        if (action == COMP_ACTION.DELETE) {
            const { componentTypeCode, hadoopVersion, id = '' } = comps
            const componentIds = getCompsId(currentCompArr, id)
            let res: any
            if (componentIds.length) {
                res = await Api.deleteComponent({ componentIds })
            }

            if (res?.code == 1 || !componentIds.length) {
                let wrapper = new Set()
                currentCompArr.forEach(comp => {
                    if (isMultiVersion(comp.componentTypeCode) && mulitple) {
                        comp.multiVersion = comp.multiVersion.filter(vComp => vComp?.hadoopVersion != hadoopVersion)
                        wrapper.add(comp)
                    }
                    if (comp.componentTypeCode != componentTypeCode) wrapper.add(comp)
                })
                currentCompArr = Array.from(wrapper)

                const multiVersion = getSingleTestStatus({ typeCode: componentTypeCode, hadoopVersion }, null, testStatus)
                const resetValue = { componentConfig: {}, specialConfig: {} }
                const fieldValue = isMultiVersion(componentTypeCode)
                    ? { [hadoopVersion]: resetValue } : { resetValue }

                this.setState({
                    testStatus: {
                        ...testStatus,
                        [componentTypeCode]: {
                            ...testStatus[componentTypeCode],
                            result: null,
                            multiVersion: multiVersion
                        }
                    }
                })
                this.props.form.setFieldsValue({ [componentTypeCode]: fieldValue })
            }
        }

        newCompData[activeKey] = currentCompArr
        this.setState({
            initialCompData: newCompData
        }, this.getLoadTemplate)
    }

    saveComp = (params: any, type?: string) => {
        const { activeKey, initialCompData } = this.state
        let newCompData = _.cloneDeep(initialCompData)
        newCompData[activeKey] = initialCompData[activeKey].map(comp => {
            if (comp.componentTypeCode !== params.componentTypeCode) return comp
            if (type == COMP_ACTION.ADD) comp.multiVersion.push(undefined)
            comp.multiVersion = comp.multiVersion.map(vcomp => {
                if (!vcomp) return { ...params }
                if (!isMultiVersion(params.componentTypeCode)) return { ...vcomp, ...params }
                if (!vcomp?.hadoopVersion || vcomp?.hadoopVersion == params.hadoopVersion) return { ...vcomp, ...params }
                return vcomp
            })
            return comp
        })
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
            const { testStatus, initialCompData, activeKey } = this.state
            const currentComp = initialCompData[activeKey].find(comp => comp.componentTypeCode == status.componentTypeCode)
            if (!isMultiVersion(status.componentTypeCode)) {
                this.setState({
                    testStatus: {
                        ...testStatus,
                        [status.componentTypeCode]: {
                            ...status
                        }
                    }
                })
                return
            }
            const multiVersion = getSingleTestStatus({
                typeCode: status.componentTypeCode,
                hadoopVersion: status?.componentVersion
            }, status, testStatus)

            let sign = false // 标记是否有测试连通性失败的多版本组件
            let errorMsg = []

            multiVersion.forEach(mv => {
                if (!mv.result) {
                    sign = true
                    errorMsg.push({
                        componentVersion: mv.componentVersion,
                        errorMsg: mv.errorMsg
                    })
                }
            })

            let msg: any = { result: null, errorMsg: [], multiVersion: multiVersion }
            if (!sign && currentComp?.multiVersion?.length == multiVersion.length) {
                msg.result = true
            }
            if (sign) {
                msg.result = false
                msg.errorMsg = errorMsg
            }

            this.setState((preState) => ({
                testStatus: {
                    ...preState.testStatus,
                    [status.componentTypeCode]: msg
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

    testConnects = (params?: any, callBack?: Function) => {
        const typeCode = params?.typeCode ?? ''
        const hadoopVersion = params?.hadoopVersion ?? ''
        const { form } = this.props
        const { initialCompData, clusterName } = this.state
        form.validateFields(null, {}, (err: any, values: any) => {
            console.log(err, values)

            /** 当前组件错误校验 */
            if (isMultiVersion(typeCode) && err && Object.keys(err[String(typeCode)]).includes(hadoopVersion)) {
                message.error('请检查配置')
                return
            }
            if ((err && !typeCode) || (err && !isMultiVersion(typeCode) && Object.keys(err).includes(String(typeCode)))) {
                message.error('请检查配置')
                return
            }

            const modifyComps = getModifyComp(values, initialCompData)
            if (typeCode || typeCode === 0) {
                if (modifyComps.size > 0 && includesCurrentComp(Array.from(modifyComps), { typeCode, hadoopVersion })) {
                    let desc = COMPONENT_CONFIG_NAME[typeCode]
                    if (isMultiVersion(typeCode)) desc = desc + ' ' + (Number(hadoopVersion) / 100).toFixed(2)
                    message.error(`组件 ${desc} 参数变更未保存，请先保存再测试组件连通性`)
                    return
                }
                callBack && callBack(true)
                Api.testConnect({
                    clusterName,
                    componentType: typeCode,
                    componentVersion: hadoopVersion ?? ''
                }).then((res: any) => {
                    if (res.code === 1) {
                        this.setTestStatus(res.data, true)
                    }
                    callBack && callBack(false)
                })
            } else {
                if (modifyComps.size > 0) {
                    const modifyCompsName = Array.from(modifyComps).map((comp: any) => {
                        if (isMultiVersion(comp.typeCode)) { return COMPONENT_CONFIG_NAME[comp.typeCode] + ' ' + (Number(comp.hadoopVersion) / 100).toFixed(2) }
                        return COMPONENT_CONFIG_NAME[comp.typeCode]
                    })
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
                            const isCheckBoxs = isDataCheckBoxs(comps) // 存在HiveServer、SparkThrift两个组件
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
                                    onChange={(key: any) => {
                                        if (!isMultiVersion(Number(key))) this.getLoadTemplate(key)
                                    }}
                                >
                                    {comps?.length > 0 && comps.map((comp: any) => {
                                        return (<TabPane
                                            tab={<span>
                                                {COMPONENT_CONFIG_NAME[comp.componentTypeCode]}
                                                <MetaIcon form={this.props.form} comp={comp} />
                                                <TestRestIcon testStatus={testStatus[comp.componentTypeCode] ?? {}}/>
                                            </span>}
                                            key={`${comp.componentTypeCode}`}
                                        >
                                            <>
                                                {isMultiVersion(comp.componentTypeCode)
                                                    ? <MultiVersionComp
                                                        comp={comp}
                                                        form={this.props.form}
                                                        view={isViewMode(mode)}
                                                        saveCompsData={saveCompsData}
                                                        versionData={versionData}
                                                        testStatus={testStatus[comp.componentTypeCode]?.multiVersion ?? []}
                                                        clusterInfo={{ clusterName, clusterId: cluster.clusterId }}
                                                        saveComp={this.saveComp}
                                                        getLoadTemplate={this.getLoadTemplate}
                                                        testConnects={this.testConnects}
                                                        handleConfirm={this.handleConfirm}
                                                    />
                                                    : comp?.multiVersion?.map(vcomp => {
                                                        return <>
                                                            <FileConfig
                                                                comp={vcomp}
                                                                view={isViewMode(mode)}
                                                                isCheckBoxs={isCheckBoxs}
                                                                form={this.props.form}
                                                                versionData={versionData}
                                                                commVersion={commVersion}
                                                                saveCompsData={saveCompsData}
                                                                clusterInfo={{ clusterName, clusterId: cluster.clusterId }}
                                                                saveComp={this.saveComp}
                                                                handleCompVersion={this.handleCompVersion}
                                                            />
                                                            <FormConfig
                                                                comp={vcomp}
                                                                view={isViewMode(mode)}
                                                                form={this.props.form}
                                                            />
                                                            {!isViewMode(mode) && <ToolBar
                                                                comp={vcomp}
                                                                clusterInfo={{ clusterName, clusterId: cluster.clusterId }}
                                                                form={this.props.form}
                                                                saveComp={this.saveComp}
                                                                testConnects={this.testConnects}
                                                                handleConfirm={this.handleConfirm}
                                                            />}
                                                        </>
                                                    })}
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
