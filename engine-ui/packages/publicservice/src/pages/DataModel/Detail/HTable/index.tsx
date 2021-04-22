import React from 'react';
import './style';
import { IModelDetail } from '../../types';
import { holder } from '../constants';

interface IPropsHTable {
  detail: Partial<IModelDetail>;
}

const HTable = (props: IPropsHTable) => {
  const { detail } = props;
  return (
    <table className="h-table" data-testid="h-table">
      <tbody>
        <tr>
          <td className="label border-top border-left">模型名称</td>
          <td className="value border-top">{holder(detail.modelName)}</td>
          <td className="label border-top">数据源</td>
          <td className="value border-right border-top">{holder(detail.dsName)}</td>
        </tr>
        <tr>
          <td className="label border-left">创建人</td>
          <td className="value">{holder(detail.creator)}</td>
          <td className="label">创建时间</td>
          <td className="value border-right">{holder(detail.createTime)}</td>
        </tr>
        <tr>
          <td className="label border-left">分区字段（日期）</td>
          <td className="value">
            {holder(detail.modelPartition?.datePartitionColumn?.columnName)}
          </td>
          <td className="label">分区字段（时间）</td>
          <td className="value border-right">
            {holder(detail.modelPartition?.timePartitionColumn?.columnName)}
          </td>
        </tr>
        <tr>
          <td className="label border-left border-bottom">备注</td>
          <td className="value border-bottom border-right" colSpan={3}>
            {holder(detail.remark)}
          </td>
        </tr>
      </tbody>
    </table>
  );
};

export default HTable;
