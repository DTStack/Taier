import { useState, useEffect, useLayoutEffect, useRef } from 'react';
import type { IPersonLists } from '@/context';
import Context from '@/context';
import { history } from 'umi';
import { extensions } from '@/extensions';
import molecule, { MoleculeProvider } from '@dtinsight/molecule';
import Workbench from './workbench';
import API from '@/api/operation';
import Task from '@/pages/operation/task';
import Schedule from '@/pages/operation/schedule';
import Patch from '@/pages/operation/patch';
import Layout from '@/layout';
import { updateDrawer } from '@/components/customDrawer';
import { Breadcrumb, Button } from 'antd';
import PatchDetail from './operation/patch/detail';
import Login from './login';
import CustomDrawer from '@/components/customDrawer';
import { CONSOLE, DRAWER_MENU_ENUM, OPERATIONS } from '@/constant';
import QueueManage from './console/queue';
import TaskDetail from './console/taskDetail';
import ResourceManage from './console/resource';
import ClusterManage from './console/cluster';
import EditCluster from './console/cluster/newEdit';
import { getCookie } from '@/utils';
import { isViewMode } from './console/cluster/newEdit/help';
import { ColorThemeMode } from '@dtinsight/molecule/esm/model';
import 'antd/dist/antd.less';
import '@dtinsight/molecule/esm/style/mo.css';
import '@ant-design/compatible/assets/index.css';
import './index.scss';

function loadStyles(url: string) {
	const link = document.createElement('link');
	link.rel = 'stylesheet';
	link.type = 'text/css';
	link.href = url;
	const head = document.getElementsByTagName('head')[0];
	head.appendChild(link);
}

