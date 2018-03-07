import React, { Component } from 'react'

import { 
    Row, Tabs, Icon, 
    Popover, Tooltip 
} from 'antd';

import { hashHistory } from 'react-router';
import { connect } from 'react-redux';

import RealTimeTabPanel from './realtimeTab'
import OfflineTabPanel from './offlineTab'

const TabPane = Tabs.TabPane

class Sidebar extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        const { pathname } = this.props.routing.locationBeforeTransitions;
        const activeKey = /^\/*(\w+)(\/*.*)$/.exec(pathname)[1];

        return (
            <div className="sidebar">
                <Row>
                    <Tabs
                        type="card"
                        className="task-dev-switcher"
                        defaultActiveKey={ activeKey } 
                        onTabClick={this.switchTaskPanel}
                    >
                        <TabPane tab={
                            <Tooltip placement="bottom" title="离线任务">
                                <Icon type="usb" />
                            </Tooltip>
                        } key="offline">
                            <OfflineTabPanel />
                        </TabPane>
                        <TabPane tab={
                            <Tooltip placement="bottom" title="实时任务">
                                <Icon type="link" />
                            </Tooltip>
                        } key="realtime">
                            <RealTimeTabPanel />
                        </TabPane>
                    </Tabs>
                </Row>
            </div>
        )
    }

    switchTaskPanel(key) {
        hashHistory.push(`/${key}/task`);
    }
}

export default connect(state => {
    return {
        routing: state.routing
    }
})(Sidebar);
