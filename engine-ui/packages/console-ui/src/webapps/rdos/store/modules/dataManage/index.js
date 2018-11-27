import { combineReducers } from 'redux';
import assign from 'object-assign';
import { cloneDeep, isEqual } from 'lodash';
import { tableAction, logAction, cataloguesAction } from './actionType';

// move up/down
Array.prototype.__move = function (from, to) {
    this.splice(to, 0, this.splice(from, 1)[0]);
    return this;
};

const initTableManageState = {
    tableList: {
        listData: [],
        currentPage: 1,
        totalCount: 0
    },
    tableCurrent: {},
    isSavedSuccess: false
};

const tableManage = (state = initTableManageState, action) => {
    switch (action.type) {
    case tableAction.LOAD_TABLE_LIST: {
        const clone = cloneDeep(state);
        const { data, currentPage, totalCount } = action.payload;

        clone.tableList = {
            listData: data,
            currentPage,
            totalCount
        };
        return clone;
    }

    case tableAction.LOAD_TABLE_DETAIL: {
        const clone = cloneDeep(state);

        clone.tableCurrent = action.payload;
        return clone;
    }

    case tableAction.MODIFY_DESC: {
        const clone = cloneDeep(state);
        const { name, value } = action.payload;

        clone.tableCurrent[name] = value;
        return clone;
    }

    case tableAction.ADD_ROW: {
        const clone = cloneDeep(state);
        const { columns, partition_keys } = clone.tableCurrent;
        const { type, data } = action.payload;

        if (type === 1) {
            columns.push(data);
        } else if (type === 2) {
            partition_keys.push(data);
        }

        return clone;
    }

    case tableAction.DEL_ROW: {
        const clone = cloneDeep(state);
        const { columns, partition_keys } = clone.tableCurrent;
        const { type, uuid } = action.payload;

        if (type === 1) {
            clone.tableCurrent.columns = columns.filter(col => {
                return col.uuid !== uuid
            });
        } else if (type === 2) {
            clone.tableCurrent.partition_keys = partition_keys.filter(col => {
                return col.uuid !== uuid
            });
        }

        return clone;
    }

    case tableAction.REPLACE_ROW: {
        const clone = cloneDeep(state);
        const { columns, partition_keys } = clone.tableCurrent;
        const { newCol, type } = action.payload;
        let { uuid, id } = newCol;
        if (!uuid) {
            uuid = id;
        }

        if (type === 1) {
            clone.tableCurrent.columns = columns.map(col => {
                if (col.uuid === uuid || col.id === uuid) return newCol;
                else return col;
            });
        } else if (type === 2) {
            clone.tableCurrent.partition_keys = partition_keys.map(col => {
                if (col.uuid === uuid || col.id === uuid) return newCol;
                else return col;
            });
        }

        return clone;
    }

    case tableAction.MOVE_ROW: {
        const clone = cloneDeep(state);
        const { columns, partition_keys } = clone.tableCurrent;
        const { uuid, type, isUp } = action.payload;
        let p1 = [];
        let p2 = [];
        let from;

        if (type === 1) {
            columns.forEach((col, i) => {
                if (col.isSaved) p1.push(col);
                else p2.push(col);
            });

            p2.forEach((col, i) => {
                if (col.uuid === uuid) from = i;
            });

            if (isUp && from === 0) { return clone } // 过滤顶部移动

            p2 = p2.__move(from, isUp ? from - 1 : from + 1);
            clone.tableCurrent.columns = p1.concat(p2);
        } else if (type === 2) {
            partition_keys.forEach((col, i) => {
                if (col.isSaved) p1.push(col);
                else p2.push(col);
            });

            p2.forEach((col, i) => {
                if (col.uuid === uuid) from = i;
            });

            if (isUp && from === 0) { return clone } // 过滤顶部移动

            p2 = p2.__move(from, isUp ? from - 1 : from + 1);
            clone.tableCurrent.partition_keys = p1.concat(p2);
        }

        return clone;
    }
    case tableAction.SAVE_TABLE: {
        const clone = cloneDeep(state);
        clone.isSavedSuccess = action.payload == 1;
        console.log('SAVE_TABLE------:', clone);
        return clone;
    }
    default:
        return state;
    }
};

const log = (state = {
    projectUsers: []
}, action) => {
    switch (action.type) {
    case logAction.GET_USERS_SUC: {
        const clone = cloneDeep(state);

        clone.projectUsers = action.payload.data.map(o => ({
            userName: o.user.email,
            userId: o.userId
        }));
        return clone;
    }

    default:
        return state;
    }
}

const dataCatalogues = (state = {
    dataCatalogues: {}
}, action) => {
    switch (action.type) {
    case cataloguesAction.DATA_CATALOGUES : {
        return action.payload
    }
    default:
        return state;
    }
}

export const dataManageReducer = combineReducers({
    tableManage, log, dataCatalogues
});
