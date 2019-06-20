import React from 'react';
import './index.scss';
import { getRuleType } from '../../consts/helper';

class RuleView extends React.Component {
    renderItem (label, text) {
        return (
            <div className='c-ruleView__item'>
                <div className='c-ruleView__item__header'>
                    {label}
                </div>
                <div className='c-ruleView__item__content'>
                    {text}
                </div>
            </div>
        )
    }
    renderColumn () {
        const { data } = this.props;
        return <React.Fragment>
            {this.renderItem('字段', data.columnName)}
            {this.renderItem('统计函数', data.functionName)}
            {this.renderItem('过滤条件', data.filter)}
            {this.renderItem('校验方法', data.verifyTypeValue)}
            {this.renderItem('期望值', `${data.operator} ${data.threshold} ${data.isPercentage ? '%' : ''}`)}
        </React.Fragment>
    }
    renderTable () {
        const { data, tableName } = this.props;
        return <React.Fragment>
            {this.renderItem('表', tableName)}
            {this.renderItem('统计函数', data.functionName)}
            {this.renderItem('过滤条件', data.filter)}
            {this.renderItem('校验方法', data.verifyTypeValue)}
            {this.renderItem('期望值', `${data.operator} ${data.threshold} ${data.isPercentage ? '%' : ''}`)}
        </React.Fragment>
    }
    renderSql () {
        const { data } = this.props;
        return <React.Fragment>
            {this.renderItem('sql', data.customizeSql)}
            {this.renderItem('校验方法', data.verifyTypeValue)}
            {this.renderItem('期望值', `${data.operator} ${data.threshold} ${data.isPercentage ? '%' : ''}`)}
        </React.Fragment>
    }
    renderTypeCheck () {
        const { data } = this.props;
        return <React.Fragment>
            {this.renderItem('字段', data.columnName)}
            {this.renderItem('校验格式', data.functionName)}
            {this.renderItem('过滤条件', data.filter)}
            {this.renderItem('期望值', `${data.operator} ${data.threshold} ${data.isPercentage ? '%' : ''}`)}
        </React.Fragment>
    }
    renderContent () {
        const { data } = this.props;
        const type = getRuleType(data);
        switch (type) {
            case 'column': {
                return this.renderColumn();
            }
            case 'table': {
                return this.renderTable();
            }
            case 'typeCheck': {
                return this.renderTypeCheck();
            }
            case 'sql': {
                return this.renderSql();
            }
        }
    }
    renderDate () {
        return (
            <div className='c-ruleView__bottom'>
                <span className='c-ruleView__common__label'>最近修改人:</span>
                <span className='c-ruleView__common__text'>admin@</span>
                <span className='c-ruleView__common__label'>最近修改时间:</span>
                <span className='c-ruleView__common__text'>2019-09-01</span>
            </div>
        )
    }
    render () {
        return (
            <section className='c-ruleView'>
                <div className='c-ruleView__item__box'>
                    {this.renderContent()}
                </div>
                {this.renderDate()}
            </section>
        )
    }
}
export default RuleView;
