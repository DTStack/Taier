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

