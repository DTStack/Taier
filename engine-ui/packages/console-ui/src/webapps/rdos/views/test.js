/* eslint-disable */
import React, { Component } from 'react'
import SplitPane from 'react-split-pane'

import {
    Form, Input, TreeSelect, Modal, Tree, Row
} from 'antd'

import GraphEditor from '../components/graph-editor'
import Editor from 'widgets/editor';

// const TreeNode = TreeSelect.TreeNode;

export default class Test extends Component {
    onEditorChange = (value) => {
        console.log('onEditorChange:', value);
    }

    // render () {
    //     return (
    //         <div style={{height: '100%', width: '100%'}}>
    //             <Row style={{height: '50%', width: '100%'}}>
    //                 <Editor
    //                     key="sql"
    //                     onChange={this.onEditorChange}
    //                     language="sql"
    //                 />
    //             </Row>
    //             <hr />
    //             <Row style={{height: '50%', width: '100%'}}>
    //                 <Editor
    //                     key="python"
    //                     onChange={this.onEditorChange}
    //                     language="python"
    //                 />
    //             </Row>
    //         </div>
    //     )
    // }

    // render() {
    //     return (
    //         <SplitPane split="vertical" minSize={300} maxSize="80%" defaultSize="60%" primary="first">
    //             {/* <div className="leftSidebar">
    //                 <div style={{height: '100%'}}>
    //                     <Editor
    //                         onChange={this.onEditorChange}
    //                         language="sql"
    //                     />
    //                 </div>
    //             </div> */}
    //             <SplitPane split="horizontal">
    //                 <div>
    //                     <h1>Right-Top</h1>
    //                 </div>
    //                 <div>
    //                     <GraphEditor />
    //                 </div>
    //             </SplitPane>
    //         </SplitPane>
    //     )
    // }

    render () {
        return (
            <div style={{ height: '100%', width: '100%' }}>
                <GraphEditor />
            </div>
        )
    }
}
/* eslint-disable */
