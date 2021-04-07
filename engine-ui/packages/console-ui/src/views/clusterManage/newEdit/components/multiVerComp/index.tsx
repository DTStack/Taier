import * as React from 'react'
import { Tabs, Icon, Menu, Dropdown } from 'antd'
import { VERSION_TYPE } from '../../const'

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
    saveComp: (params: any) => void;
}

export default class MultiVersionComp extends React.Component<IProps, any> {
    getMeunItem = () => {
        const { versionData, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        return <Menu>
            {versionData[VERSION_TYPE[typeCode]]?.map(({ key, value }) => {
                return <MenuItem>
                    <a>{VERSION_TYPE[typeCode]} {key}</a>
                </MenuItem>
            })}
        </Menu>
    }

    render () {
        const { comp, versionData, saveCompsData, testStatus, view,
            clusterInfo, saveComp, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const className = 'c-multiVersionComp'

        return <div className={className}>
            {
                !comp?.hadoopVersion ? <div className={`${className}__intail`}>
                    <span className={`${className}__intail__title`}>请选择版本号：</span>
                    <div className={`${className}__intail__container`}>
                        {versionData[VERSION_TYPE[typeCode]]?.map(({ key, value }) => {
                            return <div
                                key={key}
                                className={`${className}__intail__container__desc`}
                                onClick={() => saveComp({
                                    componentTypeCode: typeCode,
                                    hadoopVersion: value
                                })}
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
                        <Icon type="plus-circle" />
                    </Dropdown>}
                >
                    <TabPane
                        tab={
                            <span>
                                {comp.componentName}
                                <TestRestIcon testStatus={testStatus[comp.componentTypeCode] ?? {}}/>
                            </span>
                        }
                        // key={String(key)}
                    >
                        <FileConfig
                            comp={comp}
                            form={form}
                            view={view}
                            saveCompsData={saveCompsData}
                            versionData={versionData}
                            clusterInfo={clusterInfo}
                        />
                        <FormConfig
                            comp={comp}
                            view={view}
                            form={form}
                        />
                    </TabPane>
                </Tabs>
            }
        </div>
    }
}
