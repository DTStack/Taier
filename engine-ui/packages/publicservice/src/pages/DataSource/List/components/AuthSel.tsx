/*
 * @Author: 云乐
 * @Date: 2021-03-11 17:43:45
 * @LastEditTime: 2021-03-15 17:53:45
 * @LastEditors: 云乐
 * @Description: 选择授权页面
 */
import React, { useEffect, useState } from "react";
import { API } from "@/services";
import { Checkbox, Row, Col } from "antd";

interface IProps {
  record: any;
  oncheck:any;
}
export default function AuthSel(props: IProps) {
  let { record,oncheck } = props;
  const [authList, setauthList] = useState([]);
  const [checkedlist, setcheckedlist] = useState([]);

  //获取产品授权列表
  const getauthProductList = async () => {
    try {
      let { data } = await API.authProductList({ dataInfoId: record.dataInfoId });
      if (data.length > 0) {
        data.push(
          {
            isAuth: 1,
            productCode: "time",
            productName: "实时开发",
          },
          {
            isAuth: 0,
            productCode: "console",
            productName: "控制台开发",
          },
          {
            isAuth: 1,
            productCode: "dataqua",
            productName: "数据质量",
          }
        );

        data.forEach((item) => {
          item.label = item.productName;
          item.value = item.productCode;
        });

        data.forEach((item) => {
          if (item.isAuth === 1) {
            checkedlist.push(item.productCode);
          }
        });

        setauthList(data);
      }
    } catch (error) {}
  };

  useEffect(() => {
    getauthProductList();
  }, []);

  const onChange = (checkedValues) => {
    oncheck(checkedValues)
    setcheckedlist(checkedValues);
  };

  return (
    <div>
      <p>点击进行授权/取消，若已在产品中应用，不能取消授权</p>
      <br />
      <Checkbox.Group
        style={{ width: "100%" }}
        onChange={onChange}
        value={checkedlist}
      >
        <Row>
          {authList.length>0&&authList.map((item) => (
            <Col span={8}>
              <Checkbox
                value={item.productCode}
                disabled={item.isAuth === 1}
                checked={true}
              >
                {item.productName}
              </Checkbox>
            </Col>
          ))}
        </Row>
      </Checkbox.Group>
    </div>
  );
}
