import * as React from 'react';
import './index.scss';

import utils from 'utils';
import { getRuleType } from '../../consts/helper';

class RuleView extends React.Component<any, any> {
    renderItem (label: any, text: any) {
        return (
            <div className='c-ruleView__item'>
                <span className='c-ruleView__common__label'>
                    {label}:
                </span>
                <span className='c-ruleView__common__text'>
                    {text}
                </span>
            </div>
        )
    }
    renderColumn () {
        const { data } = this.props;
        return <React.Fragment>
            <div className='c-ruleView__item__line'>
                {this.renderItem('字段', data.columnName)}
                {this.renderItem('统计函数', data.functionName)}
            </div>
            <div className='c-ruleView__item__line'>
                {this.renderItem('过滤条件', data.filter)}
            </div>
            <div className='c-ruleView__item__line'>
                {this.renderItem('校验方法', data.verifyTypeValue)}
                {this.renderItem('期望值', `${data.operator} ${data.threshold} ${data.isPercentage ? '%' : ''}`)}
            </div>
        </React.Fragment>
    }
    renderTable () {
        const { data, tableName } = this.props;
        return <React.Fragment>
            <div className='c-ruleView__item__line'>
                {this.renderItem('表', tableName)}
                {this.renderItem('统计函数', data.functionName)}
            </div>
            <div className='c-ruleView__item__line'>
                {this.renderItem('过滤条件', data.filter)}
            </div>
            <div className='c-ruleView__item__line'>
                {this.renderItem('校验方法', data.verifyTypeValue)}
                {this.renderItem('期望值', `${data.operator} ${data.threshold} ${data.isPercentage ? '%' : ''}`)}
            </div>
        </React.Fragment>
    }
    renderSql () {
        const { data } = this.props;
        return <React.Fragment>
            <div className='c-ruleView__item__line'>
                {this.renderItem('sql', data.customizeSql)}
            </div>
            <div className='c-ruleView__item__line'>
                {this.renderItem('校验方法', data.verifyTypeValue)}
                {this.renderItem('期望值', `${data.operator} ${data.threshold} ${data.isPercentage ? '%' : ''}`)}
            </div>
        </React.Fragment>
    }
    renderTypeCheck () {
        const { data } = this.props;
        const isPercentage = data.verifyType == 7
        return <React.Fragment>
            <div className='c-ruleView__item__line'>
                {this.renderItem('字段', data.columnName)}
                {this.renderItem('校验格式', data.functionName)}
            </div>
            <div className='c-ruleView__item__line'>
                {this.renderItem('过滤条件', data.filter)}
            </div>
            <div className='c-ruleView__item__line'>
                {this.renderItem('期望值', `${isPercentage ? '占比' : '绝对值'} ${data.operator} ${data.threshold} ${isPercentage ? '%' : '条'}`)}
            </div>
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
        const { data } = this.props;
        return (
            <div className='c-ruleView__bottom'>
                <span className='c-ruleView__common__label'>最近修改人:</span>
                <span className='c-ruleView__common__text'>{data.modifyUser}</span>
                <span className='c-ruleView__common__label'>最近修改时间:</span>
                <span className='c-ruleView__common__text'>{utils.formatDateTime(data.gmtModified)}</span>
            </div>
        )
    }
    renderLeftView () {
        const { leftView } = this.props;
        return leftView ? (
            <div className='c-ruleView__leftView'>
                {leftView}
            </div>
        ) : null;
    }
    renderRightView () {
        const { rightView } = this.props;
        return rightView ? (
            <div className='c-ruleView__rightView'>
                {rightView}
            </div>
        ) : null;
    }
    render () {
        return (
            <section className='c-ruleView'>
                {this.renderLeftView()}
                <div className='c-ruleView__content'>
                    {this.renderContent()}
                </div>
                {this.renderDate()}
                {this.renderRightView()}
            </section>
        )
    }
}
export default RuleView;
