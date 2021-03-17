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
            history.push("/data-source");
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
