import React from 'react';
import Editor from 'widgets/editor';

class EnvConfig extends React.Component {
    render () {
        return (
            <Editor
                language='ini'
                options={{
                    minimap: {
                        enabled: false
                    }
                }}
                value={this.props.value}
                onChange={this.props.onChange}
            />
        )
    }
}
export default EnvConfig;
