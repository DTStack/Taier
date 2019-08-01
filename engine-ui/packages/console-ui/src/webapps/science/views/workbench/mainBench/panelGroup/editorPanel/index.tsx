import * as React from 'react';
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
class EditorPanel extends React.Component<any, any> {
    state: any = {};
    _editor: any;
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
    execSQL = () => {
        const {
            data
        } = this.props;
        const params: any = {
            taskVariables: data.taskVariables
        }
        const code = data.sqlText;
        this.reqExecSQL(data, params, [code]);
    };

    reqExecSQL = (tabData: any, params: any, sqls: any) => {
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

    removeConsoleTab = (targetKey: any) => {
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
    changeContent (key: any, value: any) {
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
    changeConsoleTab = (avtiveKay: any) => {
        const { data } = this.props;
        this.props.changeConsoleKey(data.id, avtiveKay);
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
        const { editor, data } = this.props;

        const currentTab = data.id;

        const consoleData = editor.console[siderBarType.notebook];
        const resultData = get(consoleData[currentTab], 'data', []);
        const consoleActivekey = get(consoleData[currentTab], 'activeKey', null);

        const editorOpts: any = {
            value: data.sqlText,
            language: 'python',
            theme: editor.options.theme,
            onChange: this.debounceChange,
            sync: data.merged || undefined
        };

        const toolbarOpts: any = {
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

        const consoleOpts: any = {
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
                    editorInstanceRef={(instance: any) => {
                        this._editor = instance;
                        this.forceUpdate();
                    }}
                />
            </CommonEditor>
        );
    }
}

export default EditorPanel;
