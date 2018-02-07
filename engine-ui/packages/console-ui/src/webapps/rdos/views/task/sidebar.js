import React, { Component } from 'react'
import { Row, Tabs } from 'antd'
import { hashHistory } from 'react-router';
import { connect } from 'react-redux';

import RealTimeTabPanel from './realtimeTab'
import OfflineTabPanel from './offlineTab'

const TabPane = Tabs.TabPane

class Sidebar extends Component {
    constructor(props) {
        super(props)
        this.state = {
            current: 'project-abstract',
        }
    }

    render() {
        const {pathname} = this.props.routing.locationBeforeTransitions;
        const activeKey = /^\/*(\w+)(\/*.*)$/.exec(pathname)[1];

        return (
            <div className="sidebar">
                <Row className="tab-menu" style={{ paddingTop: '16px' }}>
                    <Tabs
                        defaultActiveKey={ activeKey } type="card"
                        onTabClick={this.switchTaskPanel}
                    >
                        <TabPane tab="离线任务" key="offline">
                            <OfflineTabPanel />
                        </TabPane>
                        <TabPane tab="实时任务" key="realtime">
                            <RealTimeTabPanel />
                        </TabPane>
                    </Tabs>
                </Row>
            </div>
        )
    }

    switchTaskPanel(key) {
        hashHistory.push(`${key}/task`);
    }
}

export default connect(state => {
    return {
        routing: state.routing
    }
})(Sidebar);
