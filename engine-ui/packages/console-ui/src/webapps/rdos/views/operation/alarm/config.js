import React, { Component } from 'react'
import { Tabs } from 'antd'

import RealTimePane from './realtimeConfigPane'
import OfflinePane from './offlineConfigPane'

const TabPane = Tabs.TabPane

export default class AlarmList extends Component {
    render() {
        return (
            <div className="box-1 m-tabs">
                <Tabs animated={false} className="full-screen-table-40">
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
