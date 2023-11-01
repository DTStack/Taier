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

import { useState } from 'react';
import { message,Modal, Table } from 'antd';
import classNames from 'classnames';

import type { ITaskVOProps } from '@/interface';

interface IRecommendTaskProps {
    visible: boolean;
    taskList: ITaskVOProps[];
    /**
     * 已选择的任务
     */
    existTask?: ITaskVOProps[] | null;
    onCancel: () => void;
    onOk: (rows: ITaskVOProps[]) => void;
}

const COLUNMS = [
    {
        title: '表名',
        dataIndex: 'tableName',
        width: '200px',
    },
    {
        title: '任务名称',
        dataIndex: 'name',
    },
];

export default function RecommendTaskModal({ visible, taskList, existTask = [], onCancel, onOk }: IRecommendTaskProps) {
    const [selectedRows, setSelectedRows] = useState<ITaskVOProps[]>([]);

    const handleCancel = () => {
        onCancel();
        setSelectedRows([]);
    };

    const handleOk = () => {
        if (selectedRows.length === 0) {
            message.warning('请选择依赖');
            return;
        }
        onOk(selectedRows);
        setSelectedRows([]);
    };

    const getCheckboxProps = (record: ITaskVOProps) => {
        const { id } = record;
        let isExist = false;
        if (existTask) {
            existTask.forEach((item) => {
                if (item.id === id) {
                    isExist = true;
                }
            });
        }
        if (isExist) {
            return { disabled: true };
        }
        return {};
    };

    return (
        <Modal
            title="推荐上游依赖"
            maskClosable={false}
            visible={visible}
            onCancel={handleCancel}
            onOk={handleOk}
            okText="确定"
            cancelText="取消"
        >
            <p className={classNames('m-10px')}>提示：该分析仅基于您已发布过的任务进行分析</p>
            <Table
                className="dt-ant-table dt-ant-table--border"
                columns={COLUNMS}
                dataSource={taskList}
                pagination={false}
                rowSelection={{
                    selectedRowKeys: selectedRows.map((item) => item.id),
                    onChange: (_, nextSelectedRows) => {
                        setSelectedRows(nextSelectedRows);
                    },
                    getCheckboxProps,
                }}
                scroll={{ y: 400 }}
            />
        </Modal>
    );
}
