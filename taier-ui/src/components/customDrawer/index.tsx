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

import React, { useLayoutEffect, useState } from 'react';
import type { DrawerProps } from 'antd';
import { Drawer } from 'antd';
import { history } from 'umi';

import { removePopUpMenu } from '@/utils';

interface CustomDrawerProps extends DrawerProps {
    id: string;
    renderContent?: () => React.ReactNode;
    /**
     * 是否是更新。更新操作会将当前参数和已有参数结合。若非更新操作，则会摒弃已有参数
     */
    update?: boolean;
}

const defaultConfig: DrawerProps = {
    width: '80%',
};

const drawerListeners: Record<
    string,
    (renderContent: CustomDrawerProps['renderContent'], props: Partial<DrawerProps>) => void
> = {};

export function updateDrawer({ id, renderContent, ...restProps }: CustomDrawerProps) {
    drawerListeners[id](renderContent, restProps);
}

export default function CustomDrawer({ id, renderContent, ...restProps }: CustomDrawerProps) {
    const [, setForceRender] = useState(false);
    const [drawerConfig, setConfig] = useState(restProps);

    const [childrenRender, setChildrenRender] = useState<CustomDrawerProps['renderContent']>(() => renderContent);

    const forceRender = (
        contentFn?: CustomDrawerProps['renderContent'],
        { update = false, ...restDrawerProps }: Omit<CustomDrawerProps, 'renderContent' | 'id'> = {}
    ) => {
        setConfig((config) => (update ? { ...config, ...restDrawerProps } : { ...restDrawerProps }));
        if (contentFn) {
            setChildrenRender(() => contentFn);
        }
        setForceRender((f) => !f);
    };

    useLayoutEffect(() => {
        drawerListeners[id] = forceRender;
    }, []);

    const handleCloseDrawer = () => {
        history.push({
            query: {},
        });
        drawerListeners[id](() => null, { visible: false });
        removePopUpMenu();
    };

    return (
        <Drawer
            getContainer={() => document.querySelector('.mo-mainBench') || document.body}
            style={{ position: 'absolute' }}
            onClose={handleCloseDrawer}
            {...defaultConfig}
            {...drawerConfig}
        >
            {childrenRender && childrenRender()}
        </Drawer>
    );
}
