import { dataSourceListAction } from './actionTypes';

// 缓存数据源列表
export const dataSourceList = (state = [], action: any) => {
    switch (action.type) {
        case dataSourceListAction.LOAD_DATASOURCE: {
            const dataSource = action.payload;
            return dataSource;
        }
        default: return state;
    }
};
