import React from 'react';

import { Button, Dropdown, Icon, Menu } from 'antd';
import Rule from './rule';
import utils from 'utils';

class RuleList extends React.Component {
    state = {
        ruleList: []
    }
    componentDidMount () {
        this.getData();
    }
    async getData () {
        const { getInitData } = this.props;
        if (!getInitData) { return; }
        let data = await getInitData();
        if (data) {
            this.setState({
                ruleList: data.filter((item) => {
                    return item.isSnapshot == 0
                })
            })
        }
    }
    getType (data) {
        const { level, isCustomizeSql } = data;
        if (isCustomizeSql) {
            return 'sql'
        } else if (level == 1) {
            return 'table';
        } else {
            return 'column'
        }
    }
    handleMenuClick = (e) => {
        const { data } = this.props;
        const key = e.key;
        let newData = {
            id: utils.generateAKey(),
            isNew: true,
            isEdit: true,
            filter: '',
            verifyType: undefined,
            operator: undefined,
            threshold: undefined
        }
        switch (key) {
            case 'column': {
                newData.isCustomizeSql = 0;
                break;
            }
            case 'table': {
                newData.isCustomizeSql = 0;
                newData.level = 1;
                newData.columnName = data.tableName;
                newData.isTable = true;
                break;
            }
            case 'sql': {
                newData.isCustomizeSql = 1;
                newData.customizeSql = undefined;
                break;
            }
            case 'typeCheck': {

            }
        }
        this.setState({
            ruleList: [newData, ...this.state.ruleList]
        }, this.emitChangeEvent)
    }
    renderHeader () {
        const menu = (
            <Menu onClick={this.handleMenuClick}>
                <Menu.Item key="column">字段级规则</Menu.Item>
                <Menu.Item key="table">表级规则</Menu.Item>
                <Menu.Item key="sql">自定义SQL</Menu.Item>
                <Menu.Item key="typeCheck">字段格式校验</Menu.Item>
            </Menu>
        )
        return (
            <div className='c-ruleList__header'>
                <div className='c-ruleList__header__left'>
                    <Button>保存全部</Button>
                </div>
                <div className='c-ruleList__header__right'>
                    <Dropdown overlay={menu}>
                        <Button type='primary'>
                            添加规则 <Icon type="down" />
                        </Button>
                    </Dropdown>
                </div>
            </div>
        )
    }
    emitChangeEvent = () => {
        const { ruleList } = this.state;
        const { onRuleListChange } = this.props;
        if (onRuleListChange) {
            onRuleListChange(ruleList);
        }
    }
    onEditRule = (id) => {
        const { ruleList = [] } = this.state;
        const newRuleList = ruleList.map((r) => {
            if (r.id == id) {
                return {
                    ...r,
                    isEdit: true
                }
            };
            return r;
        });
        this.setState({
            ruleList: newRuleList
        }, this.emitChangeEvent)
    }
    onCancelEdit = (id) => {
        const { ruleList = [] } = this.state;
        const newRuleList = ruleList.map((r) => {
            if (r.id == id) {
                if (r.isNew) {
                    return null
                }
                return {
                    ...r,
                    isEdit: false
                }
            };
            return r;
        }).filter(Boolean);
        this.setState({
            ruleList: newRuleList
        }, this.emitChangeEvent)
    }
    onDeleteRule = async (id, next) => {
        const { ruleList = [] } = this.state;
        const ruleIndex = ruleList.findIndex((r) => {
            return r.id == id;
        });
        if (ruleIndex == -1) {
            next(false);
            return;
        }
        const newRuleList = [...ruleList];
        const rule = ruleList[ruleIndex];
        if (!rule.isNew) {
            let isSuccess = await this.props.onDeleteRule(rule);
            if (!isSuccess) {
                next(false);
                return;
            }
        }
        newRuleList.splice(ruleIndex, 1)
        next(true);
        this.setState({
            ruleList: newRuleList
        }, this.emitChangeEvent)
    }
    onSave = async (saveRule, next) => {
        const { ruleList = [] } = this.state;
        const ruleIndex = ruleList.findIndex((r) => {
            return r.id == saveRule.id;
        });
        if (ruleIndex == -1) {
            next(false);
            return;
        }
        const newRuleList = [...ruleList];
        let isSuccess = await this.props.onSaveRule(saveRule);
        if (!isSuccess) {
            next(false);
            return;
        }
        newRuleList.splice(ruleIndex, 1, { ...saveRule, isEdit: false, isNew: false });
        next(true);
        this.setState({
            ruleList: newRuleList
        }, this.emitChangeEvent);
    }
    render () {
        const { ruleList = [] } = this.state;
        const { tableColumn } = this.props;
        return (
            <div className='c-ruleList'>
                {this.renderHeader()}
                {ruleList.map(
                    (rule) => {
                        return <Rule
                            tableColumn={tableColumn}
                            key={rule.id}
                            isEdit={rule.isEdit}
                            type={this.getType(rule)}
                            data={rule}
                            onEdit={this.onEditRule}
                            onCancel={this.onCancelEdit}
                            onDelete={this.onDeleteRule}
                            onSave={this.onSave}
                        />
                    }
                )}
            </div>
        )
    }
}
export default RuleList;
