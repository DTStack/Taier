import * as React from 'react';
import { Layout } from 'antd';
import SplitPane from 'react-split-pane';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import '../../styles/views/workbench/index.scss'

import Sidebar from './sidebar';
import MainBench from './mainBench';
import NewNotebookModal from '../../components/newNotebookModal';
import NewExperimentModal from '../../components/newExperimentModal';

import workbenchActions from '../../actions/workbenchActions';
import { siderBarType } from '../../consts'

const { Content } = Layout;

@connect(
    (state: any) => {
        const { modal, common } = state;
        return {
            modal,
            siderBarKey: common.siderBarKey
        };
    },
    (dispatch: any) => {
        const actions = bindActionCreators(workbenchActions, dispatch);
        return actions;
    }
)
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
        const { siderBarKey } = this.props;
        return (
            <Layout>
                <SplitPane
                    split="vertical"
                    minSize={siderBarKey == siderBarType.model ? 32 : 240}
                    maxSize={siderBarKey == siderBarType.model ? 32 : '80%'}
                    defaultSize={siderBarKey == siderBarType.model ? 32 : 240}
                    primary="first"
                >
                    <div
                        className="ant-layout-sider bd-right"
                        style={{ width: 'inherit', height: '100%', transition: 'none' }}
                    >
                        <Sidebar />
                    </div>
                    <Content style={{ height: '100%', overflow: 'hidden' }}>
                        <MainBench />
                    </Content>
                </SplitPane>
                <NewNotebookModal />
                <NewExperimentModal />
            </Layout>
        );
    }

    beforeunload = (e: any) => {
        /* eslint-disable */
        const confirmationMessage = "\o/";
        (e || window.event).returnValue = confirmationMessage; // Gecko + IE
        return confirmationMessage; // Webkit, Safari, Chrome
        /* eslint-disable */
    };

}

export default Workbench;