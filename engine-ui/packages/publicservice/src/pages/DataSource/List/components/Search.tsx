import React, { useEffect, useState } from "react";
import { API } from "@/services";
import { Form, Input, Checkbox, Button, Select } from "antd";
import { useHistory } from "react-router";
const { Option } = Select;

function Search({ form }) {
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
        console.log("productList: ", data);
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

  // 类型改变
  const handleTypeChange = (currency) => {
    console.log("currency: ", currency);
  };

  return (
    <div>
      <Form layout="inline" onSubmit={handleSubmit}>
        <Form.Item>
          {getFieldDecorator("search", {
            initialValue: "",
            rules: [{ required: true, message: "Please input your username!" }],
          })(<Input />)}
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
              onChange={handleTypeChange}
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
              onChange={handleTypeChange}
              allowClear
              showSearch
              optionFilterProp="children"
            >
              {productList.length > 0 &&
                productList.map((item) => {
                  return (
                    <Option value={item.productCode}>{item.productCode}</Option>
                  );
                })}
            </Select>
          )}
        </Form.Item>
        <Form.Item>
          {getFieldDecorator("isMeta", {
            valuePropName: "checked",
          })(<Checkbox>显示默认数据库</Checkbox>)}
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
export default Form.create()(Search);
