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

import { cloneDeep, assign } from 'lodash';
import { combineReducers } from 'redux';

import {
	taskTreeAction,
	resTreeAction,
	sparkFnTreeAction,
	sparkCustomFnTreeAction,
	sparkSysFnTreeActon,
	libraFnTreeAction,
	libraSysFnTreeActon,
	tiDBFnTreeAction,
	tiDBSysFnTreeActon,
	oracleSysFnTreeActon,
	greenPlumSysFnTreeActon,
	greenPlumTreeAction,
	greenPlumFnTreeActon,
	greenPlumProdTreeActon,
	scriptTreeAction,
	componentTreeAction,
	tableTreeAction,
	functionTreeAction,
} from './actionTypes';

import { replaceTreeNode } from '@/utils';

/**
 * @description 加载文件夹内容
 *
 * @param {any} action
 * @param {any} state
 * @returns immutable state
 */
function loadFolderContent(action: any, state: any) {
	const data = action.payload;
	const id = data.id;
	const level = data.level;
	const clone = cloneDeep(state);
	const loop = (arr: any) => {
		arr.forEach((node: any, i: any) => {
			if (
				node.id === id &&
				node.level === level &&
				(node.type === 'folder' || node.type === 'flow' || node.type === 'catalogue')
			) {
				node.children = data.children;
				node._hasLoaded = true;
			} else {
				loop(node.children || []);
			}
		});
	};
	loop([clone]);
	return clone;
}

function sortByName(arr: any) {
	arr.sort(function (a: any, b: any) {
		return a.name.localeCompare(b.name);
	});
}

/**
 * @description 新增文件夹内容
 *
 * @param {any} action
 * @param {any} state
 * @returns immutable state
 */
function addFolderChild(action: any, state: any) {
	const data = action.payload;
	const { parentId } = data;
	const clone = cloneDeep(state);
	const loop = (arr: any) => {
		arr.forEach((node: any, i: any) => {
			if (node.id === parentId) {
				if (node.children === null || node.children === undefined) node.children = [];
				let fileIndex = 0;

				for (let i = 0; i <= node.children.length - 1; i++) {
					if (node.children[i].type === 'file') {
						fileIndex = i;
						break;
					}
				}
				node.children.splice(fileIndex, 0, data);

				// Sort children by name
				sortByName(node.children);
			} else {
				loop(node.children || []);
			}
		});
	};

	loop([clone]);
	return clone;
}

function deleteFolderChild(action: any, state: any) {
	const { id, originPid, parentId } = action.payload;
	const oPid = originPid || parentId;
	const clone = cloneDeep(state);
	const loop = (arr: any) => {
		arr.forEach((node: any, i: any) => {
			if (node.id === oPid) {
				if (!node.children) node.children = [];
				node.children = node.children.filter((o: any) => {
					return o.id !== id;
				});
			} else {
				loop(node.children || []);
			}
		});
	};

	loop([clone]);

	return clone;
}

export const clearTreeData = (dispatch: any) => {
	return (dispatch: any) => {
		dispatch({
			type: scriptTreeAction.RESET_SCRIPT_TREE,
			payload: {},
		});
	};
};

export const taskTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case taskTreeAction.RESET_TASK_TREE:
			return assign({}, action.payload);

		case taskTreeAction.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}

		case taskTreeAction.ADD_FOLDER_CHILD: {
			return addFolderChild(action, state);
		}

		case taskTreeAction.DEL_OFFLINE_TASK: {
			return deleteFolderChild(action, state);
		}

		case taskTreeAction.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}

		case taskTreeAction.EDIT_FOLDER_CHILD: {
			const payload = assign({}, action.payload, {
				parentId: action.payload.originPid,
			});
			return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
		}
		case taskTreeAction.EDIT_FOLDER_CHILD_FIELDS: {
			const updated = cloneDeep(state);
			replaceTreeNode(updated, action.payload);
			return updated;
		}
		case taskTreeAction.MERGE_FOLDER_CONTENT: {
			const origin = action.payload;
			if (origin) return origin;
			break;
		}
		default:
			return state;
	}
};

