export const lineAreaChartOptions: any = {
    // 堆叠折现图默认选项
    title: {
        text: '堆叠区域图',
        textStyle: {
            fontSize: 12,
        },
        textAlign: 'left',
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            label: {
                backgroundColor: '#6a7985',
            },
        },
    },
    color: ['#2491F7', '#7460EF', '#26DAD2', '#79E079', '#7A64F3', '#FFDC53', '#9a64fb'],
    legend: {
        data: ['邮件营销', '联盟广告', '视频广告'],
    },
    toolbox: {
        feature: {
            saveAsImage: {
                show: false,
            },
        },
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
    },
    xAxis: [
        {
            type: 'category',
            boundaryGap: false,
            data: [],
            axisTick: {
                show: true,
            },
            axisLine: {
                lineStyle: {
                    color: '#DDDDDD',
                },
            },
            axisLabel: {
                textStyle: {
                    color: '#666666',
                },
            },
            nameTextStyle: {
                color: '#666666',
            },
            splitLine: {
                color: '#666666',
            },
        },
    ],
    yAxis: [
        {
            name: '数量(个)',
            type: 'value',
            axisLabel: {
                formatter: '{value}',
                textStyle: {
                    color: '#666666',
                    baseline: 'bottom',
                },
            },
            nameTextStyle: {
                color: '#666666',
            },
            nameLocation: 'end',
            nameGap: 20,
            axisLine: {
                show: false,
            },
            axisTick: {
                show: false,
            },
            splitLine: {
                lineStyle: {
                    color: '#DDDDDD',
                    type: 'dashed',
                },
            },
        },
    ],
    series: [],
};
