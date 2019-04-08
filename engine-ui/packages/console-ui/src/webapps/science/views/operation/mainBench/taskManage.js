import React, { PureComponent } from 'react'
import { Tabs, Card } from 'antd';
import SearchInput from 'widgets/search-input';
const TabPane = Tabs.TabPane;
class TaskManage extends PureComponent {
    state = {
        activeKey: 'experiment'
    }
    render () {
        const { activeKey } = this.state;
        return (
            <div className='operation'>
                <Tabs type="card" activeKey={activeKey} onChange={(activeKey) => this.setState({ activeKey })}>
                    <TabPane tab="实验" key="experiment" forceRender={true}>
                        <Card title={<SearchInput />}>
                            1
                        </Card>
                    </TabPane>
                    <TabPane tab="NoteBook作业" key="notebook" forceRender={true}>
                        2
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
export default TaskManage
