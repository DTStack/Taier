export const lineChartOptions: any = {// 堆叠折现图默认选项
    title: {
        text: ''
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            label: {
                backgroundColor: '#6a7985'
            }
        }
    },
    color: ['#2491F7', '#7460EF', '#26DAD2', '#79E079', '#7A64F3', '#FFDC53', '#9a64fb'],
    legend: {
        data: []
    },
    toolbox: {
        feature: {
            saveAsImage: {
                show: false
            }
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            boundaryGap: false,
            data: [],
            axisTick: {
                show: true
            },
            axisLine: {
                lineStyle: {
                    color: '#DDDDDD'
                }
            },
            axisLabel: {
                textStyle: {
                    color: '#666666'
                }
            },
            nameTextStyle: {
                color: '#666666'
            },
            splitLine: {
                color: '#666666'
            }
        }
    ],
    yAxis: [
        {
            type: 'value',
            axisLabel: {
                formatter: '{value}',
                textStyle: {
                    color: '#666666',
                    baseline: 'bottom'
                }
            },
            nameTextStyle: {
                color: '#666666'
            },
            nameLocation: 'end',
            nameGap: 20,
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            splitLine: {
                lineStyle: {
                    color: '#DDDDDD',
                    type: 'dashed'
                }
            }
        }
    ],
    series: []
};
export const offlineTaskPeriodFilter: any = [{
    id: 1,
    text: '分钟任务',
    value: 0
}, {
    id: 2,
    text: '小时任务',
    value: 1
}, {
    id: 3,
    text: '天任务',
    value: 2
}, {
    id: 4,
    text: '周任务',
    value: 3
}, {
    id: 5,
    text: '月任务',
    value: 4
}]

export const dataSourceFilter: any = [{
    id: 1,
    text: 'hive',
    value: 0
}, {
    id: 2,
    text: 'txt',
    value: 1
}, {
    id: 3,
    text: 'csv',
    value: 2
}]

export const UPLOAD_STATUS: any = {
    SUCCES: 'success',
    PROGRESSING: 'progressing',
    READY: 'ready',
    FAIL: 'fail'
}

export const SCHEDULE_STATUS: any = {
    FREZED: 2,
    UNFREZED: 1
}
