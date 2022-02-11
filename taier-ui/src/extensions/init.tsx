import molecule from '@dtinsight/molecule';
import { CONSOLE, folderMenu, OPERATIONS, OUTPUT_LOG } from '@/constant';
import EditorEntry from '@/components/editorEntry';
import ResourceManager from '@/components/resourceManager';
import classNames from 'classnames';
import FunctionManager from '@/components/functionManager';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import DataSource from '@/pages/dataSource';
import type { IActivityMenuItemProps, IExtension } from '@dtinsight/molecule/esm/model';
import { FUNCTION_NEW_FUNCTION } from '@/components/functionManager/menu';
import Markdown from '@/components/markdown';
import http from '@/api/http';
import resourceManagerService from '@/services/resourceManagerService';
import functionManagerService from '@/services/functionManagerService';
import { showLoginModal } from '@/pages/login';
import { getCookie, deleteCookie } from '@/utils';
import { message } from 'antd';

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
	}
	dispose(): void {
		throw new Error('Method not implemented.');
	}
}

/**
 * 初始化主题
 */
function initializeColorTheme() {
	// 默认主题为亮色
	molecule.colorTheme.setTheme('Default Light+');
}

/**
 * 初始化入口页
 */
function initializeEntry() {
	molecule.editor.setEntry(<EditorEntry />);

	// 设置目录树的入口页面
	molecule.folderTree.setEntry(
		<div className={classNames('mt-20px', 'text-center')}>
			未找到任务开发目录，请联系管理员
		</div>,
	);

	// 设置资源管理的入口页面
	resourceManagerService.setEntry(
		<div className={classNames('mt-20px', 'text-center')}>
			未找到资源开发目录，请联系管理员
		</div>,
	);

	// 设置函数管理的入口页面
	functionManagerService.setEntry(
		<div className={classNames('mt-20px', 'text-center')}>
			未找到函数开发目录，请联系管理员
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
			contextMenu: folderMenu,
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
			contextMenu: [FUNCTION_NEW_FUNCTION],
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
	const { PANEL_OUTPUT } = molecule.builtin.getConstants();

	molecule.panel.remove(PANEL_OUTPUT!);
	molecule.panel.add({
		id: OUTPUT_LOG,
		name: '日志',
		closable: false,
		renderPane: () => <Markdown />,
	});
	molecule.panel.setActive(OUTPUT_LOG);
}

/**
 * 初始化 MenuBar
 */
function initMenuBar() {
	const state = molecule.menuBar.getState();
	const nextData = state.data.concat();
	molecule.layout.setMenuBarMode('horizontal');
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
	const menuRunning = nextData.findIndex(menu => menu.id === 'Run');
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
	const usename = getCookie('username');
	const tenantName = getCookie('tenant_name') || 'Unknown';
	updateAccountContext(
		usename
			? [
					{
						id: 'username',
						disabled: !!usename,
						name: usename,
					},
					{
						id: 'divider',
						type: 'divider',
					},
					{
						id: 'tenant-change',
						name: tenantName,
						onClick: () => showLoginModal(),
					},
					{
						id: 'logout',
						name: '登出',
						onClick: () => {
							http.post('/node/user/logout')
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
						onClick: () => showLoginModal(),
					},
			  ],
	);

	molecule.statusBar.add(
		{
			sortIndex: 0,
			id: 'login',
			name: usename || '未登录',
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
