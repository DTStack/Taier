import React from 'react';
import molecule from 'molecule';
import { IExtension } from 'molecule/esm/model';
import ResourceManager from '../../resourceManager';

function initResourceManager() {
    const resourceManager = {
        id: 'ResourceManager',
        icon: 'sync',
        name: '资源管理',
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
            contextMenu: [
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
            ],
        },
    ];

    const handleCreateFolder = () => {
        console.log('新建文件夹');
    };

    molecule.activityBar.add(resourceManager);
    molecule.sidebar.add({
        id: resourceManager.id,
        title: resourceManager.name,
        render: () => (
            <ResourceManager
                panel={resourceManager}
                headerToolBar={headerToolBar}
                onCreateFolder={handleCreateFolder}
            />
        ),
    });
}

function initFunctionManager() {
    const functionManager = {
        id: 'FunctionManager',
        icon: 'mortar-board',
        name: '函数管理',
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
