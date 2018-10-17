import React, { Component } from 'react';

export default class StepFour extends Component{
  constructor(props){
    super(props);
    this.state = {
      num: 3,
      siv: null
    }
  }
  componentDidMount(){
    console.log('MOunt')
    this.state.siv = setInterval(()=>{
      console.log(this.state.num)
      this.setState((preState)=>({
        num: preState.num-1
      }),()=>{
        if(this.state.num === 0){
          clearInterval(this.state.siv)
        }
      })
    },1000)
  }
  componentWillUnMount(){
    this.state.siv && clearInterval(this.state.siv)
  }
  render(){

    return (
      <div className="step-four-container step-container">
        <p className="result">创建成功</p>
        <p className="inter"><span>{this.state.num}秒后自动返回</span></p>
      </div>
    )
  }
}