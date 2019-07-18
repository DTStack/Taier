import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Tabs, Button, Icon } from 'antd';
import { debounce } from 'lodash';
import { bindActionCreators } from 'redux';

import CommonEditor from '../../../../../components/commonEditor';
import NormalTaskForm from '../../../../../components/normalTask';
import SchedulingConfig from '../../../../../components/schedulingConfig';
import EnvConfig from '../../../../../components/envConfig';
import TaskVariables from '../../../../../components/taskVariables'
import PublishButtons from '../../../../../components/publishButtons';
import { matchTaskParams } from '../../../../../comm'

import workbenchActions from '../../../../../actions/workbenchActions';
import * as runTaskActions from '../../../../../actions/runTaskActions';
import * as notebookActions from '../../../../../actions/notebookActions';
import * as commActions from '../../../../../actions/base';

@connect(
    state => {
        const { workbench, editor, common } = state;
        return {
            editor,
            workbench,
            common
        };
    },
    dispatch => {
        return {
            ...bindActionCreators(workbenchActions, dispatch),
            ...bindActionCreators(runTaskActions, dispatch),
            ...bindActionCreators(commActions, dispatch),
            ...bindActionCreators(notebookActions, dispatch)
        }
    }
)
class NormalTaskPanel extends Component {
    state = {};

    handleEditorTxtChange = (newVal, editorInstance) => {
        /**
         * 处理一下自定义和系统参数
         */
        let { taskVariables = [] } = this.props.data;
        let variables = matchTaskParams(this.props.common.sysParams, newVal);
        const newKeys = variables.map((v) => {
            return v.paramName;
        });
        const oldKeys = taskVariables.map((v) => {
            return v.paramName;
        });
        taskVariables = taskVariables.filter((v) => {
            return newKeys.indexOf(v.paramName) > -1
        });
        variables = variables.filter((v) => {
            return oldKeys.indexOf(v.paramName) == -1
        });
        this.changeContent('taskVariables', [...taskVariables, ...variables]);
        this.props.changeText(newVal, this.props.data);
    };
    changeVariable = (key, value) => {
        const { data } = this.props;
        const taskVariables = [...data.taskVariables];
        const index = taskVariables.findIndex((v) => {
            return v.paramName == key;
        });
        if (index > -1) {
            taskVariables[index] = {
                ...taskVariables[index],
                paramCommand: value
            }
            this.changeContent('taskVariables', taskVariables);
        }
    }

    saveTab = () => {
        const { data } = this.props;
        this.props.saveNotebook(data);
    }
    debounceChange = debounce(this.handleEditorTxtChange, 300, {
        maxWait: 2000
    });
    debounceChangeContent = debounce(this.changeContent, 100, {
        maxWait: 2000
    });
    changeContent (key, value) {
        const { data } = this.props;
        this.props.changeContent({
            [key]: value
        }, data);
    }
    renderSiderbarItems () {
        const { data } = this.props;
        return [
            <Tabs.TabPane
                tab={<span><Icon className='c-panel__siderbar__title__icon' type="rocket" />调度周期</span>}
                key='scheduleConf'
            >
                <SchedulingConfig
                    formData={Object.assign(JSON.parse(data.scheduleConf || '{}'), { scheduleStatus: data.scheduleStatus })}
                    onChange={(newFormData) => {
                        this.changeContent('scheduleStatus', newFormData.scheduleStatus);
                        this.debounceChangeContent('scheduleConf', JSON.stringify(newFormData));
                    }}
                />
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab={<span><Icon className='c-panel__siderbar__title__icon' type="tags-o" />任务参数</span>}
                key='taskParams'
            >
                <TaskVariables
                    data={data.taskVariables}
                    changeVariable={this.changeVariable}
                />
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab={<span><Icon className='c-panel__siderbar__title__icon' type="eye-o" />环境参数</span>}
                key='envParams'
            >
                <EnvConfig
                    value={data.taskParams}
                    onChange={(value) => {
                        this.debounceChangeContent('taskParams', value);
                    }}
                />
            </Tabs.TabPane>
        ]
    }
    renderSaveButton () {
        return <Button
            icon="save"
            title="保存"
            onClick={this.saveTab}
        >
            保存
        </Button>
    }
    renderPublishButton () {
        const { data } = this.props;
        return <PublishButtons
            data={data}
            name='作业'
            isNotebook={true}
            disabled={data.isDirty}
            onSubmit={(values) => {
                return this.props.submitNotebook({
                    ...data,
                    ...values
                })
            }}
            onSubmitModel={(values) => {
                return this.props.submitNotebookModel({
                    taskId: data.id,
                    ...values
                })
            }}
        />
    }
    render () {
        const { data } = this.props;

        const toolbarOpts = {
            enable: true,
            disableEdit: true,
            customToobar: this.renderSaveButton(),
            leftCustomButton: this.renderPublishButton()
        };
        return (
            <CommonEditor
                toolbar={toolbarOpts}
                siderBarItems={this.renderSiderbarItems()}
            >
                <NormalTaskForm tabData={data} />
            </CommonEditor>
        );
    }
}

export default NormalTaskPanel;
