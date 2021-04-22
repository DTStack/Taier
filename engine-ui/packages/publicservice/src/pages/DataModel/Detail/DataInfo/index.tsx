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
  const total = (index: number, count: number) => {
    switch (index) {
      case 0:
        return `共${count}张表`;
      default:
        return `共${count}个字段`;
    }
  };

  return (
    <div className="data-info">
      {tableList.map((item, index) => (
        <div key={index}>
          <div className="title-wrappe">
            <div className="title float-left">{item.title}</div>
            <div className="float-right sumary">
              {total(index, item.dataSource?.length)}
            </div>
          </div>
          <Table
            className="dt-table-border"
            rowKey={(record, index) => index.toString()}
            columns={item.columns}
            dataSource={item.dataSource}
            pagination={false}
          />
        </div>
      ))}
    </div>
  );
};

export default DataInfo;
