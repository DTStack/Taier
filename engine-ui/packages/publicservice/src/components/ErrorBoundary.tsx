import React from 'react';
import Exception from '@/components/Exception';

class ErrorBoundary extends React.PureComponent {
  state = {
    hasError: false,
  };
  componentDidCatch(error) {
    this.setState({ hasError: true });
    console.log(error);
  }
  render() {
    return this.state.hasError ? (
      <Exception status={404} subTitle="对不起，你访问的页面不存在" />
    ) : (
      this.props.children
    );
  }
}

export default ErrorBoundary;
