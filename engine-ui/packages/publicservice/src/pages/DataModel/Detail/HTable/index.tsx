import React from 'react';
import './style';
import { IModelDetail } from '../../types';
import { holder } from '../constants';

interface IPropsHTable {
  detail: Partial<IModelDetail>;
}

// TODO:部分字段缺失，与后端沟通
const HTable = (props: IPropsHTable) => {
  const { detail } = props;
  return (
    <table className="h-table" data-testid="h-table">
      <tbody>
        <tr>
          <td className="label">模型名称</td>
          <td className="value">{holder(detail.modelName)}</td>
          <td className="label">数据源</td>
          <td className="value">{holder(detail.dsUrl)}</td>
        </tr>
        <tr>
          <td className="label">创建人</td>
          <td className="value">{holder(detail.creator)}</td>
          <td className="label">创建时间</td>
          <td className="value">{holder(detail.createTime)}</td>
        </tr>
        <tr>
          <td className="label">分区字段（日期）</td>
          <td className="value">
            {holder(detail.modelPartition?.datePartitionColumn?.columnName)}
          </td>
          <td className="label">分区字段（时间）</td>
          <td className="value">
            {holder(detail.modelPartition?.timePartitionColumn?.columnName)}
          </td>
        </tr>
        <tr>
          <td className="label">备注</td>
          <td className="value" colSpan={3}>
            {holder(detail.remark)}
          </td>
        </tr>
      </tbody>
    </table>
  );
};

export default HTable;
