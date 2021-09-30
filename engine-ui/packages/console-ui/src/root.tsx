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

import * as React from 'react';
import { Router } from 'react-router';
import { Provider } from 'react-redux';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';

// 继承主应用
import 'ant-design-dtinsight-theme/theme/dt-theme/reset.less';
import 'ant-design-dtinsight-theme/theme/dt-theme/index.less';

// Styles
import './styles/main.scss';

// dt-react-component style
import 'dt-react-component/lib/style/index.css';

import routers from './routers';

export default class Root extends React.Component<any, any> {
	render() {
		const { store, history } = this.props;
		return (
			<ConfigProvider
				locale={zhCN}
				getPopupContainer={() => document.body.querySelector('div[id="app"]')}
			>
				<Provider store={store}>
					<Router
						routes={routers}
						history={history}
						key={Math.random()}
						{...{
							onEnter: () => {
								console.log('enter');
							},
						}}
					/>
				</Provider>
			</ConfigProvider>
		);
	}
}
