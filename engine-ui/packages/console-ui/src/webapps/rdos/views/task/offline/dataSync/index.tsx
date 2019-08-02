import * as React from 'react';
import PropTypes from 'prop-types';
import { Button, Modal } from 'antd';
import SplitPane from 'react-split-pane';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { isEmpty } from 'lodash';

import ToolBar from 'main/components/ide/toolbar';
import Console from 'main/components/ide/console';
import 'main/components/ide/ide.scss';

import DataSync from './dataSync';
import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction';
import * as editorActions from '../../../../store/modules/editor/editorAction';

const confirm = Modal.confirm;

const propType: any = {
    editor: PropTypes.object,
    toolbar: PropTypes.object,
    console: PropTypes.object
}

@(connect((state: any) => {
    const { workbench, dataSync } = state.offlineTask;
    const { currentTab, tabs } = workbench;
    const currentTabData = tabs.filter((tab: any) => {
        return tab.id === currentTab;
    })[0];

    return {
        editor: state.editor,
        project: state.project,
        user: state.user,
        currentTab,
        currentTabData,
        dataSync
    }
}, (dispatch: any) => {
    const taskAc = workbenchActions(dispatch);
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign(editorAc, taskAc)
    return actions;
}) as any)
class DataSyncWorkbench extends React.Component<any, any> {
    state: any = {
        changeTab: true,
        size: undefined
    };
    static propTypes = propType;
    componentDidMount () {
        const currentNode = this.props.currentTabData;
        if (currentNode) {
            this.props.getTab(currentNode.id)// 初始化console所需的数据结构
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    changeTab = (state: any) => {
        let { changeTab } = this.state;

        if (state) {
            changeTab = true;
        } else {
            changeTab = false;
        }

        this.setState({
            changeTab
        });
    };

    onRun = async () => {
        const { execDataSync, currentTabData, currentTab } = this.props;
        const params: any = { taskId: currentTabData.id, name: currentTabData.name };
        execDataSync(currentTab, params);
    }

    onStop = async () => {
        const { stopDataSync, currentTab } = this.props;
        stopDataSync(currentTab);
    }

    removeConsoleTab = (targetKey: any) => {
        const { currentTab } = this.props;
        this.props.removeRes(currentTab, parseInt(targetKey, 10))
    }

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetConsole(currentTab)
    }

    onConvertDataSyncToScriptMode = () => {
        const { convertDataSyncToScriptMode, currentTabData } = this.props;
        confirm({
            title: '转换为脚本',
            content: (<div>
                <p style={{ color: '#f04134' }}>此操作不可逆，是否继续？</p>
                <p>当前为向导模式，配置简单快捷，脚本模式可灵活配置更多参数，定制化程度高</p>
            </div>),
            okText: '确认',
            cancelText: '取消',
            onOk: function () {
                convertDataSyncToScriptMode(currentTabData)
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    render () {
        const { currentTabData, editor, saveTab, dataSync, project } = this.props;

        const currentTab = currentTabData.id;
        const consoleData = editor.console;
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;

        const data = consoleData && consoleData[currentTab]
            ? consoleData[currentTab] : { results: [] };
        const titleFix = { title: "转换同步任务由向导模式为脚本模式" };
        const convertToScriptMode = (<Button
            disabled={isLocked}
            icon="swap"
            {...titleFix}
            onClick={this.onConvertDataSyncToScriptMode}>
                转换为脚本
        </Button>);

        const unSave = currentTabData.notSynced; // 未保存的同步任务无法运行
        const unConfigured = dataSync.tabId === currentTab && isEmpty(dataSync.sourceMap);
        const toolbar: any = {
            enable: true,
            enableRun: true,
            disableEdit: true,
            disableRun: isLocked || unSave || unConfigured,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.onRun,
            onStop: this.onStop,
            leftCustomButton: convertToScriptMode
        }

        const console: any = {
            data: data,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab
        }

        const { size } = this.state;

        const hasLog = console && console.data && console.data.log;
        const defaultSplitSize = hasLog ? '60%' : '100%';
        const showSize = !hasLog ? '100%' : size;
        return (
            <div className="ide-editor">
                <div className="ide-header bd-bottom">
                    <ToolBar
                        {...toolbar}
                        changeTab={this.changeTab}
                    />
                </div>
                <div style={{ zIndex: 901 }} className="ide-content">
                    <SplitPane
                        split="horizontal"
                        minSize={100}
                        maxSize={-77}
                        defaultSize={defaultSplitSize}
                        primary="first"
                        key={`ide-split-pane`}
                        size={showSize}
                        allowResize={hasLog}
                        onChange={(size: any) => {
                            this.setState({
                                size: size
                            });
                        }}
                    >
                        <div style={{
                            width: '100%',
                            height: '100%',
                            minHeight: '400px',
                            position: 'relative'
                        }}>
                            <DataSync saveTab={saveTab} currentTabData={currentTabData} />
                        </div>
                        {
                            hasLog ? <Console
                                onConsoleTabChange={this.changeTab}
                                activedTab={this.state.changeTab}
                                isDisEabledDownload={project.isAllowDownload == 0}
                                setSplitMax={() => {
                                    this.setState({
                                        size: '100px'
                                    });
                                }}
                                setSplitMin={() => {
                                    this.setState({
                                        size: 'calc(100% - 40px)'
                                    });
                                }}
                                {...console}
                            /> : ''
                        }
                    </SplitPane>
                </div>
            </div>
        );
    }
}

export default DataSyncWorkbench;
