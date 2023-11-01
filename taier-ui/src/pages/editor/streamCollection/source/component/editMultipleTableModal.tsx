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
import { Modal, Transfer } from 'antd';
import { cloneDeep } from 'lodash';

import './editMultipleTableModal.scss';

class EditMultipleTableModal extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.state = {
            selectKeys: props.selectKeys,
        };
    }
    render() {
        const { selectKeys } = this.state;
        const { visible, onCancel, onOk } = this.props;
        return (
            <Modal
                width={520}
                visible={visible}
                title="编辑分组表"
                onCancel={onCancel}
                onOk={() => {
                    onOk(selectKeys);
                }}
            >
                <TransferEdit
                    onChange={(keys: any) => {
                        this.setState({
                            selectKeys: keys,
                        });
                    }}
                    {...this.props}
                    selectKeys={selectKeys}
                />
            </Modal>
        );
    }
}
class TransferEdit extends React.Component<any, any> {
    componentWillUnmount() {
        this.props.onSearch(null);
    }
    /**
     * 原始数据包含选中表名
     */
    getDataSource = () => {
        const { tableList, selectKeys = [] } = this.props;
        const newTableList = cloneDeep(tableList);
        function dataSourceList(tableList: any[]) {
            return tableList.map((table: any) => {
                return {
                    key: table,
                    title: table,
                };
            });
        }
        if (!selectKeys.length) return dataSourceList(newTableList);
        for (const key of selectKeys) {
            if (newTableList.indexOf(key) === -1) newTableList.push(key);
        }
        return dataSourceList(newTableList);
    };

    render() {
        const { selectKeys, onChange, onSearch } = this.props;
        return (
            <Transfer
                className="c-multiple-table__transfer"
                dataSource={this.getDataSource()}
                showSearch
                targetKeys={selectKeys}
                onChange={onChange}
                onSearch={(direction, value) => {
                    direction === 'left' && onSearch(value);
                }}
                render={(item: any) => item.title}
            />
        );
    }
}
export default EditMultipleTableModal;
