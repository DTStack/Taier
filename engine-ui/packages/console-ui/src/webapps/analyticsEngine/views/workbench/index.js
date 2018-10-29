import React, { Component } from 'react';
import { Layout } from "antd";
import SplitPane from "react-split-pane";
import { connect } from "react-redux";
import { bindActionCreators } from 'redux';

import Sidebar from './sidebar';
import Default from './default';
import MainBench from "./mainBench";

import workbenchActions from '../../actions/workbenchActions';
import commActions from "../../actions";

const { Content } = Layout;

@connect(
    state => {
        const { workbench } = state;
        return {
            mainBench: workbench.mainBench,
        };
    },
    dispatch => {
        const actionsOne = bindActionCreators(workbenchActions, dispatch);
        const actionsTow = bindActionCreators(commActions, dispatch);
        return Object.assign(actionsOne, actionsTow);
    }
)
class Workbench extends Component {

    componentDidMount() {
        if (process.env.NODE_ENV === 'production') {
            window.addEventListener('beforeunload', this.beforeunload, false);
        }
        // 预加载所有表
        this.props.getAllTable();
    }

    componentWillUnmount() {
        window.removeEventListener("beforeunload", this.beforeunload, false);
    }

    render() {

        const {
            mainBench,
            onSQLQuery,
            onCreateTable,
        } = this.props;

        return (
            <Layout>
                <SplitPane
                    split="vertical"
                    minSize={240}
                    maxSize="80%"
                    defaultSize={240}
                    primary="first"
                >
                    <div
                        className="ant-layout-sider bd-right"
                        style={{ width: "inherit", height: '100%' }}
                    >
                        <Sidebar />
                    </div>
                    <Content style={{height: '100%'}}>
                        {
                            mainBench.tabs.length ? <MainBench /> :
                            <Default 
                                onSQLQuery={onSQLQuery}
                                onCreateTable={onCreateTable}
                            />
                        }
                    </Content>
                </SplitPane>
            </Layout>
        );
    }

    beforeunload = e => {
        /* eslint-disable */
        const confirmationMessage = "\o/";
        (e || window.event).returnValue = confirmationMessage; // Gecko + IE
        return confirmationMessage; // Webkit, Safari, Chrome
        /* eslint-disable */
    };

}

export default Workbench;