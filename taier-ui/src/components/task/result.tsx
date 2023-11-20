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

import { useEffect, useMemo, useState } from 'react';
import molecule from '@dtinsight/molecule/esm';
import { Pagination, Select } from 'antd';

import SpreadSheet from '@/components/spreadSheet';
import './result.scss';

const defaultOutTable = 1;

const { Option } = Select;

interface IResultTabProps {
    id?: string;
    tableType?: number;
    tableName?: string;
    tableNameArr?: string[];
}

interface IResultProps {
    tab?: IResultTabProps;
    extraView?: React.ReactNode;
    data: string[][];
    getTableData?: (pagination: any, newTab: any, callback: (total: number) => void) => void;
    updateTableData?: (name: string, tableName: string, id: IResultTabProps['id']) => void;
}

export default function Result({ tab, extraView, data, getTableData, updateTableData }: IResultProps) {
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10,
        total: 0,
    });

    const onPageChange = (tableName?: string, nextPagination?: typeof pagination) => {
        if (tab?.tableType) {
            const newTab = { ...tab, tableName: tableName ?? tab.tableName };
            getTableData?.(nextPagination || pagination, newTab, (total) => {
                setPagination((p) => ({ ...p, total }));
            });
        }
    };

    const tableNameChange = (tableName: string) => {
        updateTableData?.('tableName', tableName, tab!.id);
        setPagination((p) => {
            const nextPagination = { ...p, current: 1 };
            onPageChange(tableName, nextPagination);
            return nextPagination;
        });
    };

    const renderOptions = () => {
        const { tableNameArr } = tab!;
        return tableNameArr!.map((name) => {
            return (
                <Option key={name} value={name}>
                    {name}
                </Option>
            );
        });
    };

    const getPageData = (pageData?: string[][]) => {
        let result: string[][] = [];
        if (!pageData) {
            return result;
        }
        const { current, pageSize } = pagination;
        const begin = (current - 1) * pageSize;
        const end = begin + pageSize;
        result = pageData.slice(begin, end);
        return result;
    };

    useEffect(() => {
        if (tab?.tableType) {
            onPageChange();
        }
    }, []);

    const showData = useMemo(() => data.slice(1, data.length), [data]);
    const resultData = tab?.tableType ? showData : getPageData(showData);
    const total = !tab?.tableType ? showData.length : pagination.total;
    const pageSizeOptions = !tab?.tableType ? ['10', '20', '30', '40'] : ['10', '20', '50', '100'];

    return (
        <div className="c-ide-result">
            {!!tab?.tableType && (
                <div className="console-select c-ide-result__select">
                    <span>{tab?.tableType === defaultOutTable ? '数据表：' : '结果表：'}</span>
                    <Select defaultValue={tab.tableName} style={{ width: 340 }} onChange={tableNameChange}>
                        {renderOptions()}
                    </Select>
                </div>
            )}
            <molecule.component.Scrollbar>
                <SpreadSheet columns={data[0]} data={resultData} />
            </molecule.component.Scrollbar>
            <div className="c-ide-result__tools">
                {extraView}
                <span className="c-ide-result__tools__pagination">
                    <Pagination
                        size="small"
                        {...pagination}
                        total={total}
                        showSizeChanger
                        pageSizeOptions={pageSizeOptions}
                        onChange={(page) => {
                            setPagination((p) => {
                                const nextPagination = { ...p, current: page };
                                onPageChange(undefined, nextPagination);
                                return nextPagination;
                            });
                        }}
                        onShowSizeChange={(_, size) => {
                            setPagination((p) => {
                                const nextPagination = { ...p, current: 1, pageSize: size };
                                onPageChange(undefined, nextPagination);
                                return nextPagination;
                            });
                        }}
                    />
                </span>
            </div>
        </div>
    );
}
