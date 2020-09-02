import React from "react";

import echarts from "../common";
import 'echarts/lib/chart/line'
import "echarts/lib/chart/bar";
import { fromJS } from "immutable";
import ReactResizeDetector from 'react-resize-detector';
import _ from 'lodash';

export default class BarChart extends React.Component {
  constructor(props) {
    super(props);
  }

  initChart = () => {
    const { option = {}, config = { handle: "" } } = this.props;
    const { chart } = this.state;
    chart.showLoading();
    chart.off("click");
    if (typeof config.handle == "function") {
      chart.on("click", config.handle.bind(this));
    }
    chart.setOption(option);
    chart.hideLoading();
  };
  shouldComponentUpdate(nextProps, nextState) {
    if (fromJS(nextProps) == fromJS(this.props)) {
      return false;
    } else {
      return true;
    }
  }
  componentDidMount() {
    let chart = echarts.init(this.id, "lz_theme");
    this.setState({ chart }, () => {
      this.initChart();
    });
  }
  componentDidUpdate() {
    this.initChart();
  }
  componentWillUnmount() {
    const { chart } = this.state;
    chart.dispose();
  }

  chartResize = _.throttle((width) => {
    const { chart } = this.state;
    console.log(chart, 'chart');
    if (chart) chart.resize();
  }, 1000)

  render() {
    let { height = "200px", width = "100%" } = this.props.config;
    return <div>
      <div ref={id => (this.id = id)} style={{ width, height }} />
      <ReactResizeDetector handleWidth onResize={this.chartResize.bind(this)} />
    </div>
  }
}
