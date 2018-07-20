import React, { Component } from 'react'
import SplitPane from 'react-split-pane'
import { connect } from 'react-redux'
import { debounce } from 'lodash';

import Editor from 'widgets/editor';

import Toolbar from './toolbar';
import Console from './console';

import {
    workbenchAction,
} from '../../../../store/modules/offlineTask/actionType';

import {
    getTab,
    setSelectionContent,
} from '../../../../store/modules/offlineTask/editorAction';

class CommonEditor extends Component {

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
        console.log('changeTab',state);
        
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

    handleEditorTxtChange = (newVal) => {
        let params = {
            merged: false,
            sqlText: newVal,
            // cursor: doc.getCursor(),
        }
        this.props.updateTaskFields(params);
    }

    onEditorSelection = (selected) => {
        if (selected) {
            this.props.setSelection(selected)
        } else {
            const oldSelection = this.props.editor.selection
            if (oldSelection !== '') this.props.setSelection('')
        }
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })

    debounceSelectionChange = debounce(this.onEditorSelection, 200, { 'maxWait': 2000 })

    render() {
        const { editor, currentTabData, options, value,mode } = this.props
        const currentTab = currentTabData.id
        const consoleData = editor.console
        const data = consoleData && consoleData[currentTab] ?
            consoleData[currentTab] : { results: [] }
        const size = this.state.size;

        const language = mode === "shell" ? 'shell' : 'python';

        const cursor = currentTabData.cursor || undefined;
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;

        const editorPane = <div className="ide-editor bd-bottom">
            <Editor 
                key="commonEditor"
                sync={currentTabData.merged || undefined}
                options={{ 
                    readOnly: isLocked, 
                }}
                language={language}
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
})(CommonEditor) 
