import * as React from 'react';
import { TagSelect } from './tagSelect';
import './style.scss';

interface IProps {

}
interface IState {

}
export class Conditions extends React.Component<IProps, IState> {
    state: IState = {

    }
    onChange = (value: {label: string; value: string|number}) => {
        console.log(value);
    }
    render () {
        return (<div className="conditions">
            <div className="condition-item">
                <div className="row-label">一级分类</div>
                <div className="row-content">
                    <TagSelect option={[{ label: '默认分组', value: '0' }, { label: '人群属性', value: '1' }, { label: '行为属性', value: '1' }]} value='0' onChange={this.onChange}/>
                </div>
            </div>
            <div className="condition-item">
                <div className="row-label">二级分类</div>
                <div className="row-content">
                    <TagSelect option={[{ label: '年龄属性', value: '0' }]} value='0' onChange={this.onChange}/>
                </div>
            </div>
            <div className="condition-item">
                <div className="row-label">三级分类</div>
                <div className="row-content">
                    <TagSelect option={[{ label: '年龄标签', value: '0' }, { label: '节段性运营标签', value: '1' }]} value='0' onChange={this.onChange}/>
                </div>
            </div>
        </div>)
    }
}
