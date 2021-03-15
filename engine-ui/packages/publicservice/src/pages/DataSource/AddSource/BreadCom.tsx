/*
 * @Author: 云乐
 * @Date: 2021-03-12 10:54:50
 * @LastEditTime: 2021-03-12 10:54:50
 * @LastEditors: 云乐
 * @Description: 面包屑
 */

import React from "react";
import { Breadcrumb } from "antd";
import { useHistory } from "react-router";

export default function BreadCom() {
  const history = new useHistory();

  return (
    <div>
      <Breadcrumb>
        <Breadcrumb.Item
          onClick={() => {
            history.push("data-source");
          }}
        >
          <a>数据源中心</a>
        </Breadcrumb.Item>
        <Breadcrumb.Item>
          <a>新增数据源</a>
        </Breadcrumb.Item>
      </Breadcrumb>
    </div>
  );
}
