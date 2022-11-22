import { useMemo, useState } from 'react';
import { Button, Dropdown, Menu, message, Space } from 'antd';
import ConstModal from './modals/constModal';
import { DATA_SOURCE_ENUM } from '@/constant';
import type { IDataColumnsProps } from '@/interface';
import type { ItemType } from 'antd/lib/menu/hooks/useItems';
import './tableFooter.scss';

interface ITableFooterProps {
	type: DATA_SOURCE_ENUM | undefined;
	source: boolean;
	onConstModalConfirm?: (col: IDataColumnsProps) => void;
	onAddFieldClick?: () => void;
	onAddFileName?: () => void;
}

const ButtonGroupIds = {
	ADD_FIELD: 'add_field',
	ADD_FTP_FILE_NAME: 'add_ftp_file_name',
} as const;

export default function TableFooter({
	type,
	source,
	onConstModalConfirm,
	onAddFieldClick,
	onAddFileName,
}: ITableFooterProps) {
	const [visibleConst, setConstVisible] = useState(false);

	const items = useMemo(() => {
		const defaultItem: ItemType[] = [
			{
				label: '+添加字段',
				key: ButtonGroupIds.ADD_FIELD,
				disabled: ![
					DATA_SOURCE_ENUM.HBASE,
					DATA_SOURCE_ENUM.HBASE2,
					DATA_SOURCE_ENUM.TBDS_HBASE,
					DATA_SOURCE_ENUM.HBASE_HUAWEI,
					DATA_SOURCE_ENUM.ES,
					DATA_SOURCE_ENUM.ES6,
					DATA_SOURCE_ENUM.ES7,
					DATA_SOURCE_ENUM.FTP,
					DATA_SOURCE_ENUM.RESTFUL,
				].includes(type!),
			},
		];

		if (type === DATA_SOURCE_ENUM.FTP) {
			defaultItem.push({
				label: '+添加文件名称字段',
				key: ButtonGroupIds.ADD_FTP_FILE_NAME,
			});
		}

		return defaultItem;
	}, [type]);

	const sourceTableFooter = (
		<>
			<Dropdown.Button
				overlay={
					<Menu
						items={items}
						onClick={({ key }) => {
							switch (key) {
								case ButtonGroupIds.ADD_FIELD:
									onAddFieldClick?.();
									break;

								case ButtonGroupIds.ADD_FTP_FILE_NAME:
									onAddFileName?.();
									break;
								default:
									break;
							}
						}}
					/>
				}
				className="dt-dataSync--tableFooter-btn"
				trigger={['click']}
				onClick={() => {
					if (type === undefined) {
						message.warn('请先选择数据源');
					} else {
						setConstVisible(true);
					}
				}}
				type="text"
			>
				+添加常量
			</Dropdown.Button>
		</>
	);

	const targetTableFooter = (() => {
		switch (type) {
			case DATA_SOURCE_ENUM.FTP:
				return (
					<Button type="text" block onClick={onAddFieldClick}>
						+添加字段
					</Button>
				);

			default:
				return null;
		}
	})();

	return (
		<>
			{source ? sourceTableFooter : targetTableFooter}
			<ConstModal
				visible={visibleConst}
				onOk={(col) => {
					onConstModalConfirm?.(col);
					setConstVisible(false);
				}}
				onCancel={() => setConstVisible(false)}
			/>
		</>
	);
}
