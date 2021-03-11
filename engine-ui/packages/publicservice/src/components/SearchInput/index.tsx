/*
 * @Author: 云乐
 * @Date: 2021-03-11 10:14:44
 * @LastEditTime: 2021-03-11 10:32:23
 * @LastEditors: 云乐
 * @Description: 和数栈搜索保持一致
 * placeholder, width, onSearch
 */

import React, { useState, useEffect } from "react";
import { Input, Icon } from "antd";

export default function index({ placeholder, width, onSearch }) {
  const [value, setValue] = useState("");
  
  useEffect(() => {
    if (!value) {
      onSearch(value);
    }
  }, [value]);

  return (
    <Input
      allowClear
      value={value}
      onChange={(e) => {
        setValue(e.target.value);
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
