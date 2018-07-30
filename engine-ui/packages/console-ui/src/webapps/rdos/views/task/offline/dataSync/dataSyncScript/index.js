import React, { Component } from "react";
import { connect } from 'react-redux';
import { debounce } from 'lodash';

import utils from "utils";

import { matchTaskParams } from '../../../../../comm';
import { jsonEditorOptions, } from "../../../../../comm/const";
import CodeEditor from '../../../../../components/code-editor';
import Editor from "widgets/editor";
import Toolbar from "./toolbar.js";

import { workbenchAction } from '../../../../../store/modules/offlineTask/actionType';
import { setSelectionContent } from '../../../../../store/modules/editor/editorAction';

@connect(state => {
    return {
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
})
class DataSyncScript extends Component {

    handleEditorTxtChange = (newVal, editorInstance) => {
        const { taskType, taskCustomParams } = this.props
        let params = {
            merged: false,
            cursorPosition: editorInstance.getPosition(),
        }
        if (utils.checkExist(taskType)) {
            params.sqlText = newVal
            params.taskVariables = matchTaskParams(taskCustomParams, newVal)//this.matchTaskParams(newVal)
        } 
        this.props.updateTaskFields(params);
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })

    render() {
        const { merged, sqlText, cursorPosition } = this.props;
        return (
            <div className="ide-sql ide-editor">
                <div className="ide-header bd-bottom">
                    <Toolbar {...this.props} />
                </div>
                <div className="ide-content">
                    {
                        <div className="ide-editor bd-bottom">
                            <Editor
                                language="json"
                                cursorPosition={cursorPosition}
                                sync={merged || undefined}
                                onChange={this.debounceChange}
                                value={sqlText}
                            />
                        </div>
                    }
                </div>
            </div>
        )
    }
}

export default DataSyncScript;