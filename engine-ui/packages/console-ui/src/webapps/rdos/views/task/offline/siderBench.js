import React from 'react';
import { connect } from 'react-redux';

import { Row, Tabs } from 'antd';

import utils from 'utils'

import SQLEditor from '../../../components/code-editor';
import { propEditorOptions, TASK_TYPE } from '../../../comm/const';
import { workbenchAction } from '../../../store/modules/offlineTask/actionType';

import TaskDetail from './taskDetail';
import ScriptDetail from './scriptDetail';
import SchedulingConfig from './schedulingConfig';
import TaskView from './taskView';
import TaskParams from './taskParams';

const TabPane = Tabs.TabPane;

class SiderBench extends React.Component {

    state = {
        selected: '',
        expanded: false,
    }

    constructor(props) {
        super(props);
    }

    handleTaskParamChange(old, newVal) {
        if(old !== newVal) {
            this.props.setTaskParams({
                taskParams: newVal
            });
        }
    }

    handleCustomParamsChange = (params) => {
        this.props.setTaskParams(params)
    }

    tabClick = (activeKey) => {
        const { selected, expanded } = this.state
        if (activeKey === selected && expanded) {
            this.setState({ selected: '', expanded: false });
            this.SideBench.style.width = '30px'
        } else if (activeKey !== selected) {
            this.SideBench.style.width = '500px'
            this.setState({ selected: activeKey,  expanded: true });
        }
    }

    getTabPanes = () => {
        const { tabData } = this.props;
        const panes = [
            <TabPane tab={<span className="title-vertical">任务属性</span>} key="params1">
                <TaskDetail tabData={tabData}></TaskDetail>
            </TabPane>,
            <TabPane tab={<span className="title-vertical">调度依赖</span>} key="params2">
                <SchedulingConfig tabData={tabData}></SchedulingConfig>
            </TabPane>,
            <TabPane tab={<span className="title-vertical">依赖视图</span>} key="params4">
                <TaskView tabData={tabData} />
            </TabPane>
        ]
        if (tabData && utils.checkExist(tabData.taskType) && tabData.taskType !== TASK_TYPE.VIRTUAL_NODE) {
            panes.push([<TabPane tab={<span className="title-vertical">环境参数</span>} key="params3">
                <SQLEditor
                    options={propEditorOptions}
                    key={'params' + tabData.id}
                    value={tabData.taskParams}
                    onFocus={() => { }}
                    focusOut={() => { }}
                    onChange={this.handleTaskParamChange.bind(this)}
                />
                </TabPane>,
                <TabPane tab={<span className="title-vertical">任务参数</span>} key="params5">
                    <TaskParams 
                        tabData={tabData} 
                        onChange={this.handleCustomParamsChange} 
                    />
                </TabPane>
            ])
        } else if (utils.checkExist(tabData.type)) {
            return <TabPane tab={<span className="title-vertical">脚本属性</span>} key="params1">
                <ScriptDetail tabData={tabData} />
            </TabPane>;
        }
        return panes;
    }

    render() {
        return <div className="m-siderbench bd-left padding-r0" ref={(e) => { this.SideBench = e }}>
            <Tabs
                activeKey={this.state.selected}
                type="card"
                className="task-params"
                tabPosition="right"
                onTabClick={this.tabClick}
            >
                {this.getTabPanes()}
            </Tabs>
        </div>
    }
}

export default connect(state => {
    return {}
}, dispatch => {
    return {
        setTaskParams(params) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE_SILENT,
                payload: params
            });
        }
    }
})(SiderBench);