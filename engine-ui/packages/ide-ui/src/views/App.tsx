import React from 'react';
import { Router } from 'react-router';
import Layout from '../layout/layout';
import routers from '../routers';
import { Provider } from 'react-redux';
import { ConfigProvider } from 'antd';

import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less';
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less';

import './registerMicroApps';
import '@/styles/App.css';
import 'ant-design-dtinsight-theme/theme/dt-theme/dark/index.less';
import store from '../store';

const packageName = require('../../package.json').name;

function App(props: any) {
    const { history } = props;

    return (
        <ConfigProvider
            prefixCls={packageName}
            getPopupContainer={(node) => {
                if (node) return document.body;
                return document.body!.querySelector(
                    'div[id="app"]'
                ) as HTMLElement;
            }}
        >
            <Layout history={history}>
                <Provider store={store}>
                    <Router routes={routers} history={history} />
                </Provider>
            </Layout>
        </ConfigProvider>
    );
}

export default App;
