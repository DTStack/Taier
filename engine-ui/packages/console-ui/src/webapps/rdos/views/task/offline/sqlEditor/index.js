import React, { Component } from 'react'
import SplitPane from 'react-split-pane'
import { connect } from 'react-redux'
import { debounce } from 'lodash';

import utils from 'utils';
import { filterComments } from 'funcs';
import Editor from 'widgets/editor';
import pureRender from 'utils/pureRender'

import Toolbar from './toolbar';
import Console from './console';

import { matchTaskParams } from '../../../../comm';

import {
    workbenchAction,
} from '../../../../store/modules/offlineTask/actionType';

import {
    getTab,
    setSelectionContent,
} from '../../../../store/modules/offlineTask/editorAction';

@pureRender
class SQLEditor extends Component {

    state = {
        customHeight: null,
        changeTab: true,
    }

    componentDidMount() {
        const currentNode = this.props.currentTabData;
        if (currentNode) {
            this.props.getTab(currentNode.id)
        }
    }

    componentWillReceiveProps(nextProps) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    changeTab = (state) => {
        let { changeTab } = this.state;
        if(state){
            changeTab = true;
        }else{
            changeTab = false;
        }
       this.setState({
        changeTab
       })
    }

    handleEditorTxtChange = (newVal, editorInstance) => {
        const task = this.props.currentTabData
        const taskCustomParams = this.props.taskCustomParams;
        let params = {
            merged: false,
            cursorPosition: editorInstance.getPosition(),
        }
        if (utils.checkExist(task.taskType)) {
            params.sqlText = newVal
            // 过滤注释内容
            const filterComm = filterComments(newVal)
            params.taskVariables = matchTaskParams(taskCustomParams, filterComm)//this.matchTaskParams(newVal)
        } else if (utils.checkExist(task.type)) {
            params.scriptText = newVal
        }
        this.props.updateTaskFields(params);
    }

    onEditorSelection = (selected) => {
        // const selected = doc.getSelection()
        if (selected) {
            this.props.setSelection(selected)
        } else {
            const oldSelection = this.props.editor.selection
            if (oldSelection !== '') this.props.setSelection('')
        }
    }

    changeEditorTheme = (theme) => {

    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })

    debounceSelectionChange = debounce(this.onEditorSelection, 200, { 'maxWait': 2000 })

    render() {
        const { editor, currentTabData, options, value } = this.props
        const currentTab = currentTabData.id
        const consoleData = editor.console
        const data = consoleData && consoleData[currentTab] ?
            consoleData[currentTab] : { results: [] }
        const size = this.state.size;

        const cursorPosition = currentTabData.cursorPosition || undefined;
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;

        const editorPane = <div className="ide-editor">
            <Editor 
                key="sqlEditor"
                sync={currentTabData.merged || undefined}
                options={{ 
                    readOnly: isLocked, 
                }}
                cursorPosition={cursorPosition}
                language="sql"
                value={value}
                onCursorSelection={this.debounceSelectionChange}
                onChange={this.debounceChange}
            />
        </div>

        return (
            <div className="ide-sql">
                <div className="ide-header bd-bottom">
                    <Toolbar {...this.props} changeTab={this.changeTab}/>
                </div>
                <div className='ide-content'>
                    {
                        data.log ?
                        <SplitPane
                            split="horizontal"
                            minSize={100}
                            maxSize={-77}
                            style={{ paddingBottom: '40px' }}
                            defaultSize="60%"
                            primary="first"
                            size={size}
                            onDragStarted={() => {
                                this.setState({
                                    size: undefined
                                })
                            }}
                        >
                            { editorPane }
                            <Console
                                changeTab={this.changeTab}
                                changeTabStatus={this.state.changeTab}
                                data={data}
                                ref="getSwordButton"
                                currentTab={currentTab}
                                dispatch={this.props.dispatch}
                                setMax={() => {
                                    this.setState({
                                        size: '100px'
                                    })
                                }}
                                setMin={() => {
                                    this.setState({
                                        size: 'calc(100% - 40px)'
                                    })
                                }}
                            />
                        </SplitPane> : editorPane
                    }
                </div>
            </div>
        )
    }
}

export default connect(state => {
    return {
        editor: state.editor,
        project: state.project,
        user: state.user,
    }
}, dispatch => {
    return {
        dispatch: dispatch,
        updateTaskFields(params) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        },
        setSelection(data) {
            dispatch(setSelectionContent(data))
        },
        getTab(id) {
            dispatch(getTab(id))
        }
    }
})(SQLEditor) 
