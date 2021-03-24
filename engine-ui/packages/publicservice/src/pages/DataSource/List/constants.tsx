import React from "react";
import { Divider, Popconfirm, Icon, Tag, Badge, notification } from "antd";
import "./style.scss";
import { ExtTableCell } from "./extTableCell";

const columns = (props: any) => {
  const { toEdit, toAuth, toDelete, left, right, filters } = props;

  return [
    {
      title: "数据源名称",
      key: "dsName",
      fixed: left,
      width: 200,
      render: (text, record) =>
        //	是否有meta标志 0-否 1-是
        record.isMeta === 0 ? (
          <span>{record.dataName}</span>
        ) : (
          <div>
            <span style={{ marginRight: 4 }}>{record.dataName}</span>
            <Tag style={{ borderColor: "#3F87FF", color: "#3F87FF" }}>meta</Tag>
          </div>
        ),
    },
    {
      title: "类型",
      dataIndex: "dataTypeName",
      key: "dataTypeName",
      ellipsis: true,
      width: 120,
    },
    {
      title: "授权产品",
      dataIndex: "productNames",
      key: "productNames",
      ellipsis: true,
      width: 220,
    },
    {
      title: "描述",
      dataIndex: "dataDesc",
      key: "dataDesc",
      ellipsis: true,
      width: 160,
    },
    {
      title: "连接信息",
      dataIndex: "linkJson",
      ellipsis: true,
      width: 200,
      render: (empty: any, record: any) => {
        return <ExtTableCell sourceData={record} />;
      },
    },
    {
      title: "连接状态",
      dataIndex: "status",
      ellipsis: true,
      width: 200,
      render: (text, record) =>
        text === 0 ? (
          <span>
            <Badge status="error" />
            连接失败
          </span>
        ) : (
          <span>
            <Badge status="success" />
            正常
          </span>
        ),
      filters: filters,
    },
    {
      title: "最近修改时间",
      dataIndex: "updateAt",
      key: "updateAt",
      ellipsis: true,
      width: 120,
    },
    {
      title: "操作",
      key: "action",
      fixed: right,
      width: 160,
      render: (_, record) => {
        return (
          <>
            <span
              className={record.isMeta === 0 ? "data-view" : "gray"}
              onClick={(event) => toEdit(record, event)}
            >
              <a>编辑</a>
            </span>
            <Divider type="vertical" />
            <span
              className="data-view"
              onClick={(event) => toAuth(record, event)}
            >
              <a>授权</a>
            </span>
            <Divider type="vertical" />

            {/* 是否授权，0为未授权，1为已授权 */}
            {!record.isAuth && record.isMeta !== 1 ? (
              <span className="data-view">
                <Popconfirm
                  title="是否删除此条记录？"
                  icon={
                    <Icon type="question-circle-o" style={{ color: "red" }} />
                  }
                  onConfirm={() => toDelete(record)}
                  okText="删除"
                  cancelText="取消"
                >
                  <a>删除</a>
                </Popconfirm>
              </span>
            ) : (
              <span
                className="gray"
                onClick={() => {
                  notification.error({
                    message: "错误！",
                    description: "数据源已授权给产品，不可删除",
                  });
                }}
              >
                <a>删除</a>
              </span>
            )}
          </>
        );
      },
    },
  ];
};

export { columns };
