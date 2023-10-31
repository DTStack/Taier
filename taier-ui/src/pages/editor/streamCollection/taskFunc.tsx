import molecule from '@dtinsight/molecule';
import { cloneDeep, isEmpty } from 'lodash';

import {
    CAT_TYPE,
    COLLECT_TYPE,
    DATA_SOURCE_ENUM,
    KAFKA_DATA_TYPE,
    QOS_TYPE,
    READ_MODE_TYPE,
    RESTFUL_METHOD,
    RESTFUL_PROPTOCOL,
    RESTFUL_RESP_MODE,
    SLOAR_CONFIG_TYPE,
    SOURCE_TIME_TYPE,
    SYNC_TYPE,
} from '@/constant';
import { isHaveTableColumn,isKafka } from '@/utils/is';

export const UnlimitedSpeed = '不限制上传速率';

// 校验字段信息
export function checkColumnsData(rule: any, value: any, callback: any, source: any) {
    if (isHaveTableColumn(source?.type)) {
        if (isEmpty(value) || value?.some((item: any) => isEmpty(item))) {
            const err = '请填写字段信息';
            return callback(err);
        }
        if (value?.some((item: { type: string }) => item.type?.toLowerCase() === 'Not Support'.toLowerCase())) {
            const err = '字段中存在不支持的类型，请重新填写';
            return callback(err);
        }
    }
    callback();
}

