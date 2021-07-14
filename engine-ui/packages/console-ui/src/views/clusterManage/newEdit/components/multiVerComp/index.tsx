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
    deployType: number;
}

export default class MultiVersionComp extends React.Component<IProps, IState> {
    constructor (props: IProps) {
        super(props)
        const { comp } = props
        this.state = {
            deployType: comp?.multiVersion[0]?.deployType ?? FLINK_DEPLOY_TYPE.YARN
        }
    }

    handleMenuClick = (e: any) => {
        const { comp, saveComp, getLoadTemplate } = this.props
        const { deployType } = this.state
        const typeCode = comp?.componentTypeCode ?? ''

        saveComp({
            componentTypeCode: typeCode,
            hadoopVersion: e.key,
            deployType,
            isDefault: false
        }, COMP_ACTION.ADD)
        getLoadTemplate(typeCode, { compVersion: e.key, deployType })
    }

    getMeunItem = () => {
        const { versionData, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const { deployType } = this.state

        return <Menu onClick={this.handleMenuClick}>
            {versionData[VERSION_TYPE[typeCode]]?.map(({ key, value }) => {
                const disabled = comp?.multiVersion?.findIndex(vcomp => vcomp.hadoopVersion == value)
                return <MenuItem disabled={disabled > -1} key={value} >
                    {isFLink(typeCode) ? FLINK_DEPLOY_NAME[deployType] : COMPONENT_CONFIG_NAME[typeCode]} {this.getCompVersion(value)}
                </MenuItem>
            })}
        </Menu>
    }

    addMultiVersionComp = (value: string) => {
        const { comp, saveComp, getLoadTemplate } = this.props
        const { deployType } = this.state
        const typeCode = comp?.componentTypeCode ?? ''

        saveComp({
            deployType,
            componentTypeCode: typeCode,
            hadoopVersion: value,
            isDefault: true
        }, COMP_ACTION.ADD)
        getLoadTemplate(typeCode, { compVersion: value, deployType })
    }

    getCompVersion = (value: string) => {
        const flinkVersion = '110'
        if (value !== flinkVersion) return (Number(value) / 100).toFixed(1)
        return (Number(value) / 100).toFixed(2)
    }

    getComponentName = (typeCode: number, deployType: number = FLINK_DEPLOY_TYPE.YARN) => {
        if (isFLink(typeCode)) return FLINK_DEPLOY_NAME[deployType]
        return COMPONENT_CONFIG_NAME[typeCode]
    }

    getDefaultVerionCompStatus = (comp: any) => {
        /** 当flink组件只有一个组件版本时勾选为默认版本 */
        const typeCode = comp?.componentTypeCode ?? ''
        if (isFLink(typeCode) && comp.multiVersion.length == 1) return true
        return false
    }

    render () {
        const { comp, versionData, saveCompsData, testStatus, view,
            clusterInfo, form, saveComp, testConnects, handleConfirm } = this.props
        const { deployType } = this.state
        const typeCode = comp?.componentTypeCode ?? ''
        const className = 'c-multiVersionComp'
        const isDefault = this.getDefaultVerionCompStatus(comp)

        if (!comp?.multiVersion[0]?.hadoopVersion) {
            return <div className={className}>
                <div className={`${className}__intail`}>
                    {isFLink(typeCode) && <Row className={`${className}__intail__row`}>
                        <Col span={10}>部署模式：</Col>
                        <Col>
                            <Radio.Group value={deployType} onChange={(e) => this.setState({ deployType: e.target.value })}>
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
                        <Col span={10}>选择版本：</Col>
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
                    const { deployType, componentTypeCode, hadoopVersion } = vcomp
                    return (
                        <TabPane
                            tab={
                                <span>
                                    {this.getComponentName(componentTypeCode, deployType)} {this.getCompVersion(hadoopVersion)}
                                    <TestRestIcon testStatus={testStatus.find(status => status?.componentVersion == hadoopVersion)}/>
                                </span>
                            }
                            key={String(hadoopVersion)}
                        >
                            <>
                                <FileConfig
                                    comp={vcomp}
                                    form={form}
                                    view={view}
                                    isDefault={isDefault}
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
