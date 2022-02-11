import { CATELOGUE_TYPE, MENU_TYPE_ENUM } from '@/constant';
import functionManagerService from '@/services/functionManagerService';
import resourceManagerService from '@/services/resourceManagerService';
import { transformCatalogueToTree, loadTreeNode, getCatalogueViaNode } from '@/utils/extensions';
import molecule from '@dtinsight/molecule';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';
import { getRootFolderViaSource } from '@/utils';

/**
 * 获取根目录
 */
export function getCatalogueTree() {
	getCatalogueViaNode({ id: 0 }).then(async (res) => {
		if (!res) return;
		const { children } = res;
		if (!children) return;

		const taskData = getRootFolderViaSource(children, CATELOGUE_TYPE.TASK);
		const resourceData = getRootFolderViaSource(children, CATELOGUE_TYPE.RESOURCE);
		const funcData = getRootFolderViaSource(children, CATELOGUE_TYPE.FUNCTION);

		// 资源根目录
		if (resourceData) {
			const resourceRoot = resourceData;
			const resourceNode = transformCatalogueToTree(
				resourceRoot,
				CATELOGUE_TYPE.RESOURCE,
				true,
			)!;
			resourceManagerService.add(resourceNode);
		}

		// 函数根目录
		if (funcData) {
			const funcRoot = funcData;
			const functionNode = transformCatalogueToTree(funcRoot, CATELOGUE_TYPE.FUNCTION, true)!;
			functionManagerService.add(functionNode);

			const SparkSqlNode = funcRoot?.children?.find(
				(child: any) => child.catalogueType === MENU_TYPE_ENUM.SPARKFUNC,
			);
			if (SparkSqlNode) {
				loadTreeNode(SparkSqlNode, CATELOGUE_TYPE.FUNCTION);
			}
		}

		// 任务开发根目录
		if (taskData) {
			const taskRootFolder = taskData?.children?.[0];
			if (taskRootFolder) {
				const taskNode = transformCatalogueToTree(
					taskRootFolder,
					CATELOGUE_TYPE.TASK,
					true,
				)!;

				molecule.folderTree.add(taskNode);
				loadTreeNode(taskRootFolder, CATELOGUE_TYPE.TASK);
			}
		}
	});
}

/**
 * This is for getting the root catalogues including the resouces and tasks and functions
 */
export default class CatalogueExtension implements IExtension {
	id: UniqueId = 'Catalogue';
	name: string = 'Catalogue';
	activate(): void {
		getCatalogueTree();
	}
	dispose(): void {
		throw new Error('Method not implemented.');
	}
}
