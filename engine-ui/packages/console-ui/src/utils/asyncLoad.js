import React from 'react'

/* eslint-disable */
export default (loader, collection) => (
  class AsyncComponent extends React.Component {
      constructor(props) {
          super(props);
          this.Component = null;
          this.state = { Component: AsyncComponent.Component };
      }

      componentWillMount() {
          if (!this.state.Component) {
              loader().then((Component) => {
                  AsyncComponent.Component = Component;
                  this.setState({ Component });
              });
          }
      }

      render() {
          if (this.state.Component) {
              return (
                <this.state.Component { ...this.props } { ...collection } />
              )
          }
          return null;
      }
    }
);
/* eslint-disable */

