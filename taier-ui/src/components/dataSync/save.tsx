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
import { Card, Button } from 'antd';

import Source from './source';
import Target from './target';
import Keymap from './keymap';
import Channel from './channel';

import './save.scss';

function Mask() {
    return <div className="mask-lock-layer" />;
}

export default class Save extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.navtoStep = this.props.navtoStep;
    }

    navtoStep: any;
    render() {
        const { navtoStep } = this;
        const { isIncrementMode, isStandeAlone } = this.props;

        return (
            <div className="g-step5">
                <div className="m-preview" style={{ padding: '0px 20px' }}>
                    <Card
                        bordered={false}
                        style={{ marginBottom: 10 }}
                        title="选择来源"
                        extra={
                            <a
                                href="javascript:void(0)"
                                onClick={() => this.navtoStep(0)}
                            >
                                修改
                            </a>
                        }
                    >
                        <Source readonly isIncrementMode={isIncrementMode} />
                        <Mask />
                    </Card>
                    <Card
                        bordered={false}
                        style={{ marginBottom: 10 }}
                        title="选择目标"
                        extra={
                            <a
                                href="javascript:void(0)"
                                onClick={() => this.navtoStep(1)}
                            >
                                修改
                            </a>
                        }
                    >
                        <Target readonly isIncrementMode={isIncrementMode} />
                        <Mask />
                    </Card>
                    <Card
                        bordered={false}
                        style={{ marginBottom: 10 }}
                        title="字段映射"
                        extra={
                            <a
                                href="javascript:void(0)"
                                onClick={() => this.navtoStep(2)}
                            >
                                修改
                            </a>
                        }
                    >
                        <Keymap readonly />
                        <Mask />
                    </Card>
                    <Card
                        bordered={false}
                        style={{ marginBottom: 10 }}
                        title="通道控制"
                        extra={
                            <a
                                href="javascript:void(0)"
                                onClick={() => this.navtoStep(3)}
                            >
                                修改
                            </a>
                        }
                    >
                        <Channel
                            readonly
                            isIncrementMode={isIncrementMode}
                            isStandeAlone={isStandeAlone}
                        />
                        <Mask />
                    </Card>
                </div>
                <div className="steps-action">
                    <Button
                        style={{ marginRight: 8 }}
                        onClick={() => this.prev(navtoStep)}
                    >
                        上一步
                    </Button>
                    <Button
                        type="primary"
                        // disabled={!notSynced}
                        onClick={() => this.save(navtoStep)}
                    >
                        保存
                    </Button>
                </div>
            </div>
        );
    }

    prev(cb: any) {
        /* eslint-disable */
        cb.call(null, 3);
        /* eslint-disable */
    }

    save(cb: any) {
        this.props.saveJob(cb);
    }
}
