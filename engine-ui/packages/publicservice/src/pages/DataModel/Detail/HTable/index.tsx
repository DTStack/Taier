import React from 'react';
import './style';
import { IModelDetail } from '../../types';

interface IPropsHTable {
  detail: Partial<IModelDetail>;
}

// TODO:部分字段缺失，与后端沟通
const HTable = (props: IPropsHTable) => {
  const { detail } = props;
  return (
    <table className="h-table">
      <tr>
        <td className="label">模型名称</td>
        <td className="value">{detail.modelName}</td>
        <td className="label">数据源</td>
        <td className="value">{detail.dsTypeName}</td>
      </tr>
      <tr>
        <td className="label">创建人</td>
        <td className="value">{detail.creator}</td>
        <td className="label">创建时间</td>
        <td className="value">--</td>
      </tr>
      <tr>
        <td className="label">分区字段（日期）</td>
        <td className="value">--</td>
        <td className="label">分区字段（时间）</td>
        <td className="value">--</td>
      </tr>
      <tr>
        <td className="label">备注</td>
        <td className="value" colSpan={3}>
          {detail.remark}
        </td>
      </tr>
    </table>
  );
};

export default HTable;
