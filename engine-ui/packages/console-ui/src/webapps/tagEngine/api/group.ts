import req from '../consts/reqGroup';
import http from './http';
import { IQueryParams } from '../model/comm';
import { IGroup } from '../model/group';

export interface IGroupsAnalysis {
    entityId: number | string;
    groupPojoIdList: { groupId?: number; groupName?: string }[];
    tagGroupList: { tagId?: number; tagName?: string }[];
}

export interface IGroupAnalysis {
    groupId?: number;
    taskId?: number;
    entityId?: number;
    uploadFileName?: string;
    entityAttrList?: { entityAttr?: string; entityAttrCn?: string }[];
}

export default {
    getGroups (params: IQueryParams) {
        return http.post(req.GET_GROUPS, params);
    },
    getGroup (group: { groupId: number }) {
        return http.post(req.GET_GROUP, group);
    },
    uploadGroup (params: any) {
        return http.post(req.UPLOAD_GROUP, params);
    },
    deleteGroup (params: { groupId: number }) {
        return http.post(req.DELETE_GROUP, params);
    },
    createOrUpdateGroup (group: IGroup) {
        return http.post(req.CREATE_OR_UPDATE_GROUP, group);
    },
    getGroupSpecimens (params: { groupId: number | string } & IQueryParams) {
        return http.post(req.GET_GROUP_SPECIMENS, params);
    },
    analysisGroup (params: IGroupAnalysis) {
        return http.post(req.ANALYSE_GROUP, params);
    },
    analysisGroups (params: IGroupsAnalysis) {
        return http.post(req.ANALYSE_GROUPS, params);
    },
    getGroupContactCount (params: { entityId?: number | string; groupIdList: number[] }) {
        return http.post(req.GET_GROUP_CONTACT_COUNT, params);
    },
    downloadGroupTemplate (params: {
        fileName: string; entityAttrList: { entityAttr?: string; entityAttrCn?: string }[];
    }) {
        // 此处需要把entityAttrList 转换成类似 ?fileName=&entityAttrList=attr1-attrCn,attr2-attrCn
        const entityAttrList = params.entityAttrList.map(o => `${o.entityAttr}-${o.entityAttrCn}`);
        return http.build(req.DOWNLOAD_GROUP_TEMPLATE, { fileName: params.fileName, entityAttrList: entityAttrList.join(',') });
    },
    /**
     *
     * @param params isOpenApi  0：关闭，1：开启

     */
    openAPI (params: { groupId: string | number; isOpenApi: 0 | 1 }) {
        return http.post(req.OPEN_API, params);
    }
}
