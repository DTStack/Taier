import React from 'react';
import { Router } from 'react-router';
import Layout from '../layout/layout';
import routers from '../routers';
import { Provider } from 'react-redux';
import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less';
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less';

import './registerMicroApps';
import '@/styles/App.css';
import 'ant-design-dtinsight-theme/theme/dt-theme/dark/index.less';
import store from '../store';

function App(props: any) {
    const { history } = props;

    return (
        <Layout history={history}>
            <Provider store={store}>
                <Router routes={routers} history={history} />
            </Provider>
        </Layout>
    );
}

export default App;
