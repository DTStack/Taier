import * as React from 'react';
import { message as Message } from 'antd';
import { TagSelect } from './tagSelect';
import { API } from '../../../../../api/apiMap';
import './style.scss';

interface IProps {
    entityId: number;
    tagSelect: any[];
    onChange: (value: any[]) => void;
}
interface IState {
    oneOption: any[];
    twoOption: any[];
    threeOption: any[];
}
export class Conditions extends React.Component<IProps, IState> {
    state: IState = {
        oneOption: [],
        twoOption: [],
        threeOption: []
    }
    onChange = (value: string, level: number) => {
        const { tagSelect } = this.props;
        let newTag = [];
        if (level == 1) {
            newTag = value ? [value] : []
        } else if (level == 2) {
            newTag = value ? [tagSelect[0], value] : tagSelect
        } else {
            newTag = value ? [tagSelect[0], tagSelect[1], value] : tagSelect
        }
        value && level != 3 && this.getSubTagCate(value, level);
        this.props.onChange(newTag)
    }
    componentDidMount () {
        const { entityId } = this.props;
        entityId && this.getSubTagCate('-1', 0);
    }
    getSubTagCate = (tagCateId, level: number) => {
        const { entityId } = this.props;
        API.getSubTagCate({
            tagCateId: tagCateId,
            entityId
        }).then((res) => {
            const { data, code, message } = res;
            if (code == 1) {
                let options = data.map(item => Object.assign({}, { label: item.cateName, value: item.tagCateId }));
                if (level == 0) {
                    this.setState({
                        oneOption: options,
                        twoOption: [],
                        threeOption: []
                    });
                } else if (level == 1) {
                    this.setState({
                        twoOption: options,
                        threeOption: []
                    });
                } else {
                    this.setState({
                        threeOption: options
                    });
                }
            } else {
                Message.error(message)
            }
        })
    }
    render () {
        const { oneOption, twoOption, threeOption } = this.state;
        const { tagSelect } = this.props;
        return (<div className="conditions">
            <div className="condition-item">
                <div className="row-label">一级分类</div>
                <div className="condition-row-content">
                    <TagSelect option={oneOption} value={tagSelect[0]} onChange={(value) => this.onChange(value, 1)}/>
                </div>
            </div>
            {
                twoOption.length > 0 && tagSelect[0] && (
                    <div className="condition-item">
                        <div className="row-label">二级分类</div>
                        <div className="condition-row-content">
                            <TagSelect option={twoOption} value={tagSelect[1]} onChange={(value) => this.onChange(value, 2)}/>
                        </div>
                    </div>
                )
            }
            {
                threeOption.length > 0 && tagSelect[1] && (
                    <div className="condition-item">
                        <div className="row-label">三级分类</div>
                        <div className="condition-row-content">
                            <TagSelect option={threeOption} value={tagSelect[2]} onChange={(value) => this.onChange(value, 3)}/>
                        </div>
                    </div>
                )
            }

        </div>)
    }
}
