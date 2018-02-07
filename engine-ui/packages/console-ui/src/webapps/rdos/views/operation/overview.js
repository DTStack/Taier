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
            <div className="operation-overview" style={{ background: '#f2f3f8', padding: '20px' }}>
                <Row>
                    <Col span={12} style={{paddingRight: '10px'}}>
                        <OfflineCount />
                    </Col>
                    <Col span={12} style={{paddingLeft: '10px'}}>
                        <RealtimeCount />
                    </Col>
                </Row>
                <Row style={{marginTop: '20px'}}>
                    <OfflineStatistics />
                </Row>
            </div>
        )
    }
}