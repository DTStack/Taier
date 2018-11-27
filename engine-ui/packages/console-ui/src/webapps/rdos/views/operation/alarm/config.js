import React, { Component } from 'react'
import { Tabs } from 'antd'
import OfflinePane from './offlineConfigPane'

const TabPane = Tabs.TabPane

export default class AlarmList extends Component {
    render () {
        return (
            <div className="box-1 m-tabs">
                <OfflinePane />
            </div>
        )
    }
}
