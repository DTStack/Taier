import * as React from 'react'
import { Form, Breadcrumb, Tabs } from 'antd'
import Api from '../../../api/console'

import ComponentButton from './buttons/componentbBtn'
import { getActionType, initialScheduling, giveMeAKey } from './help'
import { TABS_TITLE } from './const'

import FileConfig from './fileConfig'
import FormConfig from './formConfig'
import ToolBar from './buttons/toolbar'

const TabPane = Tabs.TabPane;
interface IState {
    initialCompData: any[];
    versionData: any;
    clusterName: string;
    activeKey: number;
}
class EditCluster extends React.Component<any, IState> {
    state: IState = {
        initialCompData: initialScheduling(), // 初始各组件的存储值
        versionData: {},
        clusterName: '',
        activeKey: 0
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
                })
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

    onTabChange = (key: string) => {
        this.setState({
            activeKey: Number(key)
        })
    }

    handleConfirm = (addComps: any[], deleteComps: any[]) => {
        console.log(addComps, deleteComps)
    }

    render () {
        const { mode } = this.props.location.state || {} as any
        const { clusterName, activeKey, initialCompData, versionData } = this.state

        return (
            <div className="c-editCluster__containerWrap">
                <Breadcrumb>
                    <Breadcrumb.Item>
                        <a onClick={() => { this.props.router.push('/console/clusterManage') }}>
                            多集群管理
                        </a>
                    </Breadcrumb.Item>
                    <Breadcrumb.Item>{getActionType(mode)}</Breadcrumb.Item>
                    <Breadcrumb.Item>{clusterName}</Breadcrumb.Item>
                </Breadcrumb>
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
                                <Tabs
                                    tabPosition="left"
                                    tabBarExtraContent={<ComponentButton
                                        key={giveMeAKey()}
                                        activeKey={activeKey}
                                        comps={comps}
                                        handleConfirm={this.handleConfirm}
                                    />}
                                    className="c-editCluster__container__componentTabs"
                                    // onChange={(key: any) => this.getLoadTemplate(key)}
                                >
                                    {comps?.map((comp: any) => {
                                        return (<TabPane
                                            tab={comp.componentName}
                                            key={`${comp.componentTypeCode}`}
                                        >
                                            <FileConfig
                                                comp={comp}
                                                versionData={versionData}
                                                form={this.props.form}
                                            />
                                            <FormConfig />
                                            <ToolBar />
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
