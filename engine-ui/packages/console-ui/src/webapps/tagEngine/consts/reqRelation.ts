import { TAG_ENGINE_URL } from 'config/base';

export default {
    GET_RELATIONS: `${TAG_ENGINE_URL}/relation/list`,
    GET_RELATION: `${TAG_ENGINE_URL}/relation/info`,
    CREATE_RELATION: `${TAG_ENGINE_URL}/relation/create`,
    DELETE_RELATION: `${TAG_ENGINE_URL}/relation/delete`,
    UPDATE_RELATION: `${TAG_ENGINE_URL}/relation/update`
};
