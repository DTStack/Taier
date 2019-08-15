import { get } from 'lodash';

export function regressionClassificationOptions (lineData: any) {
    const data = get(lineData, 'series[0].data', []);
    return {
        color: ['#2491F7'],
        toolbox: {
            show: true,
            right: 20,
            feature: {
                magicType: { type: ['line', 'bar'] },
                saveAsImage: {}
            }
        },
        dataZoom: [{
            type: 'inside'
        }, {
            type: 'slider'
        }],
        xAxis: {
            type: 'category',
            boundaryGap: true,
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
                },
                rotate: -45
            },
            nameTextStyle: {
                color: '#666666'
            },
            splitLine: {
                color: '#666666'
            },
            data: data.map((item: any) => { return item.name; })
        },
        yAxis: {
            nameGap: 20,
            nameLocation: 'end',
            nameTextStyle: {
                fontSize: 13,
                color: '#666666',
                align: 'center',
                padding: 0
            },
            axisLine: {
                lineStyle: {
                    color: '#DDDDDD'
                }
            },
            axisTick: {
                show: false
            },
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            },
            axisLabel: {
                color: '#666666',
                fontSize: 13
            },
            type: 'value'
        },
        series: [{
            data: data.map((item: any) => { return item.value; }),
            type: 'bar'
        }]
    }
}

export function unionClassificationOptions (lineData: any) {
    const data = get(lineData, 'series[0].data', []);
    data.forEach((obj: any, index: number) => {
        for (let key in obj) {
            if (key == 'name') {
                obj[key] = `${index + 1}: ${obj[key]}%`;
            }
        }
    })
    return {
        toolbox: {
            show: true,
            right: 20,
            feature: {
                saveAsImage: {}
            }
        },
        series: [
            {
                type: 'pie',
                radius: '55%',
                center: ['50%', '60%'],
                data: data,
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }
}
