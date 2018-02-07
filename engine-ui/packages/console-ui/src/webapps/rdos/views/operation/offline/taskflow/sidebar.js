import React, { Component } from 'react'
import { Row, Tabs } from 'antd'
import { hashHistory } from 'react-router'
import { connect } from 'react-redux'

import TaskOperation from './taskOperationPane'
import PatchData from './patchDataPane'
import * as FlowAction from '../../../../store/modules/operation/taskflow'

const TabPane = Tabs.TabPane

class Sidebar extends Component {

    switchTab = (key) => {
        this.props.dispatch(FlowAction.setTaskFlow({ id: 0 }))
    }

    render() {
        const { router } = this.props
        const activeKey = router.location.pathname.split('/')[2]
        return (
            <div
                className="sidebar-taskflow bg-w ant-layout-sider bd-right"
            >
                <header className="title bd-bottom">任务运维</header>
                <Row className="flow-menu">
                    <Tabs
                        size="small"
                        animated={false}
                        onTabClick={this.switchTab}
                        defaultActiveKey={ activeKey }
                    >
                        <TabPane tab="任务运维" key="task-flow">
                            <TaskOperation {...this.props} />
                        </TabPane>
                        <TabPane tab="补数据" key="task-patch-data">
                            <PatchData {...this.props} />
                        </TabPane>
                    </Tabs>
                </Row>
            </div>
        )
    }
}
export default connect()(Sidebar)
