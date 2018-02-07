import React, { Component } from 'react'

import MrEditor from './mrEditor'
import { TASK_TYPE } from '../../../comm/const';
import SQLEditor from '../../../components/code-editor'

export default class EditorContainer extends Component {

    render() {
        const {
            currentPage, editorChange,
            editorFocus, editorFocusOut,
        } = this.props
        const showContent = currentPage.taskType === TASK_TYPE.SQL ?
            (<SQLEditor
                key={`main-editor-${currentPage.id}`}
                value={currentPage.sqlText}
                sync={currentPage.merged || undefined}
                onFocus={editorFocus}
                focusOut={editorFocusOut}
                onChange={editorChange}
            />)
            :
            (<MrEditor
                {...this.props}
            />)

        return (
            <div className="editor-container">
                {showContent}
            </div>
        )
    }
}
