import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { debounce } from 'lodash';

import { commonFileEditDelegator } from 'widgets/editor/utils';

import IDEEditor from 'main/components/ide';

import API from '../../../../api';
import * as editorActions from '../../../../store/modules/editor/editorAction';
import { setCurrentPage } from '../../../../store/modules/realtimeTask/browser';

@(connect((state: any) => {
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
}) as any)
class CommonCodeEditor extends React.Component<any, any> {
    onContentChange = (value: any, editorInstance: any) => {
        const { editorChange } = this.props;
        editorChange({
            merged: false,
            sqlText: value,
            cursorPosition: editorInstance.getPosition()
        })
    }

    format = () => {
        const { currentPage, setCurrentPage, onFormat } = this.props;
        const params: any = {
            sql: currentPage.sqlText || ''
        };
        function updatePage(text: any) {
            const data: any = {
                merged: true,
                sqlText: text,
                id: currentPage.id
            };
            const updatedData = Object.assign(currentPage, data);
            setCurrentPage(updatedData);
        }
        if (onFormat && typeof onFormat == 'function') {
            onFormat(currentPage.sqlText).then(
                (formatText: any) => {
                    updatePage(formatText)
                }
            )
        } else {
            API.streamSqlFormat(params).then((res: any) => {
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
        const editorOpts: any = {
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

        const toolbarOpts: any = {
            enable: true,
            enableRun: false,
            enableFormat: true,
            onFileEdit: commonFileEditDelegator(this._editor),
            onFormat: this.format,
            onThemeChange: (key: any) => {
                this.props.updateEditorOptions({ theme: key })
            },
            ...toolBarOptions
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
    _editor(_editor: any) {
        throw new Error("Method not implemented.");
    }
}

export default CommonCodeEditor;
