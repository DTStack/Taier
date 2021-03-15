/*
 * @Author: 云乐
 * @Date: 2021-03-10 15:07:33
 * @LastEditTime: 2021-03-15 17:32:18
 * @LastEditors: 云乐
 * @Description: 数据源列表--列
 */
import React from "react";
import { Divider, Popconfirm, Icon, Tag,Badge } from "antd";
import "./style.scss";

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
        record.isMeta !== 0 ? (
          <span>{record.dataName}</span>
        ) : (
          <div>
            <span style={{marginRight:4}}>{record.dataName}</span>
            <Tag style={{borderColor:"#3F87FF",color:"#3F87FF"}}>meta</Tag>
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
      width: 200,
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
      key: "linkJson",
      ellipsis: true,
      width: 200,
    },
    {
      title: "连接状态",
      dataIndex: "status",
      ellipsis: true,
      width: 200,
      render: (text, record) =>
        text === 0 ? <span><Badge status="error"/>连接失败</span> : <span><Badge status="success" />正常</span>,
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
              className={record.isMeta === 0 ? "data-view" : ""}
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
            <span className={record.isAuth === 0 ? "data-view" : ""}>
              <Popconfirm
                title="是否删除此条记录？"
                icon={
                  <Icon type="question-circle-o" style={{ color: "red" }} />
                }
                onConfirm={() => toDelete(record, event)}
                okText="删除"
                cancelText="取消"
              >
                <a href="#">删除</a>
              </Popconfirm>
            </span>
          </>
        );
      },
    },
  ];
};

export { columns };
