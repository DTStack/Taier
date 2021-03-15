/*
 * @Author: 云乐
 * @Date: 2021-03-12 11:50:04
 * @LastEditTime: 2021-03-15 19:52:38
 * @LastEditors: 云乐
 * @Description: 产品授权
 */
import React, { useEffect, useState } from "react";
import { Select, Checkbox, Row, Col, message } from "antd";
import { API } from "@/services";

const { Option } = Select;

export default function ProduceAuth() {
  const [sqlType, setSqlType] = useState({
    dataType: "",
    haveVersion: true,
    imgUrl: "",
    typeId: null,
  });
  const [produceList, setProduceList] = useState([]);

  const [version, setVersion] = useState([]); //版本选择
  const [checkdList, setCheckdList] = useState([]); //产品选择
  const [defaultSelect, setDefaultSelect] = useState(""); //添加默认选择版本号

  //根据数据源类型获取版本列表
  const queryDsVersionByType = async () => {
    let type = JSON.parse(sessionStorage.getItem("sqlType")).dataType || "";
    try {
      let { data, success } = await API.queryDsVersionByType({
        dataType: type,
      });

      data = [
        {
          dataType: "type1",
          dataVersion: "1.x",
          sorted: 0,
        },
        {
          dataType: "type1",
          dataVersion: "2.x",
          sorted: 0,
        },
      ];

      if (success) {
        setVersion(data || []);

        if (data.length > 0) {
          setDefaultSelect(data[0].dataVersion);
          getauthProductList(type, data[0].dataVersion);
        } else {
          getauthProductList(type, "");
        }
      }
    } catch (error) {}
  };

  //获取产品授权列表
  const getauthProductList = async (type?: string, ver?: string) => {
    try {
      let { data } = await API.queryProductList({
        dataType: type || sqlType.dataType,
        dataVersion: ver || version,
      });

      data.push(
        {
          productCode: "time",
          productName: "实时开发",
        },
        {
          productCode: "console",
          productName: "控制台开发",
        },
        {
          productCode: "dataqua",
          productName: "数据质量",
        }
      );

      if (data.length > 0) {
        data.forEach((item) => {
          item.label = item.productName;
          item.value = item.productCode;
        });
        setProduceList(data);
      }
    } catch (error) {
      message.error("请求产品列表失败");
      setProduceList([
        {
          productCode: "time",
          productName: "实时开发",
        },
        {
          productCode: "console",
          productName: "控制台开发",
        },
        {
          productCode: "dataqua",
          productName: "数据质量",
        },
      ]);
    }
  };

  useEffect(() => {
    setSqlType(JSON.parse(sessionStorage.getItem("sqlType")) || sqlType);

    queryDsVersionByType();
  }, []);

  //存储数据库版本
  const onSelected = (value) => {
    sessionStorage.setItem("version", value);
    
    setDefaultSelect(value)
    getauthProductList(sqlType.dataType,value)//更新产品列表
  };

  //获取产品授权的列表
  const oncheck = (prolist) => {
    setCheckdList(prolist);
    sessionStorage.setItem("checkdList", prolist);
  };

  return (
    <div className="produce-auth">
      <div className="text-show">
        <p>
          将 <span style={{ color: "#3F87FF" }}>{sqlType.dataType || ""}</span>
          授权哪些产品使用：
        </p>
        {console.log(defaultSelect)}
        {console.log(version.length > 0)}

        {version.length > 0 && (
          <div className="version-sel">
            <span className="version">版本：</span>
            <Select
              showSearch
              style={{ width: "70%" }}
              placeholder="请选择对应版本"
              optionFilterProp="children"
              onChange={onSelected}
              value={defaultSelect}
            >
              {version.map((item) => {
                return (
                  <Option value={item.dataVersion}>{item.dataVersion}</Option>
                );
              })}
            </Select>
          </div>
        )}

        <div style={{ display: "flex" }}>
          <span className="version">授权产品：</span>
          <Checkbox.Group
            onChange={oncheck}
            value={checkdList}
            style={{ flex: 1 }}
          >
            <Row>
              {produceList.length > 0 &&
                produceList.map((item) => (
                  <Col span={8}>
                    <Checkbox value={item.productCode}>
                      {item.productName}
                    </Checkbox>
                  </Col>
                ))}
            </Row>
          </Checkbox.Group>
        </div>
      </div>
    </div>
  );
}
