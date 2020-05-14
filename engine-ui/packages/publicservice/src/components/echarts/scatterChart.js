import React from 'react'

import echarts from "./common";
import 'echarts/lib/chart/scatter'
import {fromJS} from 'immutable'
import ReactResizeDetector from 'react-resize-detector';
import throttle from '@/utils/throttle';
export default class ScatterChart extends React.Component {
  
  constructor(props) {
    super(props)
  }
  
  initChart=()=> {
    const { option={},config={handle:''}} = this.props;
    const{ chart }=this.state;
    chart.resize();  // 此步骤解决父容器宽度为百分比时 图大小超出父容器
    chart.showLoading();
    chart.off('click');
    chart.off('dblclick');
    if(typeof config.handle=='function' ){
      chart.on('click',config.handle.bind(this));
      chart.on('dblclick',config.dbHandle.bind(this));
    }
    chart.setOption(option);
    chart.hideLoading();
  }
  shouldComponentUpdate(nextProps,nextState){
    if(fromJS(nextProps)==fromJS(this.props)){
      return false
    }else{
      return true;
    }
  }
  componentDidMount(){
    let chart=echarts.init(this.id,'lz_theme');
    this.setState({chart},()=>{
      this.initChart();
    });
  }
  componentDidUpdate() {
    this.initChart()
  }
  componentWillUnmount(){
    const{ chart }=this.state;
    chart.dispose();
  }
  chartResize = throttle((width) => {
    const { chart } = this.state;
    if (chart) chart.resize();
  }, 1000)

  render() {
    let { height="300px",width="100%"} = this.props.config||{};
    return <div>
    <div ref={id => (this.id = id)}style={{width, height}} className="tem-classname"></div>
    <ReactResizeDetector className="temp2-classname" handleWidth handleHeight onResize={this.chartResize.bind(this)}/>
   </div>
  }
}


