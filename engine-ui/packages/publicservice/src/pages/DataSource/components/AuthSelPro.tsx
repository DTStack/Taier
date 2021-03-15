/*
 * @Author: 云乐
 * @Date: 2021-03-11 17:43:45
 * @LastEditTime: 2021-03-15 19:19:06
 * @LastEditors: 云乐
 * @Description: 选择授权页面
 */
import React from "react";
import { Checkbox, Row, Col } from "antd";

interface IProps {
  oncheck: any;
  produceList: string[];
  checkedValues: string[];
}
export default function AuthSelPro(props: IProps) {
  let { oncheck, produceList, checkedValues } = props;

  const onChange = (checkedValues) => {
    oncheck(checkedValues); //更新父组建列表
  };

  return (
    <Checkbox.Group
      onChange={onChange}
      value={checkedValues}
      style={{ flex: 1 }}
    >
      <Row>
        {produceList.length > 0 &&
          produceList.map((item) => (
            <Col span={8}>
              <Checkbox value={item.productCode} checked={true}>
                {item.productName}
              </Checkbox>
            </Col>
          ))}
      </Row>
    </Checkbox.Group>
  );
}
