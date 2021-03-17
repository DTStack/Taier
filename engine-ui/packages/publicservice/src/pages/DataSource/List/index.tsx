import React, { useEffect, useState } from "react";
import Search from "./components/Search";
import { Table, message, Modal, notification, Pagination } from "antd";
import { columns } from "./constants";
import { API } from "@/services";
import AuthSelect from "./components/AuthSelect";
import { remove } from "../utils/handelSession";
import { IPagination, IOther, IRecord } from "./type";
import "./style.scss";

function index() {
  const [dataSources, setDataSources] = useState([]);
  const [params, setParams] = useState<IPagination>({
    current: 1, //当前页码
    size: 20, //分页个数
  });
  const [other, setOther] = useState<IOther>({
    search: "",
    dataType: null,
    appType: null,
    isMeta: 0,
    status: null,
  });
  const [total, setTotal] = useState<number>(null);

  const [visible, setVisible] = useState<boolean>(false);
  const [record, setRecord] = useState<IRecord>({
    dataInfoId: null,
    isAuth: null,
  });
  const [checkedValues, setcheckedValues] = useState<number[]>([]);

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
    } catch (error) {
      notification.error({
        message: "错误！",
        description: "获取数据源分类类目列表失败",
      });
    }
  };

  useEffect(() => {
    requestTableData(); //获取数据源列表

    //清除存储数据
    remove();
  }, []);

  //编辑
  const toEdit = (record, event) => {
    if (record.isMeta === 1) {
      message.info("带meta标识的数据源不能编辑、删除");
    } else {
      alert("进去编辑页面");
    }
  };

  //删除
  const toDelete = async (record) => {
    if (record.isAuth === 1 || record.isMeta === 1) {
      message.info("数据源已授权给产品，不可删除");
    } else {
      let { success, message: msg } = await API.dataSourceDelete({
        dataInfoId: record.dataInfoId,
      });

      if (success) {
        message.success("删除成功");
        requestTableData(); //更新表格
      } else {
        notification.error({
          message: "错误！",
          description: `${msg}`,
        });
      }
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

  //连接状态筛选
  const handleTableChange = (pagination, filters, sorter) => {
    let data = { ...other, status: filters.status };
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
    try {
      let { success } = await API.dataSoProAuth({
        dataInfoId: record.dataInfoId,
        isAuth: record.isAuth,
        appTypes: checkedValues,
      });
      if (success) {
        message.success("产品授权成功");
        requestTableData(); //更新表格
      }
    } catch (error) {
      notification.error({
        message: "错误！",
        description: "产品授权失败",
      });
    }

    setVisible(false);
  };

  const showTotal = () => {
    return (
      <span>
        共 <i style={{ color: "#3F87FF" }}>{total}</i> 条数据，每页显示
        {params.size}条
      </span>
    );
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
              filters: [
                { text: "正常", value: 1 },
                { text: "连接失败", value: 0 },
              ],
            })}
            dataSource={dataSources}
            pagination={false}
            scroll={{ x: "100%" }}
            onChange={handleTableChange}
          />
        </div>
        <div className="page-com">
          <Pagination
            size="small"
            total={total}
            showTotal={showTotal}
            onChange={(page) => onChangePage(page)}
            defaultPageSize={params.size}
            current={params.current}
          />
        </div>
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
          <AuthSelect record={record} oncheck={oncheck}></AuthSelect>
        </Modal>
      )}
    </div>
  );
}

export default index;
