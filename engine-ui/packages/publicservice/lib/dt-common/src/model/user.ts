/**
 * 用户
 */
export default interface User {
    id?: number;
    userName?: string;
    email?: string;
    /**
     * UIC 用户ID
     */
    dtuicUserId?: number | string;
    /**
     * 是否是租户管理员
     */
    isTenantAdmin?: boolean;
    /**
     * 租户创建者
     */
    isTenantCreator?: boolean;
    isDeleted?: boolean | number;
    status?: boolean | number;
    gmtCreate?: Date | number;
    gmtModified?: Date | number;
    defaultProjectId?: number;
}
