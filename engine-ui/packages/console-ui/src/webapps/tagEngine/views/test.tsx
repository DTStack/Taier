/* eslint-disable */
import * as React from 'react'


import GraphEditor from '../components/graph-editor'
import Editor from 'widgets/editor';

// const TreeNode = TreeSelect.TreeNode;

export default class Test extends React.Component<any, any> {
    onEditorChange = (value: any) => {
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
