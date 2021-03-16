/*
 * @Author: 云乐
 * @Date: 2021-03-11 10:40:17
 * @LastEditTime: 2021-03-15 17:24:54
 * @LastEditors: 云乐
 * @Description: 统一数栈分页组件
 * 参数说明：
 * onChangePagin：change方法
 * total：总页数
 * params：{
 *  size:每页展示的总条数,
 *  current:当前页数
 * }
 */

import React from "react";
import { Pagination } from "antd";
import "./style.scss";

interface IProps {
  onChangePage(page: number): void;
  total: number;
  params: {
    size: number;
    current: number;
  };
}

export default function PaginationCom(props: IProps) {
  let { params, total, onChangePage } = props;
  let { size, current } = params;

  const showTotal = () => {
    return (
      <span>
        共 <i style={{ color: "#3F87FF" }}>{total}</i> 条数据，每页显示{size}条
      </span>
    );
  };

  return (
    <div className="page-com">
      <Pagination
        size="small"
        total={total}
        showTotal={showTotal}
        onChange={(page) => onChangePage(page)}
        defaultPageSize={size}
        current={current}
      />
    </div>
  );
}
