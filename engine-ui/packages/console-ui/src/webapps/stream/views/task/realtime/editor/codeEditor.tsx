import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { debounce } from 'lodash';

import { commonFileEditDelegator } from 'widgets/editor/utils';

import IDEEditor from 'main/components/ide';

import API from '../../../../api';
import * as editorActions from '../../../../store/modules/editor/editorAction';
import { setCurrentPage } from '../../../../store/modules/realtimeTask/browser';

@(connect((state: any) as any) => {
    return {
        editor: state.editor
    }
}, (dispatch: any) => {
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign(editorAc, {
        setCurrentPage: (pageData: any) => {
            dispatch(setCurrentPage(pageData));
        }
    })
    return actions;
})
class CodeEditor extends React.Component<any, any> {
    onContentChange = (value: any, editorInstance: any) => {
        const { editorChange } = this.props;
        editorChange({
            merged: false,
            sqlText: value,
            cursorPosition: editorInstance.getPosition()
        })
    }

    sqlFormat = () => {
        const { currentPage, setCurrentPage } = this.props;
        const params: any = {
            sql: currentPage.sqlText || ''
        };

        API.streamSqlFormat(params).then((res: any) => {
            if (res.data) {
                const data: any = {
                    merged: true,
                    sqlText: res.data,
                    id: currentPage.id
                };
                const updatedData = Object.assign(currentPage, data);
                setCurrentPage(updatedData);
            }
        });
    };

    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, { 'maxWait': 2000 })

    render () {
        const {
            currentPage,
            editor
        } = this.props;

        const cursorPosition = currentPage.cursorPosition || undefined;
        const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;
        const editorOpts: any = {
            value: currentPage.sqlText,
            language: 'dtflink',
            options: {
                readOnly: isLocked
            },
            cursorPosition: cursorPosition,
            theme: editor.options.theme || 'white',
            onChange: this.onContentChange,
            sync: currentPage.merged || undefined,
            onCursorSelection: this.debounceSelectionChange
        };

        const toolbarOpts: any = {
            enable: true,
            enableRun: false,
            enableFormat: true,
            onFileEdit: commonFileEditDelegator(this._editor),
            onFormat: this.sqlFormat,
            onThemeChange: (key: any) => {
                this.props.updateEditorOptions({ theme: key })
            }
        }

        return (
            <IDEEditor
                editor={editorOpts}
                toolbar={toolbarOpts}
                key={`main-editor-${currentPage.id}`}
                editorInstanceRef={(instance: any) => { this._editor = instance }}
            />
        )
    }
}

export default CodeEditor;
