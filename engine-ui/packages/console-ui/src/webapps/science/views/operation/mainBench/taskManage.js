import React, { PureComponent } from 'react'
import { Tabs } from 'antd';
import Experiment from './experiment';
import Notebook from './notebook';
const TabPane = Tabs.TabPane;
class TaskManage extends PureComponent {
    state = {
        activeKey: 'experiment',
        data: [],
        loading: false,
        pagination: {
            current: 1,
            total: 20
        },
        selectedRowKeys: [],
        params: {
            search: ''
        }
    }
    render () {
        const { activeKey } = this.state;
        return (
            <div className='operation'>
                <Tabs type="card" activeKey={activeKey} onChange={(activeKey) => this.setState({ activeKey })}>
                    <TabPane tab="实验" key="experiment">
                        <Experiment />
                    </TabPane>
                    <TabPane tab="NoteBook作业" key="notebook">
                        <Notebook />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
export default TaskManage
