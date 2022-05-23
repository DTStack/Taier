import type { IDataSourceUsedInSyncProps } from '@/interface';
import { Card, Button, Space } from 'antd';
import type { ISyncDataProps } from '@/interface';
import Channel from './channel';
import type { IKeyMapProps } from './keymap';
import Keymap from './keymap';
import Source from './source';
import Target from './target';

import './preview.scss';

interface IPreviewProps {
	data: ISyncDataProps;
	dataSourceList: IDataSourceUsedInSyncProps[];
	isIncrementMode?: boolean;
	/**
	 * For keymap
	 */
	userColumns: IKeyMapProps;
	onStepTo?: (step?: number) => void;
	onSave?: () => void;
}

function Mask() {
	return <div className="mask-lock-layer" />;
}

export default function Preview({
	data,
	dataSourceList,
	userColumns,
	isIncrementMode = false,
	onStepTo,
	onSave,
}: IPreviewProps) {
	return (
		<>
			<Space direction="vertical" size={10} className="dt-preview">
				<Card
					bordered={false}
					title="选择来源"
					extra={
						<Button type="link" onClick={() => onStepTo?.(0)}>
							修改
						</Button>
					}
				>
					<Source
						readonly
						dataSourceList={dataSourceList}
						sourceMap={data.sourceMap}
						isIncrementMode={isIncrementMode}
					/>
					<Mask />
				</Card>
				<Card
					bordered={false}
					title="选择目标"
					extra={
						<Button type="link" onClick={() => onStepTo?.(1)}>
							修改
						</Button>
					}
				>
					<Target
						readonly
						dataSourceList={dataSourceList}
						targetMap={data.targetMap}
						sourceMap={data.sourceMap}
						isIncrementMode={isIncrementMode}
					/>
					<Mask />
				</Card>
				<Card
					bordered={false}
					title="字段映射"
					extra={
						<Button type="link" onClick={() => onStepTo?.(2)}>
							修改
						</Button>
					}
				>
					<Keymap
						readonly
						sourceMap={data.sourceMap!}
						targetMap={data.targetMap!}
						userColumns={userColumns}
					/>
					<Mask />
				</Card>
				<Card
					bordered={false}
					title="通道控制"
					extra={
						<Button type="link" onClick={() => onStepTo?.(3)}>
							修改
						</Button>
					}
				>
					<Channel
						readonly
						sourceMap={data.sourceMap!}
						targetMap={data.targetMap!}
						setting={data.settingMap}
						isIncrementMode={isIncrementMode}
					/>
					<Mask />
				</Card>
			</Space>
			<div className="steps-action">
				<Space>
					<Button onClick={() => onStepTo?.()}>上一步</Button>
					<Button type="primary" onClick={() => onSave?.()}>
						保存
					</Button>
				</Space>
			</div>
		</>
	);
}
