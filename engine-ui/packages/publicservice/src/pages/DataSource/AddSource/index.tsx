/*
 * @Author: 云乐
 * @Date: 2021-03-10 16:19:35
 * @LastEditTime: 2021-03-12 10:43:06
 * @LastEditors: 云乐
 * @Description: 新增数据源
 */
import React from "react";
import { Breadcrumb } from "antd";
import { useHistory } from "react-router";
import "./style.less";

export default function index() {
  const history = new useHistory();

  return (
    <div className="source">
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
      <div className="content">内容</div>
    </div>
  );
}
