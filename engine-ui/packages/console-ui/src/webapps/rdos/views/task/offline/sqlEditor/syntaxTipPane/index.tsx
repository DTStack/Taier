import * as React from 'react';

import MdDoc from '../../../../../components/md-docs';
import DocMappingData from './docMapping';

class SyntaxHelpPane extends React.Component<any, any> {
    state: any = {
        loading: false
    }

    componentDidMount () {
        this.props.updateSyntaxPane({ selected: 'normal_create_table' });
        const defaultFile = DocMappingData[0].children[0].file;
        this.loadFile(defaultFile);
    }

    onSelectFile = (value: any, option: any) => {
        const { updateSyntaxPane } = this.props;
        const file = option.props.data;
        updateSyntaxPane({ selected: value })
        this.loadFile(file);
    }

    loadFile = (file: any) => {
        const { updateSyntaxPane } = this.props;
        this.setState({ loading: true });

        if (file) {
            fetch(file).then((response: any) => response.text())
                .then((res: any) => {
                    updateSyntaxPane({ html: res })
                })
                .catch((err: any) => {
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
