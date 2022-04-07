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
import { Modal, Button, Radio } from 'antd';
import stream from '@/api/stream';
import TablePreview, { CollapsePreview } from './component/tablepreview';
import { haveDataPreview, haveTopic } from '@/utils/enums';

const previewTypes = {
    Latest: 'latest',
    Earliest: 'earliest'
}

class DataPreviewModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            loading: false,
            previewData: [], // panel数据，若props传入dataSource，数据则为dataSource
            previewType: previewTypes.Earliest
        };
        this.retryCount = 0;
    }
    retryCount: number;
    MAX_RETRY_COUNT: number = 3;
    _clock: any;
    componentDidUpdate (prevProps: any) {
        const params = this.props.params
        if (prevProps.visible != this.props.visible && this.props.visible && params) {
            this.setState({
                previewData: []
            })
            clearTimeout(this._clock);
            this.retryCount = 0;
            this.getDataPreviewList(params);
        }
    }
    componentWillUnmount () {
        clearTimeout(this._clock);
    }
    getDataPreviewList = async (params: any) => {
        const { type, isAssetCreate } = this.props
        const { previewType } = this.state
        this.setState({
            loading: true
        })
        if (!haveTopic(type)) return;
        const reqParams = {
            ...params,
            previewModel: previewType
        }
        const res = isAssetCreate
            ? await stream.previewAssetKafkaData(reqParams)
            : await stream.getDataPreview(reqParams);
        if (params != this.props.params) {
            return;
        }
        if (res.code === 1) {
            if (res.data) {
                this.setState({
                    previewData: res.data,
                    loading: false
                })
            } else if (this.retryCount < this.MAX_RETRY_COUNT) {
                this._clock = setTimeout(() => {
                    this.getDataPreviewList({
                        ...params,
                        previewModel: previewType
                    })
                }, 1000)
                this.retryCount++;
            } else {
                this.setState({
                    loading: false
                })
            }
        } else {
            this.setState({
                loading: false
            })
        }
    }

    previewTypeChange = (e: any) => {
        const params = this.props.params
        this.setState({
            previewType: e.target.value
        }, () => this.getDataPreviewList(params))
    }

    render () {
        const { visible, onCancel, type, dataSource, params, isAssetCreate } = this.props;
        const { previewData, loading, previewType } = this.state;
        return (
            <Modal
                visible={visible}
                title='数据预览'
                onCancel={onCancel}
                maskClosable={false}
                footer={[
                    <Button key='back' type="primary" onClick={onCancel}>关闭</Button>
                ]}
            >
                {haveTopic(type) && <>
                    <Radio.Group
                        value={previewType}
                        onChange={this.previewTypeChange}
                        className="c-dataPreview__radio"
                    >
                        {Object.entries(previewTypes).map(([key, value]) => {
                            return <Radio.Button key={value} value={value}>{key}</Radio.Button>
                        })}
                    </Radio.Group>
                    <CollapsePreview
                        previewData={dataSource || previewData}
                        loading={loading}
                    />
                </>}
                {haveDataPreview(type) && <TablePreview
                    notDesc={true}
                    data={params}
                    type={type}
                    isAssetCreate={isAssetCreate}
                />}
            </Modal>
        )
    }
}

export default DataPreviewModal;
