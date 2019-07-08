import { Transfer } from 'antd';
import React, { Component } from 'react';

class BatchSelect extends Component {
  state = {
      targetKeys: [],
      tabData: []
  }

  isOtherSourceFlag = false;

  componentDidMount () {
      const { sourceMap, tabData, sourceKey } = this.props;
      let targetData; // 目标数据
      if (sourceKey) {
          targetData = sourceMap.type.extTable[sourceKey] || [];
          this.isOtherSourceFlag = true;
      } else {
          targetData = sourceMap.type.table;
          this.isOtherSourceFlag = false;
      }
      this.getData(tabData, targetData);
  }

  getData (newData = [], table = []) {
      const mockData = [];
      const targetData = [];
      newData.map(item => {
          mockData.push({
              key: item,
              item
          });
          table.indexOf(item) > -1 && targetData.push(item);
      });
      this.setState({
          tabData: mockData,
          targetKeys: targetData
      });
  }

  handleChange = (targetKeys) => {
      const { handleSelectFinish, sourceMap, sourceKey } = this.props;
      this.setState({
          targetKeys
      }, () => {
          // 通过一个标志位flag来判断处理是否来自于增加数据源的extTable数据
          handleSelectFinish(this.state.targetKeys, sourceMap.type.type, sourceKey || '');
      });
  }

  filterOption = (inputValue, option) => {
      return option.key.indexOf(inputValue) > -1;
  }

  render () {
      return (
          <Transfer className="form-item-follow-text"
              listStyle={{ width: '309px', height: '235PX' }}
              dataSource={ this.state.tabData }
              showSearch
              filterOption={ this.filterOption }
              targetKeys={this.state.targetKeys}
              onChange={this.handleChange}
              render={ele => ele.item}
              titles={ ['张表', '张表'] }
          />
      );
  }
}

export default BatchSelect;
