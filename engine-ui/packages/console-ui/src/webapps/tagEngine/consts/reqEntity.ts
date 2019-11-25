import { TAG_ENGINE_URL } from 'config/base';

export default {
    // ================== 实体 ================== //
    GET_ENTITIES: `${TAG_ENGINE_URL}/entity/entityPage`,
    CREATE_ENTITY: `${TAG_ENGINE_URL}/entity/createEntity`,
    GET_ENTITY_ATTRS: `${TAG_ENGINE_URL}/entity/entityAttrs`,
    DELETE_ENTITY: `${TAG_ENGINE_URL}/entity/deleteEntity`,

    // ================== 字典 ================== //
    GET_DICTIONARIES: `${TAG_ENGINE_URL}/dict/getDictList`, // 获取字典列表
    ADD_OR_UPDATE_DICT: `${TAG_ENGINE_URL}/dict/addOrUpdateDict`, // 新增/更新字典
    GET_DICT_DETAIL: `${TAG_ENGINE_URL}/dict/getDictDetail`, // 获取自定义字典详情
    DELETE_DICT: `${TAG_ENGINE_URL}/dict/deleteDict`, // 删除字典
    DICT_CAN_DELETE: `${TAG_ENGINE_URL}/dict/canDelete` // 删除字典
};
