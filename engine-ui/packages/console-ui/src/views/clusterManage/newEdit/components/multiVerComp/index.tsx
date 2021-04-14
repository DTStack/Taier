import * as React from 'react'
import { Tabs, Icon, Menu, Dropdown, Button } from 'antd'
import { VERSION_TYPE, COMP_ACTION } from '../../const'

import FileConfig from '../../fileConfig'
import FormConfig from '../../formConfig'
import TestRestIcon from '../../../../../components/testResultIcon'

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
}

export default class MultiVersionComp extends React.Component<IProps, any> {
    handleMenuClick = (e: any) => {
        const { comp, saveComp, getLoadTemplate } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        saveComp({
            componentTypeCode: typeCode,
            hadoopVersion: e.key
        }, COMP_ACTION.ADD)
        getLoadTemplate(typeCode, { compVersion: e.key })
    }

    getMeunItem = () => {
        const { versionData, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''

        return <Menu onClick={this.handleMenuClick}>
            {versionData[VERSION_TYPE[typeCode]]?.map(({ key, value }) => {
                const disabled = comp?.mulitiVersion?.findIndex(vcomp => vcomp.hadoopVersion == value)
                return <MenuItem disabled={disabled > -1} key={value} >
                    {VERSION_TYPE[typeCode]} {key}
                </MenuItem>
            })}
        </Menu>
    }

    addMultiVersionComp = (value: string) => {
        const { comp, saveComp, getLoadTemplate } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        saveComp({
            componentTypeCode: typeCode,
            hadoopVersion: value
        })
        getLoadTemplate(typeCode, { compVersion: value })
    }

    render () {
        const { comp, versionData, saveCompsData, testStatus, view,
            clusterInfo, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const className = 'c-multiVersionComp'

        return <div className={className}>
            {
                !comp?.mulitiVersion[0]?.hadoopVersion ? <div className={`${className}__intail`}>
                    <span className={`${className}__intail__title`}>请选择版本号：</span>
                    <div className={`${className}__intail__container`}>
                        {versionData[VERSION_TYPE[typeCode]]?.map(({ key, value }) => {
                            return <div
                                key={key}
                                className={`${className}__intail__container__desc`}
                                onClick={() => this.addMultiVersionComp(value)}
                            >
                                <span className="comp-name">
                                    <img src={`public/img/${VERSION_TYPE[typeCode]}.png`}/>
                                    <span>{VERSION_TYPE[typeCode]} {key}</span>
                                </span>
                                <Icon type="right-circle" theme="filled" />
                            </div>
                        })}
                    </div>
                </div> : <Tabs
                    tabPosition="top"
                    className={`${className}__tabs`}
                    tabBarExtraContent={<Dropdown overlay={this.getMeunItem()} placement="bottomCenter">
                        <Button type="primary" size="small" style={{ marginRight: 20 }}>
                            添加版本
                            <Icon type="down" />
                        </Button>
                    </Dropdown>}
                >
                    {comp?.mulitiVersion.map(vcomp => {
                        return (
                            <TabPane
                                tab={
                                    <span>
                                        {VERSION_TYPE[vcomp.componentTypeCode]} {Number(vcomp.hadoopVersion) / 100}
                                        <TestRestIcon testStatus={testStatus[vcomp.componentTypeCode] ?? {}}/>
                                    </span>
                                }
                                key={String(vcomp.hadoopVersion)}
                            >
                                <FileConfig
                                    comp={vcomp}
                                    form={form}
                                    view={view}
                                    saveCompsData={saveCompsData}
                                    versionData={versionData}
                                    clusterInfo={clusterInfo}
                                />
                                <FormConfig
                                    comp={vcomp}
                                    view={view}
                                    form={form}
                                />
                            </TabPane>
                        )
                    })}
                </Tabs>
            }
        </div>
    }
}
