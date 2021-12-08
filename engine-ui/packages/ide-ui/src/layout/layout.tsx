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

import React, { useState } from 'react';
import { Layout, Menu, Dropdown, Icon, message } from 'antd';
import { hashHistory } from 'react-router';
import { AppContainer } from '../views/registerMicroApps';
import { getItem, setItem, clear } from '../utils/session';
import {
    getItem as localGet,
    setItem as localSet,
    clear as localClear,
} from '../utils/local';
import { USER_NAME } from '../consts';

const { Header, Content } = Layout;

const userMenu = (
    <Menu>
        {[
            {
                name: '登出',
                path: '/logout',
                isShow: true,
            },
        ].map((i, index) => {
            return (
                <Menu.Item
                    key={index}
                    onClick={async () => {
                        localSet(USER_NAME, '');
                        const response = await fetch('/node/login/logout', {
                            method: 'POST',
                        });
                        const body = await response.json();
                        if (!body.data || !response.ok) {
                            return message.error('登出失败');
                        }
                        clear();
                        localClear();
                        hashHistory.push({
                            pathname: '/login',
                        });
                    }}
                >
                    {i.name}
                </Menu.Item>
            );
        })}
    </Menu>
);

export default function MyLayout(props: React.PropsWithChildren<any>) {
    const { children, history } = props;
    const [path, setPath] = useState(history.getCurrentLocation().pathname);
    const [curItem, setCurItem] = useState(getItem('menuStatus') || 'devTask');
    // eslint-disable-next-line prefer-regex-literals
    const regexp = new RegExp(/(\/login)$/);
    const handleClick = (menuItem: any) => {
        setCurItem(menuItem.key);
        setItem('menuStatus', menuItem.key);
    };

    history.listen((route: any) => {
        if (path !== route.pathname) setPath(route.pathname);
    });
    // setUrl(history.getCurrentLocation().pathname);

    return (
        <>
            {!regexp.test(path) ? (
                <Layout style={{ position: 'relative', height: '100%' }}>
                    <Header
                        className="dt-layout-header"
                        style={{ width: '100%', minWidth: 100 }}
                    >
                        <div
                            className="logo dt-header-log-wrapper"
                            style={{ marginRight: '50px' }}
                        >
                            <span className="c-header__title">
                                DAGScheduleX
                            </span>
                        </div>
                        <Menu
                            mode="horizontal"
                            defaultSelectedKeys={[curItem]}
                            style={{ minWidth: 450 }}
                            onClick={handleClick}
                        >
                            <Menu.Item key="dataSource">
                                <a href="/#/data-source/list">数据源</a>
                            </Menu.Item>
                            <Menu.Item key="devTask">
                                <a href="/">任务开发</a>
                            </Menu.Item>
                            <Menu.Item key="operation">
                                <a href="/#/operation-ui">运维中心</a>
                            </Menu.Item>
                            <Menu.Item key="console">
                                <a href="/#/console-ui">控制台</a>
                            </Menu.Item>
                        </Menu>
                        <div
                            className="logo dt-header-log-wrapper"
                            style={{ marginRight: '50px' }}
                        >
                            <Dropdown overlay={userMenu} trigger={['click']}>
                                <span
                                    style={{
                                        position: 'absolute',
                                        right: 20,
                                        fontSize: 14,
                                    }}
                                >
                                    <span className="username">
                                        {localGet(USER_NAME) || '未知用户'}
                                    </span>
                                    <Icon
                                        style={{
                                            marginLeft: 5,
                                            fontSize: '14px',
                                            color: '#BFBFBF',
                                        }}
                                        type="caret-down"
                                    />
                                </span>
                            </Dropdown>
                        </div>
                    </Header>
                    <Layout>
                        <Content id={AppContainer} className="dt-container">
                            {children}
                        </Content>
                    </Layout>
                </Layout>
            ) : (
                <>{children}</>
            )}
        </>
    );
}
