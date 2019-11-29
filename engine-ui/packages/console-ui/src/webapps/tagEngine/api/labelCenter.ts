export default {
    selectEntity: { // 实体筛选列表
        method: 'post',
        url: `/api/v1/entity/selectEntity`
    },
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
    getAtomTagValueList: { // 获取原子标签值列表
        method: 'post',
        url: `/api/v1/tag/getAtomTagValueList`
    },
    getEntityAtomTagList: { // 获取实体列表
        method: 'post',
        url: `/api/v1/tag/getEntityList`
    },
    getAtomTagList: { // 获取原子标签列表
        method: 'post',
        url: ` /api/v1/tag/getAtomTagList`
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
    },
    getEditorDetailVo: { // 编辑详情接口
        method: 'post',
        url: '/api/v1/tag/getEditorDetailVo'
    },
    selectEntityAttrs: { // 数据维度-筛选列表
        method: 'post',
        url: '/api/v1/entity/selectEntityAttrs '
    },
    getDataType: { // 标签数据类型
        method: 'post',
        url: '/api/v1/common/dataType'
    },
    getDictListByType: { // 获取字典引用
        method: 'post',
        url: '/api/v1/dict/getDictListByType'
    },
    editorAtomTagRule: { // 编辑原子标签规则
        method: 'post',
        url: '/api/v1/tag/editorAtomTagRule'
    },
    getGroupTag: { // 获取群组对比标签
        method: 'post',
        url: '/api/v1/tag/getGroupTag'
    }
};
