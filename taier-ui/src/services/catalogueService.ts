import molecule from '@dtinsight/molecule';
import { GlobalEvent } from '@dtinsight/molecule/esm/common/event';
import { FileTypes, TreeNodeModel } from '@dtinsight/molecule/esm/model';
import { singleton } from 'tsyringe';

import api from '@/api';
import { CATALOGUE_TYPE, MENU_TYPE_ENUM } from '@/constant';
import type { CatalogueDataProps } from '@/interface';
import { getTenantId, getUserId } from '@/utils';
import { fileIcon } from '@/utils/extensions';
import functionManagerService from './functionManagerService';
import resourceManagerService from './resourceManagerService';

interface ICatalogueService {
    /**
     * 加载目录树的根目录
     */
    loadRootFolder: () => void;
    /**
     * 获取目录树的根目录
     */
    getRootFolder: (source: CATALOGUE_TYPE) => molecule.model.IFolderTreeNodeProps | undefined;
    /**
     * 获取目录树的子节点
     * @param node 更新当前 Node 节点的子节点，不改变 Node 节点
     */
    loadTreeNode: (node: Partial<Pick<CatalogueDataProps, 'id' | 'catalogueType'>>, source: CATALOGUE_TYPE) => void;
    /**
     * 目录树更新监听事件
     */
    onUpdate: (callback: () => void) => void;
}

export enum CatalogueEventKind {
    onUpdate = 'catalogue.update',
}

@singleton()
export default class CatalogueService extends GlobalEvent implements ICatalogueService {
    /**
     * 异步加载目录树节点
     */
    private getCatalogueViaNode = async (
        node: Partial<Pick<CatalogueDataProps, 'id' | 'catalogueType'>>
    ): Promise<CatalogueDataProps | undefined> => {
        if (!node) throw new Error('[getCatalogueViaNode]: failed to get catelogue');
        const res = await api.getOfflineCatalogue({
            isGetFile: true,
            nodePid: node.id,
            catalogueType: node.catalogueType,
            userId: getUserId(),
            tenantId: getTenantId(),
        });
        if (res.code === 1) {
            return res.data;
        }
        return undefined;
    };

    private getRootFolderViaSource = (data: CatalogueDataProps[], source: CATALOGUE_TYPE) => {
        switch (source) {
            case CATALOGUE_TYPE.TASK: {
                return data.find((item) => item.catalogueType === MENU_TYPE_ENUM.TASK);
            }

            case CATALOGUE_TYPE.RESOURCE: {
                return data.find((item) => item.catalogueType === MENU_TYPE_ENUM.RESOURCE);
            }

            case CATALOGUE_TYPE.FUNCTION: {
                return data.find((item) => item.catalogueType === MENU_TYPE_ENUM.FUNCTION);
            }
            default:
                return undefined;
        }
    };

    /**
     * Only transform current catalogue to treeNodeModel, ignore children
     */
    private transformCatalogueToTree = (catalogue: CatalogueDataProps | undefined, source: CATALOGUE_TYPE) => {
        if (!catalogue) return;
        const folderType = ['folder', 'catalogue'];
        switch (source) {
            case CATALOGUE_TYPE.RESOURCE: {
                const fileType = folderType.includes(catalogue.type) ? FileTypes.Folder : FileTypes.File;

                const id = fileType === FileTypes.File ? catalogue.id : `${catalogue.id}-folder`;
                return new TreeNodeModel({
                    // prevent same id between folder and file
                    id,
                    name: catalogue.name,
                    fileType,
                    icon: fileIcon(catalogue.resourceType, source),
                    isLeaf: fileType === FileTypes.File,
                    data: catalogue,
                    children: resourceManagerService.get(id)?.children || [],
                });
            }

            case CATALOGUE_TYPE.FUNCTION: {
                const { type, name } = catalogue;
                const fileType = folderType.includes(type) ? FileTypes.Folder : FileTypes.File;

                // Because of the same id in different levels, so we should set another uniq id for each tree node
                const id = `${catalogue.id}-${folderType.includes(type) ? 'folder' : 'file'}`;
                return new TreeNodeModel({
                    id,
                    name,
                    location: name,
                    fileType,
                    isLeaf: fileType === FileTypes.File,
                    data: catalogue,
                    icon: fileIcon(catalogue.taskType, source),
                    children: functionManagerService.get(id)?.children || [],
                });
            }

            case CATALOGUE_TYPE.TASK: {
                const fileType = folderType.includes(catalogue.type) ? FileTypes.Folder : FileTypes.File;

                // prevent same id between folder and file
                const id = fileType === FileTypes.File ? catalogue.id : `${catalogue.id}-folder`;
                return new TreeNodeModel({
                    id,
                    name: catalogue.name,
                    location: catalogue.name,
                    fileType,
                    icon: fileIcon(catalogue.taskType, source),
                    isLeaf: fileType === FileTypes.File,
                    data: catalogue,
                    children: molecule.folderTree.get(id)?.children || [],
                });
            }

            default:
                return undefined;
        }
    };

