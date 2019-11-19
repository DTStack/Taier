import { TAG_ENGINE_URL } from 'config/base';

export default {
    GET_GROUPS: `${TAG_ENGINE_URL}/group/list`,
    GET_GROUP: `${TAG_ENGINE_URL}/group/info`,
    GET_GROUP_SPECIMENS: `${TAG_ENGINE_URL}/group/specimens`,
    CREATE_GROUP: `${TAG_ENGINE_URL}/group/create`,
    DELETE_GROUP: `${TAG_ENGINE_URL}/group/delete`,
    UPDATE_GROUP: `${TAG_ENGINE_URL}/group/update`,
    UPLOAD_GROUP: `${TAG_ENGINE_URL}/group/upload`
};
