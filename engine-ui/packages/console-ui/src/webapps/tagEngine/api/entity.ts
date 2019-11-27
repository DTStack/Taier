import { TAG_ENGINE_URL } from 'config/base';

export default {
    // ================== 公用 ================== //
    getLabelType: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/common/dataType`
    },

    // ================== 实体 ================== //
    getEntities: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/entityPage`
    },
    deleteEntity: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/deleteEntity`
    },
    createEntity: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/createEntity`
    },
    selectDataSource: { // 获取数据源下拉
        method: 'post',
        url: `${TAG_ENGINE_URL}/dataSource/selectDataSource`
    },
    getDataTableList: { // 获取数据表下拉
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/dataTableList`
    },
    getColumnList: { // 获取数据表属性列表
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/columnList`
    },
    getColumnVals: { // 获取维度值详情
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/columnValues`
    },
    getEntityAttrs: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/entityAttrs`
    },
    checkEntityUserd: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/checkEntityUserd`
    },
    entityAttrsEdit: { // 实体详情 编辑维度名称
        method: 'post',
        url: `${TAG_ENGINE_URL}/entity/entityAttrsEdit`
    },

    // ================== 字典 ================== //
    getDictList: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/dict/getDictList`
    },
    addOrUpdateDict: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/dict/addOrUpdateDict`
    },
    getDictDetail: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/dict/getDictDetail`
    },
    deleteDict: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/dict/deleteDict`
    },
    dictCanDelete: {
        method: 'post',
        url: `${TAG_ENGINE_URL}/dict/canDelete`
    },
    getDictListByType: { // 通过类型 获取字典下拉列表
        method: 'post',
        url: `${TAG_ENGINE_URL}/dict/getDictListByType`
    }
};
