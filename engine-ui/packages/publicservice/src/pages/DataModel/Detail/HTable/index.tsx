import React from 'react';
import './style';

const HTable = () => {
  return (
    <table className="h-table">
      <tr>
        <td className="label">模型名称</td>
        <td className="value">学生成绩表</td>
        <td className="label">数据源</td>
        <td className="value">test-1(MySql)</td>
      </tr>
      <tr>
        <td className="label">创建人</td>
        <td className="value">小李</td>
        <td className="label">创建时间</td>
        <td className="value">2020-12-22 17:17:17</td>
      </tr>
      <tr>
        <td className="label">分区字段（日期）</td>
        <td className="value">小李</td>
        <td className="label">分区字段（时间）</td>
        <td className="value">2020-12-22 17:17:17</td>
      </tr>
      <tr>
        <td className="label">备注</td>
        <td className="value" colSpan={3}>2020级学生成绩信息</td>
      </tr>
    </table>
  )
}

export default HTable;
