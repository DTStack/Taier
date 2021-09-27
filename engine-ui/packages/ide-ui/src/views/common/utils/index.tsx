import molecule from 'molecule/esm';
import React from 'react';
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
import resourceManagerTree from '../../../services/resourceManagerService';
import functionManagerService from '../../../services/functionManagerService';
import { RESOURCE_TYPE, TASK_TYPE } from '../../../comm/const';
import {
    EggIcon,
    JarIcon,
    OtherIcon,
    PythonIcon,
    ZipIcon,
} from '../../../components/icon/resouceIcon';

export type Source = 'task' | 'resource' | 'function';
export interface CatalogueDataProps {
    id: number;
    type: string;
    taskType: number;
    resourceType: number;
    name: string;
    children: CatalogueDataProps[] | null;
    catalogueType: string;
}

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

export function fileIcon(type: number, source: Source): string | JSX.Element {
    let res: string | JSX.Element = '';

    switch (source) {
        case 'task': {
            res = {
                [TASK_TYPE.SQL]: 'icon_sparkSQL iconfont',
                [TASK_TYPE.SYNC]: 'sync',
            }[type];
            break;
        }
        case 'resource': {
            res = {
                [RESOURCE_TYPE.OTHER]: <OtherIcon />,
                [RESOURCE_TYPE.JAR]: <JarIcon />,
                [RESOURCE_TYPE.PY]: <PythonIcon />,
                [RESOURCE_TYPE.ZIP]: <ZipIcon />,
                [RESOURCE_TYPE.EGG]: <EggIcon />,
            }[type];
            break;
        }
        case 'function': {
            res = 'file';
            break;
        }
        default:
            break;
    }

    return res || 'file';
}

/**
 * 异步加载树结点，会将数据保存到 redux 对应位置中
 * @param node
 * @returns
 */
export async function getCatalogueViaNode(
    node: Pick<CatalogueDataProps, 'id' | 'catalogueType'>
) {
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

/**
 * Get the root folder which distinguished from the source
 * @param data
 * @param source
 * @returns
 */
export function getRootFolderViaSource(
    data: CatalogueDataProps[],
    source: Source
) {
    switch (source) {
        case 'task': {
            return data
                .find((item) => item.catalogueType === 'TaskManager')
                ?.children?.find(
                    (item) => item.catalogueType === 'TaskDevelop'
                );
        }

        case 'resource': {
            return data.find(
                (item) => item.catalogueType === 'ResourceManager'
            );
        }

        case 'function': {
            return data.find(
                (item) => item.catalogueType === 'FunctionManager'
            );
        }
        default:
            return undefined;
    }
}

/**
 * Transform the catalogue data from back-end to the tree structure
 * @param catalogue
 * @param source
 * @returns
 */
export function transformCatalogueToTree(
    catalogue: CatalogueDataProps,
    source: Source,
    isRootFolder: boolean = false
): TreeNodeModel | undefined {
    const folderType = ['folder', 'catalogue'];
    switch (source) {
        case 'task':
        case 'resource': {
            const children = (catalogue.children || [])
                .map((child) => transformCatalogueToTree(child, source))
                .filter(Boolean) as TreeNodeModel[];

            const catalogueType = folderType.includes(catalogue.type)
                ? FileTypes.Folder
                : FileTypes.File;

            const fileType = isRootFolder
                ? FileTypes.RootFolder
                : catalogueType;

            return new TreeNodeModel({
                id: catalogue.id,
                name: catalogue.name,
                location: catalogue.name,
                fileType,
                icon: fileIcon(
                    source === 'task'
                        ? catalogue.taskType
                        : catalogue.resourceType,
                    source
                ),
                isLeaf: fileType === FileTypes.File,
                data: catalogue,
                children,
            });
        }
        case 'function': {
            const { id, type, name } = catalogue;
            const children = (catalogue.children || [])
                // there is a system function in the children node of root folder, we'd better to filter it
                .filter((child) => !isRootFolder || child.name !== '系统函数')
                .map((child) => transformCatalogueToTree(child, source))
                .filter(Boolean) as TreeNodeModel[];

            const catalogueType = folderType.includes(type)
                ? FileTypes.Folder
                : FileTypes.File;

            const fileType = isRootFolder
                ? FileTypes.RootFolder
                : catalogueType;

            // Because of the same id in different levels, so we should set another uniq id for each tree node
            return new TreeNodeModel({
                id: `${id}-${folderType.includes(type) ? 'folder' : 'file'}`,
                name,
                location: name,
                fileType,
                isLeaf: fileType === FileTypes.File,
                data: catalogue,
                icon: fileIcon(catalogue.taskType, source),
                children,
            });
        }

        default:
            return;
    }
}

/**
 * Get the children data in node and save it into Service
 * @param node
 * @param source
 */
export async function loadTreeNode(node: CatalogueDataProps, source: Source) {
    const data = await getCatalogueViaNode(node);
    const nextNode = transformCatalogueToTree(data, source);
    if (nextNode) {
        switch (source) {
            case 'task':
                molecule.folderTree.update(nextNode);
                break;
            case 'resource':
                resourceManagerTree.update(nextNode);
                break;
            case 'function':
                functionManagerService.update(nextNode);
                break;
            default:
                break;
        }
    }
}