export const componentTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case componentTreeAction.RESET_COMPONENT_TREE:
			return assign({}, action.payload);
		case componentTreeAction.LOAD_FOLDER_CONTENT:
			return loadFolderContent(action, state);
		case componentTreeAction.DELETE_COMPONENT: {
			return deleteFolderChild(action, state);
		}
		case componentTreeAction.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}
		case scriptTreeAction.EDIT_FOLDER_CHILD: {
			const payload = assign({}, action.payload, {
				parentId: action.payload.originPid,
			});
			return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
		}
		case componentTreeAction.MERGE_FOLDER_CONTENT: {
			const origin = action.payload;
			if (origin) return origin;
			break;
		}
		default:
			return state;
	}
};

export const resourceTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case resTreeAction.RESET_RES_TREE:
			return assign({}, action.payload);

		case resTreeAction.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}

		case resTreeAction.ADD_FOLDER_CHILD: {
			return addFolderChild(action, state);
		}

		case resTreeAction.DEL_OFFLINE_RES: {
			return deleteFolderChild(action, state);
		}

		case resTreeAction.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}

		case resTreeAction.EDIT_FOLDER_CHILD: {
			const payload = assign({}, action.payload, {
				parentId: action.payload.originPid,
			});
			return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
		}
		default:
			return state;
	}
};

// 这里对 spark，hive，oracle，greenPlum 等函数管理中的 reducer 做一个整合，统一由该 reducer 做分发到对应的 reducer 中
export const functionTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case functionTreeAction.RESET_FUNCTION_TREE:
			return assign({}, action.payload);
		case functionTreeAction.LOAD_FOLDER_CONTENT:
			return loadFolderContent(action, state);

		default:
			return state;
	}
};

// ===== Spark Tree =====
export const sparkFnTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case sparkFnTreeAction.GET_SPARK_ROOT:
			return assign({}, state, action.payload);
		case sparkFnTreeAction.LOAD_FOLDER_CONTENT:
			return assign({}, state, action.payload);
		default:
			return state;
	}
};

export const sparkCustFuncTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case sparkCustomFnTreeAction.RESET_FUC_TREE:
			return assign({}, state, action.payload);

		case sparkCustomFnTreeAction.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}

		case sparkCustomFnTreeAction.ADD_FOLDER_CHILD: {
			return addFolderChild(action, state);
		}

		case sparkCustomFnTreeAction.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}

		case sparkCustomFnTreeAction.DEL_OFFLINE_FN: {
			return deleteFolderChild(action, state);
		}

		case sparkCustomFnTreeAction.EDIT_FOLDER_CHILD: {
			const payload = assign({}, action.payload, {
				parentId: action.payload.parentId,
			});
			const y = deleteFolderChild({ payload: payload }, state);
			const x = addFolderChild(action, y);
			return x;
		}

		default:
			return state;
	}
};

export const sparkSysFunctionTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case sparkSysFnTreeActon.RESET_SYSFUC_TREE:
			return assign({}, state, action.payload);

		case sparkSysFnTreeActon.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}

		case sparkSysFnTreeActon.ADD_FOLDER_CHILD: {
			return addFolderChild(action, state);
		}

		case sparkSysFnTreeActon.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}

		default:
			return state;
	}
};

// ===== librA Tree =====
export const libraFnTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case libraFnTreeAction.GET_LIBRA_ROOT:
			return assign({}, state, action.payload);
		case libraFnTreeAction.LOAD_FOLDER_CONTENT:
			return assign({}, state, action.payload);
		default:
			return state;
	}
};

export const libraSysFnTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case libraSysFnTreeActon.RESET_SYSFUC_TREE:
			return assign({}, state, action.payload);

		case libraSysFnTreeActon.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}
		default:
			return state;
	}
};

// ===== TiDB Tree =====
export const tiDBFnTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case tiDBFnTreeAction.GET_LIBRA_ROOT:
			return assign({}, state, action.payload);
		case tiDBFnTreeAction.LOAD_FOLDER_CONTENT:
			return assign({}, state, action.payload);
		default:
			return state;
	}
};
export const tiDBSysFnTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case tiDBSysFnTreeActon.RESET_SYSFUC_TREE:
			return assign({}, state, action.payload);

		case tiDBSysFnTreeActon.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}
		default:
			return state;
	}
};

// ===== Oracle Tree =====
export const oracleSysFnTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case oracleSysFnTreeActon.RESET_SYSFUC_TREE:
			return assign({}, state, action.payload);

		case oracleSysFnTreeActon.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}
		default:
			return state;
	}
};

