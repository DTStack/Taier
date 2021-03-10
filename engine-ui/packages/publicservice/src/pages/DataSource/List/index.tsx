/*
 * @Author: 云乐
 * @Date: 2021-03-10 14:32:56
 * @LastEditTime: 2021-03-10 17:08:40
 * @LastEditors: 云乐
 * @Description: 数据源列表展示
 */

import React, { useEffect, useState } from "react";
import Search from "./components/Search";
import { Table,Modal,message } from "antd";
import { columns } from "./constants";
import "./style.less"
import { API } from '@/services';
const { confirm } = Modal;

function index() {
  const [dataSources, setdataSources] = useState([]);

  //获取表格数据
  const requestTableData = async (para?: any) => {
    try {
      let { data, success } = await API.dataSourcepage({
        "asc": true,
        "current": 0,
        "dsType": "Mysql",
        "field": "",
        "isMeta": 0,
        "productCode": "",
        "search": "",
        "size": 0
      });
      if (success) {
        data.contentList.forEach((item, index) => {
          item.index = index + 1;
        });
        setdataSources(data.contentList);
        // settotal(data.total);
      }
    } catch (error) {}
  };
  
  useEffect(() => {
    requestTableData(); //获取数据源列表
  }, []);

  //编辑
  const toEdit = (record, event) => {
    event.stopPropagation();
    // confirm({
    //   title: `是否删除?`,
    //   icon: <CloseCircleOutlined />,
    //   content: `删除后将不可恢复`,
    //   okText: "确定",
    //   cancelText: "取消",
    //   okType: "danger",
    //   onOk: async () => {
    //     let { success } = await API.entityDimDel({
    //       id: record.infoId,
    //     });
    //     if (success) {
    //       message.success("成功删除维度");
    //     } else {
    //       message.error("删除失败,该维度已被指标引用");
    //     }
    //     props.refresh(); //更新表格
    //   },
    // });
  };
  //删除
  const toDelete = (record, event) => {
    event.stopPropagation();
    confirm({
      title: `是否删除?`,
      content: `删除后将不可恢复`,
      okText: "确定",
      cancelText: "取消",
      okType: "danger",
      onOk: async () => {
        let { success } = await API.entityDimDel({
          id: record.infoId,
        });
        if (success) {
          message.success("成功删除维度");
          requestTableData(); //更新表格
        } else {
          message.error("删除失败,该维度已被指标引用");
        }
      },
    });
  };
  //授权
  const toAuth = (record, event) => {
    console.log("toAuth: ", toAuth);
  };

  return (
    <div className="source">
      <Search></Search>
      <Table
        rowKey={(record) => record.index}
        columns={columns({
          toEdit: toEdit,
          toAuth: toAuth,
          toDelete: toDelete,
          fixed: "right",
        })}
        dataSource={dataSources}
        pagination={false}
        style={{ width: "100%", overflow: "scroll" }}
      />
    </div>
  );
}

export default index;
