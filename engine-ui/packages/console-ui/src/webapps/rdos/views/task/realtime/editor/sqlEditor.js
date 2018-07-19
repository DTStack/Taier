import React, { Component } from 'react'

import CodeEditor from '../../../../components/code-editor'
import ToolBar from './toolbar';

export default class SQLEditor extends Component {

    render() {
        const {
            currentPage, editorChange,
            editorFocus, editorFocusOut,
            dispatch,
        } = this.props

        return (
            <div className="ide-sql">
                <ToolBar currentTabData={currentPage} dispatch={dispatch} />
                <div className="ide-content">
                    <CodeEditor
                        key={`main-editor-${currentPage.id}`}
                        value={currentPage.sqlText}
                        sync={currentPage.merged || undefined}
                        onFocus={editorFocus}
                        focusOut={editorFocusOut}
                        onChange={editorChange}
                    />
                </div>
            </div>
        )
    }
}