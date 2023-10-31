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

import { useEffect, useLayoutEffect,useState } from 'react';
import molecule, { create, Workbench } from '@dtinsight/molecule';
import { connect } from '@dtinsight/molecule/esm/react';
import { Breadcrumb } from 'antd';
import { history } from 'umi';
import 'reflect-metadata';

import api from '@/api';
import CustomDrawer, { updateDrawer } from '@/components/customDrawer';
import TaskListener from '@/components/TaskListener';
import { CONSOLE, DRAWER_MENU_ENUM } from '@/constant';
import type { IPersonLists } from '@/context';
import Context from '@/context';
import { extensions } from '@/extensions';
import Layout from '@/layout';
import Patch from '@/pages/operation/patch';
import Schedule from '@/pages/operation/schedule';
import StreamTask from '@/pages/operation/streamTask';
import Task from '@/pages/operation/task';
import { taskRenderService } from '@/services';
import type { ITaskRenderState } from '@/services/taskRenderService';
import { getCookie } from '@/utils';
import ClusterManage from './console/cluster';
import ClusterDetail from './console/cluster/detail';
import QueueManage from './console/queue';
import ResourceManage from './console/resource';
import TaskDetail from './console/taskDetail';
import PatchDetail from './operation/patch/detail';
import Login from './login';
import '@dtinsight/molecule/esm/style/mo.css';
import './index.scss';

const moInstance = create({
    extensions,
    defaultLocale: 'Taier-zh-CN',
});

moInstance.onBeforeInit(() => {
    molecule.builtin.inactiveModule('builtInOutputPanel');
    molecule.builtin.inactiveModule('FOLDER_PANEL_CONTEXT_MENU');
});

const MoleculeProvider = () => moInstance.render(<Workbench />);

export default connect(taskRenderService, ({ supportTaskList }: ITaskRenderState) => {
    const [personList, setPersonList] = useState<IPersonLists[]>([]);
    const [username, setUsername] = useState<string | undefined>(undefined);

    const checkLoginStatus = () => {
        const usernameInCookie = getCookie('username');
        // 当 username 存在，仅表示前端确认登录状态
        if (usernameInCookie) {
            setUsername(usernameInCookie);
        }
    };

    useEffect(() => {
        api.getPersonInCharge().then((res) => {
            if (res.code === 1) {
                setPersonList(res.data ?? []);
            }
        });

        checkLoginStatus();
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
                                <ClusterDetail />
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
                const confirmationMessage = '\\o/';
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
                supportJobTypes: supportTaskList,
            }}
        >
            <MoleculeProvider />
            <Login />
            <CustomDrawer id="root" renderContent={() => null} />
            <TaskListener />
        </Context.Provider>
    );
});
