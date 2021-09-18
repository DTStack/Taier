import molecule from 'molecule/esm';
import {
    FileTypes,
    IStatusBarItem,
    TreeNodeModel,
    Float,
} from 'molecule/esm/model';
import { TASK_RUN_ID, TASK_STOP_ID } from './const';
import ajax from '../../../api';
import { catalogueTypeToDataType } from '../../../components/func';
import { updateCatalogueData } from '../../../controller/catalogue/actionCreator';
import store from '../../../store';

export function resetEditorGroup() {
    molecule.editor.updateActions([
        { id: TASK_RUN_ID, disabled: true },
        { id: TASK_STOP_ID, disabled: true },
    ]);
}

export function updateStatusBarLanguage(item: IStatusBarItem) {
    const states = molecule.statusBar.getState();
    const languageStatus = states.rightItems.find(
        (item) => item.id === 'language'
    );
    if (languageStatus) {
        molecule.statusBar.update(item);
    } else {
        molecule.statusBar.add(item, Float.right);
    }
}

export function convertToTreeNode(data: any[]) {
    if (!data) {
        return;
    }
    const folderType = ['folder', 'catalogue'];
    return data.map((child) => {
        const { id, name, children, type } = child;
        const node: TreeNodeModel = new TreeNodeModel({
            id,
            name: !name ? '数据开发' : name,
            location: name,
            fileType: folderType.includes(type)
                ? FileTypes.Folder
                : FileTypes.File,
            isLeaf: !folderType.includes(type),
            data: child,
            children: convertToTreeNode(children),
        });

        return node;
    });
}

// [TODO]: 把该函数和 convertToTreeNode 整合起来
export function convertToFunctionsTreeNode(data: any[]) {
    if (!data) {
        return;
    }
    const folderType = ['folder', 'catalogue'];
    return data.map((child) => {
        const { id, name, children, type } = child;
        const node: TreeNodeModel = new TreeNodeModel({
            id: `${id}-${folderType.includes(type) ? 'folder' : 'file'}`,
            name: !name ? '数据开发' : name,
            location: name,
            fileType: folderType.includes(type)
                ? FileTypes.Folder
                : FileTypes.File,
            isLeaf: !folderType.includes(type),
            data: child,
            children: convertToFunctionsTreeNode(children),
        });

        return node;
    });
}

/**
 * 异步加载树结点，会将数据保存到 redux 对应位置中
 * @param node
 * @returns
 */
export async function getCatalogueViaNode(node: any) {
    return new Promise<any>(async (resolve, reject) => {
        if (!node)
            reject(new Error('[getCatalogueViaNode]: failed to get catelogue'));
        const res = await ajax.getOfflineCatalogue({
            isGetFile: !!1,
            nodePid: node!.id,
            catalogueType: node.catalogueType,
            taskType: 1,
            appointProjectId: 1,
            projectId: 1,
            userId: 1,
        });
        if (res.code === 1) {
            updateCatalogueData(
                store.dispatch,
                res.data,
                catalogueTypeToDataType(node.catalogueType)
            );
            resolve(res.data);
        }
    });
}

// 获取任务管理根目录
export function getTaskManagerRootFolder(data: any[]) {
    return data
        .find((item) => item.catalogueType === 'TaskManager')
        .children.find((item: any) => item.catalogueType === 'TaskDevelop');
}

// 获取函数管理根目录
export function getFunctionManagerRootFolder(data: any[]) {
    return data.find((item) => item.catalogueType === 'FunctionManager');
}

// 获取资源管理根目录
export function getResourceManagerRootFolder(data: any[]) {
    return data.find((item) => item.catalogueType === 'ResourceManager');
}
