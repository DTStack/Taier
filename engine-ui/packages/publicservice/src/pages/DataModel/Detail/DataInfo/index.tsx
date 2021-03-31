import React from 'react';
import { Table } from 'antd';
import './style';
import { relationListColumns, columns } from './constants';

interface ITableItem {
  title: string;
  columns: any[];
  dataSource: any[];
}

const tableList: ITableItem[] = [
  {
    title: '关联表',
    columns: relationListColumns,
    dataSource: [],
  },
  {
    title: '维度',
    columns: columns,
    dataSource: [],
  },
  {
    title: '度量',
    columns: columns,
    dataSource: [],
  },
];

interface IPropsDataInfo {
  relationTableList: any[];
  dimensionList: any[];
  metricList: any[];
}

const DataInfo = (props: IPropsDataInfo) => {
  const { relationTableList, dimensionList, metricList } = props;
  const list = [relationTableList, dimensionList, metricList];
  // 构造tableList参数
  tableList.forEach((item, index) => {
    item.dataSource = list[index];
  });

  return (
    <div className="data-info">
      {tableList.map((item) => (
        <>
          <div className="title">{item.title}</div>
          <Table
            rowKey={(record, index) => index.toString()}
            columns={item.columns}
            dataSource={item.dataSource}
            pagination={false}
          />
        </>
      ))}
    </div>
  );
};

export default DataInfo;
