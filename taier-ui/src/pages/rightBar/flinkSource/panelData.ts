import type { DefaultOptionType } from 'antd/lib/cascader';

import stream from '@/api';

const mapToArray = (data: Partial<DefaultOptionType>, dataMap: Record<string, any>) => {
    const names = Object.getOwnPropertyNames(dataMap);
    if (names.length === 0) {
        data.children = undefined;
    }
    for (let i = 0; i < names.length; i += 1) {
        const name = names[i];
        const item: DefaultOptionType = {
            value: name,
            label: name,
            children: [],
        };
        data.children?.push(item);
        if (dataMap[name]) {
            mapToArray(item, dataMap[name]);
        }
    }
};

// 将接口数据处理成 Cascader 组件所需的数据类型
const handTimeZoneData = (data: string[]) => {
    if (data && data.length === 0) return [];
    const result: { children: DefaultOptionType[] } = { children: [] };
    const map: Record<string, any> = {};
    for (let i = 0; i < data.length; i += 1) {
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
    return new Promise<DefaultOptionType[]>((resolve) => {
        stream.getTimeZoneList().then((res) => {
            const timeZoneData = handTimeZoneData(res.data || []);
            resolve(timeZoneData);
        });
    });
}