export default function HomePage() {
	const [personList, setPersonList] = useState<IPersonLists[]>([]);
	const [username, setUsername] = useState<string | undefined>(undefined);
	const loading = useRef(false);
	const refs = useRef<any>(null);

	const checkLoginStatus = () => {
		const usernameInCookie = getCookie('username');
		// 当 username 存在，仅表示前端确认登录状态
		if (usernameInCookie) {
			setUsername(usernameInCookie);
		}
	};

	const handleTestConnects = async () => {
		refs.current.testConnects(undefined, (bool: boolean) => {
			loading.current = bool;
			updateDrawer({
				id: 'root',
				extra: renderClusterExtra(),
				update: true,
			});
		});
	};

	const renderClusterExtra = () => {
		const { mode = 'view' } = history.location.query || {};
		return isViewMode(mode as string) ? (
			<Button
				type="primary"
				onClick={() => {
					history.push({
						query: {
							...history.location.query,
							mode: 'edit',
						},
					});
				}}
			>
				编辑
			</Button>
		) : (
			<>
				<Button
					loading={loading.current}
					style={{ marginRight: 12 }}
					onClick={handleTestConnects}
				>
					测试所有组件连通性
				</Button>
				<Button type="primary" onClick={() => refs.current.handleComplete()}>
					完成
				</Button>
			</>
		);
	};

	useEffect(() => {
		API.getPersonInCharge().then((res) => {
			if (res.code === 1) {
				setPersonList(res.data ?? []);
			}
		});

		checkLoginStatus();
	}, []);

	const openDrawer = (drawerId: string) => {
		switch (drawerId) {
			case DRAWER_MENU_ENUM.TASK:
			case DRAWER_MENU_ENUM.SCHEDULE:
			case DRAWER_MENU_ENUM.PATCH:
				updateDrawer({
					id: 'root',
					visible: true,
					title: (
						<Breadcrumb>
							<Breadcrumb.Item>
								{OPERATIONS.find((i) => i.id === drawerId)?.name || 'Default'}
							</Breadcrumb.Item>
						</Breadcrumb>
					),
					renderContent: () => {
						const children = (() => {
							switch (drawerId) {
								case DRAWER_MENU_ENUM.TASK:
									return <Task />;
								case DRAWER_MENU_ENUM.SCHEDULE:
									return <Schedule />;
								case DRAWER_MENU_ENUM.PATCH:
									return <Patch />;
								default:
									return <div>404</div>;
							}
						})();
						return <Layout>{children}</Layout>;
					},
				});
				break;
			case DRAWER_MENU_ENUM.PATCH_DETAIL: {
				updateDrawer({
					id: 'root',
					visible: true,
					title: (
						<Breadcrumb>
							<Breadcrumb.Item>
								<a
									onClick={() => {
										history.push({
											query: {
												drawer: DRAWER_MENU_ENUM.PATCH,
											},
										});
									}}
								>
									补数据实例
								</a>
							</Breadcrumb.Item>
							<Breadcrumb.Item>
								{JSON.parse(sessionStorage.getItem('task-patch-data') || '{}').name}
							</Breadcrumb.Item>
						</Breadcrumb>
					),
					renderContent: () => {
						return (
							<Layout>
								<PatchDetail />
							</Layout>
						);
					},
				});
				break;
			}
			case DRAWER_MENU_ENUM.QUEUE:
			case DRAWER_MENU_ENUM.RESOURCE:
			case DRAWER_MENU_ENUM.CLUSTER: {
				updateDrawer({
					id: 'root',
					visible: true,
					title: (
						<Breadcrumb>
							<Breadcrumb.Item>
								{CONSOLE.find((i) => i.id === drawerId)?.name || 'Default'}
							</Breadcrumb.Item>
						</Breadcrumb>
					),
					renderContent: () => {
						const children = (() => {
							switch (drawerId) {
								case DRAWER_MENU_ENUM.QUEUE:
									return <QueueManage />;
								case DRAWER_MENU_ENUM.RESOURCE:
									return <ResourceManage />;
								case DRAWER_MENU_ENUM.CLUSTER:
									return <ClusterManage />;
								default:
									return <div>404</div>;
							}
						})();
						return <Layout>{children}</Layout>;
					},
				});
				break;
			}
			case DRAWER_MENU_ENUM.QUEUE_DETAIL: {
				const { jobResource, clusterName } = history.location.query || {};
				updateDrawer({
					id: 'root',
					visible: true,
					title: (
						<Breadcrumb>
							<Breadcrumb.Item>
								<a
									onClick={() => {
										history.push({
											query: {
												drawer: DRAWER_MENU_ENUM.QUEUE,
											},
										});
									}}
								>
									队列管理
								</a>
							</Breadcrumb.Item>
							<Breadcrumb.Item>{jobResource || '-'}</Breadcrumb.Item>
						</Breadcrumb>
					),
					extra: <span>集群：{clusterName || '-'}</span>,
					renderContent: () => {
						return (
							<Layout>
								<TaskDetail />
							</Layout>
						);
					},
				});
				break;
			}
			case DRAWER_MENU_ENUM.CLUSTER_DETAIL: {
				const { clusterName } = history.location.query || {};
				updateDrawer({
					id: 'root',
					visible: true,
					extra: renderClusterExtra(),
					title: (
						<Breadcrumb>
							<Breadcrumb.Item>
								<a
									onClick={() => {
										history.push({
											query: {
												drawer: DRAWER_MENU_ENUM.CLUSTER,
											},
										});
									}}
								>
									多集群管理
								</a>
							</Breadcrumb.Item>
							<Breadcrumb.Item>{clusterName || '-'}</Breadcrumb.Item>
						</Breadcrumb>
					),
					renderContent: () => {
						return (
							<Layout>
								<EditCluster ref={refs} />
							</Layout>
						);
					},
				});
				break;
			}
			default:
				break;
		}
	};

	useLayoutEffect(() => {
		if (history.location.query?.drawer) {
			openDrawer(history.location.query?.drawer as string);
		}

		// load dark.css for antd
		const colorThemeMode = molecule.colorTheme.getColorThemeMode();
		if (colorThemeMode === ColorThemeMode.dark) {
			loadStyles('https://unpkg.com/antd@4.18.5/dist/antd.dark.css');
		}

		const unlisten = history.listen((route) => {
			if (route.query?.drawer) {
				openDrawer(route.query?.drawer as string);
			}
		});
		return unlisten;
	}, []);

	return (
		<Context.Provider
			value={{
				personList,
				username,
			}}
		>
			<MoleculeProvider extensions={extensions} defaultLocale="Taier-zh-CN">
				<Workbench />
			</MoleculeProvider>
			<Login />
			<CustomDrawer id="root" renderContent={() => null} />
		</Context.Provider>
	);
}
