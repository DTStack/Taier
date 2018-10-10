import React, { Component } from 'react';
import PropTypes from "prop-types";
import { Layout } from "antd";
import SplitPane from "react-split-pane";
import { connect } from "react-redux";


import Sidebar from './sidebar';

const { Content } = Layout;

export default class Workbench extends Component {

    componentDidMount() {
        if (process.env.NODE_ENV === 'production') {
            window.addEventListener('beforeunload', this.beforeunload, false);
        }
    }

    componentWillUnmount() {
        window.removeEventListener("beforeunload", this.beforeunload, false);
    }

    beforeunload = e => {
        /* eslint-disable */
        const confirmationMessage = "\o/";
        (e || window.event).returnValue = confirmationMessage; // Gecko + IE
        return confirmationMessage; // Webkit, Safari, Chrome
        /* eslint-disable */
    };

    render() {
        const { children } = this.props;
        return (
            <Layout className="dt-dev-task">
                <SplitPane
                    split="vertical"
                    minSize={240}
                    maxSize="80%"
                    defaultSize={240}
                    primary="first"
                >
                    <div
                        className="ant-layout-sider"
                        style={{ width: "inherit", height: '100%' }}
                    >
                        <Sidebar />
                    </div>
                    <Content style={{height: '100%'}}>
                        {children || "i'm container."}
                    </Content>
                </SplitPane>
            </Layout>
        );
    }
}