// ===== GreenPlum Tree =====
export const greenPlumTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case greenPlumTreeAction.GET_SPARK_ROOT:
			return assign({}, state, action.payload);
		case greenPlumTreeAction.LOAD_FOLDER_CONTENT:
			return assign({}, state, action.payload);
		default:
			return state;
	}
};

export const greenPlumSysFnTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case greenPlumSysFnTreeActon.RESET_SYSFUC_TREE:
			return assign({}, state, action.payload);

		case greenPlumSysFnTreeActon.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}
		default:
			return state;
	}
};

export const greenPlumFnTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case greenPlumFnTreeActon.RESET_FUC_TREE:
			return assign({}, state, action.payload);
		case greenPlumFnTreeActon.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}
		case greenPlumFnTreeActon.ADD_FOLDER_CHILD: {
			return addFolderChild(action, state);
		}

		case greenPlumFnTreeActon.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}

		case greenPlumFnTreeActon.DEL_OFFLINE_FN: {
			return deleteFolderChild(action, state);
		}

		case greenPlumFnTreeActon.EDIT_FOLDER_CHILD: {
			const payload = assign({}, action.payload, {
				parentId: action.payload.parentId,
			});
			const x = addFolderChild(action, deleteFolderChild({ payload: payload }, state));
			return x;
		}

		default:
			return state;
	}
};

export const greenPlumProdTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case greenPlumProdTreeActon.RESET_FUC_TREE:
			return assign({}, state, action.payload);

		case greenPlumProdTreeActon.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}

		case greenPlumProdTreeActon.ADD_FOLDER_CHILD: {
			return addFolderChild(action, state);
		}

		case greenPlumProdTreeActon.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}

		case greenPlumProdTreeActon.DEL_OFFLINE_FN: {
			return deleteFolderChild(action, state);
		}

		case greenPlumProdTreeActon.EDIT_FOLDER_CHILD: {
			const payload = assign({}, action.payload, {
				parentId: action.payload.originPid,
			});
			const x = addFolderChild(action, deleteFolderChild({ payload: payload }, state));
			return x;
		}

		default:
			return state;
	}
};

export const scriptTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case scriptTreeAction.RESET_SCRIPT_TREE:
			return assign({}, action.payload);

		case scriptTreeAction.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}

		case scriptTreeAction.ADD_FOLDER_CHILD: {
			return addFolderChild(action, state);
		}

		case scriptTreeAction.DEL_SCRIPT: {
			return deleteFolderChild(action, state);
		}

		case scriptTreeAction.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}

		case scriptTreeAction.EDIT_FOLDER_CHILD: {
			const payload = assign({}, action.payload, {
				parentId: action.payload.originPid,
			});
			return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
		}

		case scriptTreeAction.EDIT_FOLDER_CHILD_FIELDS: {
			const updated = cloneDeep(state);
			replaceTreeNode(updated, action.payload);
			return updated;
		}

		case scriptTreeAction.MERGE_FOLDER_CONTENT: {
			const origin = action.payload;
			if (origin) return origin;
			break;
		}

		default:
			return state;
	}
};

export const tableTreeReducer = (state: any = {}, action: any) => {
	switch (action.type) {
		case tableTreeAction.RESET_TABLE_TREE:
			return assign({}, action.payload);

		case tableTreeAction.LOAD_FOLDER_CONTENT: {
			return loadFolderContent(action, state);
		}

		case tableTreeAction.ADD_FOLDER_CHILD: {
			return addFolderChild(action, state);
		}

		case tableTreeAction.DEL_TABLE: {
			return deleteFolderChild(action, state);
		}

		case tableTreeAction.DEL_OFFLINE_FOLDER: {
			return deleteFolderChild(action, state);
		}

		case tableTreeAction.EDIT_FOLDER_CHILD: {
			const payload = assign({}, action.payload, {
				parentId: action.payload.originPid,
			});
			return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
		}

		default:
			return state;
	}
};

export const catalogueReducer = combineReducers({
	taskTree: taskTreeReducer,
	resourceTree: resourceTreeReducer,
	functionTree: functionTreeReducer,
	// sparkCustomFuncTree:sparkCustFuncTreeReducer,
	// sparkSystemFuncTreeData:sparkSysFunctionTreeReducer,
});
