/*
 * @Author: 云乐
 * @Date: 2021-03-11 10:14:44
 * @LastEditTime: 2021-03-16 12:34:48
 * @LastEditors: 云乐
 * @Description: 和数栈搜索保持一致
 * placeholder, width, onSearch
 */

import React, { useState } from "react";
import { Input, Icon } from "antd";

export default function SearchInput({
  placeholder = "请求输入搜索内容",
  width = 200,
  onSearch,
}) {
  const [value, setValue] = useState("");

  return (
    <Input
      allowClear
      value={value}
      onChange={(e) => {
        setValue(e.target.value);
        if(!e.target.value){
          onSearch(e.target.value);
        }
      }}
      placeholder={placeholder}
      suffix={
        <Icon
          type="search"
          onClick={() => {
            onSearch(value);
          }}
          style={{ cursor: "pointer" }}
        />
      }
      style={{ width: width }}
    />
  );
}
