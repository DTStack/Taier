/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { useMemo } from 'react';
import type { ColumnsType } from 'antd/lib/table';
import { cloneDeep } from 'lodash';

import Chart from '@/components/chart';
import { useCurrentTheme } from '@/components/customHooks';
import type { SCHEDULE_TYPE } from '@/constant';

export interface ResouceProps {
    type: string;
    title: string;
    useNum: number;
    total: number;
    value: number;
}

export interface INodeProps {
    memory: number;
    nodeName: string;
    usedMemory: number;
    usedVirtualCores: number;
    virtualCores: number;
}

export interface IResourceMetrics {
    coresRate: number;
    memRate: number;
    totalCores: number;
    totalMem: number;
    usedCores: number;
    usedMem: number;
}

/**
 * For SCHEDULE_TYPE.Capacity
 */
export interface ICapacityProps {
    queueName: string;
    usedCapacity: number;
    capacity: number;
    maxCapacity: number;
    users: {
        username: string;
        maxResource: { memory: number; vCores: number };
        resourcesUsed: { memory: number; vCores: number };
        maxAMResource: { memory: number; vCores: number };
        AMResourceUsed: { memory: number; vCores: number };
    }[];
}

/**
 * For SCHEDULE_TYPE.Fair
 */
export interface IFairProps {
    queueName: string;
    usedResources: { memory: number; vCores: number };
    maxResources: { memory: number; vCores: number };
    minResources: { memory: number; vCores: number };
}

/**
 * For SCHEDULE_TYPE.FIFO
 */
export interface IFIFOProps {
    capacity: number;
    usedCapacity: number;
    numNodes: number;
}

export type IQueueProps = ICapacityProps | IFairProps | IFIFOProps;

export interface IClusterResourceProps {
    nodes?: INodeProps[];
    queues?: ICapacityProps[];
    resourceMetrics?: IResourceMetrics;
    scheduleInfo?: {
        type: SCHEDULE_TYPE;
        [key: string]: any;
    };
}

const ALARM_DEFAULT = 40;

const ALARM_HIGHT = 70;

const pieOption = {
    // 第一个图表
    series: [
        {
            type: 'pie',
            hoverAnimation: false, // 鼠标经过的特效
            radius: ['72%', '80%'],
            startAngle: 210,
            labelLine: {
                normal: {
                    show: false,
                },
            },
            label: {
                normal: {
                    position: 'center',
                },
            },
            data: [
                {
                    value: ALARM_DEFAULT,
                    itemStyle: {
                        normal: {
                            color: '#16DE9A',
                        },
                    },
                },
                {
                    value: ALARM_HIGHT - ALARM_DEFAULT,
                    itemStyle: {
                        normal: {
                            color: '#FFB310',
                        },
                    },
                },
                {
                    value: ALARM_HIGHT - ALARM_DEFAULT,
                    itemStyle: {
                        normal: {
                            color: '#FF5F5C',
                        },
                    },
                },
                {
                    value: 50,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                            },
                            labelLine: {
                                show: false,
                            },
                            color: 'rgba(0,0,0,0)',
                            borderWidth: 0,
                        },
                    },
                },
            ],
        },
        // 上层环形配置
        {
            type: 'pie',
            hoverAnimation: false, // 鼠标经过的特效
            radius: ['52%', '70%'],
            startAngle: 210,
            labelLine: {
                normal: {
                    show: false,
                },
            },
            label: {
                normal: {
                    position: 'center',
                },
            },
            data: [
                {
                    value: 75,
                    itemStyle: {
                        normal: {
                            color: '#FF5F5C',
                        },
                    },
                    label: {
                        normal: {
                            formatter: '{c}%',
                            position: 'center',
                            show: true,
                            textStyle: {
                                fontSize: 12,
                                fontWeight: 600,
                                color: '#333333',
                            },
                        },
                    },
                },
                {
                    value: 75,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                            },
                            labelLine: {
                                show: false,
                            },
                            color: 'rgba(0,0,0,0)',
                            borderWidth: 0,
                        },
                    },
                },
            ],
        },
    ],
};

export const NODE_COLUMNS: ColumnsType<INodeProps> = [
    {
        title: 'nodeName',
        dataIndex: 'nodeName',
        render(text) {
            return text || '-';
        },
    },
    {
        title: 'virtualCores',
        dataIndex: 'virtualCores',
    },
    {
        title: 'usedVirtualCores',
        dataIndex: 'usedVirtualCores',
    },
    {
        title: 'memory (M)',
        dataIndex: 'memory',
    },
    {
        title: 'usedMemory (M)',
        dataIndex: 'usedMemory',
    },
];

export const RESOURCE_DETAIL_COLUMNS: ColumnsType<ICapacityProps['users'][number]> = [
    {
        title: 'Username',
        dataIndex: 'username',
    },
    {
        title: 'Max Resource',
        dataIndex: 'maxResource',
        render(text: ICapacityProps['users'][number]['maxResource']) {
            return (
                <span>
                    memory:
                    <span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
                    vCores:
                    <span style={{ margin: 5 }}>{text.vCores}</span>
                </span>
            );
        },
    },
    {
        title: 'Used Resource',
        dataIndex: 'resourcesUsed',
        render(text: ICapacityProps['users'][number]['resourcesUsed']) {
            return (
                <span>
                    memory:
                    <span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
                    vCores:
                    <span style={{ margin: 5 }}>{text.vCores}</span>
                </span>
            );
        },
    },
    {
        title: 'Max AM Resource',
        dataIndex: 'maxAMResource',
        render(text: ICapacityProps['users'][number]['maxAMResource']) {
            return (
                <span>
                    memory:
                    <span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
                    vCores:
                    <span style={{ margin: 5 }}>{text.vCores}</span>
                </span>
            );
        },
    },
    {
        title: 'Used AM Resource',
        dataIndex: 'AMResourceUsed',
        render(text: ICapacityProps['users'][number]['AMResourceUsed']) {
            return (
                <span>
                    memory:
                    <span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
                    vCores:
                    <span style={{ margin: 5 }}>{text.vCores}</span>
                </span>
            );
        },
    },
];

/**
 * The Resource Card
 */
export const ResourceCard = ({ title, useNum, total, value = 0 }: ResouceProps) => {
    const [currentTheme] = useCurrentTheme();

    const getColor = (rawValue: number) => {
        if (rawValue >= ALARM_DEFAULT) {
            return rawValue >= ALARM_HIGHT ? '#FF5F5C' : '#FFB310';
        }
        return '#16DE9A';
    };

    const option = useMemo(() => {
        const rawOption = cloneDeep(pieOption);
        rawOption.series[1].data = [
            {
                ...rawOption.series[1].data[0],
                value,
                itemStyle: {
                    normal: {
                        color: getColor(value),
                    },
                },
            },
            {
                ...rawOption.series[1].data[1],
                value: 150 - value,
            },
        ];

        const fontCSSColor = window
            .getComputedStyle(document.documentElement)
            .getPropertyValue('--descriptionForeground');
        (rawOption.series[1].data[0] as any).label.normal.textStyle.color = fontCSSColor;

        return rawOption;
    }, [value, currentTheme]);

    return (
        <div className="c-resourceCard__container">
            <Chart option={option} width={110} height={110} />
            <div className="c-resourceCard__container__title">
                <p>{title}</p>
                <p>
                    <span style={{ fontSize: 18 }}>{useNum || '-'}</span> / {total || '-'}
                </p>
            </div>
        </div>
    );
};
