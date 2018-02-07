import React, { Component } from 'react'
import SplitPane from 'react-split-pane'
import { Layout } from 'antd'

import Sidebar from './sidebar'
import GraphEditor from './graphEditor'

const { Content } = Layout

export default class TaskFlow extends Component {
    render() {
        return (
            <Layout className="graph-content">
               <SplitPane
                split="vertical"
                minSize={300}
                maxSize="80%"
                defaultSize="300"
                primary="first"
               >
                    <Sidebar {...this.props}/>
                    <Content>
                        <GraphEditor/>
                    </Content>
                </SplitPane>
            </Layout>
        )
    }
}
