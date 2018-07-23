import React, { Component } from 'react'

import Editor from 'widgets/editor';
import ToolBar from './toolbar';

export default class SQLEditor extends Component {

    onContentChange = (value, editorInstance) => {
        const { editorChange } = this.props;
        editorChange({
            merged: false,
            sqlText: value,
            cursorPosition: editorInstance.getPosition() 
        })
    }

    render() {
        const {
            currentPage,
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
                        onChange={this.onContentChange}
                    />
                </div>
            </div>
        )
    }
}