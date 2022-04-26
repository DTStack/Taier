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

import { CATELOGUE_TYPE } from '@/constant';
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

			// sql 节点必存在 catalogueType，对所有的节点都求一遍子树
			const SqlNodes = funcRoot?.children?.filter((child: any) => child.catalogueType) || [];
			SqlNodes.forEach((sqlNode) => {
				loadTreeNode(sqlNode, CATELOGUE_TYPE.FUNCTION);
			});
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
