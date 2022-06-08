import { useState, useRef, useEffect } from 'react';
import * as ReactDOMServer from 'react-dom/server';
import { Modal } from 'antd';
import LinkDiagram from './linkDiagram';
import MxGraphContainer from '@/components/mtest/container';
import './common.scss';

interface ISubJobVertices {
	id: string;
	name: string;
	delayMapList: Record<string, number>;
	parallelism: number;
	recordsReceivedMap: Record<string, number>;
	recordsSentMap: Record<string, number>;
}

interface ICommonProps {
	flinkJson: {
		jobVertexId: string;
		jobVertexName: string;
		delayMap: Record<string, string | null>;
		parallelism: number;
		recordsReceived: number;
		recordsSent: number;
		backPressureMap: Record<string, number>;
		subJobVertices: ISubJobVertices[];
	}[];
	loading: boolean;
	refresh: () => void;
}

interface ITopological {
	id: string;
	title: string;
	desc: string;
	delay: number;
	parallelism: number;
	received: number;
	sent: number;
	backPressured: number;
	isCollapsed: boolean;
	childNode: ITopological[];
}

const getBackPressureColor = (backPressure: number, type: string) => {
	let color = '';
	if (backPressure >= 0 && backPressure <= 0.1) {
		// 正常
		if (type === 'title') {
			color = 'linear-gradient(298deg,rgba(90,203,255,1) 0%,rgba(36,145,247,1) 100%)';
		} else {
			color = '#F1FAFF';
		}
	} else if (backPressure >= 0.1 && backPressure <= 0.5) {
		// 低反压
		if (type === 'title') {
			color = 'linear-gradient(270deg,rgba(255,190,76,1) 0%,rgba(255,160,41,1) 100%)';
		} else {
			color = '#fff9f0';
		}
	} else if (backPressure > 0.5 && backPressure <= 1) {
		// 高反压
		if (type === 'title') {
			color = 'linear-gradient(270deg,#FF5F5C 0%,#ea8785 100%)';
		} else {
			color = '#ff5f5c4d';
		}
	}
	return color;
};

