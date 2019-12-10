import * as React from 'react';
import PropTypes from 'prop-types';
import SplitPane from 'react-split-pane';
import './ide.scss';
import 'handsontable/dist/handsontable.full.css';

import Editor from 'widgets/editor';
import ToolBar from './toolbar';
import Console from './console';

// import './style.scss';

const propType = {
    editor: PropTypes.object,
    toolbar: PropTypes.object,
    console: PropTypes.object
}

class IDEEditor extends React.Component<any, any> {
    static propTypes = propType;

    state: any = {
        changeTab: true,
        size: undefined,
        editorSize: undefined
    };

    changeTab = (state: any) => {
        let { changeTab } = this.state;

        if (state) {
            changeTab = true;
        } else {
            changeTab = false;
        }

        this.setState({
            changeTab
        });
    };

    renderEditorPane () {
        const { editor, editorInstanceRef, extraPane } = this.props;
        const { editorSize } = this.state;
        const editorView = <Editor style={{ minHeight: '100px' }} editorInstanceRef={editorInstanceRef} {...editor} />;
        const extraView = extraPane;
        if (extraPane) {
            return <SplitPane
                split="vertical"
                minSize={500}
                maxSize={-300}
                defaultSize="calc(100% - 300px)"
                primary="first"
                key="editor-split-pane"
                className="remove-default border"
                size={editorSize}
                onDragFinished={(newSize) => {
                    this.setState({
                        editorSize: newSize
                    })
                }}
            >
                {editorView}
                <div className="extra-view">
                    {extraView}
                </div>
            </SplitPane>
        } else {
            return editorView;
        }
    }

    render () {
        const { toolbar, console } = this.props;

        const { size } = this.state;

        const editorPane = this.renderEditorPane();

        return (
            <div className="ide-editor">
                {
                    toolbar && toolbar.enable
                        ? <div className="ide-header bd-bottom">
                            <ToolBar
                                {...toolbar}
                                changeTab={this.changeTab}
                            />
                        </div>
                        : ''
                }
                <div style={{ zIndex: 901 }} className="ide-content">
                    {console && console.data && console.data.log ? (
                        <SplitPane
                            split="horizontal"
                            minSize={100}
                            maxSize={-77}
                            defaultSize="60%"
                            primary="first"
                            key="ide-split-pane"
                            size={size}
                            onChange={(size) => {
                                this.setState({
                                    size: size
                                });
                            }}
                        >
                            {editorPane}
                            <Console
                                onConsoleTabChange={this.changeTab}
                                activedTab={this.state.changeTab}
                                setSplitMax={() => {
                                    this.setState({
                                        size: '100px'
                                    });
                                }}
                                setSplitMin={() => {
                                    this.setState({
                                        size: 'calc(100% - 40px)'
                                    });
                                }}
                                {...console}
                            />
                        </SplitPane>
                    ) : (
                        editorPane
                    )}
                </div>
            </div>
        );
    }
}

export default IDEEditor;
