import * as React from 'react'

// 引入 ECharts 主模块
const echarts = require('echarts/lib/echarts')

require('echarts/lib/chart/pie')
require('echarts/lib/component/title')

class Echarts extends React.Component<any, any> {
    placeHolderStyle: any = {
        normal: {
            label: {
                show: false
            },
            labelLine: {
                show: false
            },
            color: 'rgba(0,0,0,0)',
            borderWidth: 0
        },
        emphasis: {
            color: 'rgba(0,0,0,0)',
            borderWidth: 0
        }
    }

    dataStyle: any = {
        normal: {
            formatter: '{c}%',
            position: 'center',
            show: true,
            textStyle: {
                fontSize: '12',
                fontWeight: 'bolder',
                color: '#34374E'
            }
        }
    }

    componentDidMount () {
        this.initEcharts()
    }

    initEcharts = () => {
        const { name } = this.props
        const targetEchart = echarts.init(document.getElementById(`echarts-${name}`))
        let title: any = ''
        let content: any = ''

        switch (name) {
            case 'cpu':
                title = 'CPU (core)'
                content = '18/20'
                break;
            case 'memory':
                title = '内存 (GB)'
                content = '18/20'
                break;
            default:
                title = name
                break;
        }

        const option = {
            backgroundColor: '#fff',
            title: [
                {
                    text: title,
                    subtext: content,
                    left: 230,
                    top: '30%',
                    textAlign: 'center',
                    textStyle: {
                        color: '#333',
                        fontWeight: 'bolder',
                        fontSize: '16',
                        textAlign: 'center'
                    },
                    subtextStyle: {
                        color: '#333',
                        fontWeight: 'bolder',
                        fontSize: '20',
                        textAlign: 'center'
                    }
                }
            ],

            // 第一个图表
            series: [
                {
                    type: 'pie',
                    hoverAnimation: false, // 鼠标经过的特效
                    radius: ['70%', '80%'],
                    center: [100, '60%'],
                    startAngle: 200,
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
                            value: 60 * 0.4,
                            itemStyle: {
                                normal: {
                                    color: '#16DE9A'
                                }
                            }
                        },
                        {
                            value: 60 * 0.3,
                            itemStyle: {
                                normal: {
                                    color: '#FFB310'
                                }
                            }
                        },
                        {
                            value: 60 * 0.3,
                            itemStyle: {
                                normal: {
                                    color: '#FF5F5C'
                                }
                            }
                        },
                        {
                            value: 40,
                            itemStyle: this.placeHolderStyle
                        }

                    ]
                },

                // 第二个图表
                {
                    type: 'pie',
                    hoverAnimation: false,
                    radius: ['30%', '68%'],
                    center: [100, '60%'],
                    startAngle: 200,
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
                            value: 60,
                            itemStyle: {
                                normal: {
                                    color: '#E1E8EE'
                                }
                            }
                        },
                        {
                            value: 40,
                            itemStyle: this.placeHolderStyle
                        }
                    ]
                },

                // 上层环形配置
                {
                    type: 'pie',
                    hoverAnimation: false,
                    radius: ['30%', '68%'],
                    center: [100, '60%'],
                    startAngle: 200,
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
                            value: 60 * 0.7,
                            itemStyle: {
                                normal: {
                                    color: '#FF5F5C'
                                }
                            },
                            label: this.dataStyle
                        },
                        {
                            value: 100 - 60 * 0.7,
                            itemStyle: this.placeHolderStyle
                        }
                    ]
                }
            ]
        }

        // 绘制图表
        targetEchart.setOption(option)
        this.setState({
            targetEchart
        })
    }

    render () {
        const { name } = this.props
        return (
            <div id={`echarts-${name}`} style={{ height: '100%', width: '100%' }}>
            </div>
        )
    }
}

export default Echarts
