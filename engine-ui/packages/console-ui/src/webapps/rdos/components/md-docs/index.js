import React from 'react';
import { Select, Spin } from 'antd';
import MarkdownRender from 'widgets/markdown-render';

import './style.scss';

const { Option, OptGroup } = Select;

class SyntaxHelpPane extends React.Component {
    renderLoading () {
        return (
            <div className="c-syntaxPane__loading">
                <Spin />
            </div>
        )
    }

    renderHeader () {
        const { mappingData, onSelect, selectedVal } = this.props;
        const options = mappingData && mappingData.map(group =>
            <OptGroup key={group.id} title={group.name} label={group.name}>
                {
                    group.children.map(child => {
                        return (
                            <Option data={child.file} key={child.id} title={child.name}>{child.name}</Option>
                        )
                    })
                }
            </OptGroup>
        )
        return (
            <Select
                className="c-syntaxPane-header"
                value={selectedVal}
                showSearch
                optionFilterProp="name"
                filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                onSelect={onSelect}
                defaultActiveFirstOption
                placeholder="选择帮助对象">
                { options }
            </Select>
        )
    }

    render () {
        const { loading, html, theme } = this.props;
        return (
            <div className="c-syntaxPane">
                {this.renderHeader()}
                <div className="c-syntaxPane-content">
                    <MarkdownRender
                        text={html}
                        dark={theme !== 'vs'}
                        className="c-syntaxPane-content__md-docs"
                    />
                </div>
                {loading && this.renderLoading()}
            </div>
        )
    }
}

export default SyntaxHelpPane;
