import { useState } from 'react';
import { Button, Space, Tooltip } from 'antd';

interface IToolbarProps {
	disabled?: boolean;
	onConnection?: () => Promise<void>;
	onSave?: () => Promise<void>;
}

export default function Toolbar({ disabled, onConnection, onSave }: IToolbarProps) {
	const [connecting, setConnecting] = useState(false);

	const handleTestConnectable = () => {
		if (onConnection) {
			setConnecting(true);

			onConnection().finally(() => {
				setConnecting(false);
			});
		}
	};

	const handleSaveComponent = () => {
		if (onConnection) {
			setConnecting(true);

			onSave?.().finally(() => {
				setConnecting(false);
			});
		}
	};

	return (
		<Space>
			<Tooltip title="测试连通性需要先保存当前组件">
				<Button disabled={disabled} loading={connecting} onClick={handleTestConnectable}>
					测试连通性
				</Button>
			</Tooltip>
			<Button disabled={disabled} type="primary" onClick={handleSaveComponent}>
				保存当前组件
			</Button>
		</Space>
	);
}
