import { TAG_ENGINE_URL } from 'config/base';

export default {
    GET_GROUPS: `${TAG_ENGINE_URL}/group/getGroupList`,
    GET_GROUP: `${TAG_ENGINE_URL}/group/getGroupDetail`,
    GET_GROUP_SPECIMENS: `${TAG_ENGINE_URL}/group/getGroupContactCount`, // 重合度
    CREATE_OR_UPDATE_GROUP: `${TAG_ENGINE_URL}/group/addOrUpdateGroup`,
    DELETE_GROUP: `${TAG_ENGINE_URL}/group/delete`,
    UPLOAD_GROUP: `${TAG_ENGINE_URL}/group/uploadModule`,
    ANALYSE_GROUPS: `${TAG_ENGINE_URL}/group/groupUpdateAnalysis`, // 组群分析
    ANALYSE_GROUP: `${TAG_ENGINE_URL}/group/groupsAnalysis` // 群组分析
};
