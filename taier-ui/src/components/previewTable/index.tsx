import { useMemo } from 'react';
import { Table } from 'antd';
import type { ColumnType } from 'antd/lib/table';

import { useCalcTableScroll } from '../customHooks';

export default function PreviewTable({ data }: { data: { columnList: string[]; dataList: string[][] } }) {
    const { scroll } = useCalcTableScroll({ className: 'taier__preview__table' });
    const columns = useMemo<ColumnType<any>[]>(() => {
        if (data?.columnList.length) {
            return data.columnList.map((s) => {
                return {
                    title: s,
                    dataIndex: s,
                    key: s,
                    width: 20 + s.length * 10,
                };
            });
        }

        return [];
    }, [data?.columnList]);

    const dataSource = useMemo<Record<string, any>[]>(() => {
        if (data?.dataList.length) {
            return data.dataList.map((arr, i) => {
                const o: Record<string, string | number> = {};
                for (let j = 0; j < arr.length; j += 1) {
                    o.key = i;
                    o[data?.columnList[j]] = arr[j];
                }
                return o;
            });
        }

        return [];
    }, [data?.dataList, data?.columnList]);

    return (
        <div className="p-4 h-full">
            <Table
                className="taier__preview__table h-full"
                columns={columns}
                dataSource={dataSource}
                scroll={scroll}
                pagination={false}
                bordered
            />
        </div>
    );
}
