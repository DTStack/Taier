import { MY_APPS } from 'consts'
export function DatabaseType(props) {
    const value = props.value
    switch (value) {
    case 1:
        return <span>MySQL</span>
    case 2:
        return <span>Oracle</span>
    case 3:
        return <span>SQLServer</span>
    case 6:
        return <span>HDFS</span>
    case 7:
        return <span>Hive</span>
    case 8:
        return <span>HBASE</span>
    case 9:
        return <span>FTP</span>
    default:
        return <span>其他</span>
    }
}

export function AppName(app) {
    switch(app) {
        case MY_APPS.RDOS: 
            return '开发套件'
        case MY_APPS.DATA_QUALITY: 
            return '数据质量'
        case MY_APPS.API: 
            return 'API管理'
        case MY_APPS.LABEL: 
            return '标签管理'
        case MY_APPS.DATA_MAP: 
            return '数据地图'
        case MY_APPS.META_DATA: 
            return '元数据管理'
        default: return '';
    }
}

