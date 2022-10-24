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

import { createRoot } from 'react-dom/client';
import molecule from '@dtinsight/molecule';
import {
	CONSOLE,
	OPERATIONS,
	ID_COLLECTIONS,
	DRAWER_MENU_ENUM,
	RESOURCE_ACTIONS,
	FUNCTOIN_ACTIONS,
} from '@/constant';
import EditorEntry from '@/components/editorEntry';
import ResourceManager from '@/pages/resource';
import { history } from 'umi';
import classNames from 'classnames';
import FunctionManager from '@/pages/function';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import DataSource from '@/pages/dataSource';
import type { IActivityMenuItemProps, IExtension } from '@dtinsight/molecule/esm/model';
import { Float } from '@dtinsight/molecule/esm/model';
import { ColorThemeMode } from '@dtinsight/molecule/esm/model';
import LogEditor from '@/components/logEditor';
import http from '@/api/http';
import resourceManagerService from '@/services/resourceManagerService';
import functionManagerService from '@/services/functionManagerService';
import { showLoginModal } from '@/pages/login';
import { getCookie, deleteCookie } from '@/utils';
import { Button, message } from 'antd';
import { Logo } from '@/components/icon';
import Language from '@/components/language';
import AddTenantModal from '@/components/addTenantModal';

function loadStyles(url: string) {
	const link = document.createElement('link');
	link.rel = 'stylesheet';
	link.type = 'text/css';
	link.href = url;
	link.id = 'antd_dark';
	if (!document.getElementById('antd_dark')) {
		const head = document.getElementsByTagName('head')[0];
		head.appendChild(link);
	}
}

function removeStyles() {
	const darkStyle = document.querySelector('#antd_dark');
	darkStyle?.remove();
}

export default class InitializeExtension implements IExtension {
	id: UniqueId = 'initialize';
	name: string = 'initialize';
	activate(): void {
		initializeColorTheme();
		initializeEntry();
		initResourceManager();
		initFunctionManager();
		initializePane();
		// 默认不展示 Panel
		molecule.layout.togglePanelVisibility();
		initMenuBar();
		initLogin();
		initExplorer();
		initDataSource();
		initLanguage();
		initExpandCollapse();
	}
	dispose(): void {
		throw new Error('Method not implemented.');
	}
}

/**
 * 设置默认展开的左侧目录树项
 */
function initExpandCollapse() {
	const { SAMPLE_FOLDER_PANEL_ID, EDITOR_PANEL_ID } = molecule.builtin.getConstants();
	molecule.explorer.setExpandedPanels([EDITOR_PANEL_ID!, SAMPLE_FOLDER_PANEL_ID!]);
}

/**
 * 初始化主题
 */
function initializeColorTheme() {
	const defaultThemeId = localStorage.getItem(ID_COLLECTIONS.COLOR_THEME_ID);
	const defaultTheme = defaultThemeId && molecule.colorTheme.getThemeById(defaultThemeId);
	if (defaultTheme) {
		molecule.colorTheme.setTheme(defaultTheme.id);
	} else {
		// 默认加载 DtStack 主题色
		molecule.colorTheme.setTheme('DTStack Theme');
	}

	const currentThemeMode = molecule.colorTheme.getColorThemeMode();
	if (currentThemeMode === ColorThemeMode.dark) {
		loadStyles('/assets/antd.dark.css');
	}
	document.documentElement.setAttribute('data-prefers-color', currentThemeMode);

	molecule.colorTheme.onChange((_, nextTheme, themeMode) => {
		localStorage.setItem(ID_COLLECTIONS.COLOR_THEME_ID, nextTheme.id);
		document.documentElement.setAttribute('data-prefers-color', themeMode);

		if (themeMode === ColorThemeMode.dark) {
			loadStyles('/assets/antd.dark.css');
		} else {
			removeStyles();
		}
	});
}

/**
 * 初始化入口页
 */
