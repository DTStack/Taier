import React from 'react';
import { connect } from 'react-redux';

import { Tabs } from 'antd';
import { debounce } from 'lodash';
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
    _updateKey = 0;

    componentDidUpdate (prevProps) {
        const tabData = this.props.tabData
        const { tabData: oldData } = prevProps
        if (tabData && tabData !== oldData) {
            this._updateKey++;
        }
    }

    handleTaskParamChange (newVal) {
        this.props.setTaskParams({
            taskParams: newVal
        });
    }

    handleCustomParamsChange = (params) => {
        this.props.setTaskParams(params)
    }

    debounceChange = debounce(this.handleCustomParamsChange, 300, { 'maxWait': 2000 })

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
        const { tabData, project, user, editor } = this.props;
        const isPro = project.projectType == PROJECT_TYPE.PRO;
        const isScienceTask = tabData.taskType == TASK_TYPE.NOTEBOOK || tabData.taskType == TASK_TYPE.EXPERIMENT;
        const couldEdit = isProjectCouldEdit(project, user) && !isScienceTask;
        if (!tabData) return null;

        const isLocked = tabData && tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock;
        const isWorkflowNode = tabData && tabData.flowId && tabData.flowId !== 0;
        const prefixLabel = isWorkflowNode ? '节点' : '任务';
        const isIncrementMode = tabData.syncModel !== undefined && DATA_SYNC_MODE.INCREMENT === tabData.syncModel;

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
                        isScienceTask={isScienceTask}
                        tabData={tabData}
                        updateKey={this._updateKey}
                        key={`schedule-${tabData && tabData.version}`}
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
            tabData.taskType !== TASK_TYPE.WORKFLOW &&
            tabData.taskType !== TASK_TYPE.CUBE_KYLIN
        ) {
            panes.push(
                <TabPane tab={<span className="title-vertical">{prefixLabel}参数</span>} key="params5">
                    <TaskParams
                        isPro={isPro}
                        couldEdit={couldEdit}
                        tabData={tabData}
                        onChange={this.debounceChange}
                    />
                </TabPane>
            )
            panes.push(
                <TabPane tab={<span className="title-vertical">环境参数</span>} key="params3">
                    <SQLEditor
                        options={{ readOnly: isLocked || !couldEdit, minimap: { enabled: false }, theme: editor.options.theme }}
                        value={tabData.taskParams}
                        sync={true}
                        onFocus={() => { }}
                        focusOut={() => { }}
                        language="ini"
                        onChange={this.handleTaskParamChange.bind(this)}
                    />
                </TabPane>
            )
        } else if (tabData && utils.checkExist(tabData.taskType) && tabData.taskType === TASK_TYPE.CUBE_KYLIN) {
            panes.push(
                <TabPane tab={<span className="title-vertical">{prefixLabel}参数</span>} key="params5">
                    <TaskParams
                        isPro={isPro}
                        couldEdit={couldEdit}
                        tabData={tabData}
                        onChange={this.debounceChange}
                    />
                </TabPane>
            )
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
        user: state.user,
        editor: state.editor
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
