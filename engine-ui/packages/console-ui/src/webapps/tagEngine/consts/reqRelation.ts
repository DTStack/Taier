import { TAG_ENGINE_URL } from 'config/base';

export default {
    GET_RELATIONS: `${TAG_ENGINE_URL}/relation/relationPage`,
    GET_RELATION: `${TAG_ENGINE_URL}/relation/relationDetail`,
    CREATE_RELATION: `${TAG_ENGINE_URL}/relation/createRelation`,
    DELETE_RELATION: `${TAG_ENGINE_URL}/relation/deleteRelation`,
    UPDATE_RELATION: `${TAG_ENGINE_URL}/relation/updateRelation`
};