function initializeEntry() {
	molecule.editor.setEntry(<EditorEntry />);

	const handleGoto = (url: string) => {
		history.push({
			query: {
				drawer: url,
			},
		});
	};

	// 设置目录树的入口页面
	molecule.folderTree.setEntry(
		<div className={classNames('mt-20px', 'text-center', 'text-xs')}>
			未找到任务开发目录，请先
			<Button
				style={{ padding: 0 }}
				type="link"
				onClick={() => handleGoto(DRAWER_MENU_ENUM.CLUSTER)}
			>
				配置集群
			</Button>
			并进行
			<Button
				style={{ padding: 0 }}
				type="link"
				onClick={() => handleGoto(DRAWER_MENU_ENUM.RESOURCE)}
			>
				绑定
			</Button>
		</div>,
	);

	// 设置资源管理的入口页面
	resourceManagerService.setEntry(
		<div className={classNames('mt-20px', 'text-center', 'text-xs')}>
			未找到资源开发目录，请先
			<Button
				style={{ padding: 0 }}
				type="link"
				onClick={() => handleGoto(DRAWER_MENU_ENUM.CLUSTER)}
			>
				配置集群
			</Button>
			并进行
			<Button
				style={{ padding: 0 }}
				type="link"
				onClick={() => handleGoto(DRAWER_MENU_ENUM.RESOURCE)}
			>
				绑定
			</Button>
		</div>,
	);

	// 设置函数管理的入口页面
	functionManagerService.setEntry(
		<div className={classNames('mt-20px', 'text-center', 'text-xs')}>
			未找到函数管理目录，请先
			<Button
				style={{ padding: 0 }}
				type="link"
				onClick={() => handleGoto(DRAWER_MENU_ENUM.CLUSTER)}
			>
				配置集群
			</Button>
			并进行
			<Button
				style={{ padding: 0 }}
				type="link"
				onClick={() => handleGoto(DRAWER_MENU_ENUM.RESOURCE)}
			>
				绑定
			</Button>
		</div>,
	);
}

/**
 * 初始化资源管理界面
 */
function initResourceManager() {
	const resourceManager = {
		id: 'ResourceManager',
		icon: 'package',
		name: '资源管理',
		title: '资源管理',
	};

	const headerToolBar: any[] = [
		{
			id: 'refresh',
			title: '刷新',
			icon: 'refresh',
		},
		{
			id: 'menus',
			title: '更多操作',
			icon: 'menu',
			contextMenu: [
				RESOURCE_ACTIONS.UPLOAD,
				RESOURCE_ACTIONS.REPLACE,
				RESOURCE_ACTIONS.CREATE,
			],
		},
	];

	molecule.activityBar.add(resourceManager);
	molecule.sidebar.add({
		id: resourceManager.id,
		title: resourceManager.name,
		render: () => <ResourceManager panel={resourceManager} headerToolBar={headerToolBar} />,
	});
}

/**
 * 初始化函数管理界面
 */
function initFunctionManager() {
	const functionManager = {
		id: 'FunctionManager',
		icon: 'variable-group',
		name: '函数管理',
		title: '函数管理',
	};
	const { CONTEXT_MENU_SEARCH } = molecule.builtin.getConstants();

	molecule.activityBar.remove([CONTEXT_MENU_SEARCH!]);

	const headerToolBar = [
		{
			id: 'refresh',
			title: '刷新',
			icon: 'refresh',
		},
		{
			id: 'menus',
			title: '更多操作',
			icon: 'menu',
			contextMenu: [FUNCTOIN_ACTIONS.CREATE_FUNCTION],
		},
	];

	molecule.activityBar.add(functionManager);
	molecule.sidebar.add({
		id: functionManager.id,
		title: functionManager.name,
		render: () => <FunctionManager panel={functionManager} headerToolBar={headerToolBar} />,
	});
}

/**
 * 初始化 Pane 界面
 */
function initializePane() {
	molecule.panel.add({
		id: ID_COLLECTIONS.OUTPUT_LOG_ID,
		name: '日志',
		closable: false,
		renderPane: () => <LogEditor />,
	});
	molecule.panel.setActive(ID_COLLECTIONS.OUTPUT_LOG_ID);
}

/**
 * 初始化 MenuBar
 */
