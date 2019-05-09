import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Tabs } from 'antd';
import Experiment from './experiment';
import Notebook from './notebook';
const TabPane = Tabs.TabPane;

@connect(state => {
    return {
        currentProject: state.project.currentProject
    }
})
class TaskManage extends Component {
    state = {
        activeKey: 'experiment'
    }
    render () {
        const { activeKey } = this.state;
        const { currentProject } = this.props;
        return (
            <div className='operation'>
                <Tabs type="card" activeKey={activeKey} onChange={(activeKey) => this.setState({ activeKey })}>
                    <TabPane tab="实验" key="experiment">
                        <Experiment currentProject={currentProject} />
                    </TabPane>
                    <TabPane tab="NoteBook作业" key="notebook">
                        <Notebook currentProject={currentProject} />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
export default TaskManage
