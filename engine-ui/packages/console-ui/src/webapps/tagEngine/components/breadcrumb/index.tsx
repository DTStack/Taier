import * as React from 'react';
import { Breadcrumb } from 'antd';
import { Link } from 'react-router';
import './style.scss';

interface IProps {
    breadcrumbNameMap: any;
    style?: any;
}
export default (props: IProps) => {
    const { breadcrumbNameMap, style = {} } = props;
    const length = breadcrumbNameMap.length;
    return (
        <div className="newBreadcrumb" style={style}>
            <Breadcrumb>
                {breadcrumbNameMap.map((item: any, index: number) => (
                    <Breadcrumb.Item key={item.path}>
                        {index == length - 1 ? (
                            item.name
                        ) : (
                            <Link to={item.path}>{item.name}</Link>
                        )}
                    </Breadcrumb.Item>
                ))}
            </Breadcrumb>
        </div>
    );
};
