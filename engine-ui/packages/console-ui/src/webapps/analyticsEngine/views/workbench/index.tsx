import * as React from 'react';
import { Layout } from 'antd';
import SplitPane from 'react-split-pane';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import Sidebar from './sidebar';
import Default from './default';
import MainBench from './mainBench';
import CreateDBModal from './mainBench/database/create';
import CreateTableDDLModal from './mainBench/tableDetail/ddlModal';

import workbenchActions from '../../actions/workbenchActions';

const { Content } = Layout;

@(connect(
    (state: any) => {
        const { workbench, modal } = state;
        return {
            modal,
            mainBench: workbench.mainBench
        };
    },
    (dispatch: any) => {
        const actions = bindActionCreators(workbenchActions, dispatch);
        return actions;
    }
) as any)
class Workbench extends React.Component<any, any> {
    componentDidMount () {
        if (process.env.NODE_ENV === 'production') {
            window.addEventListener('beforeunload', this.beforeunload, false);
        }
    }

    componentWillUnmount () {
        window.removeEventListener('beforeunload', this.beforeunload, false);
    }

    render () {
        const {
            mainBench,
            onSQLQuery,
            onCreateTable
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
                        style={{ width: 'inherit', height: '100%', transition: 'none' }}
                    >
                        <Sidebar />
                    </div>
                    <Content style={{ height: '100%' }}>
                        {
                            mainBench.tabs.length ? <MainBench />
                                : <Default
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

    beforeunload = (e: any) => {
        // eslint-disable-next-line no-useless-escape
        const confirmationMessage = '\o/';
        (e || window.event).returnValue = confirmationMessage; // Gecko + IE
        return confirmationMessage; // Webkit, Safari, Chrome
    };
}

export default Workbench;
