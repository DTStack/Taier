import * as React from 'react';
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

@(connect(
    (state: any) => {
        const { workbench, editor, common } = state;
        return {
            editor,
            workbench,
            common
        };
    },
    (dispatch: any) => {
        return {
            ...bindActionCreators(workbenchActions, dispatch),
            ...bindActionCreators(runTaskActions, dispatch),
            ...bindActionCreators(commActions, dispatch),
            ...bindActionCreators(notebookActions, dispatch)
        }
    }
) as any)
class NormalTaskPanel extends React.Component<any, any> {
    state: any = {};

    handleEditorTxtChange = (newVal: any, editorInstance: any) => {
        /**
         * 处理一下自定义和系统参数
         */
        let { taskVariables = [] } = this.props.data;
        let variables = matchTaskParams(this.props.common.sysParams, newVal);
        const newKeys = variables.map((v: any) => {
            return v.paramName;
        });
        const oldKeys = taskVariables.map((v: any) => {
            return v.paramName;
        });
        taskVariables = taskVariables.filter((v: any) => {
            return newKeys.indexOf(v.paramName) > -1
        });
        variables = variables.filter((v: any) => {
            return oldKeys.indexOf(v.paramName) == -1
        });
        this.changeContent('taskVariables', [...taskVariables, ...variables]);
        this.props.changeText(newVal, this.props.data);
    };
    changeVariable = (key: any, value: any) => {
        const { data } = this.props;
        const taskVariables: any = [...data.taskVariables];
        const index = taskVariables.findIndex((v: any) => {
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
    changeContent (key: any, value: any) {
        const { data } = this.props;
        if (key) {
            this.props.changeContent({
                [key]: value
            }, data);
        } else {
            this.props.changeContent(value, data);
        }
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
                    onChange={(newFormData: any) => {
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
                    onChange={(value: any) => {
                        this.debounceChangeContent('taskParams', value);
                    }}
                />
            </Tabs.TabPane>
        ]
    }
    renderSaveButton () {
        return <Button
            icon="save"
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
            onSubmit={(values: any) => {
                return this.props.submitNotebook({
                    ...data,
                    ...values
                })
            }}
            onSubmitModel={(values: any) => {
                return this.props.submitNotebookModel({
                    taskId: data.id,
                    ...values
                })
            }}
        />
    }
    render () {
        const { data } = this.props;

        const toolbarOpts: any = {
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
                <NormalTaskForm tabData={data} onChange={(newFormData: any) => {
                    this.changeContent(null, {
                        options: newFormData.options,
                        taskDesc: newFormData.taskDesc,
                        nodePid: newFormData.nodePid,
                        resourceIdList: newFormData.resourceIdList,
                        refResourceIdList: newFormData.refResourceIdList
                    });
                }} />
            </CommonEditor>
        );
    }
}

export default NormalTaskPanel;
