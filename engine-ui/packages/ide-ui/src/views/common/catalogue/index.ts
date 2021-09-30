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

import molecule from '@dtinsight/molecule/esm';
import { IExtension } from '@dtinsight/molecule/esm/model';
import {
	taskTreeAction,
	resTreeAction,
	functionTreeAction,
} from '../../../controller/catalogue/actionTypes';
import functionManagerService from '../../../services/functionManagerService';
import resourceManagerService from '../../../services/resourceManagerService';
import store from '../../../store';
import {
	getRootFolderViaSource,
	transformCatalogueToTree,
	getCatalogueViaNode,
	loadTreeNode,
} from '../utils';
import type { CatalogueDataProps } from '../utils';

// get tree
function getCatalogueTree() {
	getCatalogueViaNode({ id: 0, catalogueType: '1' }).then((res: CatalogueDataProps) => {
		const { children } = res;
		if (!children) return;

		const taskData = getRootFolderViaSource(children, 'task');
		const resourceData = getRootFolderViaSource(children, 'resource');
		const funcData = getRootFolderViaSource(children, 'function');

		// 资源根目录
		if (resourceData) {
			const resourceNode = transformCatalogueToTree(resourceData, 'resource', true)!;
			store.dispatch({
				type: resTreeAction.RESET_RES_TREE,
				payload: resourceData,
			});
			resourceManagerService.add(resourceNode);
		}

		// 函数根目录
		if (funcData) {
			const functionNode = transformCatalogueToTree(funcData, 'function', true)!;
			store.dispatch({
				type: functionTreeAction.RESET_FUNCTION_TREE,
				payload: funcData,
			});
			functionManagerService.add(functionNode);

			const SparkSqlNode = funcData.children?.find(
				(child) => child.catalogueType === 'SparkSQLFunction',
			);
			SparkSqlNode && loadTreeNode(SparkSqlNode, 'function');
		}

		// 任务开发根目录
		if (taskData) {
			const taskNode = transformCatalogueToTree(taskData, 'task', true)!;
			store.dispatch({
				type: taskTreeAction.RESET_TASK_TREE,
				payload: taskData,
			});
			molecule.folderTree.add(taskNode);
			loadTreeNode(taskData, 'task');
		}
	});
}

export default class CatalogueExtension implements IExtension {
	activate() {
		getCatalogueTree();
	}
}
