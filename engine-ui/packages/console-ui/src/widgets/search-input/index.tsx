import * as React from 'react'
import {
    Input
} from 'antd';

import './style.scss'

const Search = Input.Search;

class SearchInput extends React.PureComponent<any, any> {
    render () {
        const { placeholder, onSearch, onClose, display, className, style, id } = this.props;

        let myClass = 'search-control';
        let myStyle: any = {
            display
        }

        if (className) myClass = `${myClass} ${className}`;
        if (style) myStyle = Object.assign(myStyle, style);

        return (
            <div className="search-input">
                <div className={ myClass } style={ myStyle }>
                    <Search
                        ref={(self: any) => this._input = self }
                        id={id}
                        placeholder={placeholder}
                        onSearch={onSearch}
                    />
                </div>
                <div
                    className="search-mask-layer"
                    style={{ display }}
                    onClick={onClose}></div>
            </div>
        )
    }
}

export default SearchInput
