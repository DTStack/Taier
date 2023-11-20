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

import { HotTable } from '@handsontable/react';
import { registerAllModules } from 'handsontable/registry';

import { copyText } from '@/utils';
import 'handsontable/dist/handsontable.full.css';
import './index.scss';

// register Handsontable's modules
registerAllModules();

interface ISpreadSheet {
    columns: string[];
    data: string[][];
}

export default function SpreadSheet({ columns = [], data }: ISpreadSheet) {
    const handleCopy = (arr: any[][]) => {
        /**
         * 去除格式化
         */
        const value = arr
            .map((row: any) => {
                return row.join('\t');
            })
            .join('\n');
        copyText(value);
        // prevent the native behavior
        return false;
    };

    const getData = () => {
        let showData = data;
        if (!showData || !showData.length) {
            const emptyArr = new Array(columns.length).fill('', 0, columns.length);
            emptyArr[0] = '暂无数据';
            showData = [emptyArr];
        }
        return showData;
    };

    const getCell = () => {
        if (!data || !data.length) {
            return [{ row: 0, col: 0, className: 'htCenter htMiddle' }];
        }
        return undefined;
    };

    const getMergeCells = () => {
        if (!data || !data.length) {
            return [{ row: 0, col: 0, rowspan: 1, colspan: columns.length }];
        }
        return false;
    };

    // 空数组情况，不显示colHeaders，否则colHeaders默认会按照 A、B...显示
    // 具体可见 https://handsontable.com/docs/7.1.1/Options.html#colHeaders
    const isShowColHeaders = columns && columns.length > 0;

    return (
        <HotTable
            data={getData()}
            cell={getCell()}
            mergeCells={getMergeCells()}
            colHeaders={isShowColHeaders ? columns : false}
            rowHeaders
            readOnly
            width="100%"
            className="dtc-handsontable-no-border"
            columnHeaderHeight={25}
            beforeCopy={handleCopy}
            manualRowResize
            manualColumnResize
            licenseKey="non-commercial-and-evaluation"
            contextMenu={{
                items: {
                    copy: {
                        name: '复制',
                        callback() {
                            const indexArr = this.getSelected();

                            if (indexArr) {
                                const [firstRow, firstCol, lastRow, lastCol] = indexArr[0];
                                const dataArr = this.getData(
                                    firstRow === -1 ? 0 : firstRow,
                                    firstCol === -1 ? 0 : firstCol,
                                    lastRow === -1 ? 0 : lastRow,
                                    lastCol === -1 ? 0 : lastCol
                                );
                                handleCopy(dataArr);
                            }
                        },
                    },
                },
            }}
            stretchH="all"
        />
    );
}
