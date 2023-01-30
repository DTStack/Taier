// @ts-nocheck
/* eslint-disable */
import * as React from 'react';
import { Spin, Tooltip } from 'antd';
import { DateTime } from '@dtinsight/dt-utils';
import { cloneDeep, isArray } from 'lodash';
import { QuestionCircleOutlined, ArrowsAltOutlined, ShrinkOutlined, DeleteOutlined } from '@ant-design/icons';

import { SOURCE_INPUT_BPS_UNIT_TYPE, COLLECTION_BPS_UNIT_TYPE, UNIT_TYPE } from '@/constant';

import Resize from '@/components/resize';
import Fullscreen from '@/components/fullScreen';
import { metricsType as METRICSTYPE } from './index';
import { lineAreaChartOptions } from './help';

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/legend');
require('echarts/lib/component/legendScroll');
require('echarts/lib/component/tooltip');

function haveData(lineData: any = {}) {
    const { y = [] } = lineData;

    let haveData = false;
    y.forEach((item: any) => {
        if (item && item.length) {
            haveData = true;
        }
    });
    return haveData;
}

function isExchangeData(data: any = [], unitType: any) {
    let exChangeArr: any = [];
    switch (unitType) {
        case SOURCE_INPUT_BPS_UNIT_TYPE[UNIT_TYPE.B]:
        case COLLECTION_BPS_UNIT_TYPE[UNIT_TYPE.B]: {
            exChangeArr = data;
            break;
        }
        case SOURCE_INPUT_BPS_UNIT_TYPE[UNIT_TYPE.KB]:
        case COLLECTION_BPS_UNIT_TYPE[UNIT_TYPE.KB]: {
            exChangeArr = data.map((item: any) =>
                isArray(item) ? [item[0], Number((item[1] / 1024).toFixed(6))] : Number((item / 1024).toFixed(6))
            );
            break;
        }
        case SOURCE_INPUT_BPS_UNIT_TYPE[UNIT_TYPE.MB]:
        case COLLECTION_BPS_UNIT_TYPE[UNIT_TYPE.MB]: {
            exChangeArr = data.map((item: any) =>
                isArray(item)
                    ? [item[0], Number((item[1] / Math.pow(1024, 2)).toFixed(6))]
                    : Number((item / Math.pow(1024, 2)).toFixed(6))
            );
            break;
        }
        case SOURCE_INPUT_BPS_UNIT_TYPE[UNIT_TYPE.GB]:
        case COLLECTION_BPS_UNIT_TYPE[UNIT_TYPE.GB]: {
            exChangeArr = data.map((item: any) =>
                isArray(item)
                    ? [item[0], Number((item[1] / Math.pow(1024, 3)).toFixed(6))]
                    : Number((item / Math.pow(1024, 3)).toFixed(6))
            );
            break;
        }
        case SOURCE_INPUT_BPS_UNIT_TYPE[UNIT_TYPE.TB]:
        case COLLECTION_BPS_UNIT_TYPE[UNIT_TYPE.tB]: {
            exChangeArr = data.map((item: any) =>
                isArray(item)
                    ? [item[0], Number((item[1] / Math.pow(1024, 4)).toFixed(6))]
                    : Number((item / Math.pow(1024, 4)).toFixed(6))
            );
            break;
        }
        default: {
            return exChangeArr;
        }
    }
    return exChangeArr;
}

class AlarmBaseGraphBox extends React.Component<any, any> {
    state: any = {
        key: `${Math.random()}`,
    };

    render() {
        const { key } = this.state;
        const { title, lineData, desc, allowDelete, onDelete } = this.props;
        const { loading } = lineData;
        const haveLineData = haveData(lineData);
        return (
            <div className="basegraph-size">
                <div id={key} className="alarm-basegraph-box">
                    <header>
                        <span className="title">{title}</span>
                        {desc && (
                            <Tooltip
                                title={desc}
                                overlayClassName="big-tooltip"
                                getPopupContainer={(triggerNode: any) => {
                                    return triggerNode;
                                }}
                                placement="bottomLeft"
                                arrowPointAtCenter
                            >
                                <QuestionCircleOutlined style={{ marginLeft: '8px', color: '#666', fontSize: 12 }} />
                            </Tooltip>
                        )}
                        {!loading && haveLineData ? (
                            <Fullscreen
                                target={key}
                                fullIcon={<ArrowsAltOutlined className="alt" />}
                                exitFullIcon={<ShrinkOutlined className="alt" />}
                                isShowTitle={false}
                            />
                        ) : null}
                        {!loading && allowDelete && <DeleteOutlined onClick={onDelete} />}
                    </header>
                    {loading ? (
                        <div className="loading-box">
                            <Spin className="loading" />
                        </div>
                    ) : (
                        <div className="graph-content">
                            {haveLineData ? (
                                <AlarmBaseGraph {...this.props} />
                            ) : (
                                <p className="no-data-text">暂无数据</p>
                            )}
                        </div>
                    )}
                </div>
            </div>
        );
    }
}

class AlarmBaseGraph extends React.Component<any, any> {
    _dom: any;
    state: any = {
        lineChart: null,
    };

    componentDidMount() {
        this.initGraph();
    }

    UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (this.props.lineData != nextProps.lineData || this.props.time != nextProps.time) {
            this.initGraph(nextProps.lineData, nextProps.time);
        }
    }

    exchangeDate(date: any, time: any, joinLine: any) {
        const [unit] = time.replace(/\d/g, '').split('');
        switch (unit) {
            case 's':
            case 'm': {
                return DateTime.formatMinute(parseInt(date));
            }
            case 'h':
            case 'd': {
                return DateTime.formatHours(parseInt(date));
            }
            case 'w': {
                if (joinLine) {
                    return DateTime.formatDayHours(parseInt(date)).split(' ').join('\n');
                }
                return DateTime.formatDayHours(parseInt(date));
            }
            case 'y': {
                return DateTime.formatDate(parseInt(date));
            }
            default: {
                return DateTime.formatHours(parseInt(date));
            }
        }
    }

    initGraph(lineData?: any, time?: any) {
        lineData = lineData || this.props.lineData;
        time = time || this.props.time;
        if (!lineData) {
            return;
        }
        const { loading } = lineData;
        if (loading) {
            return;
        }
        const { x, y = [], legend, color, unit, metricsType, legendOption, gridOption, tooltipOption } = lineData;
        /**
         * 先检验是不是有数据，没数据就不渲染了
         */
        if (!haveData(lineData)) {
            return;
        }
        /**
         * 初始化
         */
        const myChart = echarts.init(this._dom);
        const options = cloneDeep(lineAreaChartOptions);
        /**
         * 隐藏标题
         */
        options.title.show = false;
        /**
         * 设置纵坐标名称
         */
        if (unit == 's') {
            options.yAxis[0].name = '';
            options.yAxis[0].axisLabel.formatter = (value: any) => {
                return DateTime.formatSecond(value);
            };
            options.yAxis[0].axisPointer = {
                label: {
                    formatter: (params: any) => {
                        return DateTime.formatSecond(params.value);
                    },
                },
            };
        } else {
            options.yAxis[0].name = unit;
        }

        const splitLineColor = window
            .getComputedStyle(document.documentElement)
            .getPropertyValue('--breadcrumb-foreground');
        options.yAxis[0].splitLine.lineStyle.color = splitLineColor;
        /**
         * 设置横坐标数值
         */
        options.xAxis[0].data = x;
        options.xAxis[0].axisLabel = {
            ...options.xAxis[0].axisLabel,
            formatter: (value: any) => {
                return this.exchangeDate(value, time, true);
            },
        };
        options.xAxis[0].axisPointer = {
            label: {
                formatter: (params: any) => {
                    return DateTime.formatDateTime(parseInt(params.value));
                },
            },
        };
        /**
         * 设置小图标
         */
        options.legend.type = 'scroll';
        options.legend.data = legend.map((item: any) => {
            return {
                name: item,
                icon: 'circle',
            };
        });
        options.legend.top = '7px';
        options.legend.right = '20px';
        options.legend.itemWidth = 6;
        options.legend.itemHeight = 6;

        const fontCSSColor = window
            .getComputedStyle(document.documentElement)
            .getPropertyValue('--descriptionForeground');
        options.legend.textStyle = {
            color: fontCSSColor,
        };
        // options.legend.borderRadius="50%";
        /**
         * 画图区域的定位
         */
        options.grid.bottom = '16px';
        options.grid.left = '20px';
        options.grid.right = '40px';
        options.grid.top = '50px';
        options.grid.containLabel = true;
        /**
         * tooltip
         */
        options.tooltip.formatter = (params: any[]) => {
            return `<span style="color: var(--descriptionForeground)">${params[0].axisValueLabel}</br>${params
                .map((param) => {
                    return `${param.marker} ${param.seriesName}: ${isArray(param.data) ? param.data[1] : param.data}`;
                })
                .join('</br>')}</span>`;
        };
        /**
         * 设置具体的数据
         */
        options.series = y.map((item: any, index: any) => {
            let arrData = item;
            if (
                metricsType == METRICSTYPE.SOURCE_INPUT_BPS ||
                metricsType == METRICSTYPE.DATA_COLLECTION_BPS ||
                metricsType == METRICSTYPE.DATA_COLLECTION_TOTAL_BPS
            ) {
                arrData = isExchangeData(item, unit);
            }
            const line: any = {
                name: legend[index],
                data: arrData,
                type: 'line',
                smooth: true,
                showSymbol: !(arrData.length > 2),
            };
            if (color[index]) {
                line.lineStyle = {
                    normal: {
                        color: color[index],
                    },
                };
                line.itemStyle = {
                    normal: {
                        color: color[index],
                    },
                };
            }
            return line;
        });
        /**
         * 执行绘图
         */
        // 自定义option覆盖
        if (legendOption) {
            if (legendOption?.textStyle?.overflow === 'truncate') {
                const container = this._dom;
                legendOption.formatter = function (name: string) {
                    return echarts.format.truncateText(name, container?.clientWidth * 0.97 || 500, '...');
                };
            }
            options.legend = { ...options.legend, ...legendOption };
        }
        if (gridOption) {
            options.grid = { ...options.grid, ...gridOption };
        }
        if (tooltipOption) {
            options.tooltip = { ...options.tooltip, ...tooltipOption };
        }

        myChart.setOption(options);
        this.setState({
            lineChart: myChart,
        });
    }

    resize = () => {
        if (this.state.lineChart) this.state.lineChart.resize();
    };

    render() {
        return (
            <Resize onResize={this.resize.bind(this)}>
                <article
                    ref={(ref: any) => {
                        this._dom = ref;
                    }}
                    style={{ width: '100%', height: '100%' }}
                />
            </Resize>
        );
    }
}

export default AlarmBaseGraphBox;
