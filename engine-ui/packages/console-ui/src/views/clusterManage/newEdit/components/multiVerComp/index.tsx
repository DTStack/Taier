import * as React from 'react'
import { Tabs, Icon, Menu, Dropdown, Button,
    Radio, Row, Col } from 'antd'
import { VERSION_TYPE, COMP_ACTION, COMPONENT_CONFIG_NAME,
    FLINK_DEPLOY_TYPE, FLINK_DEPLOY_NAME } from '../../const'
import { isFLink } from '../../help'

import FileConfig from '../../fileConfig'
import FormConfig from '../../formConfig'
import ToolBar from '../toolbar'
import TestRestIcon from '../../../../../components/testResultIcon'
import './index.scss'

const TabPane = Tabs.TabPane
const MenuItem = Menu.Item

interface IProps {
    comp: any;
    form: any;
    view: boolean;
    saveCompsData: any[];
    versionData: any;
    clusterInfo: any;
    testStatus: any;
    saveComp: (params: any, type?: string) => void;
    getLoadTemplate: (key?: string, params?: any) => void;
    handleConfirm: (action: string, comps: any | any[], mulitple?: boolean) => void;
    testConnects: Function;
}

interface IState {
    deployMode: number;
}

export default class MultiVersionComp extends React.Component<IProps, IState> {
    state: IState ={
        deployMode: FLINK_DEPLOY_TYPE.YARN
    }
    handleMenuClick = (e: any) => {
        const { comp, saveComp, getLoadTemplate } = this.props
        const { deployMode } = this.state
        const typeCode = comp?.componentTypeCode ?? ''
        saveComp({
            componentTypeCode: typeCode,
            hadoopVersion: e.key,
            deployMode
        }, COMP_ACTION.ADD)
        getLoadTemplate(typeCode, { compVersion: e.key, deployMode })
    }

    getMeunItem = () => {
        const { versionData, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const { deployMode } = this.state

        return <Menu onClick={this.handleMenuClick}>
            {versionData[VERSION_TYPE[typeCode]]?.map(({ key, value }) => {
                const disabled = comp?.multiVersion?.findIndex(vcomp => vcomp.hadoopVersion == value)
                return <MenuItem disabled={disabled > -1} key={value} >
                    {isFLink(typeCode) ? FLINK_DEPLOY_NAME[deployMode] : COMPONENT_CONFIG_NAME[comp.typeCode]} {this.getCompVersion(value)}
                </MenuItem>
            })}
        </Menu>
    }

    addMultiVersionComp = (value: string) => {
        const { comp, saveComp, getLoadTemplate } = this.props
        const { deployMode } = this.state
        const typeCode = comp?.componentTypeCode ?? ''
        saveComp({
            deployMode,
            componentTypeCode: typeCode,
            hadoopVersion: value
        }, COMP_ACTION.ADD)
        getLoadTemplate(typeCode, { compVersion: value, deployMode })
    }

    getCompVersion = (value: string) => {
        const flinkVersion = '110'
        if (value !== flinkVersion) return (Number(value) / 100).toFixed(1)
        return (Number(value) / 100).toFixed(2)
    }

    render () {
        const { comp, versionData, saveCompsData, testStatus, view,
            clusterInfo, form, saveComp, testConnects, handleConfirm } = this.props
        const { deployMode } = this.state
        const typeCode = comp?.componentTypeCode ?? ''
        const className = 'c-multiVersionComp'

        if (!comp?.multiVersion[0]?.hadoopVersion) {
            return <div className={className}>
                <div className={`${className}__intail`}>
                    {isFLink(typeCode) && <Row className={`${className}__intail__row`}>
                        <Col span={10}>部署模式：</Col>
                        <Col>
                            <Radio.Group value={deployMode} onChange={(e) => this.setState({ deployMode: e.target.value })}>
                                <Radio value={FLINK_DEPLOY_TYPE.YARN}>
                                    {FLINK_DEPLOY_NAME[FLINK_DEPLOY_TYPE.YARN]}
                                </Radio>
                                <Radio value={FLINK_DEPLOY_TYPE.STANDALONE}>
                                    {FLINK_DEPLOY_NAME[FLINK_DEPLOY_TYPE.STANDALONE]}
                                </Radio>
                            </Radio.Group>
                        </Col>
                    </Row>}
                    <Row className={`${className}__intail__row`}>
                        <Col span={10}>版本：</Col>
                        <Col style={{ display: 'flex' }}>
                            {versionData[VERSION_TYPE[typeCode]]?.map(({ key, value }) => {
                                return <div
                                    key={key}
                                    className={`${className}__intail__container__desc`}
                                    onClick={() => this.addMultiVersionComp(value)}
                                >
                                    <span className="comp-name">
                                        <img src={`public/img/${VERSION_TYPE[typeCode]}.png`}/>
                                        <span>{!isFLink(typeCode) && (COMPONENT_CONFIG_NAME[typeCode] + ' ')}{this.getCompVersion(value)}</span>
                                    </span>
                                    <Icon type="right-circle" theme="filled" />
                                </div>
                            })}
                        </Col>
                    </Row>
                </div>
                {!view && <ToolBar
                    mulitple={false}
                    comp={comp}
                    clusterInfo={clusterInfo}
                    form={form}
                    saveComp={saveComp}
                    handleConfirm={handleConfirm}
                />}
            </div>
        }

        return <div className={className}>
            <Tabs
                tabPosition="top"
                className={`${className}__tabs`}
                tabBarExtraContent={<Dropdown disabled={view} overlay={this.getMeunItem()} placement="bottomCenter">
                    <Button type="primary" size="small" style={{ marginRight: 20 }}>
                        添加版本
                        <Icon type="down" />
                    </Button>
                </Dropdown>}
            >
                {comp?.multiVersion.map(vcomp => {
                    return (
                        <TabPane
                            tab={
                                <span>
                                    {isFLink(vcomp.componentTypeCode) ? FLINK_DEPLOY_NAME[vcomp?.deployMode ?? FLINK_DEPLOY_TYPE.YARN] : COMPONENT_CONFIG_NAME[vcomp.componentTypeCode]} {this.getCompVersion(vcomp.hadoopVersion)}
                                    <TestRestIcon testStatus={testStatus.find(status => status?.componentVersion == vcomp.hadoopVersion)}/>
                                </span>
                            }
                            key={String(vcomp.hadoopVersion)}
                        >
                            <>
                                <FileConfig
                                    comp={vcomp}
                                    form={form}
                                    view={view}
                                    saveCompsData={saveCompsData}
                                    versionData={versionData}
                                    clusterInfo={clusterInfo}
                                    saveComp={saveComp}
                                />
                                <FormConfig
                                    comp={vcomp}
                                    view={view}
                                    form={form}
                                />
                                {!view && <ToolBar
                                    mulitple={true}
                                    comp={vcomp}
                                    clusterInfo={clusterInfo}
                                    form={form}
                                    saveComp={saveComp}
                                    testConnects={testConnects}
                                    handleConfirm={handleConfirm}
                                />}
                            </>
                        </TabPane>
                    )
                })}
            </Tabs>
        </div>
    }
}
