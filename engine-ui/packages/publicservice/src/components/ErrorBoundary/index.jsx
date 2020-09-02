import React from './node_modules/react';
import Error from './node_modules/@/pages/exception/404'

export default class ErrorBoundary extends React.PureComponent{
  state={
    hasError:false
  }
  componentDidCatch(error, info) {
    this.setState({ hasError: true });
    console.log(error);
    console.log(info);
  }
  render(){
    const {hasError} = this.state;
    return hasError?<Error/>:this.props.children;
  }
}