    private getServiceBySource = (source: CATALOGUE_TYPE) => {
        switch (source) {
            case CATALOGUE_TYPE.TASK:
                return molecule.folderTree;
            case CATALOGUE_TYPE.FUNCTION:
                return functionManagerService;
            case CATALOGUE_TYPE.RESOURCE:
                return resourceManagerService;
            default:
                return null;
        }
    };

    /**
     * @param node 更新当前 Node 节点的子节点，不改变 Node 节点
     */
    public loadTreeNode = async (
        node: Partial<Pick<CatalogueDataProps, 'id' | 'catalogueType'>>,
        source: CATALOGUE_TYPE
    ) => {
        const data = await this.getCatalogueViaNode(node);
        if (data) {
            const childrenNodes = <molecule.model.TreeNodeModel[]>(
                (data?.children?.map((child) => this.transformCatalogueToTree(child, source)) || []).filter(Boolean)
            );
            const service = this.getServiceBySource(source);
            const treeNode = service?.get(`${node.id!}-folder`);
            if (treeNode) {
                service?.update({
                    ...treeNode,
                    children: childrenNodes,
                });

                this.emit(CatalogueEventKind.onUpdate);
                return service?.get(treeNode.id);
            }
        }
    };

    public getRootFolder = (source: CATALOGUE_TYPE) => {
        const service = this.getServiceBySource(source);
        switch (source) {
            case CATALOGUE_TYPE.TASK:
            case CATALOGUE_TYPE.FUNCTION:
                return service?.getState().folderTree?.data?.[0];
            case CATALOGUE_TYPE.RESOURCE:
                return service?.getState().folderTree?.data?.[0]?.children?.[0];
            default:
                return undefined;
        }
    };

    public loadRootFolder = () => {
        this.getCatalogueViaNode({ id: 0 }).then((res) => {
            if (!res || !res.children) {
                return;
            }
            const { children } = res;

            const taskData = this.getRootFolderViaSource(children, CATALOGUE_TYPE.TASK);
            const resourceData = this.getRootFolderViaSource(children, CATALOGUE_TYPE.RESOURCE);
            const funcData = this.getRootFolderViaSource(children, CATALOGUE_TYPE.FUNCTION);

            // 更新资源目录树
            if (resourceData) {
                const resourceRoot = resourceData;
                const resourceNode = this.transformCatalogueToTree(resourceRoot, CATALOGUE_TYPE.RESOURCE)!;
                const childrenNodes =
                    resourceRoot.children?.map(
                        (child) => this.transformCatalogueToTree(child, CATALOGUE_TYPE.RESOURCE)!
                    ) || [];

                // set a root folder
                resourceNode.fileType = FileTypes.RootFolder;
                // put children to root folder
                resourceNode.children = childrenNodes;
                resourceManagerService.add(resourceNode);
            }

            // 更新函数目录树
            if (funcData) {
                const funcRoot = funcData;
                const functionNode = this.transformCatalogueToTree(funcRoot, CATALOGUE_TYPE.FUNCTION)!;
                const childrenNodes =
                    funcRoot.children
                        // there is a system function in the children node of root folder, we'd better to filter it
                        ?.filter((child) => child.name !== '系统函数')
                        .map((child) => this.transformCatalogueToTree(child, CATALOGUE_TYPE.FUNCTION)!) || [];

                // set a root folder
                functionNode.fileType = FileTypes.RootFolder;
                // put children to root folder
                functionNode.children = childrenNodes;
                functionManagerService.add(functionNode);

                // // sql 节点必存在 catalogueType，对所有的节点都求一遍子树
                // const SqlNodes =
                // 	funcRoot?.children?.filter((child: any) => child.catalogueType) || [];
                // SqlNodes.forEach((sqlNode) => {
                // 	loadTreeNode(sqlNode, CATELOGUE_TYPE.FUNCTION);
                // });
            }

            // 任务开发根目录
            if (taskData) {
                const taskRootFolder = taskData?.children?.[0];
                if (taskRootFolder) {
                    const taskNode = this.transformCatalogueToTree(taskRootFolder, CATALOGUE_TYPE.TASK)!;

                    taskNode.fileType = FileTypes.RootFolder;
                    molecule.folderTree.add(taskNode);

                    // 获取当前根目录的下级目录，确保打开 Explorer 有数据展示
                    this.loadTreeNode(taskRootFolder, CATALOGUE_TYPE.TASK);
                }
            }
        });
    };

    public onUpdate = (callback: () => void) => {
        this.subscribe(CatalogueEventKind.onUpdate, callback);
    };
}
