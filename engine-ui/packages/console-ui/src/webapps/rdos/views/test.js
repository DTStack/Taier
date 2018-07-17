import React, { Component } from 'react'
import SplitPane from 'react-split-pane'

import {
    Form, Input, TreeSelect, Modal, Tree
} from 'antd'

import GraphEditor from '../components/graph-editor'
import Editor from 'widgets/editor';

const TreeNode = TreeSelect.TreeNode;

export default class Test extends Component {

    onEditorChange = (value) => {
        console.log('onEditorChange:', value);
    }

    render() {
        return (
            <SplitPane split="vertical" minSize={300} maxSize="80%" defaultSize="60%" primary="first">
                <div className="leftSidebar">
                    <h1>Left.</h1>
                    <div style={{height: '100%'}}>
                        <Editor 
                            onChange={this.onEditorChange}
                            language="sql"
                        />
                    </div>
                </div>
                <SplitPane split="horizontal">
                    <div>
                        <h1>Right-Top</h1>
                    </div>
                    <div>
                    </div>
                </SplitPane>
            </SplitPane>
        )
    }
}

