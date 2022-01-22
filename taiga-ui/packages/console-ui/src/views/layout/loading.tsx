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

import * as React from 'react'
declare var window: any;
export default class GlobalLoading extends React.Component<any, any> {
    render () {
        return (
            <div className="laoding-wrapper" style={{ zIndex: 2005, position: 'fixed' }}>
                <div className="loading-center">
                    <h1 className="loading-title">{window.APP_CONF.prefix} CONSOLE</h1>
                    <div className="bouncywrap">
                        <div className="dotcon dc1">
                            <div className="dot"></div>
                        </div>
                        <div className="dotcon dc2">
                            <div className="dot"></div>
                        </div>
                        <div className="dotcon dc3">
                            <div className="dot"></div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
