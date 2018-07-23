import React, { Component } from 'react'

import Editor from 'widgets/editor';
import ToolBar from './toolbar';

export default class IDEEditor extends Component {

    render() {
        const {
            currentPage, editorChange,
            dispatch,
        } = this.props

        return (
            <div className="ide-sql">
                <ToolBar currentTabData={currentPage} dispatch={dispatch} />
                <div className="ide-content">
                    <Editor
                        key={`main-editor-${currentPage.id}`}
                        value={currentPage.sqlText}
                        sync={currentPage.merged || undefined}
                        language="sql"
                        onChange={editorChange}
                    />
                </div>
            </div>
        )
    }
}