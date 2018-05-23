import React, { Component } from "react";
import { Steps, Button, message } from 'antd';
import { connect } from 'react-redux';
import { debounce } from 'lodash';

import utils from "utils";

import ajax from '../../../../../api';
import { jsonEditorOptions, } from "../../../../../comm/const";
import CodeEditor from '../../../../../components/code-editor';
import Toolbar from "./toolbar.js";

import { workbenchAction } from '../../../../../store/modules/offlineTask/actionType';
import { setSelectionContent } from '../../../../../store/modules/offlineTask/sqlEditor';

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

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {

    }

    handleEditorTxtChange = (old, newVal, doc) => {
        const { taskType } = this.props
        let params = {
            merged: false,
            cursor: doc.getCursor(),
        }

        if (old !== newVal) {
            if (utils.checkExist(taskType)) {
                params.sqlText = newVal
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

            if (oldSelection !== '') this.props.setSelection('')
        }
    }

    debounceChange = debounce(this.handleEditorTxtChange, 300, { 'maxWait': 2000 })

    render() {
        const { merged, sqlText, cursor } = this.props;

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
                                sync={merged||undefined}
                                options={jsonEditorOptions}
                                cursor={cursor}
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