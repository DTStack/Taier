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

import * as React from 'react';
import * as echarts from 'echarts/lib/echarts';
import { isEqual } from 'lodash';
import 'echarts/lib/component/title';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/grid';
import 'echarts/lib/component/legend';
import 'echarts/lib/chart/line';
import 'echarts/lib/chart/bar';
import 'echarts/lib/chart/pie';
import 'echarts/lib/chart/gauge';

interface IProps {
    option: any;
    width?: string | number;
    height?: string | number;
    style?: any;
}

interface IState {
    myChart: any;
}

export default class Chart extends React.PureComponent<IProps, IState> {
    constructor(props: IProps) {
        super(props);
        this.state = {
            myChart: null,
        };
    }

    public chart: any = null;

    componentDidMount() {
        const { option } = this.props;
        const myChart = echarts.init(this.chart);
        myChart.setOption(option);
        this.setState({ myChart });
    }

    componentWillUnmount() {
        this.state.myChart.dispose();
    }

    componentDidUpdate(prevProps: IProps) {
        const { option } = this.props;
        if (!isEqual(prevProps.option, option)) {
            this.state.myChart.setOption(option);
        }
    }

    styleFormat = (param: string | number) => {
        if (typeof param === 'string') {
            return param.indexOf('%') > -1 ? param : `${param.replace(/[px]/gi, '')}px`;
        }
        return `${param}px`;
    };

    render() {
        const { width, height, style } = this.props;
        const styleParams = {
            ...style,
            width: width ? this.styleFormat(width) : '100%',
            height: height ? this.styleFormat(height) : '300px',
        };
        return (
            <div
                ref={(chart) => {
                    this.chart = chart;
                }}
                style={styleParams}
            />
        );
    }
}
