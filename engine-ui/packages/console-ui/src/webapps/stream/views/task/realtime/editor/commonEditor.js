import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { debounce } from 'lodash';

import { commonFileEditDelegator } from 'widgets/editor/utils';

import IDEEditor from 'main/components/ide';

import API from '../../../../api';
import * as editorActions from '../../../../store/modules/editor/editorAction';
import { setCurrentPage } from '../../../../store/modules/realtimeTask/browser';

@connect(state => {
    return {
        editor: state.editor
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
class CommonCodeEditor extends Component {
    onContentChange = (value, editorInstance) => {
        const { editorChange } = this.props;
        editorChange({
            merged: false,
            sqlText: value,
            cursorPosition: editorInstance.getPosition()
        })
    }

    format = () => {
        const { currentPage, setCurrentPage, onFormat } = this.props;
        const params = {
            sql: currentPage.sqlText || ''
        };
        function updatePage (text) {
            const data = {
                merged: true,
                sqlText: text,
                id: currentPage.id
            };
            const updatedData = Object.assign(currentPage, data);
            setCurrentPage(updatedData);
        }
        if (onFormat && typeof onFormat == 'function') {
            onFormat(currentPage.sqlText).then(
                (formatText) => {
                    updatePage(formatText)
                }
            )
        } else {
            API.streamSqlFormat(params).then(res => {
                if (res.data) {
                    updatePage(res.data)
                }
            });
        }
    };

    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, { 'maxWait': 2000 })

    render () {
        const {
            currentPage,
            editor,
            mode,
            toolBarOptions = {}
        } = this.props;

        const cursorPosition = currentPage.cursorPosition || undefined;
        const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;
        const editorOpts = {
            value: currentPage.sqlText,
            language: mode,
            options: {
                readOnly: isLocked
            },
            cursorPosition: cursorPosition,
            theme: editor.options.theme,
            onChange: this.onContentChange,
            sync: currentPage.merged || undefined,
            onCursorSelection: this.debounceSelectionChange
        };

        const toolbarOpts = {
            enable: true,
            enableRun: false,
            enableFormat: true,
            onFileEdit: commonFileEditDelegator(this._editor),
            onFormat: this.format,
            onThemeChange: (key) => {
                this.props.updateEditorOptions({ theme: key })
            },
            ...toolBarOptions
        }

        return (
            <IDEEditor
                editor={editorOpts}
                toolbar={toolbarOpts}
                key={`main-editor-${currentPage.id}`}
                editorInstanceRef={(instance) => { this._editor = instance }}
            />
        )
    }
}

export default CommonCodeEditor;
