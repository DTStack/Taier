import React, { Component } from 'react';
import { Layout } from "antd";
import SplitPane from "react-split-pane";
import { connect } from "react-redux";
import { bindActionCreators } from 'redux';

import Sidebar from './sidebar';
import Default from './default';
import MainBench from "./mainBench";
import CreateDBModal from './mainBench/database/create';
import CreateTableDDLModal from './mainBench/tableDetail/ddlModal';


import workbenchActions from '../../actions/workbenchActions';

const { Content } = Layout;

@connect(
    state => {
        const { workbench, modal } = state;
        return {
            modal,
            mainBench: workbench.mainBench,
        };
    },
    dispatch => {
        const actions = bindActionCreators(workbenchActions, dispatch);
        return actions;
    }
)
class Workbench extends Component {

    componentDidMount() {
        if (process.env.NODE_ENV === 'production') {
            window.addEventListener('beforeunload', this.beforeunload, false);
        }
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
                        <CreateDBModal {...this.props} />
                        <CreateTableDDLModal {...this.props} />
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