export interface SettingMap {
    speed?: number | string;
    readerChannel?: number;
    writerChannel?: number;
    isSaveDirty?: boolean;
    sourceId?: number;
    tableName?: string;
    lifeDay?: number;
}
export const streamTaskActions = {
    initState: {
        // 实时任务初始化数据
        currentStep: null,
        sourceMap: {
            type: undefined,
            port: undefined,
            table: [],
            sourceId: undefined,
            collectType: COLLECT_TYPE.ALL,
            cat: [CAT_TYPE.INSERT, CAT_TYPE.UPDATE, CAT_TYPE.DELETE],
            pavingData: false,
            rdbmsDaType: SYNC_TYPE.BINLOG,
            startSCN: undefined,
            codec: 'plain',
            qos: QOS_TYPE.EXACTLY_ONCE,
            isCleanSession: true,
            parse: 'text',
            decoder: READ_MODE_TYPE.LENGTH,
            codecType: 'text',
            collectPoint: 'taskRun',
            requestMode: RESTFUL_METHOD[1].value,
            decode: RESTFUL_RESP_MODE[0].value,
            protocol: RESTFUL_PROPTOCOL[0].value,
            startLocation: undefined,
            temporary: false,
            slotConfig: Number(Object.keys(SLOAR_CONFIG_TYPE)[0]),
            mode: 'group-offsets',
        },
        targetMap: {
            sourceId: undefined,
            topic: undefined,
            isCleanSession: true,
            qos: QOS_TYPE.EXACTLY_ONCE,
        },
        settingMap: {
            // 通道控制
            speed: -1,
            readerChannel: '1',
            writerChannel: 1,
        },
    },
    /**
     * 获取当前任务数据
     * @returns
     */
    getCurrentPage() {
        const state = molecule.editor.getState();
        return cloneDeep(state.current?.tab?.data);
    },
    /**
     * 更新当前任务数据
     * @param key 字段名
     * @param value 对应字段值
     * @param isDirty 是否有修改任务
     */
    setCurrentPageValue(key: any, value: any, isDirty?: any) {
        const page = this.getCurrentPage();
        const state = molecule.editor.getState();
        const tab: any = state.current?.tab || {};

        page[key] = value;
        if (typeof isDirty == 'boolean') {
            page.notSynced = isDirty;
        }

        tab['data'] = page;
        molecule.editor.updateTab(tab);
    },
    setCurrentPage(data: any) {
        const state = molecule.editor.getState();
        const tab: any = state.current?.tab || {};
        tab['data'] = data;
        molecule.editor.updateTab(tab);
    },
    updateCurrentPage(data: any) {
        let page = this.getCurrentPage();
        page = Object.assign({}, page, data);

        this.setCurrentPage(page);
    },
    navtoStep(step: number) {
        this.setCurrentPageValue('currentStep', step);
    },
    exchangeDistributeTable(distributeTable: any) {
        if (!distributeTable) {
            return [];
        }
        const KeyAndValue = Object.entries(distributeTable);
        return KeyAndValue.map(([name, tables]) => {
            return {
                name,
                tables,
                isSaved: true,
            };
        });
    },
    initCurrentPage() {
        const page = cloneDeep(this.getCurrentPage());
        this.setCurrentPage({ ...page, ...this.initState });
    },
    /**
     * 获取实时采集task初始化信息
     * @param {Int} taskId
     */
    initCollectionTask() {
        const page = this.getCurrentPage();
        /**
         * 假如已经存在这个属性，则说明当前的task不是第一次打开，所以采用原来的数据
         */
        if (typeof page.currentStep != 'undefined') {
            return;
        }
        if (page.submitted) {
            this.setCurrentPageValue('isEdit', true);
        }
        const { sourceMap, targetMap } = page;
        if (!isEmpty(sourceMap) || !isEmpty(targetMap)) {
            sourceMap.pavingData = !!sourceMap.pavingData;
            sourceMap.multipleTable = sourceMap.multipleTable || false;
            if (sourceMap.journalName || sourceMap.startSCN) {
                sourceMap.collectType = COLLECT_TYPE.FILE;
            } else if (sourceMap.timestamp) {
                sourceMap.collectType = COLLECT_TYPE.TIME;
            } else if (sourceMap.lsn) {
                sourceMap.collectType = COLLECT_TYPE.LSN;
            } else {
                sourceMap.collectType = COLLECT_TYPE.ALL;
            }
            if (sourceMap.distributeTable) {
                sourceMap.distributeTable = this.exchangeDistributeTable(sourceMap.distributeTable);
                sourceMap.multipleTable = true;
            }
            /**
             * 针对 kafka 的 codec, timestamp 做处理
             */
            if (isKafka(sourceMap.type)) {
                sourceMap.collectType = undefined;
                sourceMap.codec = sourceMap.codec || KAFKA_DATA_TYPE.TYPE_COLLECT_JSON;
            }
            if (
                [
                    DATA_SOURCE_ENUM.KAFKA,
                    DATA_SOURCE_ENUM.KAFKA_2X,
                    DATA_SOURCE_ENUM.TBDS_KAFKA,
                    DATA_SOURCE_ENUM.KAFKA_HUAWEI,
                ].includes(targetMap.type)
            ) {
                targetMap.dataSequence = targetMap.dataSequence || false;
            }
            this.updateSourceMap(page.sourceMap, false, true);
            this.updateTargetMap(page.targetMap, false, true);
            this.updateChannelControlMap(page.setting, false, true);
            this.setCurrentPageValue('currentStep', 3);
        } else {
            this.initCurrentPage();
            this.setCurrentPageValue('currentStep', 0);
        }
    },

    updateSourceMap(params: any = {}, clear: any = false, notDirty: any = false) {
        const page = this.getCurrentPage();
        let sourceMap = page?.sourceMap || {};
        if (clear) {
            sourceMap = {
                ...this.initState.sourceMap,
                type: sourceMap?.type,
                sourceId: sourceMap?.sourceId,
                rdbmsDaType: sourceMap?.rdbmsDaType,
                multipleTable: false,
                pavingData: sourceMap?.type == DATA_SOURCE_ENUM.POSTGRESQL,
                codec: isKafka(sourceMap?.type) ? KAFKA_DATA_TYPE.TYPE_COLLECT_JSON : 'plain',
            };
            this.setCurrentPageValue('targetMap', cloneDeep(this.initState.targetMap));
        }
        if (params.distributeTable) {
            const tables = params.distributeTable.reduce((prevValue: any, item: any) => {
                return prevValue.concat(item.tables);
            }, []);
            params.table = tables;
        }
        this.setCurrentPageValue(
            'sourceMap',
            cloneDeep({
                ...sourceMap,
                ...params,
            }),
            !notDirty
        );
    },

    updateTargetMap(params = {}, clear: any, notDirty = false) {
        const page = this.getCurrentPage();
        let targetMap = page?.targetMap || {};
        if (clear) {
            targetMap = this.initState.targetMap;
        }
        this.setCurrentPageValue(
            'targetMap',
            cloneDeep({
                ...targetMap,
                ...params,
            }),
            notDirty ? undefined : true
        );
    },

    updateChannelControlMap(params: SettingMap = {}, clear: any = false, notDirty = false) {
        const page = this.getCurrentPage();
        let settingMap = page?.settingMap || {};
        if (clear) {
            settingMap = this.initState.settingMap;
        }
        if (params.isSaveDirty == false) {
            params.sourceId = undefined;
            params.tableName = undefined;
            params.lifeDay = undefined;
        } else if (params.isSaveDirty) {
            params.lifeDay = 90;
        }
        if (params.speed === UnlimitedSpeed) params.speed = -1;
        this.setCurrentPageValue(
            'settingMap',
            cloneDeep({
                ...settingMap,
                ...params,
            }),
            !notDirty
        );
    },
};

