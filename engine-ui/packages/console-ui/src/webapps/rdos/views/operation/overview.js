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
                <Row style={{marginTop:10}}>
                    <Col span={12}><OfflineCount /></Col>
                    <Col span={12}><RealtimeCount /></Col>
                </Row>
                <OfflineStatistics /> 
            </div>
        )
    }
}