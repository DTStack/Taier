import * as React from 'react';
import './style.scss';

// import utils from 'utils';
// import { Link } from 'react-router';

interface IProps {
    data: any;
}

function wrapVal (value: string | number) {
    return value || '-';
}

const BasicInfo: React.SFC<IProps> = function (props: IProps) {
    const { data = {} } = props;
    return (
        <div className="basicInfo">
            <div className="info_item"><span>标签类型： </span><span>{wrapVal(data.tagType)}</span></div>
            <div className="info_item"><span>标签ID： </span><span>{wrapVal(data.tagId)}</span></div>
            <div className="info_item"><span>所属类目： </span><span>{wrapVal(data.tagCate)}</span></div>
            <div className="info_item"><span>数据量： </span><span>{wrapVal(data.tagCount)}</span></div>
            <div className="info_item"><span>创建人： </span><span>{wrapVal(data.createBy)}</span></div>
            <div className="info_item"><span>创建时间: </span><span>{wrapVal(data.createAt)}</span></div>
            <div className="info_item"><span>标签描述： </span><span>{wrapVal(data.tagDesc)}</span></div>
            <div className="info_item"><span>调用次数： </span><span>{wrapVal(data.tagCallCount)}</span></div>
            {
            //     <div className="info_item">
            //     <span>关联客群： </span>
            //     <Link to="#">客群1</Link>
            //     <Link to="#">客群1</Link>
            // </div>
            // <div className="info_item">
            //     <span>关联API： </span>
            //     <Link to="#">API1</Link>
            //     <Link to="#">API2</Link>
            // </div>
            }
        </div>
    )
}

export default BasicInfo;
