import * as React from 'react';
import {Row,Col} from 'antd';
import TagValues from '../tagValues/index';
import shortid from 'shortid';
import PanelSelect from '../panelSelect';
import { API } from '../../../../../api/apiMap';
import './style.scss';


interface IProps {
    tagId: string | number;
    entityId: string | number;
}
interface IState {
    data: any;
    tags: any[];
    atomTagList: any[];
    select: string;
    entityName: string;
}

function wrapVal (value: string | number) {
    return value || '-';
}
class DerivativeRules extends React.PureComponent<IProps, IState> {
    state: IState = {
        data: {},
        tags: [],
        atomTagList: [],
        select: '',
        entityName: ''
    }
    componentDidMount () {
        const { tagId } = this.props;
        if (tagId) {
            this.getDeriveTagVO(tagId);
            this.getAtomTagList();
        }
    }
    getDeriveTagVO = (tagId) => {
        API.getDeriveTagVO({ tagId }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                const { entityName, tags } = data;
                let newtags = tags.map(item => {
                    return {
                        tagValueId: item.tagValueId,
                        label: item.tagValue,
                        value: shortid(),
                        valid: true,
                        params: JSON.parse(item.param)
                    }
                })
                this.setState({
                    tags: newtags,
                    entityName,
                    select: newtags.length ? newtags[0].value : ''
                })
                this.setState({
                    data: data
                })
            }
        })
    }
    getAtomTagList = () => { // 获取原子标签列表
        const { entityId } = this.props;
        API.getAtomTagList({
            entityId
        }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                if (data && data.length) {
                    this.setState({
                        atomTagList: data,
                    })
                }
            }
        })
    }
    onSelect = (value) => {
        this.setState({
            select: value
        })
    }
    render () {
        const { tags, select, entityName,atomTagList} = this.state;
        const currentTag = select ? tags.find(item => item.value == select) : '';
        const treeData = currentTag ? currentTag.params : '';
        return (
            <div className="derivativeRules">
                <Row className="info_item" type="flex"><Col className="label">所属实体： </Col><Col>{wrapVal(entityName)}</Col></Row>
                <Row className="info_item" type="flex"><Col className="label">标签值：</Col> <Col><TagValues select={select} config={{}} value={tags} onSelect={this.onSelect} /></Col> </Row>
                <Row className="info_item" type="flex">
                    <Col className="label">标签值规则： </Col>
                    <Col>
                        <PanelSelect atomTagList={atomTagList} treeData={treeData}/>
                    </Col>
               </Row>
            </div>
        )
    }
}

export default DerivativeRules;
