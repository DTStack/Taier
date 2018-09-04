import React, { Component } from 'react'
import { Tabs } from 'antd'

import RealTimePane from './realtimeListPane'
import OfflinePane from './offLineListPane'

const TabPane = Tabs.TabPane

export default class AlarmList extends Component {

    render() {
        return (
            <div className="box-1 m-tabs">
                <Tabs animated={false} style={{height: "calc(100% - 40px)"}}>
                    <TabPane tab="离线任务" key="1">
                        <OfflinePane />
                    </TabPane>
                    <TabPane tab="实时任务" key="2">
                        <RealTimePane />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
