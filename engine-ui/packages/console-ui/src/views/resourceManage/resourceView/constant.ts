export const ALARM_DEFAULT = 40

export const ALARM_HIGHT = 70

export const pieOption = {
    // 第一个图表
    series: [
        {
            type: 'pie',
            hoverAnimation: false, // 鼠标经过的特效
            radius: ['72%', '80%'],
            startAngle: 210,
            labelLine: {
                normal: {
                    show: false
                }
            },
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [
                {
                    value: ALARM_DEFAULT,
                    itemStyle: {
                        normal: {
                            color: '#16DE9A'
                        }
                    }
                },
                {
                    value: ALARM_HIGHT - ALARM_DEFAULT,
                    itemStyle: {
                        normal: {
                            color: '#FFB310'
                        }
                    }
                },
                {
                    value: ALARM_HIGHT - ALARM_DEFAULT,
                    itemStyle: {
                        normal: {
                            color: '#FF5F5C'
                        }
                    }
                },
                {
                    value: 50,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false
                            },
                            labelLine: {
                                show: false
                            },
                            color: 'rgba(0,0,0,0)',
                            borderWidth: 0
                        }
                    }
                }

            ]
        },
        // 上层环形配置
        {
            type: 'pie',
            hoverAnimation: false, // 鼠标经过的特效
            radius: ['52%', '70%'],
            startAngle: 210,
            labelLine: {
                normal: {
                    show: false
                }
            },
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [
                {
                    value: 75,
                    itemStyle: {
                        normal: {
                            color: '#FF5F5C'
                        }
                    },
                    label: {
                        normal: {
                            formatter: '{c}%',
                            position: 'center',
                            show: true,
                            textStyle: {
                                fontSize: 12,
                                fontWeight: 600,
                                color: '#333333'
                            }
                        }
                    }
                }, {
                    value: 75,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false
                            },
                            labelLine: {
                                show: false
                            },
                            color: 'rgba(0,0,0,0)',
                            borderWidth: 0
                        }
                    }
                }
            ]
        }
    ]
}

export const SCHEDULE_TYPE = {
    Capacity: 'capacityScheduler',
    Fair: 'fairScheduler',
    FIFO: 'fifoScheduler'
}
