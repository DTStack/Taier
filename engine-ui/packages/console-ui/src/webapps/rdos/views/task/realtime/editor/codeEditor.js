import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { debounce } from 'lodash';

// import Editor from 'widgets/editor';
import IDEEditor from "../../../../components/editor";

import ToolBar from './toolbar';
import API from "../../../../api";
import * as editorActions from '../../../../store/modules/editor/editorAction';
import { setCurrentPage } from '../../../../store/modules/realtimeTask/browser';

@connect(state => {
    return {
        editor: state.editor,
    }
}, dispatch => {
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign(editorAc, {
        setCurrentPage: (pageData) => {
            dispatch(setCurrentPage(pageData));
        }
    })
    return actions;
})
class CodeEditor extends Component {

    onContentChange = (value, editorInstance) => {
        const { editorChange } = this.props;
        editorChange({
            merged: false,
            sqlText: value,
            cursorPosition: editorInstance.getPosition() 
        })
    }

    sqlFormat = () => {

        const { currentPage, setCurrentPage } = this.props;
        const params = {
            sql: currentPage.sqlText || ""
        };

        API.streamSqlFormat(params).then(res => {
            if (res.data) {
                const data = {
                    merged: true,
                    sqlText: res.data,
                    id: currentPage.id,
                };
                const updatedData = assign(currentPage, data);
                setCurrentPage(updatedData);
            }
        });
    };

    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, { 'maxWait': 2000 })

    render() {

        const {
            currentPage,
            editor,
        } = this.props;

        const cursorPosition = currentPage.cursorPosition || undefined;
        const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;

        const editorOpts = {
            value: currentPage.sqlText,
            language: '',
            options: {
                readOnly: isLocked,
            },
            cursorPosition: cursorPosition,
            theme: editor.options.theme,
            onChange: this.onContentChange,
            sync: currentPage.merged || undefined,
            onCursorSelection: this.debounceSelectionChange,
        };

        const toolbarOpts = {
            enable: true,
            enableRun: false,
            enableFormat: true,
            onFileEdit: null,
            onFormat: this.sqlFormat,
            onThemeChange: (key) => {
                this.props.updateEditorOptions({theme: key})
            },
        }

        return (
            <IDEEditor 
                editor={editorOpts}
                toolbar={toolbarOpts}
            />
        )
    }
}

export default CodeEditor;