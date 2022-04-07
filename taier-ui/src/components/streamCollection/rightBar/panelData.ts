import stream from "@/api/stream"
import { FLINK_SQL_TYPE, TASK_TYPE_ENUM } from "@/constant"
import { streamTaskActions } from "../taskFunc"

export const getFlinkVersion = () => {
    return new Promise<string[]>((resolve, reject) => {
        stream.getFlinkVersion().then((res: any) => {
            if (res.code === 1) {
                resolve(res.data as string[])
            }
        })
    })
}
// 获取表来源
export function getCreateTypes () {
    return new Promise<any[]>((resolve, reject) => {
        stream.getCreateTypes().then((res: any) => {
            if (res.code === 1) {
                resolve(res.data as any[])
            }
        })
    })
}
const mapToArray = (data: any, dataMap: any) => {
    const names = Object.getOwnPropertyNames(dataMap);
    if (names.length === 0) {
        data.children = undefined;
    }
    for (let i = 0; i < names.length; i++) {
        const name = names[i];
        const item: any = {
            value: name,
            label: name,
            children: []
        };
        data.children.push(item);
        if (dataMap[name]) {
            mapToArray(item, dataMap[name]);
        }
    }
}
const handTimeZoneData = (data: any) => {
    if (data && data.length === 0) return [];
    const result: any = { children: [] };
    const map: any = {};
    for (let i = 0; i < data.length; i++) {
        const keys = data[i].split('/');
        const key1 = keys[0];
        const key2 = keys[1];
        const key3 = keys[2];

        if (key1 && !map[key1]) {
            map[key1] = {};
        } else if (key2 && !map[key1][key2]) {
            map[key1][key2] = {};
        } else if (key3 && !map[key1][key2][key3]) {
            map[key1][key2][key3] = {};
        }
    }

    mapToArray(result, map);
    return result.children || [];
};
export function getTimeZoneList() {
    return new Promise<any[]>((resolve, reject) => {
        stream.getTimeZoneList().then((res: any) => {
            const timeZoneData = handTimeZoneData(res.data || []);
            resolve(timeZoneData)
        })
    })
}
// 获取元数据 - 数据库列表
export const getDataBaseList = async (createTypes: any[]) => {
    const currentPage = streamTaskActions.getCurrentPage();
    const { taskType, createModel }: any = currentPage || {};
    const isGuideMode = createModel === FLINK_SQL_TYPE.GUIDE || !createModel;
    if (taskType === TASK_TYPE_ENUM.SQL && isGuideMode && createTypes?.length) {
        const res = await stream.getDBList();
        if (res.code === 1) {
            return res.data || [];
        }
    }
    return []
}