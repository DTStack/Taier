import * as React from 'react';
import { Link } from 'react-router';
import { API } from '../../../../../api/apiMap';
import './style.scss';

interface IProps {
    tagId: string|number;
    entityId: string|number;
}
interface IState {
    data: any;
}

function wrapVal (value: string | number) {
    return value || '-';
}
class LabelRules extends React.PureComponent<IProps, IState> {
    state: IState = {
        data: {}
    }
    componentDidMount () {
        this.getTagRule()
    }
    getTagRule = () => {
        const { tagId, entityId } = this.props;
        API.getTagRule({ tagId, entityId }).then(res => {
            const { data, code } = res;
            if (code === 1) {
                this.setState({
                    data
                })
            }
        })
    }
    render () {
        const { data } = this.state;
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
}

export default LabelRules;
