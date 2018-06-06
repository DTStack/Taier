import React, { Component } from 'react'
import SplitPane from 'react-split-pane'
import { connect } from 'react-redux'
import { debounce } from 'lodash';

import utils from 'utils';
import { filterComments } from 'funcs';

import Toolbar from './toolbar';
import Console from './console';

import Api from '../../../../api';
import { matchTaskParams } from '../../../../comm';
import CodeEditor from '../../../../components/code-editor';

import {
    workbenchAction,
} from '../../../../store/modules/offlineTask/actionType';

import {
    getTab,
    setSelectionContent,
} from '../../../../store/modules/offlineTask/sqlEditor';

 class SQLEditor extends Component {

    state={
        customHeight:null
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

    handleEditorTxtChange = (old, newVal, doc) => {
        const task = this.props.currentTabData
        const taskCustomParams = this.props.taskCustomParams;
        let params = {
            merged: false,
            cursor: doc.getCursor(),
        }
        if (old !== newVal) {
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
    }

    onEditorSelection = (old, doc) => {
        const selected = doc.getSelection()
        if (doc.somethingSelected()) {
            this.props.setSelection(selected)
        } else {
            const oldSelection = this.props.sqlEditor.selection
            if (oldSelection !== '' ) this.props.setSelection('')
        }
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })

    debounceSelectionChange = debounce(this.onEditorSelection, 200, { 'maxWait': 2000 })

    render() {
        const { sqlEditor, currentTabData, options, value } = this.props
        const currentTab = currentTabData.id
        const consoleData = sqlEditor.console
        const data = consoleData && consoleData[currentTab] ? 
        consoleData[currentTab] : { results: [] }
        const size = this.state.size;

        const cursor = currentTabData.cursor || undefined;



        return (
            <div className="ide-sql">
                <div className="ide-header bd-bottom">
                    <Toolbar {...this.props} />
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
                            onDragStarted={()=>{
                                this.setState({
                                    size:undefined
                                })
                            }}
                        >
                            <div className="ide-editor bd-bottom">
                                <CodeEditor 
                                    key="sqlEditor"
                                    sync={currentTabData.merged || undefined}
                                    options={options}
                                    value={value}
                                    cursor={cursor}
                                    cursorActivity={ this.debounceSelectionChange }
                                    onChange={ this.debounceChange }
                                   
                                />
                            </div>
                            <Console
                                data={data}
                                currentTab={currentTab}
                                dispatch={this.props.dispatch}
                                setMax={()=>{
                                    this.setState({
                                        size:'100px'
                                    })
                                }}
                                setMin={()=>{
                                    this.setState({
                                        size:'calc(100% - 40px)'
                                    })
                                }}
                            /> 
                        </SplitPane> : 
                        <div className="ide-editor bd-bottom">
                            <CodeEditor
                                key="sqlEditor"
                                sync={currentTabData.merged || undefined}
                                options={options}
                                cursor={cursor}
                                cursorActivity={ this.debounceSelectionChange }
                                onChange={ this.debounceChange }  
                                value={value}
                                
                            />
                        </div>
                    }
                </div>
            </div>
        )
    }
}

export default connect(state => {
    return {
        sqlEditor: state.sqlEditor,
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
