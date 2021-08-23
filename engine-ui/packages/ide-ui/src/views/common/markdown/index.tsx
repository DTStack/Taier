import * as React from 'react';
import CodeMirrorEditor from 'dt-react-codemirror-editor';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { workbenchActions } from '../../../controller/dataSync/offlineAction';
import * as editorActions from '../../../controller/dataSync/workbench';

@(connect(
    (state: any) => {
        const { workbench } = state.dataSync;
        const { currentTab, tabs } = workbench;
        const currentTabData = tabs.filter((tab: any) => {
            return tab.id === currentTab;
        })[0];
        return {
            editor: state.editor,
            currentTab,
            currentTabData,
        };
    },
    (dispatch: any) => {
        const taskAc = workbenchActions(dispatch);
        const editorAc = bindActionCreators(editorActions, dispatch);
        const actions = Object.assign(editorAc, taskAc);
        return actions;
    }
) as any)
class Markdown extends React.Component<any> {
    componentDidMount() {
        // const currentNode = this.props.currentTabData;
        // if (currentNode) {
        //     this.props.getTab(currentNode.id); // 初始化console所需的数据结构
        // }
    }

    render() {
        const { currentTabData, editor } = this.props;
        const currentTab = currentTabData?.id;
        const consoleData = editor.console;

        const data =
            consoleData && consoleData[currentTab]
                ? consoleData[currentTab]
                : { results: [] };
        const defaultValue = data && data.log;
        const defaultEditorOptions: any = {
            mode: 'dtlog',
            lint: true,
            indentWithTabs: true,
            smartIndent: true,
            lineNumbers: false,
            autofocus: false,
            lineWrapping: true,
            readOnly: true
        };

        return (
            <div className="mo_code_mirror">
                <CodeMirrorEditor
                    value={defaultValue}
                    options={{ ...defaultEditorOptions }}
                />
            </div>
        );
    }
}

export default Markdown;