export default function Common({ flinkJson, loading, refresh }: ICommonProps) {
	const [graphData, setGraphData] = useState<ITopological[] | null>(null);

	const convertChildNode = (
		children: ISubJobVertices[],
	): Omit<ITopological, 'backPressured'>[] => {
		return children.map((child, index) => ({
			id: child.id,
			title: `Operator ${index}`,
			desc: child.name,
			delay: Object.values(child.delayMapList)[0],
			parallelism: child.parallelism,
			received: Object.values(child.recordsReceivedMap)[0],
			sent: Object.values(child.recordsSentMap)[0],
			isCollapsed: false,
			childNode: [],
		}));
	};

	const handleClick = (
		cell: IMxCell<ITopological>,
		graph: IMxGraph,
		event: React.MouseEvent<HTMLElement, MouseEvent>,
	) => {
		if ((event.target as HTMLImageElement).nodeName === 'IMG') {
			const collapse = !cell.collapsed;
			graph.foldCells(collapse, false, [cell], null);
		}
	};

	const handleDrawVertex = (data: ITopological) => {
		if (data.childNode.length) {
			return 'shape=swimlane;startSize=20;';
		}
	};

	const handleRenderCell = (cell: IMxCell<ITopological>, graph: IMxGraph) => {
		if (cell.value) {
			const { backPressured, title, desc, delay, parallelism, received, sent } = cell.value;
			const isShowDetail = !!cell.value.childNode.length && !graph.model.isCollapsed(cell);
			console.log('isShowDetail:', isShowDetail);
			const res = ReactDOMServer.renderToString(
				<div
					className="vertex-diagram"
					style={{ background: getBackPressureColor(backPressured, 'title') }}
				>
					<div className="vertex-title">
						<div className="t_title">{title}</div>
						{cell.value.isCollapsed && (
							<img className="cursor-pointer" src="images/expand.svg" />
						)}
					</div>
					<div
						className="vertex-content"
						style={{ background: getBackPressureColor(backPressured, 'content') }}
					>
						<div className="tcolumn" title={desc}>
							{desc}
						</div>
						{!isShowDetail &&
							[
								{ field: 'delay', title: 'Delay', data: `${delay}ms` },
								{ field: 'parallelism', title: 'Parallelism', data: parallelism },
								{ field: 'received', title: 'Record Received', data: received },
								{ field: 'sent', title: 'Record Sent', data: sent },
								{
									field: 'dashboard',
									title: 'BackPressured(max)',
									data: `${(backPressured * 100).toFixed(0)}%`,
								},
							].map(({ field, title: fieldTitle, data }) => (
								<div className="t-text-col" title={fieldTitle}>
									<span className="t-text-col-key">
										<img
											src={`images/${field}.svg`}
											className="t-text-col_img"
										/>
										${fieldTitle}
									</span>
									<span className="t-text-col-value">${data}</span>
								</div>
							))}
					</div>
				</div>,
			);
			return res;
			return `<div class='vertex-diagram'>
				<div class='vertex-title' style='background: ${getBackPressureColor(backPressured, 'title')}'>
					<div class='t_title'>${title}</div>
					<img class="cursor-pointer" onclick="console.log(${cell})" src='images/expand.svg' />
				</div>
				<div class='vertex-content' style='background: ${getBackPressureColor(backPressured, 'content')}'>
					<div class='tcolumn' title="${desc}">${desc}</div>
					${[
						{ field: 'delay', title: 'Delay', data: `${delay}ms` },
						{ field: 'parallelism', title: 'Parallelism', data: parallelism },
						{ field: 'received', title: 'Record Received', data: received },
						{ field: 'sent', title: 'Record Sent', data: sent },
						{
							field: 'dashboard',
							title: 'BackPressured(max)',
							data: `${(backPressured * 100).toFixed(0)}%`,
						},
					]
						.map(
							({ field, title: fieldTitle, data }) =>
								`<div class='t-text-col' title="${fieldTitle}">
                                <span class='t-text-col-key'>
                                    <img src="images/${field}.svg" class='t-text-col_img' />
                                    ${fieldTitle}
                                </span>
                                <span class='t-text-col-value'>${data}</span>
                            </div>`,
						)
						.join('')}
				</div>
			</div>`.replace(/(\r\n|\n)/g, '');
		}
		return '';
	};

	useEffect(() => {
		if (flinkJson.length) {
			setGraphData(
				flinkJson.map((json) => {
					return {
						id: json.jobVertexId,
						title: `Chain ${json.subJobVertices.length} Operators`,
						desc: json.jobVertexName,
						delay: Number(Object.values(json.delayMap)[0]) || 0,
						parallelism: json.parallelism,
						received: json.recordsReceived,
						sent: json.recordsSent,
						isCollapsed: true,
						backPressured: Math.max(...Object.values(json.backPressureMap)),
						childNode: convertChildNode(json.subJobVertices).map((i) => ({
							...i,
							// 子节点的最大反压数据从父节点获取
							backPressured: Math.max(...Object.values(json.backPressureMap)),
						})),
					};
				}),
			);
		}
	}, [flinkJson]);

	console.log('graphData:', graphData);
	return (
		<MxGraphContainer<ITopological>
			config={{
				tooltips: false,
			}}
			vertextSize={{
				width: 280,
				height: 234,
			}}
			onClick={handleClick}
			loading={loading}
			graphData={graphData}
			onRenderCell={handleRenderCell}
			onDrawVertex={handleDrawVertex}
		/>
	);
	// const [visible, setVisible] = useState(false);
	// const [subTreeData, setSubTreeData] = useState([]);
	// // 绑定 graph id
	// const targetKey = useRef('' + Math.random());

	// const showSubVertex = (data: any) => {
	// 	setVisible(true);
	// 	setSubTreeData(data);
	// };

	// return (
	// 	<>
	// 		<LinkDiagram
	// 			loading={loading}
	// 			targetKey={targetKey.current}
	// 			flinkJson={flinkJson}
	// 			refresh={refresh}
	// 			showSubVertex={showSubVertex}
	// 		/>
	// 		<Modal
	// 			wrapClassName="modal-body-nopadding modal-body--height100"
	// 			visible={visible}
	// 			title="工作流"
	// 			onCancel={() => setVisible(false)}
	// 			footer={null}
	// 			zIndex={1000}
	// 			width={900}
	// 		>
	// 			<div id={targetKey.current} className="graph_wrapper__height">
	// 				<LinkDiagram
	// 					loading={loading}
	// 					flinkJson={flinkJson}
	// 					refresh={refresh}
	// 					targetKey={targetKey.current}
	// 					subTreeData={subTreeData}
	// 					isSubVertex
	// 					showSubVertex={showSubVertex}
	// 				/>
	// 			</div>
	// 		</Modal>
	// 	</>
	// );
}
