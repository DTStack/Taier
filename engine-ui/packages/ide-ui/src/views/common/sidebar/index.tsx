import React from 'react';
import molecule from '@dtinsight/molecule';
import {
    IExtension,
    CONTEXT_MENU_SEARCH,
    ACTIVITY_BAR_GLOBAL_ACCOUNT,
} from '@dtinsight/molecule/esm/model';
import ResourceManager from '../../resourceManager';
import FunctionManager from '../../functionManager';
import { FUNCTION_NEW_FUNCTION } from '../../functionManager/menu';

export const folderMenu = [
    {
        id: 'upload',
        name: '上传资源',
    },
    {
        id: 'replace',
        name: '替换资源',
    },
    {
        id: 'create-folder',
        name: '新建文件夹',
    },
];

function initResourceManager() {
    const resourceManager = {
        id: 'ResourceManager',
        icon: 'icon_ziyuan iconfont',
        name: '资源管理',
        title: '资源管理',
    };

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
            contextMenu: folderMenu,
        },
    ];

    molecule.activityBar.add(resourceManager);
    molecule.sidebar.add({
        id: resourceManager.id,
        title: resourceManager.name,
        render: () => (
            <ResourceManager
                panel={resourceManager}
                headerToolBar={headerToolBar}
            />
        ),
    });
}

function initFunctionManager() {
    const functionManager = {
        id: 'FunctionManager',
        icon: 'icon_hanshu iconfont',
        name: '函数管理',
        title: '函数管理',
    };
    molecule.activityBar.remove([
        CONTEXT_MENU_SEARCH,
        ACTIVITY_BAR_GLOBAL_ACCOUNT,
    ]);

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
        render: () => (
            <FunctionManager
                panel={functionManager}
                headerToolBar={headerToolBar}
            />
        ),
    });
}

export default class SidebarExtension implements IExtension {
    activate() {
        // 初始化资源管理
        initResourceManager();
        // 初始化函数管理
        initFunctionManager();
    }
}
