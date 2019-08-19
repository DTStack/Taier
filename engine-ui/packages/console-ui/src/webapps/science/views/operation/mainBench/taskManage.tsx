import * as React from 'react'
import { connect } from 'react-redux'
import { Tabs } from 'antd';
import Experiment from './experiment';
import Notebook from './notebook';
const TabPane = Tabs.TabPane;

@(connect((state: any) => {
    return {
        currentProject: state.project.currentProject
    }
}) as any)
class TaskManage extends React.Component<any, any> {
    state: any = {
        activeKey: 'experiment'
    }
    render () {
        const { activeKey } = this.state;
        const { currentProject } = this.props;
        return (
            <div className='operation'>
                <Tabs type="card" activeKey={activeKey} onChange={(activeKey: any) => this.setState({ activeKey })}>
                    <TabPane tab="实验" key="experiment">
                        <Experiment currentProject={currentProject} />
                    </TabPane>
                    <TabPane tab="Notebook作业" key="notebook">
                        <Notebook currentProject={currentProject} />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
export default TaskManage
