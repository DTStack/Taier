import { TAG_ENGINE_URL } from 'config/base';

export default {
    GET_GROUPS: `${TAG_ENGINE_URL}/group/getGroupList`,
    GET_GROUP: `${TAG_ENGINE_URL}/group/getGroupDetail`,
    GET_GROUP_SPECIMENS: `${TAG_ENGINE_URL}/group/group`, // 样本列表
    GET_GROUP_CONTACT_COUNT: `${TAG_ENGINE_URL}/group/getGroupContactCount`, // 组群重合度查询
    CREATE_OR_UPDATE_GROUP: `${TAG_ENGINE_URL}/group/addOrUpdateGroup`,
    DELETE_GROUP: `${TAG_ENGINE_URL}/group/deleteGroup`,
    UPLOAD_GROUP: `${TAG_ENGINE_URL}/group/uploadModule`,
    ANALYSE_GROUP: `${TAG_ENGINE_URL}/group/groupUpdateAnalysis`, // 组群分析
    ANALYSE_GROUPS: `${TAG_ENGINE_URL}/group/groupsAnalysis`, // 群组分析
    DOWNLOAD_GROUP_TEMPLATE: `${TAG_ENGINE_URL}/group/downloadModule`, // 下载模板
    OPEN_API: `${TAG_ENGINE_URL}/group/isOpenApi` // 是否开启api
};
