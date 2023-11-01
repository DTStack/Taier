import { useContext } from 'react';
import CopyToClipboard from 'react-copy-to-clipboard';
import { Badge, Button, Descriptions, message, Modal, Spin, Tooltip } from 'antd';
import { isNumber } from 'lodash';
import moment from 'moment';

import { CATALOGUE_TYPE } from '@/constant';
import context from '@/context';
import type { IDataSourceProps, IFunctionProps, IOfflineTaskProps } from '@/interface';
import LinkInfoCell from '@/pages/dataSource/linkInfoCell';
import { dataSourceService } from '@/services';
import { formatDateTime } from '@/utils';
import { TaskStatus, TaskTimeType } from '@/utils/enums';
import './index.scss';

interface IDetailInfoProps {
    type: CATALOGUE_TYPE | 'dataSource' | 'taskJob';
    data: Record<string, any>;
}

export default function DetailInfo({ type, data }: IDetailInfoProps) {
    const { supportJobTypes } = useContext(context);

    switch (type) {
        case CATALOGUE_TYPE.TASK: {
            const labelPrefix = '任务';
            const tab = data as IOfflineTaskProps;
            return (
                <Descriptions className="dt-taskinfo" bordered size="small" column={1}>
                    <Descriptions.Item label={`${labelPrefix}名称`}>{tab.name}</Descriptions.Item>
                    <Descriptions.Item label={`${labelPrefix}类型`}>
                        {supportJobTypes.find((t) => t.key === tab.taskType)?.value || '未知'}
                    </Descriptions.Item>
                    {isNumber(tab?.datasourceId) && (
                        <Descriptions.Item label="数据源">
                            {(() => {
                                const target = dataSourceService
                                    .getDataSource()
                                    .find((l) => l.dataInfoId === tab.datasourceId);

                                if (!target) return '未知数据源';

                                return `${target.dataName}(${target.dataType})`;
                            })()}
                        </Descriptions.Item>
                    )}
                    {tab?.componentVersion && (
                        <Descriptions.Item label="引擎版本">{tab?.componentVersion}</Descriptions.Item>
                    )}
                    <Descriptions.Item label="创建时间">{formatDateTime(tab.gmtCreate)}</Descriptions.Item>
                    <Descriptions.Item label="修改时间">{formatDateTime(tab.gmtModified)}</Descriptions.Item>
                    <Descriptions.Item label="描述">{tab.taskDesc || '-'}</Descriptions.Item>
                </Descriptions>
            );
        }
        case CATALOGUE_TYPE.FUNCTION: {
            const functionData = data as IFunctionProps;
            return (
                <Descriptions className="dt-taskinfo" bordered size="small" column={1}>
                    <Descriptions.Item label="函数名称">
                        <code>{functionData.name}</code>
                    </Descriptions.Item>
                    {functionData.className && (
                        <Descriptions.Item label="类名">{functionData.className}</Descriptions.Item>
                    )}
                    {functionData.sqlText && (
                        <Descriptions.Item label="SQL">{functionData.sqlText || '/'}</Descriptions.Item>
                    )}
                    <Descriptions.Item label="用途">{functionData.purpose || '/'}</Descriptions.Item>
                    <Descriptions.Item label="命令格式">
                        <code>{functionData.commandFormate || '/'}</code>
                    </Descriptions.Item>

                    <Descriptions.Item label="参数说明">{functionData.paramDesc || '/'}</Descriptions.Item>
                    <Descriptions.Item label="创建">{formatDateTime(functionData.gmtCreate)}</Descriptions.Item>
                    <Descriptions.Item label="最后修改">{formatDateTime(functionData.gmtModified)}</Descriptions.Item>
                </Descriptions>
            );
        }
        case CATALOGUE_TYPE.RESOURCE: {
            const resourceData: Record<string, any> = data;
            return (
                <Descriptions className="dt-taskinfo" bordered size="small" column={1}>
                    <Descriptions.Item label="资源名称">{resourceData.resourceName}</Descriptions.Item>
                    <Descriptions.Item label="资源描述">{resourceData.resourceDesc || '-'}</Descriptions.Item>
                    <Descriptions.Item label="存储路径">
                        <CopyToClipboard text={resourceData.url} onCopy={() => message.success('复制成功！')}>
                            <Tooltip title="点击复制">
                                <code className="cursor-pointer">{resourceData.url}</code>
                            </Tooltip>
                        </CopyToClipboard>
                    </Descriptions.Item>
                    <Descriptions.Item label="创建">
                        {resourceData.createUser.userName} 于{formatDateTime(resourceData.gmtCreate)}
                    </Descriptions.Item>
                    <Descriptions.Item label="修改时间">{formatDateTime(resourceData.gmtModified)}</Descriptions.Item>
                </Descriptions>
            );
        }
        case 'dataSource': {
            const dataSourceData = data as IDataSourceProps;
            return (
                <Descriptions className="dt-taskinfo" bordered size="small" column={1}>
                    <Descriptions.Item label="名称">{dataSourceData.dataName}</Descriptions.Item>
                    <Descriptions.Item label="类型">
                        {dataSourceData.dataType}
                        {dataSourceData.dataVersion || ''}
                    </Descriptions.Item>
                    <Descriptions.Item label="描述">{dataSourceData.dataDesc || '--'}</Descriptions.Item>
                    <Descriptions.Item label="连接信息">
                        <LinkInfoCell sourceData={dataSourceData} />
                    </Descriptions.Item>
                    <Descriptions.Item label="连接状态">
                        {dataSourceData.status === 0 ? (
                            <Badge status="error" text="连接失败" />
                        ) : (
                            <Badge status="success" text="正常" />
                        )}
                    </Descriptions.Item>
                    <Descriptions.Item label="修改时间">
                        {moment(dataSourceData.gmtModified).format('YYYY-MM-DD hh:mm:ss')}
                    </Descriptions.Item>
                </Descriptions>
            );
        }
        case 'taskJob': {
            return (
                <Descriptions className="dt-taskinfo" bordered size="small" column={1}>
                    <Descriptions.Item label="任务名称">{data.taskName || '-'}</Descriptions.Item>
                    <Descriptions.Item label="实例ID">
                        <CopyToClipboard text={data.jobId || '-'} onCopy={() => message.success('复制成功')}>
                            <Tooltip title="点击复制">
                                <code className="cursor-pointer">{data.jobId || '-'}</code>
                            </Tooltip>
                        </CopyToClipboard>
                    </Descriptions.Item>
                    <Descriptions.Item label="任务类型">
                        {supportJobTypes.find((t) => t.key === data.taskType)?.value || '未知'}
                    </Descriptions.Item>
                    <Descriptions.Item label="状态">
                        <TaskStatus value={data.status} />
                    </Descriptions.Item>
                    <Descriptions.Item label="调度周期">
                        <TaskTimeType value={data.taskPeriodId} />
                    </Descriptions.Item>
                    <Descriptions.Item label="计划时间">{data.cycTime}</Descriptions.Item>
                </Descriptions>
            );
        }
        default:
            return null;
    }
}

export interface IDetailModalProps {
    visible?: boolean;
    title?: string;
    loading?: boolean;
    type: IDetailInfoProps['type'];
    data?: IDetailInfoProps['data'];
    onCancel?: () => void;
}

/**
 * 详情模态框，用于展示函数或资源详情
 */
export function DetailInfoModal({ visible, loading, title, type, data, onCancel }: IDetailModalProps) {
    return (
        <Modal
            title={title}
            visible={visible}
            onCancel={onCancel}
            width={550}
            footer={
                <Button type="default" onClick={onCancel}>
                    关闭
                </Button>
            }
        >
            <Spin spinning={loading}>{data ? <DetailInfo type={type} data={data} /> : '系统异常'}</Spin>
        </Modal>
    );
}
