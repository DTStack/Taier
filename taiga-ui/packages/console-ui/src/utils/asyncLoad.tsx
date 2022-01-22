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

export default (loader: any, collection: { name: string }) => (

    class AsyncComponent extends React.Component<any, any> {
        public static Component: any = null;
        public state: any = {
            Component: null
        };
        constructor (props: any) {
            super(props);
            this.state = { Component: AsyncComponent.Component, error: false };
        }

        // eslint-disable-next-line react/no-deprecated
        public componentWillMount () {
            if (!this.state.Component) {
                loader().then((Component: any) => {
                    AsyncComponent.Component = Component;
                    this.setState({ Component });
                }).catch(
                    (e: any) => {
                        console.error(e);
                        this.setState({
                            error: true
                        });
                    }
                );
            }
        }
        public render () {
            const { error } = this.state;
            if (error) {
                return (
                    <div>
                        <h2 style={{ textAlign: 'center' }}>该模块更新中，请刷新重试。</h2>
                        <h4 style={{ textAlign: 'center' }}>若该问题长时间存在，请联系管理员。</h4>
                    </div>
                );
            }
            if (this.state.Component) {
                return (<this.state.Component {...this.props} {...collection} />);
            }
            return null;
        }
    }
);
