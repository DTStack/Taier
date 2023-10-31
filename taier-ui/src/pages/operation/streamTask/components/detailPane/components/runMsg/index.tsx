import { useEffect, useRef,useState } from 'react';
import { Radio } from 'antd';

import stream from '@/api';
import { TASK_STATUS } from '@/constant';
import type { IStreamJobProps } from '@/interface';
import Common from './common';
import DetailTable from './detailTable';
import './index.scss';

interface IProps {
    data?: IStreamJobProps;
}

export interface IFlinkJsonProps {
    jobVertexId: string;
    jobVertexName: string;
    delayMap: Record<string, string | null>;
    parallelism: number;
    recordsReceived: number;
    recordsSent: number;
    backPressureMap: Record<string, number>;
    bytesSent: string;
    bytesReceived: string;
    subJobVertices: {
        id: string;
        name: string;
        delayMapList: Record<string, number>;
        parallelism: number;
        recordsReceivedMap: Record<string, number>;
        recordsSentMap: Record<string, number>;
    }[];
}

enum TABS_ENUM {
    VERTEX = 'vertex',
    DETAIL = 'detail',
}

const TABS = [
    {
        label: 'Vertex 拓扑',
        value: TABS_ENUM.VERTEX,
    },
    {
        label: '详情列表',
        value: TABS_ENUM.DETAIL,
    },
];

export default function RunMsg({ data }: IProps) {
    const [tabKey, setTabKey] = useState(TABS_ENUM.VERTEX);
    const [flinkJson, setFlinkJson] = useState<IFlinkJsonProps[]>([]);
    const [loading, setLoading] = useState(false);
    const timeClock = useRef<number | undefined>(undefined);

    const getFlinkJsonData = async (isSlient?: boolean) => {
        const { id, status } = data || {};
        if (!id || status !== TASK_STATUS.RUNNING) return;
        if (!isSlient) {
            setLoading(true);
        }

        if (timeClock.current) {
            window.clearTimeout(timeClock.current);
        }

        stream
            .getTaskJson({ taskId: id })
            .then((res) => {
                if (res.code === 1) {
                    rollData(status);
                    const { taskVertices } = res.data || {};
                    setFlinkJson(taskVertices || []);
                }
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const rollData = (status: number) => {
        if (status === TASK_STATUS.RUNNING) {
            timeClock.current = window.setTimeout(() => {
                timeClock.current = undefined;
                getFlinkJsonData(true);
            }, 15000);
        }
    };

    useEffect(() => {
        getFlinkJsonData();

        return () => {
            if (timeClock.current) {
                window.clearTimeout(timeClock.current);
            }
        };
    }, []);

    const renderContent = (key: TABS_ENUM) => {
        switch (key) {
            case TABS_ENUM.VERTEX:
                return <Common flinkJson={flinkJson} loading={loading} refresh={getFlinkJsonData} />;
            case TABS_ENUM.DETAIL:
                return <DetailTable tableData={flinkJson} loading={loading} />;

            default:
                return null;
        }
    };

    return (
        <div className="c-collapse-wrapper">
            <div className="graph-content-box">
                <Radio.Group
                    style={{ padding: '0 20px 12px' }}
                    value={tabKey}
                    onChange={(e) => setTabKey(e.target.value)}
                >
                    {TABS.map((tab) => (
                        <Radio.Button key={tab.value} value={tab.value}>
                            {tab.label}
                        </Radio.Button>
                    ))}
                </Radio.Group>
                {renderContent(tabKey)}
            </div>
        </div>
    );
}
