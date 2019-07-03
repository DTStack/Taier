import React from 'react';

import { Button, Dropdown, Icon, Menu } from 'antd';
import Rule from './rule';

import { getRuleType } from '../../consts/helper';
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
                newData.level = 0;
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
                newData.level = 2;
                newData.isCustomizeSql = 0;
                newData.isFormat = true;
                break;
            }
        }
        this.setState({
            ruleList: [newData, ...this.state.ruleList]
        }, this.emitChangeEvent)
    }
    renderSaveAll () {
        const { ruleList } = this.state;
        const { couldSaveAll } = this.props;
        const rule = ruleList.find((r) => {
            return r.isEdit;
        })
        return (couldSaveAll && rule) ? (<Button onClick={this.saveAll}>保存全部</Button>) : null;
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
                    {/* {this.renderSaveAll()} */}
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
            if (this.props.onDeleteRule) {
                let isSuccess = await this.props.onDeleteRule(rule);
                if (!isSuccess) {
                    next(false);
                    return;
                }
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
        if (this.props.onSaveRule) {
            let newRule = await this.props.onSaveRule(saveRule);
            if (!newRule) {
                next(false);
                return;
            }
            saveRule = { ...saveRule, ...newRule };
        }
        newRuleList.splice(ruleIndex, 1, { ...saveRule, isEdit: false, isNew: false });
        next(true);
        this.setState({
            ruleList: newRuleList
        }, this.emitChangeEvent);
    }
    onClone = (cloneRule) => {
        const { ruleList = [] } = this.state;
        const newRuleList = [ ...ruleList ];
        const ruleIndex = newRuleList.findIndex((rule) => {
            return rule.id == cloneRule.id;
        });
        if (ruleIndex == -1) {
            return;
        }
        newRuleList.splice(ruleIndex + 1, 0, { ...cloneRule, id: utils.generateAKey(), isNew: true, isEdit: true })
        this.setState({
            ruleList: newRuleList
        }, this.emitChangeEvent);
    }
    render () {
        const { ruleList = [] } = this.state;
        const { style, tableColumn, data } = this.props;
        return (
            <div style={style} className='c-ruleList'>
                {this.renderHeader()}
                {ruleList.map(
                    (rule) => {
                        return <Rule
                            tableName={data.tableName}
                            tableColumn={tableColumn}
                            key={rule.id}
                            isEdit={rule.isEdit}
                            type={getRuleType(rule)}
                            data={rule}
                            onEdit={this.onEditRule}
                            onCancel={this.onCancelEdit}
                            onDelete={this.onDeleteRule}
                            onSave={this.onSave}
                            onClone={this.onClone}
                        />
                    }
                )}
            </div>
        )
    }
}
export default RuleList;
