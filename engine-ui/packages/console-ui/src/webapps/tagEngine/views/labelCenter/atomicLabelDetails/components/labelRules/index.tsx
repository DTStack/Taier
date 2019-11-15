import * as React from 'react';
import { Link } from 'react-router';
import './style.scss';

interface IProps {
    data: any;
}

function wrapVal (value: string | number) {
    return value || '-';
}

const LabelRules: React.SFC<IProps> = function (props: IProps) {
    const { data = {} } = props;
    return (
        <div className="labelRules">
            <div className="info_item"><span>所属实体： </span><span>{wrapVal(data.id)}</span></div>
            <div className="info_item"><span>所属维度： </span><span>{wrapVal(data.storeSize)}</span></div>
            <div className="info_item"><span>属性值类型： </span><span>{wrapVal(data.description)}</span></div>
            <div className="info_item"><span>标签值： </span><span>{wrapVal(data.creator)}</span></div>
            <div className="info_item">
                <span>标签字典： </span>
                <Link to="#">年龄信息</Link>
            </div>
        </div>
    )
}

export default LabelRules;
