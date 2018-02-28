import React, { Component } from 'react'

import {
    Row, Col
} from 'antd'

import RealtimeCount from './realtime/realtimeCount'
import OfflineCount from './offline/offlineCount'
import OfflineStatistics from './offline'

export default class Index extends Component {
    render() {
        return (
            <div className="operation-overview" style={{ background: '#f2f7fa' }}>
                <RealtimeCount />
                <OfflineCount />
                <OfflineStatistics />
            </div>
        )
    }
}