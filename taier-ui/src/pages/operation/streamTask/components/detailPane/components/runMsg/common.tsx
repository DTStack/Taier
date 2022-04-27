import { useState, useRef } from 'react';
import { Modal } from 'antd';
import LinkDiagram from './linkDiagram';

interface ICommonProps {
	flinkJson: any[];
	loading: boolean;
	refresh: () => void;
}

export default function Common({ flinkJson, loading, refresh }: ICommonProps) {
	const [visible, setVisible] = useState(false);
	const [subTreeData, setSubTreeData] = useState([]);
	// 绑定 graph id
	const targetKey = useRef('' + Math.random());

	const showSubVertex = (data: any) => {
		setVisible(true);
		setSubTreeData(data);
	};

	return (
		<>
			<LinkDiagram
				loading={loading}
				targetKey={targetKey.current}
				flinkJson={flinkJson}
				refresh={refresh}
				showSubVertex={showSubVertex}
			/>
			<Modal
				wrapClassName="modal-body-nopadding modal-body--height100"
				visible={visible}
				title="工作流"
				onCancel={() => setVisible(false)}
				footer={null}
				zIndex={1000}
				width={900}
			>
				<div id={targetKey.current} className="graph_wrapper__height">
					<LinkDiagram
						loading={loading}
						flinkJson={flinkJson}
						refresh={refresh}
						targetKey={targetKey.current}
						subTreeData={subTreeData}
						isSubVertex
						showSubVertex={showSubVertex}
					/>
				</div>
			</Modal>
		</>
	);
}
