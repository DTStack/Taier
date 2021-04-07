import * as React from 'react'
import { Icon } from 'antd'
import { VERSION_TYPE } from '../../const'
interface IProps {
    comp: any;
    versionData: any;
    saveComp: (params: any) => void;
}

export default class InitailComp extends React.Component<IProps, any> {
    render () {
        const { versionData, comp, saveComp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const className = 'c-initailComp__wrapper'
        return <div className={className}>
            <span className={`${className}__title`}>请选择版本号：</span>
            <div className={`${className}__container`}>
                {versionData[VERSION_TYPE[typeCode]]?.map(({ key, value }) => {
                    return <div
                        key={key}
                        className={`${className}__container__desc`}
                        onClick={() => saveComp({
                            componentTypeCode: typeCode,
                            hadoopVersion: value
                        })}
                    >
                        <span className="comp-name">
                            <img src={`public/img/${VERSION_TYPE[typeCode]}.png`}/>
                            <span>{VERSION_TYPE[typeCode]} {key}</span>
                        </span>
                        <Icon type="right-circle" theme="filled" />
                    </div>
                })}
            </div>
        </div>
    }
}
