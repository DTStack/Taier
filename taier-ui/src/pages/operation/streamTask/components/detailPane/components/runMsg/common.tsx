import { useEffect, useMemo,useState } from 'react';
import * as ReactDOMServer from 'react-dom/server';
import { Modal } from 'antd';
import classnames from 'classnames';
import { cloneDeep } from 'lodash';
import type { mxCell, mxGraph } from 'mxgraph';

import MxGraphContainer from '@/components/mxGraph/container';
import type { IFlinkJsonProps } from '.';
import './common.scss';

interface ICommonProps {
    flinkJson: IFlinkJsonProps[];
    loading: boolean;
    refresh: () => void;
}

type ISubJobVertices = IFlinkJsonProps['subJobVertices'][number];

interface ITopological {
    id: string;
    title: string;
    desc: string;
    delay: number;
    parallelism: number;
    received: number;
    sent: number;
    backPressured: number;
    subJobVertices?: ISubJobVertices[];
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
    // 当前选中的 cell
    const [selectedCell, setSelectedCell] = useState<mxCell | null>(null);
    const [visible, setVisible] = useState(false);

    const handleClick = (cell: mxCell, graph: mxGraph, event: React.MouseEvent<HTMLElement, MouseEvent>) => {
        if ((event.target as HTMLImageElement).nodeName === 'IMG') {
            if ((cell.value as ITopological)?.subJobVertices) {
                setVisible(true);

                setSelectedCell(cell);
            }
        }
    };

    const handleRenderCell = (cell: mxCell) => {
        if (cell.value) {
            const { backPressured, title, desc, delay, parallelism, received, sent, subJobVertices } =
                cell.value as ITopological;
            const res = ReactDOMServer.renderToString(
                <div className="vertex-diagram" style={{ background: getBackPressureColor(backPressured, 'title') }}>
                    <div className="vertex-title">
                        <div className="t_title">{title}</div>
                        {subJobVertices?.length && <img className="cursor-pointer" src="images/expand.svg" />}
                    </div>
                    <div
                        className="vertex-content"
                        style={{ background: getBackPressureColor(backPressured, 'content') }}
                    >
                        <div className="tcolumn" title={desc}>
                            {desc}
                        </div>
                        {[
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
                            <div className="t-text-col" title={fieldTitle} key={field}>
                                <span className="t-text-col-key">
                                    <img src={`images/${field}.svg`} className="t-text-col_img" />${fieldTitle}
                                </span>
                                <span className="t-text-col-value">{data}</span>
                            </div>
                        ))}
                    </div>
                </div>
            );
            return res;
        }
        return '';
    };

    const convertArrToLinkedList = (arr?: ISubJobVertices[]): ITopological[] | null => {
        let res: ITopological[] | null = null;
        arr?.forEach(
            function (this: { point: ITopological[] | null }, vertice, index) {
                if (!this.point) {
                    res = [
                        {
                            id: vertice.id,
                            title: `Operator ${index}`,
                            desc: vertice.name,
                            delay: Object.values(vertice.delayMapList)[0],
                            parallelism: vertice.parallelism,
                            received: Object.values(vertice.recordsReceivedMap)[0],
                            sent: Object.values(vertice.recordsSentMap)[0],
                            // 子节点的最大反压数据从父节点获取
                            backPressured: selectedCell?.value?.backPressured || 0,
                            childNode: [],
                        },
                    ];
                    this.point = res[0].childNode;
                } else {
                    this.point[0] = {
                        id: vertice.id,
                        title: `Operator ${index}`,
                        desc: vertice.name,
                        delay: Object.values(vertice.delayMapList)[0],
                        parallelism: vertice.parallelism,
                        received: Object.values(vertice.recordsReceivedMap)[0],
                        sent: Object.values(vertice.recordsSentMap)[0],
                        backPressured: selectedCell?.value?.backPressured || 0,
                        childNode: [],
                    };
                    this.point = this.point[0].childNode;
                }
            },
            { point: res }
        );

        return res;
    };

    const handleRefreshSub = () => {
        setSelectedCell((c) => cloneDeep(c));
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
                        backPressured: Math.max(...Object.values(json.backPressureMap)),
                        subJobVertices: json.subJobVertices,
                        childNode: [],
                    };
                })
            );
        }
    }, [flinkJson]);

    const subGraphData = useMemo(() => {
        if (selectedCell) {
            return convertArrToLinkedList((selectedCell.value as ITopological)?.subJobVertices);
        }
        return null;
    }, [selectedCell]);

    if (!graphData) {
        return (
            <div className={classnames('full-w', 'flex-1', 'flex', 'items-center', 'justify-center')}>
                暂未生成拓扑图
            </div>
        );
    }

    return (
        <>
            <MxGraphContainer<ITopological>
                config={{
                    tooltips: false,
                }}
                vertexSize={{
                    width: 280,
                    height: 234,
                }}
                onClick={handleClick}
                loading={loading}
                graphData={graphData}
                onRenderCell={handleRenderCell}
                onRefresh={refresh}
            />
            <Modal
                visible={visible}
                title="工作流"
                onCancel={() => setVisible(false)}
                footer={null}
                zIndex={1000}
                width={900}
                destroyOnClose
            >
                <div style={{ height: 500 }}>
                    <MxGraphContainer<ITopological>
                        config={{
                            tooltips: false,
                        }}
                        vertexSize={{
                            width: 280,
                            height: 234,
                        }}
                        direction="west"
                        onClick={handleClick}
                        loading={loading}
                        graphData={subGraphData}
                        onRenderCell={handleRenderCell}
                        onRefresh={handleRefreshSub}
                    />
                </div>
            </Modal>
        </>
    );
}
