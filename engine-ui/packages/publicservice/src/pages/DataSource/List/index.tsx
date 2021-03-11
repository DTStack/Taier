/*
 * @Author: 云乐
 * @Date: 2021-03-10 14:32:56
 * @LastEditTime: 2021-03-11 16:40:32
 * @LastEditors: 云乐
 * @Description: 数据源列表展示
 */

import React, { useEffect, useState } from "react";
import Search from "./components/Search";
import { Table, message } from "antd";
import { columns } from "./constants";
import PaginationCom from "components/PaginationCom";
import "./style.less";
import { API } from "@/services";

function index() {
  const [dataSources, setdataSources] = useState([]);
  const [params, setparams] = useState({
    current: 1, //当前页码
    size: 20, //分页个数
  });
  const [other, setother] = useState({
    search: "",
    dataType: "",
    productCode: "",
    isMeta: 0,
  });

  const [total, settotal] = useState(null);

  //获取表格数据
  const requestTableData = async (query?: any) => {
    try {
      let { data, success } = await API.dataSourcepage({
        ...params,
        ...other,
        ...query,
      });
      if (success) {
        data.contentList.forEach((item, index) => {
          item.index = index + 1;
        });

        setparams({
          current: data.current, //当前页码
          size: data.size, //分页个数
        });

        settotal(data.total); //总页数
        setdataSources(data.contentList);
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
  const toDelete = async (record, event) => {
    event.stopPropagation();
    let { success } = await API.dataSourceDelete({
      id: record.dataInfoId,
    });
    if (success) {
      requestTableData(); //更新表格
    } else {
      message.error("删除失败");
    }
  };

  //授权
  const toAuth = (record, event) => {
    console.log("toAuth: ", toAuth);
  };

  const onChangePage = (page, pageSize) => {
    let data = { ...params, ...{ current: page } };
    setparams(data);
    requestTableData(data);
  };

  const onSearch = (value) => {
    let data = { ...other, ...value };
    setother(data);
    requestTableData(data);
  };

  return (
    <div className="source">
      <Search onSearch={onSearch}></Search>

      <div className="bottom">
        <div className="conent-table">
          <Table
            rowKey={(record) => record.index}
            columns={columns({
              toEdit: toEdit,
              toAuth: toAuth,
              toDelete: toDelete,
              fixed: "right",
              fixleft: "left",
            })}
            dataSource={dataSources}
            pagination={false}
            style={{ width: "100%", overflow: "scroll" }}
          />
        </div>
        <PaginationCom
          onChangePage={onChangePage}
          params={params}
          total={total}
        />
      </div>
    </div>
  );
}

export default index;
