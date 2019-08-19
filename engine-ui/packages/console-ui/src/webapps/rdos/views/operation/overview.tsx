import * as React from 'react'

import {
    Row, Col
} from 'antd'

import OfflineCount from './offline/offlineCount'
import OfflineStatistics from './offline'

export default class Index extends React.Component<any, any> {
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
