import React, { Component } from 'react';
import PropTypes from 'prop-types';
import SplitPane from 'react-split-pane';
import './ide.scss';

import Editor from 'widgets/editor';
import ToolBar from './toolbar';
import Console from './console';

// import './style.scss';

const propType = {
    editor: PropTypes.object,
    toolbar: PropTypes.object,
    console: PropTypes.object
}

class IDEEditor extends Component {
    state = {
        changeTab: true,
        size: undefined
    };

    changeTab = state => {
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
        const editorView = <Editor editorInstanceRef={editorInstanceRef} {...editor} />;
        const extraView = extraPane;

        if (extraPane) {
            return <div className="editor-pane-box">
                <div className="editor-view">
                    {editorView}
                </div>
                <div className="extra-view">
                    {extraView}
                </div>
            </div>
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
                            size={size}
                            onDragStarted={() => {
                                this.setState({
                                    size: undefined
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

IDEEditor.propTypes = propType

export default IDEEditor;
