import * as React from 'react';

import Resize from 'widgets/resize';

import echarts from 'echarts/lib/echarts';
import 'echarts/lib/chart/bar';
import 'echarts/lib/chart/pie';

export type getOptions = (lineData: any) => {
    [propName: string]: any;
};

export interface SingleChartPropType {
    getData: () => Promise<any>;
    getOptions: getOptions;
    visible: boolean;
    data: {
        id?: number;
        [propName: string]: any;
    };
}

class SingleChart extends React.Component<SingleChartPropType, any> {
    _domId: string = 'JS_SingleChart';
    _chart1: any;
    componentDidMount () {
        this._chart1 = echarts.init(document.getElementById(this._domId) as HTMLDivElement);
        this.renderChart();
    }
    componentDidUpdate (prevProps: any, prevState: any) {
        if (this.props.visible && !prevProps.visible) {
            this.renderChart();
        }
    }
    async renderChart () {
        const { getData, getOptions } = this.props;
        let res = await getData();
        if (!res || res.code !== 1) {
            return;
        }
        this._chart1.showLoading('default', {
            text: 'loading...',
            color: '#108ee9',
            textColor: '#000',
            maskColor: 'rgba(255, 255, 255, 0.8)',
            zlevel: 0
        });
        let options = getOptions(res.data);
        this._chart1.setOption(options, true);
        this._chart1.hideLoading()
    }
    resizeChart = () => {
        if (this._chart1) {
            this._chart1.resize();
        }
    }
    render () {
        return (
            <Resize onResize={this.resizeChart}>
                <div id={this._domId} style={{
                    height: '400px',
                    width: '100%'
                }}></div>
            </Resize>
        )
    }
}
export default SingleChart;
