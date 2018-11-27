import React, { Component } from 'react'

import {
    Row, Col
} from 'antd'

import OfflineCount from './offline/offlineCount'
import OfflineStatistics from './offline'

export default class Index extends Component {
    render () {
        return (
            <div className="operation-overview" style={{ background: '#f2f7fa' }}>
                <Row style={{ marginTop: 10 }}>
                    <Col span={24}><OfflineCount /></Col>
                </Row>
                <OfflineStatistics />
            </div>
        )
    }
}
