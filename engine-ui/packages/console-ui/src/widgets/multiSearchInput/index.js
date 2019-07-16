import PropTypes from 'prop-types'
// import { assign } from 'lodash'
import { Input } from 'antd'
import React, { Component } from 'react'

const propType = {
    placeholder: PropTypes.string,
    style: PropTypes.object,
    value: PropTypes.any,
    onChange: PropTypes.func,
    onSearch: PropTypes.func,
    searchType: PropTypes.string
}

class SlidePane extends Component {
    constructor (props) {
        super(props);
        this.state = {
            placeholder: this.props.placeholder || '',
            style: this.props.style || {},
            value: this.props.value || '',
            onChange: this.props.onChange || (() => {}),
            onSearch: this.props.onSearch || (() => {}),
            onTypeChange: this.props.onTypeChange || (() => {}),
            searchType: this.props.searchType || 'fuzzy'
        }
    }

    render () {
        const {
            placeholder,
            style,
            value,
            onChange,
            onSearch,
            onTypeChange,
            searchType
        } = this.state;
        return (
            <div
                style={{
                    position: 'relative'
                }}
            >
                <Input
                    value={value}
                    placeholder={placeholder}
                    style={{
                        ...style,
                        paddingRight: '95px'
                    }}
                    onChange={(e) => {
                        console.log(e.target.value);
                        this.setState({ value: e.target.value });
                        onChange(e.target.value);
                    }}
                    onPressEnter={(e) => {
                        console.log(e.target, e.target.value);
                        onSearch(e.target);
                    }}
                />
                <div
                    style={{
                        position: 'absolute',
                        height: '100%',
                        top: '0px',
                        right: '0px',
                        width: '95px',
                        display: 'flex',
                        justifyContent: 'space-around',
                        alignItems: 'center'
                    }}
                >
                    <div
                        style={{
                            cursor: 'pointer',
                            display: 'block',
                            height: '22px',
                            width: '26px',
                            border: searchType === 'precise' ? '1px solid #006fc5' : 'none',
                            overflow: 'hidden'
                        }}
                        onClick={() => {
                            const newSearchType = searchType === 'precise' ? 'fuzzy' : 'precise'
                            this.setState({
                                searchType: newSearchType
                            });
                            onTypeChange(newSearchType)
                        }}
                    >
                        <img
                            src="/public/widgets/img/jq_icon.svg"
                            style={{
                                marginTop: '-6px',
                                marginLeft: '-3px'
                            }}
                        />
                    </div>
                    <div
                        style={{
                            cursor: 'pointer',
                            display: 'block',
                            height: '22px',
                            width: '26px',
                            border: searchType === 'front' ? '1px solid #006fc5' : 'none',
                            overflow: 'hidden'
                        }}
                        onClick={() => {
                            const newSearchType = searchType === 'front' ? 'fuzzy' : 'front'
                            this.setState({
                                searchType: newSearchType
                            });
                            onTypeChange(newSearchType)
                        }}
                    >
                        <img
                            src="/public/widgets/img/kt_icon.svg"
                            style={{
                                marginTop: '-6px',
                                marginLeft: '-3px'
                            }}
                        />
                    </div>
                    <div
                        style={{
                            cursor: 'pointer',
                            display: 'block',
                            height: '22px',
                            width: '26px',
                            border: searchType === 'tail' ? '1px solid #006fc5' : 'none',
                            overflow: 'hidden'
                        }}
                        onClick={() => {
                            const newSearchType = searchType === 'tail' ? 'fuzzy' : 'tail'
                            this.setState({
                                searchType: newSearchType
                            });
                            onTypeChange(newSearchType)
                        }}
                    >
                        <img
                            src="/public/widgets/img/jw_icon.svg"
                            style={{
                                marginTop: '-6px',
                                marginLeft: '-3px'
                            }}
                        />
                    </div>
                </div>
            </div>
        )
    }
}

SlidePane.propTypes = propType

export default SlidePane
