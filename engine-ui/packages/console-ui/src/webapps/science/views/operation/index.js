import React, { Component } from 'react'
import { Tabs } from 'antd';
import Overview from './mainBench/overview';
import TaskManage from './mainBench/taskManage';
import '../../styles/views/operation/index.scss';
const TabPane = Tabs.TabPane;
class Operation extends Component {
    render () {
        return (
            <div className="inner-container" style={{ padding: 0, backgroundColor: '#fff' }}>
                <Tabs className='c-antd-tabs-sidebar' tabPosition='left'>
                    <TabPane tab={<span>运维概览</span>} key="overview">
                        <Overview />
                    </TabPane>
                    <TabPane tab={<span>任务管理</span>} key="taskManage">
                        <TaskManage />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
export default Operation
