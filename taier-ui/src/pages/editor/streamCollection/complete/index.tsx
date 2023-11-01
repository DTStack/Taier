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

import React from 'react';
import molecule from '@dtinsight/molecule';
import { connect as moleculeConnect } from '@dtinsight/molecule/esm/react';
import { Button, Card, FormProps } from 'antd';

import { IDataSourceUsedInSyncProps } from '@/interface';
import Channel from '../channel';
import Source from '../source';
import TargetSource from '../targetSource';
import { streamTaskActions } from '../taskFunc';

function Mask() {
    return <div className="mask-lock-layer" />;
}
interface IProps extends FormProps {
    collectionData?: any;
    saveJob?: any;
    sourceList: IDataSourceUsedInSyncProps[];
}
class CollectionComplete extends React.Component<IProps, any> {
    navtoStep(step: any) {
        streamTaskActions.navtoStep(step);
    }
    prev() {
        streamTaskActions.navtoStep(2);
    }
    save() {
        this.props.saveJob();
    }
    render(): React.ReactNode {
        const { collectionData, sourceList } = this.props;
        return (
            <div>
                <div className="m-preview" style={{ padding: '0px 20px' }}>
                    <Card
                        bordered={false}
                        title={<div>选择来源</div>}
                        style={{ marginBottom: 10 }}
                        extra={<a onClick={() => this.navtoStep(0)}>修改</a>}
                    >
                        <Source collectionData={collectionData} sourceList={sourceList} readonly />
                        <Mask />
                    </Card>
                    <Card
                        bordered={false}
                        title={<div>选择目标</div>}
                        style={{ marginBottom: 10 }}
                        extra={<a onClick={() => this.navtoStep(1)}>修改</a>}
                    >
                        <TargetSource collectionData={collectionData} sourceList={sourceList} readonly />
                        <Mask />
                    </Card>
                    <Card
                        bordered={false}
                        title={<div>通道控制</div>}
                        style={{ marginBottom: 10 }}
                        extra={<a onClick={() => this.navtoStep(2)}>修改</a>}
                    >
                        <Channel collectionData={collectionData} sourceList={sourceList} readonly />
                        <Mask />
                    </Card>
                </div>
                <div className="steps-action">
                    <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>
                        上一步
                    </Button>
                    <Button type="primary" disabled={!collectionData.notSynced} onClick={() => this.save()}>
                        保存
                    </Button>
                </div>
            </div>
        );
    }
}

export default moleculeConnect(molecule.editor, CollectionComplete);
