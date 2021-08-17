import React from 'react';
import { Router } from 'react-router';
import Layout from '../layout/layout';
import routers from '../routers';
import { Provider } from 'react-redux';
import { getStore } from './common/utils';
import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less';
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less';

import './registerMicroApps';
import '@/styles/App.css';
import 'ant-design-dtinsight-theme/theme/dt-theme/dark/index.less';

function App(props: any) {
    const { history } = props;
    const rootReducer = require('../controller').default;
    const { store } = getStore(rootReducer, 'hash');

    return (
        <Layout history={history}>
            <Provider store={store}>
                <Router routes={routers} history={history} />
            </Provider>
        </Layout>
    );
}

export default App;
