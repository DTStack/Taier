import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { debounce } from 'lodash';

import { commonFileEditDelegator } from 'widgets/editor/utils';

import IDEEditor from 'main/components/ide';
import { language } from 'widgets/editor/languages/dt-flink/dtflink';

import API from '../../../../api';
import * as editorActions from '../../../../store/modules/editor/editorAction';
import { setCurrentPage } from '../../../../store/modules/realtimeTask/browser';

interface CodeEditorState {
    funcList: string[];
    funcCompleteItems: [string, string, string, string][];
}
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
class CodeEditor extends React.Component<any, CodeEditorState> {
    state: CodeEditorState = {
        funcList: [],
        funcCompleteItems: []
    }
    _editor: any;
    componentDidMount () {
        this.initFuncList();
    }
    initFuncList () {
        API.getAllFunction()
            .then(
                (res: any) => {
                    if (res.code == 1) {
                        let { data } = res;
                        this.setState({
                            funcList: data || [],
                            funcCompleteItems: data && data.map(
                                (funcName: any) => {
                                    return [funcName, '函数', '2000', 'Function']
                                }
                            )
                        })
                    }
                }
            )
    }
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
        const selectRange: any = this._editor.getSelection();
        const isSelect = selectRange && (selectRange.startColumn != selectRange.endColumn || selectRange.startLineNumber != selectRange.endLineNumber);
        let oldText = currentPage.sqlText || '';
        isSelect && (oldText = this._editor.getModel().getValueInRange(selectRange));
        const params: any = {
            sql: oldText
        };

        API.streamSqlFormat(params).then((res: any) => {
            if (res.data) {
                const data: any = {
                    sqlText: res.data,
                    id: currentPage.id
                };
                let newText = res.data;
                if (isSelect) {
                    // 格式化部分
                    this._editor && this._editor.executeEdits(this._editor.getModel().getValue(), [{
                        range: selectRange,
                        text: res.data
                    }]);
                    newText = this._editor.getModel().getValue();
                } else {
                    data.merged = true;
                }
                data.sqlText = newText;
                const updatedData = Object.assign({}, currentPage, data);
                setCurrentPage(updatedData);
            }
        });
    };
    customCompleteProvider = (completeItems: any, resolve: (items: any[]) => void, customCompletionItemsCreater: any, ext: any) => {
        const { funcCompleteItems } = this.state;
        resolve([].concat(completeItems).concat(customCompletionItemsCreater(funcCompleteItems)));
    }
    debounceSelectionChange = debounce(this.props.setSelectionContent, 200, { 'maxWait': 2000 })

    render () {
        const {
            currentPage,
            editor,
            toolBarOptions
        } = this.props;

        const { funcList } = this.state;

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
            onCursorSelection: this.debounceSelectionChange,
            customCompleteProvider: this.customCompleteProvider,
            languageConfig: {
                ...language,
                builtinFunctions: [],
                windowsFunctions: [],
                innerFunctions: [],
                otherFunctions: [],
                customFunctions: funcList
            }
        };

        const toolbarOpts: any = {
            enable: true,
            enableRun: false,
            enableFormat: true,
            onFileEdit: commonFileEditDelegator(this._editor),
            onFormat: this.sqlFormat,
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
}

export default CodeEditor;
