import * as React from 'react';

import { Spin } from 'antd';

export interface MatrixGraphProp {
    getData: () => Promise<any>;
    visible: boolean;
    data: {
        id?: number;
        [propName: string]: any;
    };
    dataKey?: string;
    showColor?: boolean;
}

interface MatrixGraphState {
    loading: boolean;
    data: string[][];
}

class MatrixGraph extends React.Component<MatrixGraphProp, MatrixGraphState> {
    state: MatrixGraphState = {
        loading: false,
        data: []
    }
    componentDidMount () {
        this.initData();
    }
    componentDidUpdate (prevProps: any, prevState: any) {
        if (this.props.visible && !prevProps.visible) {
            this.initData();
        }
    }
    renderLoading () {
        return <Spin tip='加载中' />;
    }
    async initData () {
        const { dataKey } = this.props;
        this.setState({
            loading: true
        });
        try {
            let res = await this.props.getData();
            if (res && res.code == 1) {
                let data = res.data;
                if (dataKey) {
                    data = data[dataKey]
                }
                this.setState({
                    data
                })
            }
        } finally {
            this.setState({
                loading: false
            })
        }
    }
    renderGraph () {
        const { data } = this.state;
        const result: React.ReactNode[] = [];
        for (let i = 0; i < data.length; i++) {
            const lineData = data[i];
            result.push(this.renderLine(lineData, i));
        }
        if (result.length) {
            return <div className='c-matrixGraph__content'>
                <div>{result}</div>
                <div className='c-matrixGraph__left-title'>真实</div>
                <div className='c-matrixGraph__top-title'>预测</div>
            </div>;
        }
        return <div style={{ textAlign: 'center', color: '#999', fontSize: 14 }}>暂无数据</div>;
    }
    renderLine (data: string[], lineIndex: number) {
        const { showColor } = this.props;
        return <div data-index={lineIndex} className='c-matrixLine'>
            {data.map((num, index) => {
                const style: any = {};
                if (showColor) {
                    const alpha = parseFloat(num);
                    style['background'] = `rgba(153, 153, 153, ${alpha})`;
                    if (alpha > 0.5) {
                        style['color'] = '#FFFFFF';
                    }
                }
                return <div style={style} data-index={index} className='c-matrixLine__item' key={lineIndex + '' + index}>
                    {num}
                </div>
            })}
        </div>
    }
    render () {
        const { loading } = this.state;
        return (
            <div className='c-martrixGraph'>
                {loading ? this.renderLoading() : this.renderGraph()}
            </div>
        )
    }
}
export default MatrixGraph;
