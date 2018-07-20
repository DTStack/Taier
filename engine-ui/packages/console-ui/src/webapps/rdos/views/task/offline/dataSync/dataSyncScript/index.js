import React, { Component } from "react";
import { connect } from 'react-redux';
import { debounce } from 'lodash';

import utils from "utils";

import { matchTaskParams } from '../../../../../comm';
import { jsonEditorOptions, } from "../../../../../comm/const";
import CodeEditor from '../../../../../components/code-editor';
import Toolbar from "./toolbar.js";

import { workbenchAction } from '../../../../../store/modules/offlineTask/actionType';
import { setSelectionContent } from '../../../../../store/modules/offlineTask/editorAction';

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

    handleEditorTxtChange = (old, newVal, doc) => {
        const { taskType, taskCustomParams } = this.props
        let params = {
            merged: false,
            cursor: doc.getCursor(),
        }

        if (utils.checkExist(taskType)) {
            params.sqlText = newVal
            params.taskVariables = matchTaskParams(taskCustomParams, newVal);
        }
        this.props.updateTaskFields(params);
    }

    onEditorSelection = (old, doc) => {
        const selected = doc.getSelection()

        if (doc.somethingSelected()) {
            this.props.setSelection(selected)
        } else {
            const oldSelection = this.props.editor.selection

            if (oldSelection !== '') this.props.setSelection('')
        }
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })

    render() {
        const { merged, sqlText, cursor } = this.props;
        // TODO 最好默认不处理格式
        const formated = utils.jsonFormat(sqlText);
        return (
            <div className="ide-sql">
                <div className="ide-header bd-bottom">
                    <Toolbar {...this.props} />
                </div>
                <div className="ide-content">
                    {
                        <div className="ide-editor bd-bottom">
                            <CodeEditor
                                key="jsonEditor"
                                sync={merged || undefined}
                                options={jsonEditorOptions}
                                cursor={cursor}
                                onChange={this.debounceChange}
                                value={formated}
                            />
                        </div>
                    }
                </div>
            </div>
        )
    }
}

export default DataSyncScript;