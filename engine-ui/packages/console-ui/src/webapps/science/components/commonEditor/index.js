import React, { Component } from 'react';
import PropTypes from 'prop-types';
import SplitPane from 'react-split-pane';
import './ide.scss';
import 'handsontable/dist/handsontable.full.css';

import ToolBar from './toolbar';
import Console from './console';
import SiderBar from './siderbar';

const propType = {
    editor: PropTypes.object,
    toolbar: PropTypes.object,
    console: PropTypes.object
}

class CommonEditor extends Component {
    state = {
        changeTab: true,
        size: undefined,
        editorSize: undefined
    };
    componentDidMount () {
        this.props.SiderBarRef(this.SiderBar);
    }

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
        const { extraPane, children } = this.props;
        const { editorSize } = this.state;
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
                {children}
                <div className="extra-view">
                    {extraView}
                </div>
            </SplitPane>
        } else {
            return children;
        }
    }

    render () {
        const { toolbar, console, siderBarItems } = this.props;

        const { size } = this.state;

        const editorPane = this.renderEditorPane();

        return (
            <div className='c-panel'>
                <div className='c-panel__content'>
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
                            {console && console.data && console.data.length ? (
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
                </div>
                <div className='c-panel__siderbar'>
                    <SiderBar onRef={(siderBar) => { this.SiderBar = siderBar }}>
                        {siderBarItems}
                    </SiderBar>
                </div>
            </div>
        );
    }
}

CommonEditor.propTypes = propType

export default CommonEditor;
