import api from "@/api";
import stream from "@/api/stream";
import { DATA_SOURCE_ENUM, DIRTY_DATA_SAVE, FLINK_SQL_TYPE, FLINK_VERSIONS, TASK_SAVE_ID, TASK_TYPE_ENUM } from "@/constant";
import { formatDateTime } from "@/utils";
import { isKafka } from "@/utils/enums";
import molecule from "@dtinsight/molecule";
import { message, Tag, Modal } from "antd";
import { intersection, debounce } from "lodash";
import { streamTaskActions, validTableData, cleanCollectionParams, resolveGraphData, validDataSource, validDataOutput, preparePage } from "./taskFunc";
const confirm = Modal.confirm;

const checkSide = (sides: any, componentVersion: string) => {
    if (sides) {
        for (let i = 0; i < sides.length; i++) {
            let side = sides[i];
            const { type, primaryKey, hbasePrimaryKey, hbasePrimaryKeyType } = side;
            switch (type) {
                case DATA_SOURCE_ENUM.REDIS:
                case DATA_SOURCE_ENUM.UPRedis: {
                    if (!primaryKey || !primaryKey.length) {
                        return `维表${i + 1}中的主键不能为空`;
                    }
                    return null;
                }
                case DATA_SOURCE_ENUM.HBASE:
                case DATA_SOURCE_ENUM.TBDS_HBASE:
                case DATA_SOURCE_ENUM.HBASE_HUAWEI: {
                    if (!hbasePrimaryKey) {
                        return `维表${i + 1}中的主键不能为空`;
                    }
                    if (!hbasePrimaryKeyType && componentVersion === '1.12') {
                        return `维表${i + 1}中的主键类型不能为空`;
                    }
                    return null;
                }
            }
        }
    }
}

