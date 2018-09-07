import React from 'react'

/* eslint-disable */
export default (loader, collection) => (
    class AsyncComponent extends React.Component {
        constructor(props) {
            super(props);
            this.Component = null;
            this.state = { Component: AsyncComponent.Component, error: false };
        }

        componentWillMount() {
            if (!this.state.Component) {
                loader().then((Component) => {
                    AsyncComponent.Component = Component;
                    this.setState({ Component });
                }).catch(
                    (e) => {
                        this.setState({
                            error: true
                        })
                    }
                );
            }
        }

        render() {
            const {error} = this.state;
            if (error) {
                return <div>
                    <h2 style={{textAlign:"center"}}>服务端该模块更新中，请刷新重试。</h2>
                    <h4 style={{textAlign:"center"}}>若该问题长时间存在，请联系管理员。</h4>
                </div>
            }
            if (this.state.Component) {
                return (
                    <this.state.Component {...this.props} {...collection} />
                )
            }
            return null;
        }
    }
);
/* eslint-disable */

