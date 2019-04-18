import { combineReducers } from 'redux';

import { componentFilesType } from '../../../consts/actionType/filesType';
import { updateTreeNode, replaceTreeNode } from '../../helper';
import * as graph from './graph';
const initState = [{
    'id': 1297,
    'parentId': 1296,
    'name': '原始数据层(ODS)',
    'level': 2,
    'type': 'folder',
    'taskType': null,
    'resourceType': null,
    'catalogueType': 'TaskDevelop',
    'createUser': null,
    'orderVal': null,
    'children': [
        {
            'id': 3176,
            'parentId': 1297,
            'name': '11',
            'level': 3,
            'type': 'folder',
            'taskType': null,
            'resourceType': null,
            'catalogueType': 'TaskDevelop',
            'createUser': null,
            'orderVal': null,
            'children': null,
            'readWriteLockVO': null,
            'version': 0,
            'operateModel': 1,
            'pythonVersion': 0,
            'learningType': 0,
            'scriptType': null,
            'isSubTask': 0
        },
        {
            'id': 1148,
            'parentId': 1297,
            'name': 'exam_ods_ddl',
            'level': 3,
            'type': 'file',
            'taskType': 0,
            'resourceType': null,
            'catalogueType': null,
            'createUser': 'admin@dtstack.com',
            'orderVal': null,
            'children': null,
            'readWriteLockVO': {
                'id': 1294,
                'gmtCreate': 1538639182000,
                'gmtModified': 1538639182000,
                'isDeleted': 0,
                'lockName': '1148_86_BATCH_TASK',
                'modifyUserId': 108,
                'version': 1,
                'projectId': 86,
                'relationId': 1148,
                'type': 'BATCH_TASK',
                'lastKeepLockUserName': 'admin@dtstack.com',
                'result': 0,
                'getLock': true
            },
            'version': 0,
            'operateModel': 1,
            'pythonVersion': 0,
            'learningType': 0,
            'scriptType': null,
            'isSubTask': 0
        }
    ],
    'readWriteLockVO': null,
    'version': 0,
    'operateModel': 1,
    'pythonVersion': 0,
    'learningType': 0,
    'scriptType': null,
    'isSubTask': 0
}]
function files (state = initState, action) {
    const { type, payload } = action;
    switch (type) {
        case componentFilesType.UPDATE_TREE_NODE: {
            return updateTreeNode(state, payload) || state;
        }
        case componentFilesType.REPLACE_TREE_NODE: {
            return replaceTreeNode(state, payload) || state;
        }
        case componentFilesType.INIT_TREE: {
            return payload;
        }
        default: {
            return state;
        }
    }
}

export default combineReducers({
    files,
    ...graph
});
