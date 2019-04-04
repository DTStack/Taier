import React, { Component } from 'react';
import { Layout } from 'antd';
import SplitPane from 'react-split-pane';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import '../../styles/views/workbench/index.scss'

import Sidebar from './sidebar/container';
import MainBench from './mainBench';

import workbenchActions from '../../actions/workbenchActions';

const { Content } = Layout;

@connect(
    state => {
        const { modal } = state;
        return {
            modal
        };
    },
    dispatch => {
        const actions = bindActionCreators(workbenchActions, dispatch);
        return actions;
    }
)
class Workbench extends Component {
    componentDidMount () {
        if (process.env.NODE_ENV === 'production') {
            window.addEventListener('beforeunload', this.beforeunload, false);
        }
    }

    componentWillUnmount () {
        window.removeEventListener('beforeunload', this.beforeunload, false);
    }

    render () {
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
                        style={{ width: 'inherit', height: '100%' }}
                    >
                        <Sidebar />
                    </div>
                    <Content style={{ height: '100%' }}>
                        <MainBench />
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