import req from '../consts/reqGroup';
import http from './http';
import { IReqParams } from '../model/comm';
import IGroup from '../model/group';

interface IGroupAnalysis {
    entityId?: number;
    groupPojoIdList?: { groupId?: number; groupName?: string }[];
    tagGroupList: { tagId?: number; tagName?: string }[];
}

interface IAnalysisGroups {
    taskId?: number;
    entityId?: number;
    uploadFileName?: string;
    uploadFileType?: string;
    entityAttrList?: { entityAttr?: string; entityAttrCn?: string }[];
}

export default {
    getGroups (params?: IReqParams) {
        return http.post(req.GET_GROUPS, params);
    },
    getGroup (group?: { groupId: number }) {
        return http.post(req.GET_GROUP, group);
    },
    uploadGroup (params?: any) {
        return http.post(req.UPLOAD_GROUP, params);
    },
    deleteGroup (params?: any) {
        return http.post(req.DELETE_GROUP, params);
    },
    createOrUpdateGroup (group?: IGroup) {
        return http.post(req.CREATE_OR_UPDATE_GROUP, group);
    },
    getGroupSpecimens (params?: any) {
        return http.post(req.GET_GROUP_SPECIMENS, params);
    },
    analysisGroup (params?: IGroupAnalysis) {
        return http.post(req.ANALYSE_GROUP, params);
    },
    analysisGroups (params?: IAnalysisGroups) {
        return http.post(req.ANALYSE_GROUPS, params);
    },
    getGroupContactCount (params?: { entityId?: number; groupIdList: number[] }) {
        return http.post(req.ANALYSE_GROUP, params);
    },
    downloadGroupTemplate (params?: { fileName: string }) {
        return http.post(req.GET_GROUP_SPECIMENS, params);
    }
}
