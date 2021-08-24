import React from 'react';
import molecule from 'molecule';
import { FileTypes, IExtension, TreeNodeModel } from 'molecule/esm/model';
import ResourceManager from '../../resourceManager';

function convertToTreeNode(data: any[]) {
    if (!data) {
        return;
    }
    return data.map((child) => {
        const { id, name, children, type } = child;
        const node: TreeNodeModel = new TreeNodeModel({
            id,
            name: !name ? '数据开发' : name,
            location: name,
            fileType: type === 'folder' ? FileTypes.Folder : FileTypes.File,
            isLeaf: type !== 'folder',
            data: child,
            children: convertToTreeNode(children),
        });

        return node;
    });
}

// mock 的请求数据
const requestData = {
    catalogueType: 'ResourceManager',
    children: [
        {
            catalogueType: 'ResourceManager',
            children: null,
            createUser: null,
            engineType: 0,
            id: 251,
            isSubTask: 0,
            learningType: null,
            level: 1,
            name: '资源管理',
            operateModel: 1,
            orderVal: null,
            parentId: 249,
            projectAlias: null,
            pythonVersion: null,
            readWriteLockVO: null,
            resourceType: null,
            scriptType: null,
            status: null,
            taskType: null,
            type: 'folder',
        },
    ],
    createUser: null,
    engineType: 0,
    id: 249,
    isSubTask: 0,
    learningType: null,
    level: 0,
    name: '资源管理',
    operateModel: 1,
    orderVal: 3,
    parentId: 0,
    projectAlias: null,
    pythonVersion: null,
    readWriteLockVO: null,
    resourceType: null,
    scriptType: null,
    status: null,
    taskType: null,
    type: 'folder',
};

function initResourceManager() {
    const resourceManager = {
        id: 'ResourceManager',
        icon: 'sync',
        name: '资源管理',
    };

    const { id, name, children } = requestData;
    // 根目录
    const node = new TreeNodeModel({
        id,
        name: name || '数据开发',
        location: name,
        fileType: FileTypes.RootFolder,
        data: requestData,
        children: convertToTreeNode(children),
    });

    const treeData = {
        data: [node],
        contextMenu: [{}],
        folderPanelContextMenu: [],
    };

    const headerToolBar = [
        {
            id: 'refresh',
            title: '刷新',
            icon: 'refresh',
        },
    ];

    molecule.activityBar.add(resourceManager);
    molecule.sidebar.add({
        id: resourceManager.id,
        title: resourceManager.name,
        render: () => (
            <ResourceManager
                treeData={treeData}
                panel={resourceManager}
                headerToolBar={headerToolBar}
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