const _saveTask = async (saveMode?: any, isSilent?: boolean | 'undefined') => {
    // let { outputData, dimensionData } = this.props;
    let currentPage = streamTaskActions.getCurrentPage();
    let { outputData, dimensionData } = currentPage
    const {
        targetMap,
        sourceMap,
        dataSourceList = [],
        resourceList = [],
        additionalResourceList = [],
        componentVersion,
        streamTaskDirtyDataManageVO,
        isDirtyDataManage
    } = currentPage;
    const isFlinkSQLGuide = currentPage.createModel == FLINK_SQL_TYPE.GUIDE || !currentPage.createModel;
    const isFlinkGraphData = currentPage.createModel == FLINK_SQL_TYPE.GRAPH;
    /**
     * 当目标数据源为Hive时，必须勾选Json平铺
     */
    if (currentPage.taskType == TASK_TYPE_ENUM.DATA_COLLECTION) {
        const haveJson = isKafka(sourceMap?.type) || sourceMap?.type === DATA_SOURCE_ENUM.EMQ || sourceMap?.type === DATA_SOURCE_ENUM.SOCKET;
        const index = dataSourceList.findIndex((v: any) => v.id == targetMap?.sourceId)
        if (index > -1 && dataSourceList[index].type === DATA_SOURCE_ENUM.HIVE && !sourceMap.pavingData && !haveJson) {
            message.error('请勾选嵌套Json平铺后重试');
            return;
        }
    }
    /**
     * 校验源表和结果表和维表
     */
    if (currentPage.taskType == TASK_TYPE_ENUM.FLINKSQL && isFlinkSQLGuide) {
        const { source = [], sink = [], side = [] } = currentPage
        const error = await validTableData(currentPage, { source, sink, side })
        if (error) return
    }
    // 检查页面输入输出参数配置
    const { checkFormParams: outputCheckFormParams = [] } = outputData[currentPage.id] || {};
    const { checkFormParams: dimensionCheckFormParams = [] } = dimensionData[currentPage.id] || {};
    if (outputCheckFormParams.length > 0) {
        for (let index = 0, len = outputCheckFormParams.length; index < len; index++) { // 检查出一个未填选项,不再检查其它的选项,只弹一次错误
            const result = outputCheckFormParams[index].checkParams();
            // 存储的是整个组件。。后面要重构掉
            const data = outputCheckFormParams[index]?.props;
            const tableName = data?.panelColumn[data.index]?.tableName;
            if (!result.status) {
                return message.error(`结果表${outputCheckFormParams[index].props.index + 1} ${tableName ? `(${tableName})` : ''}: ${result.message || '您还有未填选项'}`);
            }
        }
    }
    if (dimensionCheckFormParams.length > 0) {
        for (let index = 0, len = dimensionCheckFormParams.length; index < len; index++) { // 检查出一个未填选项,不再检查其它的选项,只弹一次错误
            const result = dimensionCheckFormParams[index].checkParams();
            const data = dimensionCheckFormParams[index]?.props;
            const tableName = data?.panelColumn[data.index]?.tableName;
            if (!result.status) {
                return message.error(`维表--维表${dimensionCheckFormParams[index].props.index + 1} ${tableName ? `(${tableName})` : ''}: ${result.message || '您还有未填选项'}`);
            }
        }
    }

    currentPage.preSave = true;

    if (resourceList) {
        currentPage.resourceIdList = resourceList.map((item: any) => item?.id || item)
    }
    if (Array.isArray(additionalResourceList)) {
        if (additionalResourceList.length > 10) {
            message.error('资源不可超过10个')
            return
        }
        
        if (additionalResourceList.length > 0 && intersection(additionalResourceList, currentPage.resourceIdList).length > 0) {
            message.error('资源与附加资源不可相同')
            return
        }
        currentPage.additionalResourceIdList = additionalResourceList.map((item: any) => item?.id || item)
    }

    const err = checkSide(currentPage.side, componentVersion);
    if (err) {
        message.error(err);
        return;
    }

    currentPage.lockVersion = currentPage.readWriteLockVO.version;

    currentPage = cleanCollectionParams(currentPage);
    /**
     * flink图操作的处理
     */
    if (currentPage.taskType == TASK_TYPE_ENUM.FLINKSQL && isFlinkGraphData) {
        currentPage = resolveGraphData(currentPage);
        if (!currentPage) {
            return;
        }
        const source = currentPage?.source?.[0];
        const sink = currentPage?.sink?.[0];
        let error: any = await validDataSource(source, componentVersion);
        if (error) {
            console.warn(error);
            message.error(`数据源表：${error[0].message}`);
            return;
        }
        error = await validDataOutput(sink, componentVersion);
        if (error) {
            console.warn(error);
            message.error(`数据结果表：${error[0].message}`);
            return;
        }
        if (!sink?.columns?.length) {
            console.warn(error);
            message.error(`数据结果表：请填写完整！`);
            return;
        }
    }

    /**
     * flink1.12 脏数据管理校验
     */
    const showDirtyManage = componentVersion === FLINK_VERSIONS.FLINK_1_12 && isDirtyDataManage;
    if (showDirtyManage) {
        if (streamTaskDirtyDataManageVO?.outputType === DIRTY_DATA_SAVE.BY_MYSQL && !streamTaskDirtyDataManageVO?.linkInfo?.sourceId) {
            message.error('任务设置 : 请选择脏数据写入库');
            return;
        }
        if (streamTaskDirtyDataManageVO?.maxRows < streamTaskDirtyDataManageVO?.logPrintInterval) {
            message.error('日志打印频率不可超过脏数据最大值');
            return;
        }
    }
    if (!currentPage) {
        return;
    }
    // 后端区分右键编辑保存
    currentPage.updateSource = true;
    return new Promise((resolve) => {
        currentPage = preparePage(currentPage);
        const uploadTask = () => {
            const { id } = currentPage;
            api.getOfflineTaskByID({ id }).then((res) => {
                const { success, data } = res;
                if (success) {
                    molecule.folderTree.update({
                        id,
                        data,
                    });
                    molecule.editor.updateActions([
                        {
                            id: TASK_SAVE_ID,
                            disabled: false,
                        }
                    ]);
                }
            });
        };
        const succCallback = (res: any) => {
            if (res.code === 1) {
                const fileData = res.data;
                const lockInfo = fileData.readWriteLockVO;
                const lockStatus = lockInfo?.result; // 1-正常，2-被锁定，3-需同步
                if (lockStatus === 0) {
                    message.success('保存成功！');
                    uploadTask();
                    // 如果是锁定状态，点击确定按钮，强制更新，否则，取消保存
                } else if (lockStatus === 1) {
                    // 2-被锁定
                    confirm({
                        title: '锁定提醒', // 锁定提示
                        content: (
                            <span>
                                文件正在被
                                {lockInfo.lastKeepLockUserName}
                                编辑中，开始编辑时间为
                                {formatDateTime(lockInfo.gmtModified)}。 强制保存可能导致
                                {lockInfo.lastKeepLockUserName}
                                对文件的修改无法正常保存！
                            </span>
                        ),
                        okText: '确定保存',
                        okType: 'danger',
                        cancelText: '取消',
                        onOk() {
                            const succCall = (successRes: any) => {
                                if (successRes.code === 1) {
                                    message.success('保存成功！');
                                    uploadTask();
                                }
                            };
                            api.forceUpdateOfflineTask(currentPage).then(succCall);
                        },
                    });
                    // 如果同步状态，则提示会覆盖代码，
                    // 点击确认，重新拉取代码并覆盖当前代码，取消则退出
                } else if (lockStatus === 2) {
                    // 2-需同步
                    confirm({
                        title: '保存警告',
                        content: (
                            <span>
                                文件已经被
                                {lockInfo.lastKeepLockUserName}
                                编辑过，编辑时间为
                                {formatDateTime(lockInfo.gmtModified)}。 点击确认按钮会
                                <Tag color="orange">覆盖</Tag>
                                您本地的代码，请您提前做好备份！
                            </span>
                        ),
                        okText: '确定覆盖',
                        okType: 'danger',
                        cancelText: '取消',
                        onOk() {
                            const reqParams: any = {
                                id: currentPage.id,
                                lockVersion: lockInfo.version,
                            };
                            // 更新version, getLock信息
                            api.getOfflineTaskDetail(reqParams).then((detailRes) => {
                                if (detailRes.code === 1) {
                                    const taskInfo = detailRes.data;
                                    taskInfo.merged = true;
                                    uploadTask();
                                }
                            });
                        },
                    });
                }
                return res;
            }
        };
        stream.saveTask(currentPage).then(succCallback);
    })
}

export const saveTask = debounce(_saveTask, 500, { maxWait: 2000 });
