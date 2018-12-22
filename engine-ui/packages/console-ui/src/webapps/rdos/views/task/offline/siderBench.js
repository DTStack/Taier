import React from 'react';
import { connect } from 'react-redux';

import { Tabs } from 'antd';

import utils from 'utils'

import SQLEditor from 'widgets/editor';
import { TASK_TYPE, PROJECT_TYPE, DATA_SYNC_MODE } from '../../../comm/const';
import { workbenchAction } from '../../../store/modules/offlineTask/actionType';

import TaskDetail from './taskDetail';
import ScriptDetail from './scriptDetail';
import SchedulingConfig from './schedulingConfig';
import TaskView from './taskView';
import TaskParams from './taskParams';
import { isProjectCouldEdit } from '../../../comm';

const TabPane = Tabs.TabPane;

class SiderBench extends React.Component {
    state = {
        selected: '',
        expanded: false
    }

    constructor (props) {
        super(props);
    }

    handleTaskParamChange (newVal) {
        this.props.setTaskParams({
            taskParams: newVal
        });
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
            this.setState({ selected: activeKey, expanded: true });
        }
    }

    getTabPanes = () => {
        const { tabData, project, user } = this.props;
        const isPro = project.projectType == PROJECT_TYPE.PRO;
        const couldEdit = isProjectCouldEdit(project, user);
        if (!tabData) return null;

        const isLocked = tabData && tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock;
        const isWorkflowNode = tabData && tabData.flowId && tabData.flowId !== 0;
        const prefixLabel = isWorkflowNode ? '节点' : '任务';
        const isIncrementMode = tabData.syncMode !== undefined && tabData.syncMode === DATA_SYNC_MODE.INCREMENT;

        const panes = [
            <TabPane tab={<span className="title-vertical">{isWorkflowNode ? '属性与调度' : '任务属性'}</span>} key="params1">
                <TaskDetail
                    isPro={isPro}
                    couldEdit={couldEdit}
                    isWorkflowNode={isWorkflowNode}
                    tabData={tabData}
                ></TaskDetail>
            </TabPane>
        ];

        if (!isWorkflowNode) {
            panes.push(
                <TabPane tab={<span className="title-vertical">{'调度依赖'}</span>} key="params2">
                    <SchedulingConfig
                        isPro={isPro}
                        couldEdit={couldEdit}
                        tabData={tabData}
                        isIncrementMode={isIncrementMode}
                    >
                    </SchedulingConfig>
                </TabPane>
            )

            if (tabData.taskType !== TASK_TYPE.WORKFLOW) {
                panes.push(
                    <TabPane tab={<span className="title-vertical">依赖视图</span>} key="params4">
                        <TaskView tabData={tabData} isPro={isPro} couldEdit={couldEdit} />
                    </TabPane>
                )
            }
        }

        if (tabData && utils.checkExist(tabData.taskType) &&
            tabData.taskType !== TASK_TYPE.VIRTUAL_NODE &&
            tabData.taskType !== TASK_TYPE.WORKFLOW
        ) {
            panes.push(
                <TabPane tab={<span className="title-vertical">{prefixLabel}参数</span>} key="params5">
                    <TaskParams
                        isPro={isPro}
                        couldEdit={couldEdit}
                        tabData={tabData}
                        onChange={this.handleCustomParamsChange}
                    />
                </TabPane>
            )
            if (tabData.taskType !== TASK_TYPE.SYNC) {
                panes.push(
                    <TabPane tab={<span className="title-vertical">环境参数</span>} key="params3">
                        <SQLEditor
                            options={{ readOnly: isLocked || !couldEdit, minimap: { enabled: false } }}
                            key={'params' + tabData.id}
                            value={tabData.taskParams}
                            onFocus={() => { }}
                            focusOut={() => { }}
                            language="ini"
                            onChange={this.handleTaskParamChange.bind(this)}
                        />
                    </TabPane>
                )
            }
        } else if (utils.checkExist(tabData.type)) {
            return <TabPane tab={<span className="title-vertical">脚本属性</span>} key="params1">
                <ScriptDetail tabData={tabData} isPro={isPro} couldEdit={couldEdit} />
            </TabPane>;
        }
        return panes;
    }

    render () {
        return <div className="m-siderbench padding-r0" ref={(e) => { this.SideBench = e }}>
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
    return {
        project: state.project,
        user: state.user
    }
}, dispatch => {
    return {
        setTaskParams (params) {
            dispatch({
                type: workbenchAction.SET_TASK_SQL_FIELD_VALUE,
                payload: params
            });
        }
    }
})(SiderBench);
