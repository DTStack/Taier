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
import { Icon } from 'antd'
import { browserHistory, hashHistory } from 'react-router'

export default class GoBack extends React.Component<any, any> {
    go = () => {
        const { url, history, autoClose } = this.props

        if (url) {
            if (history) { browserHistory.push(url) } else { hashHistory.push(url) }
        } else {
            if (window.history.length == 1) {
                if (autoClose) {
                    window.close();
                }
            } else {
                hashHistory.go(-1);
            }
        }
    }

    getButtonView () {
        const { style } = this.props;

        let iconStyle: any = {
            cursor: 'pointer',
            fontFamily: 'anticon',
            fontSize: '18px',
            color: 'rgb(148, 168, 198)',
            letterSpacing: '5px',
            position: 'relative',
            top: '2px'
        }

        if (style) {
            Object.assign(iconStyle, style)
        }

        return (
            <span
                style={iconStyle}
                onClick={this.go}
            >
                <Icon
                    type={'left-circle-o'}
                />
                { this.props.children }
            </span>
        )
    }

    render () {
        return this.getButtonView();
    }
}
