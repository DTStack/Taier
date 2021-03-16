import React, { useEffect, useState } from "react";
import { API } from "@/services";
import {
  Form,
  Checkbox,
  Button,
  Select,
  message,
  Tooltip,
  Icon,
  notification,
} from "antd";
import { useHistory } from "react-router";
import SearchInput from "@/components/SearchInput";
import { FormComponentProps } from "antd/es/form";
import "../style.scss";

interface IProps extends FormComponentProps {
  onSearch(value: string): void; //父组件传递过来的值
}

const { Option } = Select;

function Search(props) {
  const { onSearch } = props;
  const history = useHistory();

  const [typeList, setTypeList] = useState([]);
  const [productList, setProductList] = useState([]);

  const getTypeList = async () => {
    try {
      let { data, success } = await API.typeList();
      data.unshift({
        dataType: "全部",
      });
      if (success) {
        setTypeList(data);
      }
    } catch (error) {
      notification["error"]({
        message: "错误！",
        description: "获取类型下拉框失败",
      });
    }
  };

  const getProductList = async () => {
    try {
      let { data, success } = await API.productList();
      if (success) {
        data.unshift({
          appName: "全部",
          appType: "all",
        });
        setProductList(data);
      }
    } catch (error) {
      notification["error"]({
        message: "错误！",
        description: "获取授权产品下拉框失败",
      });
    }
  };

  useEffect(() => {
    getTypeList();
    getProductList();
  }, []);

  // 新增数据源
  const addList = () => {
    history.push("/add-source");
  };

  //类型多选方法
  const onMultType = (value) => {
    if (value.length > 0 && value.includes("全部")) {
      onSearch({ dataType: null });
    } else {
      onSearch({ dataType: value });
    }
  };
  //类型多选方法
  const onMultAppType = (value) => {
    if (value.length > 0 && value.includes("all")) {
      onSearch({ appType: null });
    } else {
      onSearch({ appType: value });
    }
  };

  return (
    <div className="top-search">
      <Form layout="inline">
        <Form.Item>
          <SearchInput
            placeholder="数据源名称/描述"
            onSearch={(value) => onSearch({ search: value })}
            width={208}
          ></SearchInput>
        </Form.Item>

        <Form.Item label="类型">
          <Select
            mode="multiple"
            placeholder="请选择类型"
            allowClear
            showSearch
            optionFilterProp="children"
            style={{ width: 140 }}
            onChange={(value) => onMultType(value)}
            defaultValue={["全部"]}
          >
            {typeList.length > 0 &&
              typeList.map((item) => {
                return <Option value={item.dataType}>{item.dataType}</Option>;
              })}
          </Select>
        </Form.Item>
        <Form.Item label="授权产品">
          <Select
            mode="multiple"
            style={{ width: 140 }}
            onChange={(value) => onMultAppType(value)}
            allowClear
            showSearch
            optionFilterProp="children"
            defaultValue={["all"]}
          >
            {productList.length > 0 &&
              productList.map((item) => {
                return (
                  <Option value={item.appType} key={item.appType}>
                    {item.appName}
                  </Option>
                );
              })}
          </Select>
        </Form.Item>
        <Form.Item>
          <Checkbox
            onChange={(e) =>
              onSearch({ isMeta: e.target.checked ? 1 : 0, current: 1 })
            }
          >
            显示默认数据库
            <Tooltip title="各模块在创建项目时的默认数据源">
              <Icon style={{ marginLeft: 8 }} type="question-circle-o" />
            </Tooltip>
          </Checkbox>
        </Form.Item>

        <Button type="primary" onClick={addList} style={{ float: "right" }}>
          新增数据源
        </Button>
      </Form>
    </div>
  );
}
export default Form.create<IProps>({})(Search);
