import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Tabs, Button, Icon } from 'antd';
import { debounce, get } from 'lodash';
import { bindActionCreators } from 'redux';
import { commonFileEditDelegator } from 'widgets/editor/utils';

import CommonEditor from '../../../../../components/commonEditor';
import Editor from 'widgets/editor/index'
import SchedulingConfig from '../../../../../components/schedulingConfig';
import EnvConfig from '../../../../../components/envConfig';
import TaskVariables from '../../../../../components/taskVariables'
import PublishButtons from '../../../../../components/publishButtons';

import reqUrls from '../../../../../consts/reqUrls';
import { siderBarType } from '../../../../../consts';
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
class EditorPanel extends Component {
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
    execSQL = () => {
        const {
            data
        } = this.props;
        const params = {
            taskVariables: data.taskVariables
        }
        const code = data.sqlText;
        this.reqExecSQL(data, params, [code]);
    };

    reqExecSQL = (tabData, params, sqls) => {
        const { exec } = this.props;
        exec(tabData, params, sqls);
    };

    stopSQL = () => {
        const { currentTab, stopTask } = this.props;
        stopTask(currentTab);
    };

    // 执行确认
    execConfirm = () => {
        // 不检测，直接执行
        this.execSQL();
    };

    removeConsoleTab = targetKey => {
        const { currentTab } = this.props;
        this.props.removeNotebookRes(currentTab, targetKey);
    };

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetNotebookConsole(currentTab);
    };
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
    changeConsoleTab = (avtiveKay) => {
        const { data } = this.props;
        this.props.changeConsoleKey(data.id, avtiveKay);
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
        const { editor, data } = this.props;

        const currentTab = data.id;

        const consoleData = editor.console[siderBarType.notebook];
        const resultData = get(consoleData[currentTab], 'data', []);
        const consoleActivekey = get(consoleData[currentTab], 'activeKey', null);

        const editorOpts = {
            value: data.sqlText,
            language: 'python',
            theme: editor.options.theme,
            onChange: this.debounceChange,
            sync: data.merged || undefined,
            onCursorSelection: this.debounceSelectionChange
        };

        const toolbarOpts = {
            enable: true,
            enableRun: true,
            enableFormat: false,
            disableEdit: false,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL,
            customToobar: this.renderSaveButton(),
            leftCustomButton: this.renderPublishButton(),
            onFileEdit: commonFileEditDelegator(this._editor)
        };

        const consoleOpts = {
            data: resultData,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab,
            downloadUri: reqUrls.DOWNLOAD_SQL_RESULT,
            tabOptions: {
                activeKey: consoleActivekey,
                onChange: this.changeConsoleTab

            }
        };

        return (
            <CommonEditor
                console={consoleOpts}
                toolbar={toolbarOpts}
                siderBarItems={this.renderSiderbarItems()}
            >
                <Editor
                    {...editorOpts}
                    editorInstanceRef={instance => {
                        this._editor = instance;
                        this.forceUpdate();
                    }}
                />
            </CommonEditor>
        );
    }
}

export default EditorPanel;