// 对多资源附加资源 resourceTree 的处理
// 初始时，在基础上拼接没请求到的 additionalResourceList
export function joinAdditionTree(tree: any, addtionalTree: any[], resourceTree?: any[]) {
    const sourceTree = resourceTree ? [...addtionalTree, ...resourceTree] : addtionalTree;
    for (let i = 0; i < tree.length; i++) {
        const nodeTree = tree[i];
        const children = (nodeTree.children = nodeTree.children || []);
        if (!(nodeTree.type === 'folder')) {
            break;
        }
        if (!nodeTree.children) {
            nodeTree.children = [];
        }
        if (children.length > 0) {
            joinAdditionTree(children, addtionalTree, resourceTree);
        }
        const childrenList = sourceTree.filter((item: any) => item.nodePid === nodeTree?.id);
        if (childrenList.length > 0) {
            childrenList.forEach((child: any) => {
                if (!children.map((item: any) => item.id).includes(child.id)) {
                    const nodeChild = formatTreeChild(nodeTree, child);
                    children.push(nodeChild);
                }
            });
        }
    }
    return tree;
}

// 对 node 参数稍作处理
function formatTreeChild(parent: any, child: any) {
    const result = {
        ...child,
        parentId: child.nodePid,
        name: child.resourceName,
        level: parent.level + 1,
        type: 'file',
        catalogueType: null,
        readWriteLockVO: null,
        children: null,
        taskType: null,
        createUser: child.createUser || '',
    };
    return result;
}

// 遍历树形节点，用新节点替换老节点
export function replaceTreeNode(treeNode: any, replace: any) {
    if (treeNode.id === parseInt(replace.id, 10) && treeNode.type == replace.type) {
        // 多级目录的情况处理
        const replaceChildren = replace.children || [];
        const regionChildren = treeNode.children || [];
        replaceChildren.forEach((child: any) => {
            const sameChild = regionChildren.find((item: any) => item.id === child.id);
            if (sameChild && sameChild.children && sameChild.children.length && !child.children) {
                child.children = sameChild.children;
            }
        });
        Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.children) {
        const children = treeNode.children;
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace);
        }
    }
}

interface Task {
    componentVersion: string;
    source: {
        offset: number;
        offsetUnit: string;
        timeType: number;
        timeTypeArr: number[];
        procTime: string;
    }[];
}

/**
 * 切换 flink 版本时，源表的时间特征会在 timeType 和 timeTypeArr 两个字段切换
 * flink1.12 时 timeTypeArr 字段只在前端使用
 * @param oldTask - 用于比较 componentVersion 是否变化
 * @param newTask - 用于接口传参的 task，会改变 newTask 本身！
 */
export function transformTimeType(oldTask: Task, newTask: Task) {
    // 切换为 1.12 时，根据 timeType 修改 timeTypeArr
    if (oldTask.componentVersion !== '1.12' && newTask.componentVersion === '1.12') {
        for (const form of newTask?.source || []) {
            form.timeTypeArr = form.timeType === SOURCE_TIME_TYPE.EVENT_TIME ? [1, 2] : [SOURCE_TIME_TYPE.PROC_TIME];
            // timeType 勾选 procTime 时，带上默认名称
            form.procTime = 'proc_time';
        }
    }
    // 1.12 切换为其他版本时，根据 timeTypeArr 修改 timeType
    if (oldTask.componentVersion === '1.12' && newTask.componentVersion !== '1.12') {
        for (const form of newTask?.source || []) {
            form.timeType = form.timeTypeArr?.includes(SOURCE_TIME_TYPE.EVENT_TIME)
                ? SOURCE_TIME_TYPE.EVENT_TIME
                : SOURCE_TIME_TYPE.PROC_TIME;
        }
    }
}

/**
 * 切换 flink 版本时，源表的 offset 的时间单位需要进行转化
 * @param oldTask - 用于比较 componentVersion 是否变化
 * @param newTask - 用于接口传参的 task，会改变 newTask 本身！
 */
export function transformOffsetUnit(oldTask: Task, newTask: Task) {
    // 切换为 1.12 时，时间单位由 ms 变为 s
    if (oldTask.componentVersion !== '1.12' && newTask.componentVersion === '1.12') {
        for (const form of newTask?.source || []) {
            form.offset = form.offset / 1000;
            form.offsetUnit = 'SECOND';
        }
    }
    // 1.12 切换为其他版本时，单位转化为 ms
    if (oldTask.componentVersion === '1.12' && newTask.componentVersion !== '1.12') {
        // 各单位转换为秒
        const unit: any = {
            SECOND: 1,
            MINUTE: 60,
            HOUR: 3600,
            DAY: 86400,
            MONTH: 86400 * 30,
            YEAR: 86400 * 365,
        };
        for (const form of newTask?.source || []) {
            form.offset = unit[form.offsetUnit] * form.offset * 1000;
            // flink 版本不是 1.12 也不会读取这个字段，不改也行
            form.offsetUnit = 'ms';
        }
    }
}
