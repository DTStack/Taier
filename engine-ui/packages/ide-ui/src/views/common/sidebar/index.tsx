import React from 'react';
import molecule from 'molecule';
import { IExtension } from 'molecule/esm/model';
import ResourceManager from '../../resourceManager';

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
        title: '资源管理'
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
        title: '函数管理'
    };
    molecule.activityBar.add(functionManager);
    molecule.sidebar.add({
        id: functionManager.id,
        title: functionManager.name,
        render: () => <div>123</div>,
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
