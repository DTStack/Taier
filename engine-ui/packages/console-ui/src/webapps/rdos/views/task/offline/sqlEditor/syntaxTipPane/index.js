import React from 'react';

import MdDoc from '../../../../../components/md-docs';
import DocMappingData from './docMapping';

class SyntaxHelpPane extends React.Component {
    state = {
        loading: false
    }

    onSelectFile = (value, option) => {
        const { updateSyntaxPane } = this.props;
        const file = option.props.data;
        this.setState({ loading: true });
        updateSyntaxPane({ selected: value })

        if (file) {
            fetch(file).then(response => response.text())
                .then(res => {
                    updateSyntaxPane({ html: res })
                })
                .catch(err => {
                    console.log(err);
                }).finally(() => {
                    this.setState({ loading: false });
                });
        } else {
            this.setState({ loading: false });
        }
    }

    render () {
        const { syntaxPane, theme } = this.props;
        return (
            <MdDoc
                selectedVal={syntaxPane.selected}
                loading={this.state.loading}
                mappingData={DocMappingData}
                onSelect={this.onSelectFile}
                html={syntaxPane.html}
                theme={theme}
                key={theme}
            />
        )
    }
}

export default SyntaxHelpPane;
