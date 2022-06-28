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

import 'reflect-metadata';
import { useState, useEffect, useLayoutEffect, useRef } from 'react';
import type { IPersonLists, ISupportJobTypes } from '@/context';
import Context from '@/context';
import { history } from 'umi';
import { extensions } from '@/extensions';
import api from '@/api';
import notification from '@/components/notification';
import molecule, { MoleculeProvider } from '@dtinsight/molecule';
import Workbench from './workbench';
import Task from '@/pages/operation/task';
import StreamTask from '@/pages/operation/streamTask';
import Schedule from '@/pages/operation/schedule';
import Patch from '@/pages/operation/patch';
import Layout from '@/layout';
import { updateDrawer } from '@/components/customDrawer';
import { Breadcrumb, Button } from 'antd';
import PatchDetail from './operation/patch/detail';
import Login from './login';
import CustomDrawer from '@/components/customDrawer';
import { CONSOLE, DRAWER_MENU_ENUM } from '@/constant';
import QueueManage from './console/queue';
import TaskDetail from './console/taskDetail';
import ResourceManage from './console/resource';
import ClusterManage from './console/cluster';
import EditCluster from './console/cluster/newEdit';
import type { IEditClusterRefProps } from './console/cluster/newEdit/interface';
import { getCookie } from '@/utils';
import { isViewMode } from './console/cluster/newEdit/help';
import '@dtinsight/molecule/esm/style/mo.css';
import './index.scss';

export default function HomePage() {
	const [personList, setPersonList] = useState<IPersonLists[]>([]);
	const [username, setUsername] = useState<string | undefined>(undefined);
	const [supportJobTypes, setJobTypes] = useState<ISupportJobTypes[]>([]);
	const loading = useRef(false);
	const refs = useRef<IEditClusterRefProps>(null);

	const checkLoginStatus = () => {
		const usernameInCookie = getCookie('username');
		// 当 username 存在，仅表示前端确认登录状态
		if (usernameInCookie) {
			setUsername(usernameInCookie);
		}
	};

	const handleTestConnects = async () => {
		refs.current?.testConnects(undefined, (bool: boolean) => {
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
				<Button type="primary" onClick={() => refs.current?.handleComplete()}>
					完成
				</Button>
			</>
		);
	};

	useEffect(() => {
		api.getPersonInCharge().then((res) => {
			if (res.code === 1) {
				setPersonList(res.data ?? []);
			}
		});

		checkLoginStatus();

		// 获取当前支持的任务类型
		api.getTaskTypes({}).then((res) => {
			if (res.code === 1) {
				setJobTypes(res.data || []);
			} else {
				notification.error({
					key: 'FailedJob',
					message: `获取支持的类型失败，将无法创建新的任务！`,
				});
			}
		});
	}, []);

	const openDrawer = (drawerId: string) => {
		switch (drawerId) {
			case DRAWER_MENU_ENUM.TASK:
			case DRAWER_MENU_ENUM.STREAM_TASK:
			case DRAWER_MENU_ENUM.SCHEDULE:
			case DRAWER_MENU_ENUM.PATCH:
				updateDrawer({
					id: 'root',
					visible: true,
					title: (
						<Breadcrumb>
							<Breadcrumb.Item>
								{molecule.menuBar.getMenuById(drawerId)?.name || 'Default'}
							</Breadcrumb.Item>
						</Breadcrumb>
					),
					renderContent: () => {
						const children = (() => {
							switch (drawerId) {
								case DRAWER_MENU_ENUM.TASK:
									return <Task />;
								case DRAWER_MENU_ENUM.STREAM_TASK:
									return <StreamTask />;
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

		const unlisten = history.listen((route) => {
			if (route.query?.drawer) {
				openDrawer(route.query?.drawer as string);
			}
		});
		return unlisten;
	}, []);

	useEffect(() => {
		function handleBeforeLeave(e: BeforeUnloadEvent) {
			const { groups } = molecule.editor.getState();
			if (groups?.length) {
				// refer to: https://developer.mozilla.org/en-US/docs/Web/API/BeforeUnloadEvent
				// prettier-ignore
				// eslint-disable-next-line no-useless-escape
				const confirmationMessage = '\o/';
				e.preventDefault();
				(e || window.event).returnValue = confirmationMessage; // Gecko + IE
				return confirmationMessage; // Webkit, Safari, Chrome
			}
		}
		window.addEventListener('beforeunload', handleBeforeLeave);

		return () => window.removeEventListener('beforeunload', handleBeforeLeave);
	}, []);

	return (
		<Context.Provider
			value={{
				personList,
				username,
				supportJobTypes,
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
