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

export interface IGroup {
    groupId?: number;
    entityId?: number;
    groupType?: IGroupType;
    groupName?: string;
    groupDesc?: string;
    /**
     * 创建者
     */
    createBy?: string;
    /**
     * 组群数据量
     */
    groupDataCount?: number;
    createAt?: Date | string | number;
    updateAt?: Date | string | number;
}
