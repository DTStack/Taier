/*
 * @Author: 云乐
 * @Date: 2021-03-10 14:32:56
 * @LastEditTime: 2021-03-12 10:16:54
 * @LastEditors: 云乐
 * @Description: 数据源列表展示
 */

import React, { useEffect, useState } from "react";
import Search from "./components/Search";
import { Table, message, Modal } from "antd";
import { columns } from "./constants";
import PaginationCom from "@/components/PaginationCom/PaginationCom";
import "./style.less";
import { API } from "@/services";
import AuthSel from "./components/AuthSel";

function index() {
  const [dataSources, setDataSources] = useState([]);
  const [params, setParams] = useState({
    current: 1, //当前页码
    size: 20, //分页个数
  });
  const [other, setOther] = useState({
    search: "",
    dataType: "",
    productCode: "",
    isMeta: 0,
  });
  const [total, setTotal] = useState(null);

  const [visible, setVisible] = useState(false);
  const [record, setRecord] = useState({
    dataInfoId: null,
    isAuth: null,
  });
  const [checkedValues, setcheckedValues] = useState([]);

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

        setParams({
          current: data.current, //当前页码
          size: data.size, //分页个数
        });

        setTotal(data.total); //总页数
        setDataSources(data.contentList);
      }
    } catch (error) {}
  };

  useEffect(() => {
    requestTableData(); //获取数据源列表
  }, []);

  //编辑
  const toEdit = (record, event) => {
    event.stopPropagation();
  };

  //删除
  const toDelete = async (record, event) => {
    event.stopPropagation();
    let { success } = await API.dataSourceDelete({
      dataInfoId: record.dataInfoId,
    });

    if (success) {
      message.success("删除成功");
      requestTableData(); //更新表格
    } else {
      message.error("删除失败");
    }
  };

  //分页事件
  const onChangePage = (page) => {
    let data = { ...params, ...{ current: page } };
    setParams(data);
    requestTableData(data);
  };

  //搜索事件
  const onSearch = (value) => {
    let data = { ...other, ...value };
    setOther(data);
    requestTableData(data);
  };

  //点击授权按钮
  const toAuth = (record) => {
    setRecord(record);
    setVisible(true);
  };
  //获取产品授权的列表
  const oncheck = (prolist) => {
    setcheckedValues(prolist);
  };

  //产品授权隐藏
  const handleAutoProduc = async () => {
    console.log("record: ", record);
    try {
      let { data, message: msg } = await API.dataSoProAuth({
        dataInfoId: record.dataInfoId,
        isAuth: record.isAuth,
        productCode: checkedValues,
      });

      console.log("msg: ", msg);
      console.log("data: ", data);

      requestTableData(); //更新表格
    } catch (error) {}
    setVisible(false);
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
              left: "left",
              right: "right",
            })}
            dataSource={dataSources}
            pagination={false}
            scroll={{ x: "100%" }}
          />
        </div>
        <PaginationCom
          onChangePage={onChangePage}
          params={params}
          total={total}
        />
      </div>

      {visible && (
        <Modal
          title="授权"
          visible={visible}
          onOk={handleAutoProduc}
          onCancel={() => {
            setVisible(false);
          }}
        >
          <AuthSel record={record} oncheck={oncheck}></AuthSel>
        </Modal>
      )}
    </div>
  );
}

export default index;
