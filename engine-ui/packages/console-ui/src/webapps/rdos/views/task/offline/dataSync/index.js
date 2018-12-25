import React, { Component } from 'react';
import PropTypes from 'prop-types';
import SplitPane from 'react-split-pane';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { isEmpty } from 'lodash';

import ToolBar from 'main/components/ide/toolbar';
import Console from 'main/components/ide/console';
import 'main/components/ide/ide.scss';

import DataSync from './dataSync';
// import {
//     workbenchActions
// } from '../../../../store/modules/offlineTask/offlineAction';
import * as editorActions from '../../../../store/modules/editor/editorAction';

const propType = {
    editor: PropTypes.object,
    toolbar: PropTypes.object,
    console: PropTypes.object
}

@connect(state => {
    const { workbench, dataSync } = state.offlineTask;
    const { currentTab, tabs } = workbench;
    const currentTabData = tabs.filter(tab => {
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
}, dispatch => {
    // const taskAc = workbenchActions(dispatch);
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign(editorAc)
    return actions;
})
class DataSyncWorkbench extends Component {
    state = {
        changeTab: true,
        size: undefined
    };

    componentDidMount () {
        const currentNode = this.props.currentTabData;
        if (currentNode) {
            this.props.getTab(currentNode.id)// 初始化console所需的数据结构
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    changeTab = state => {
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
        const params = { taskId: currentTabData.id, name: currentTabData.name };
        execDataSync(currentTab, params);
    }

    onStop = async () => {
        const { stopDataSync, currentTab } = this.props;
        stopDataSync(currentTab);
    }

    removeConsoleTab = (targetKey) => {
        const { currentTab } = this.props;
        this.props.removeRes(currentTab, parseInt(targetKey, 10))
    }

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetConsole(currentTab)
    }

    render () {
        const { currentTabData, editor, saveTab, dataSync } = this.props;

        const currentTab = currentTabData.id;
        const consoleData = editor.console;

        const data = consoleData && consoleData[currentTab]
            ? consoleData[currentTab] : { results: [] }

        const unSave = currentTabData.notSynced; // 未保存的同步任务无法运行
        const unConfigured = dataSync.tabId === currentTab && isEmpty(dataSync.sourceMap);
        const toolbar = {
            enable: true,
            enableRun: true,
            disableEdit: true,
            disableRun: unSave || unConfigured,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.onRun,
            onStop: this.onStop
        }

        const console = {
            data: data,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab
        }

        const { size } = this.state;

        const hasLog = console && console.data && console.data.log;
        const defaultSplitSize = hasLog ? '60%' : '100%';

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
                        key="ide-split-pane"
                        size={size}
                        allowResize={hasLog}
                        onChange={(size) => {
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

DataSyncWorkbench.propTypes = propType

export default DataSyncWorkbench;
