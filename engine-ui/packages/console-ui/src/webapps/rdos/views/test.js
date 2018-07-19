import React, { Component } from 'react'
// import SplitPane from 'react-split-pane'

// import {
//     Form, Input, TreeSelect, Modal, Tree
// } from 'antd'

// import GraphEditor from '../components/graph-editor'
import Editor from 'widgets/editor';

// const TreeNode = TreeSelect.TreeNode;

export default class Test extends Component {

    onEditorChange = (value) => {
        console.log('onEditorChange:', value);
    }

    render () {
        return (
            <div style={{height: '100%', width: '100%'}}>
                <Editor 
                    onChange={this.onEditorChange}
                    language="sql"
                />
            </div>
        )
    }

    // render() {
    //     return (
    //         <SplitPane split="vertical" minSize={300} maxSize="80%" defaultSize="60%" primary="first">
    //             <div className="leftSidebar">
    //                 <div style={{height: '100%'}}>
    //                     <Editor 
    //                         onChange={this.onEditorChange}
    //                         language="sql"
    //                     />
    //                 </div>
    //             </div>
    //             <SplitPane split="horizontal">
    //                 <div>
    //                     <h1>Right-Top</h1>
    //                 </div>
    //                 <div>
    //                     {/* <GraphEditor /> */}
    //                 </div>
    //             </SplitPane>
    //         </SplitPane>
    //     )
    // }
}

