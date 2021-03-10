/*
 * @Author: 云乐
 * @Date: 2021-03-10 15:07:33
 * @LastEditTime: 2021-03-10 16:54:23
 * @LastEditors: 云乐
 * @Description: 数据源列表--列
 */
import React from "react";
import { Divider } from "antd";

const columns = (props: any) => {
  const { toEdit, toAuth, toDelete, fixed } = props;
  return [
    {
      title: "数据源名称",
      // dataIndex: "dsName",
      key: "dsName",
      // width: 80,
      // ellipsis: true,
      render: (text, record) =>
        //	是否有meta标志 0-否 1-是
        record.isMeta === 0 ? (
          <span>{record.dsName}</span>
        ) : (
          <span>{record.dsName} meta</span>
        )
    },
    {
      title: "类型",
      dataIndex: "dsTypeName",
      key: "dsTypeName",
      ellipsis: true,
      // width: 120,
    },
    {
      title: "描述",
      dataIndex: "dsDesc",
      key: "dsDesc",
      ellipsis: true,
      // width: 160,
    },
    {
      title: "连接信息",
      dataIndex: "linkInfo",
      key: "linkInfo",
      ellipsis: true,
      // width: 200,
    },
    {
      title: "连接状态",
      dataIndex: "status",
      key: "status",
      ellipsis: true,
      // width: 200,
    },
    {
      title: "最近修改时间",
      dataIndex: "updateAt",
      key: "updateAt",
      ellipsis: true,
      // width: 120,
    },
    {
      title: "操作",
      key: "action",
      fixed: fixed,
      width: 160,
      render: (_, record) => {
        return (
          <>
            <span
              className="data-view"
              onClick={(event) => toEdit(record, event)}
            >
              编辑
            </span>
            <Divider type="vertical" />
            <span
              className="data-view"
              onClick={(event) => toAuth(record, event)}
            >
              授权
            </span>
            <Divider type="vertical" />
            <span
              className="data-view"
              onClick={(event) => toDelete(record, event)}
            >
              删除
            </span>
          </>
        );
      },
    },
  ];
};

export { columns };
