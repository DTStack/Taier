export default {
    /* ----------------------标签类目-----------------------------------  */
    addOrUpdateTagCate: { // 标签引擎-新增/重命名标签层级
        method: 'post',
        url: `/api/v1/tagCate/addOrUpdateTagCate`
    },
    getTagCate: { // 标签引擎-查询标签层级目录
        method: 'post',
        url: `/api/v1/tagCate/getTagCate`
    },
    moveTagCate: { // 标签引擎-移动标签层级
        method: 'post',
        url: `/api/v1/tagCate/moveTagCate`
    },
    deleteTagCate: { // 标签引擎-删除标签层级
        method: 'post',
        url: `/api/v1/tagCate/deleteTagCate`
    },
    getSubTagCate: { // 标签引擎-获取子目录
        method: 'post',
        url: `/api/v1/tagCate/getSubTagCate`
    },
    /* ----------------------新建标签-----------------------------------  */
    addOrUpdateDeriveTag: {
        method: 'post',
        url: `/api/v1/tag/addOrUpdateDeriveTag`
    },
    getAtomTagValueList: {
        method: 'get',
        url: `/api/v1/tag/getAtomTagValueList`
    },
    getEntityAtomTagList: {
        method: 'get',
        url: `/api/v1/tag/getEntityAtomTagList`
    },
    getRelationList: {
        method: 'post',
        url: `/api/v1/tag/getRelationList`
    },
    /* ----------------------标签管理-----------------------------------  */
    getTagList: { // 标签引擎-标签查询接口
        method: 'post',
        url: `/api/v1/tag/getTagList`
    },
    getTagDetail: { // 标签-标签详情查询接口
        method: 'post',
        url: `/api/v1/tag/getTagDetail`
    },
    deleteTag: { // 删除标签
        method: 'post',
        url: '/api/v1/tag/deleteTag'
    },
    moveTag: { // 移动标签
        method: 'post',
        url: '/api/v1/tag/moveTag'
    },
    getTagRule: { // 获取原子标签规则
        method: 'post',
        url: '/api/v1/tag/getTagRule'
    }

};
