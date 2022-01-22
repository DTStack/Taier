/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { Router } from 'react-router';
import Layout from '../layout/layout';
import routers from '../routers';
import { Provider } from 'react-redux';
import { ConfigProvider } from 'antd';

import '@/assets/iconfont/iconfont.css';
import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less';
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less';
import 'dt-react-component/lib/style/index.css';

import './registerMicroApps';
import '@/styles/App.css';
import '@/styles/theme.scss';
// TODO clean the task.scss file
import '@/styles/task/task.scss';

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
