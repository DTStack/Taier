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

import './public-path';

import React from 'react';
import ReactDOM from 'react-dom';
import { getStore } from './reducers/store';
import Root from './root';
import 'assets/styles/index.less';

const render = (props: any) => {
	const rootReducer = require('./reducers').default;
	const { store, history } = getStore(rootReducer, 'hash');

	ReactDOM.render(
		<Root store={store} history={history} />,
		props.container ? props.container.querySelector('#app') : document.getElementById('app'),
	);
};

if (!(window as any).__POWERED_BY_QIANKUN__) {
	render({});
}

if ((module as any).hot) {
	(module as any).hot.accept(['./root'], () => {
		const newRoot = require('./root').default;
		render(newRoot);
	});
}

/**
 * bootstrap 只会在微应用初始化的时候调用一次，下次微应用重新进入时会直接调用 mount 钩子，不会再重复触发 bootstrap。
 * 通常我们可以在这里做一些全局变量的初始化，比如不会在 unmount 阶段被销毁的应用级别的缓存等。
 */
export async function bootstrap() {
	console.log('data-source app bootstraped');
}

/**
 * 应用每次进入都会调用 mount 方法，通常我们在这里触发应用的渲染方法
 */
export async function mount(props) {
	console.log('data-source app mount');
	render(props);
}

/**
 * 应用每次 切出/卸载 会调用的方法，通常在这里我们会卸载微应用的应用实例
 */
export async function unmount(props) {
	ReactDOM.unmountComponentAtNode(
		props.container ? props.container.querySelector('#app') : document.getElementById('app'),
	);
}

/**
 * 可选生命周期钩子，仅使用 loadMicroApp 方式加载微应用时生效
 */
export async function update(props) {
	console.log('data-source update props', props);
}
