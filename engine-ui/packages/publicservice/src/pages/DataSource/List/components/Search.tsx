import React, { useEffect, useState } from "react";
import { API } from "@/services";
import { Form, Checkbox, Button, Select } from "antd";
import { useHistory } from "react-router";
import SearchInput from "components/SearchInput";
import { FormComponentProps } from "antd/es/form";
import "../style.less";

interface IProps extends FormComponentProps {
  onSearch: any; //父组件传递过来的值
}

const { Option } = Select;

function Search(props) {
  const { form, onSearch } = props;
  const history = useHistory();

  const { getFieldDecorator, validateFields } = form;
  const [typeList, settypeList] = useState([]);
  const [productList, setproductList] = useState([]);

  const getTypeList = async () => {
    try {
      let { data, success } = await API.typeList();
      if (success) {
        settypeList(data);
      }
    } catch (error) {}
  };
  const getProductList = async () => {
    try {
      let { data, success } = await API.productList();
      if (success) {
        setproductList(data);
      }
    } catch (error) {}
  };

  useEffect(() => {
    getTypeList();
    getProductList();
  }, []);

  // 提交
  const handleSubmit = (e) => {
    e.preventDefault();
    validateFields((err, values) => {
      if (!err) {
        console.log(values);
      }
    });
  };

  // 新增数据源
  const addList = () => {
    history.push("/edit-source");
  };

  return (
    <div className="top-search">
      <Form layout="inline" onSubmit={handleSubmit}>
        <Form.Item>
          {getFieldDecorator("search", {
            initialValue: "",
            rules: [{ required: true, message: "Please input your username!" }],
          })(
            <SearchInput
              placeholder="数据源名称/描述"
              onSearch={(value) => onSearch({search:value})}
              width={208}
            ></SearchInput>
          )}
        </Form.Item>
        <Form.Item label="类型">
          {getFieldDecorator("dsType", {
            initialValue: "",
            rules: [{ message: "Please input your type!" }],
          })(
            <Select
              placeholder="请选择类型"
              allowClear
              showSearch
              optionFilterProp="children"
              style={{ width: 140 }}
              onChange={(value) => onSearch({dataType:value})}
            >
              {typeList.length > 0 &&
                typeList.map((item) => {
                  return <Option value={item.dsType}>{item.dsType}</Option>;
                })}
            </Select>
          )}
        </Form.Item>
        <Form.Item label="授权产品">
          {getFieldDecorator("productCode", {
            initialValue: "",
            rules: [{ message: "Please input your produce!" }],
          })(
            <Select
              style={{ width: 140 }}
              onChange={(value) => onSearch({productCode:value})}
              allowClear
              showSearch
              optionFilterProp="children"
            >
              {productList.length > 0 &&
                productList.map((item) => {
                  return (
                    <Option value={item.productCode} key={item.productCode}>{item.productName}</Option>
                  );
                })}
            </Select>
          )}
        </Form.Item>
        <Form.Item>
          {getFieldDecorator("isMeta", {
            valuePropName: "checked",
          })(<Checkbox onChange={(e)=>onSearch({"isMeta":e.target.checked?1:0})}>显示默认数据库</Checkbox>)}
        </Form.Item>

        {/* <Form.Item>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Form.Item> */}

        <Button type="primary" onClick={addList}>
          新增数据源
        </Button>
      </Form>
    </div>
  );
}
export default Form.create<IProps>({})(Search);