function initMenuBar() {
	molecule.menuBar.setState({
		logo: <Logo />,
	});
	molecule.layout.setMenuBarMode('horizontal');
	const state = molecule.menuBar.getState();
	const nextData = state.data.concat();
	nextData.splice(1, 0, {
		id: 'operation',
		name: '运维中心',
		data: [...OPERATIONS],
	});
	nextData.splice(2, 0, {
		id: 'console',
		name: '控制台',
		data: [...CONSOLE],
	});
	const menuRunning = nextData.findIndex((menu) => menu.id === 'Run');
	if (menuRunning > -1) {
		nextData.splice(menuRunning, 1);
	}
	molecule.menuBar.setState({
		data: nextData,
	});
}

function updateAccountContext(contextMenu: IActivityMenuItemProps[]) {
	const nextData = molecule.activityBar.getState().data || [];
	const { ACTIVITY_BAR_GLOBAL_ACCOUNT } = molecule.builtin.getConstants();
	const target = nextData.find((item) => item.id === ACTIVITY_BAR_GLOBAL_ACCOUNT);
	if (target) {
		target.contextMenu = contextMenu;
	}
	molecule.activityBar.setState({ data: nextData });
}

/**
 * 初始化登录
 */
function initLogin() {
	const userName = getCookie('username');
	const tenantName = getCookie('tenant_name') || 'Unknown';
	updateAccountContext(
		userName
			? [
					{
						id: 'username',
						disabled: !!userName,
						icon: 'person',
						name: userName,
					},
					{
						id: 'divider',
						type: 'divider',
					},
					{
						id: 'tenant-change',
						icon: 'feedback',
						name: tenantName,
						onClick: () => showLoginModal(),
					},
					{
						id: ID_COLLECTIONS.ADD_TENANT,
						name: '新增租户',
						icon: 'person-add',
						onClick: () => {
							const node = document.createElement('div');
							node.id = 'add-tenant-modal';
							document.getElementById('molecule')!.appendChild(node);
							const root = createRoot(node);
							root.render(<AddTenantModal />);
						},
					},
					{
						id: 'logout',
						icon: 'log-out',
						name: '登出',
						onClick: () => {
							http.post('/taier/api/user/logout')
								.then((res) => {
									if (!res.data) {
										return message.error('登出失败');
									}
									// clear login infos in cookie
									deleteCookie('userId');
									deleteCookie('username');
									deleteCookie('tenantId');
									deleteCookie('tenant_name');
									window.location.reload();
								})
								.catch(() => {
									message.error('登出失败');
								});
						},
					},
			  ]
			: [
					{
						id: 'login',
						name: '去登录',
						icon: 'log-in',
						onClick: () => showLoginModal(),
					},
			  ],
	);

	molecule.statusBar.add(
		{
			sortIndex: 0,
			id: 'login',
			name: userName || '未登录',
			onClick: () => {
				if (!userName) {
					showLoginModal();
				}
			},
		},
		molecule.model.Float.left,
	);
}

/**
 * 初始化任务属性
 */
function initExplorer() {
	// 优化右键菜单
	const explorerData = molecule.explorer.getState().data?.concat() || [];
	const { SAMPLE_FOLDER_PANEL_ID } = molecule.builtin.getConstants();
	const folderTreePane = explorerData.find((item) => item.id === SAMPLE_FOLDER_PANEL_ID);
	if (folderTreePane?.toolbar) {
		folderTreePane.toolbar[0].title = '新建任务';
		molecule.explorer.setState({
			data: explorerData,
		});
	}

	molecule.explorer.onCollapseAllFolders(() => {
		molecule.folderTree.setExpandKeys([]);
	});
}

/**
 * 初始化数据源
 */
function initDataSource() {
	const dataSource = {
		id: 'dataSource',
		sortIndex: -1,
		icon: 'database',
		name: '数据源',
		title: '数据源',
	};

	molecule.activityBar.add(dataSource);
	molecule.sidebar.add({
		id: dataSource.id,
		title: dataSource.name,
		render: () => <DataSource />,
	});
}

/**
 * 初始化状态栏语言
 */
function initLanguage() {
	molecule.statusBar.add(
		{
			id: ID_COLLECTIONS.LANGUAGE_STATUS_BAR,
			render: () => <Language />,
		},
		Float.right,
	);
}
