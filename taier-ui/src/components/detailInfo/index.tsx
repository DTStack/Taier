import { Button, Descriptions, message, Modal, Spin, Tooltip } from 'antd';
import CopyToClipboard from 'react-copy-to-clipboard';
import { CATELOGUE_TYPE } from '@/constant';
import type { IFunctionProps, IOfflineTaskProps } from '@/interface';
import { formatDateTime } from '@/utils';
import { taskTypeText } from '@/utils/enums';
import { CopyOutlined } from '@ant-design/icons';
import './index.scss';

interface IDetailInfoProps {
	type: CATELOGUE_TYPE;
	data: Record<string, any>;
}

export default function DetailInfo({ type, data }: IDetailInfoProps) {
	switch (type) {
		case CATELOGUE_TYPE.TASK: {
			const labelPrefix = '任务';
			const tab = data as IOfflineTaskProps;
			return (
				<Descriptions className="dt-taskinfo" bordered size="small">
					<Descriptions.Item label={`${labelPrefix}名称：`} span={12}>
						{tab.name}
					</Descriptions.Item>
					<Descriptions.Item label={`${labelPrefix}类型：`} span={12}>
						{taskTypeText(tab.taskType)}
					</Descriptions.Item>
					{tab?.componentVersion && (
						<Descriptions.Item label="引擎版本：" span={12}>
							{tab?.componentVersion}
						</Descriptions.Item>
					)}
					<Descriptions.Item label="创建时间：" span={12}>
						{formatDateTime(tab.gmtCreate)}
					</Descriptions.Item>
					<Descriptions.Item label="修改时间：" span={12}>
						{formatDateTime(tab.gmtModified)}
					</Descriptions.Item>
					<Descriptions.Item label="描述：" span={12}>
						{tab.taskDesc || '-'}
					</Descriptions.Item>
				</Descriptions>
			);
		}
		case CATELOGUE_TYPE.FUNCTION: {
			const functionData = data as IFunctionProps;
			return (
				<Descriptions className="dt-taskinfo" bordered size="small">
					<Descriptions.Item label="函数名称" span={12}>
						<code>{functionData.name}</code>
					</Descriptions.Item>
					{functionData.className && (
						<Descriptions.Item label="类名" span={12}>
							{functionData.className}
						</Descriptions.Item>
					)}
					{functionData.sqlText && (
						<Descriptions.Item label="SQL" span={12}>
							{functionData.sqlText || '/'}
						</Descriptions.Item>
					)}
					<Descriptions.Item label="用途" span={12}>
						{functionData.purpose || '/'}
					</Descriptions.Item>
					<Descriptions.Item label="命令格式" span={12}>
						<code>{functionData.commandFormate || '/'}</code>
					</Descriptions.Item>

					<Descriptions.Item label="参数说明" span={12}>
						{functionData.paramDesc || '/'}
					</Descriptions.Item>
					<Descriptions.Item label="创建" span={12}>
						{formatDateTime(functionData.gmtCreate)}
					</Descriptions.Item>
					<Descriptions.Item label="最后修改" span={12}>
						{formatDateTime(functionData.gmtModified)}
					</Descriptions.Item>
				</Descriptions>
			);
		}
		case CATELOGUE_TYPE.RESOURCE: {
			const resourceData: Record<string, any> = data;
			return (
				<Descriptions className="dt-taskinfo" bordered size="small">
					<Descriptions.Item label="资源名称" span={12}>
						{resourceData.resourceName}
					</Descriptions.Item>
					<Descriptions.Item label="资源描述" span={12}>
						{resourceData.resourceDesc}
					</Descriptions.Item>
					<Descriptions.Item
						label={
							<>
								存储路径
								<CopyToClipboard
									text={resourceData.url}
									onCopy={() => message.success('复制成功！')}
								>
									<Tooltip title="复制">
										<CopyOutlined />
									</Tooltip>
								</CopyToClipboard>
							</>
						}
						span={12}
					>
						<code>{resourceData.url}</code>
					</Descriptions.Item>
					<Descriptions.Item label="创建" span={12}>
						{resourceData.createUser.userName} 于
						{formatDateTime(resourceData.gmtCreate)}
					</Descriptions.Item>
					<Descriptions.Item label="修改时间" span={12}>
						{formatDateTime(resourceData.gmtModified)}
					</Descriptions.Item>
				</Descriptions>
			);
		}
		default:
			return null;
	}
}

interface IDetailModalProps {
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
export function DetailInfoModal({
	visible,
	loading,
	title,
	type,
	data,
	onCancel,
}: IDetailModalProps) {
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
			<Spin spinning={loading}>
				{data ? <DetailInfo type={type} data={data} /> : '系统异常'}
			</Spin>
		</Modal>
	);
}
