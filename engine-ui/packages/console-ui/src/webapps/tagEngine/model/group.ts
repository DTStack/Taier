export enum IGroupType {
    /**
     * 按规则
     */
    REGULAR = 0,
    /**
     * 通过上传
     */
    UPLOAD = 1
}

export default interface IGroup {
    groupId?: number;
    entityId?: number;
    groupType?: IGroupType;
    groupName?: string;
    groupDesc?: string;
}
