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

import stream from "@/api/stream"
import { Collapse, Input, Row, Spin, Table } from "antd"
import { UpOutlined, DownOutlined, FrownOutlined } from "@ant-design/icons"
import React from "react"
import { isShowCollapse } from "../../helper"
import { assign } from "lodash"
import { Utils } from "@dtinsight/dt-utils/lib"

const Panel = Collapse.Panel;
const TextArea = Input.TextArea;

interface CollaProps {
    previewData: any[];
    loading: boolean;
    type?: number;
    style?: any;
    className?: string;
}

export class CollapsePreview extends React.Component<CollaProps, any> {
    render () {
        const { previewData, style, className, loading, type } = this.props
        let defaultStyle: any = {
            maxHeight: '300px',
            minHeight: '200px'
        }
        let defaultClass = 'ellipsis';
        if (style) defaultStyle = assign(defaultStyle, style)
        if (className) defaultClass = `${defaultClass} ${className}`

        return previewData?.length > 0 ? <Collapse accordion>
            {
                previewData.map((item: any, index: any) => {
                    if (type && isShowCollapse(type)) {
                        item = JSON.stringify(item)
                    }
                    const jsonStr = Utils.isJSONStr(item) ? Utils.jsonFormat(item, 4) : item
                    return (
                        <Panel
                            header={<div className={defaultClass}>{jsonStr || '无数据'}</div>}
                            key={`data-preview-${index}`}
                        >
                            <TextArea
                                style={defaultStyle}
                                value={jsonStr}
                            />
                        </Panel>
                    )
                })
            }
        </Collapse> : (<div style={{ textAlign: 'center' }}>
            {loading ? <Spin size="small" tip="加载中..." />
                : <><FrownOutlined /> 暂无数据</>}
        </div>)
    }
}

class TablePreview extends React.Component<any, any> {
    state: any = {
        isShow: false,
        loading: false,
        data: null
    }

    componentDidMount () {
        if (this.props?.notDesc) {
            this.loadData()
        }
    }

    componentDidUpdate (prevProps: any) {
        const { data, notDesc } = this.props
        /** flinkSql任务es数据源索引值可相同，区分处理flinkSql任务和实时采集任务 */
        if ((prevProps?.data !== data && notDesc) ||
            prevProps?.data?.tableName != data?.tableName) {
            this.setState({
                data: null,
                isShow: false
            })
            if (notDesc) {
                this.loadData()
            }
        }
    }

    showPreview = () => {
        const { isShow } = this.state;
        if (isShow) {
            this.setState({
                isShow: false,
                data: []
            })
        } else {
            this.setState({
                isShow: true
            });
            this.loadData()
        }
    }
    loadData = async () => {
        const { data, isAssetCreate } = this.props;
        const { sourceId, schema, tableName } = data;
        if (!isAssetCreate && (!sourceId || !tableName)) {
            return;
        }
        this.setState({
            loading: true
        })
        try {
            let res = isAssetCreate
                ? await stream.previewAssetData(data)
                : await stream.pollPreview({
                    sourceId,
                    schema,
                    tableName
                });
            if (res && res.code == 1) {
                this.setState({
                    data: res.data
                })
            }
        } finally {
            this.setState({
                loading: false
            })
        }
    }
    getColumn = () => {
        const { data } = this.state;
        const columns = data?.columnList
        if (!columns) {
            return {
                columns: [],
                width: 0
            };
        }
        let width = 0;
        const c = columns.map((column: string, index: number) => {
            let w = Math.max(30 + column.length * 10, 60);
            width += w;
            return {
                title: column,
                dataIndex: index,
                width: w
            }
        });
        return {
            columns: c,
            width
        }
    }
    getData = () => {
        const { data } = this.state;
        return data?.dataList || []
    }

    render () {
        const { notDesc, type } = this.props // 是否展示数据预览, flinkSql任务标记
        const { isShow, loading } = this.state;
        const { columns, width } = this.getColumn();

        return (
            <div style={{ textAlign: 'center' }}>
                {!notDesc && <a onClick={this.showPreview}>数据预览 {isShow ? <UpOutlined /> : <DownOutlined />}</a>}
                {(isShow || notDesc) && !isShowCollapse(type) && (
                    <Row style={{ padding: !notDesc ? 20 : 0 }}>
                        <Table
                            className="dt-table-border"
                            columns={columns}
                            dataSource={this.getData()}
                            pagination={false}
                            loading={loading}
                            scroll={{ x: width }}
                            size="middle"
                        />
                    </Row>
                )}

                {(isShow || notDesc) && isShowCollapse(type) && (
                    <CollapsePreview
                        previewData={this.getData()}
                        loading={loading}
                        type={type}
                    />
                )}
            </div>
        )
    }
}

export default TablePreview;