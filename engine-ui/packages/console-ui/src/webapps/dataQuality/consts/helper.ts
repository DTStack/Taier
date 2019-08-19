export function getRuleType (data: any) {
    const { level, isCustomizeSql } = data;
    if (isCustomizeSql) {
        return 'sql'
    } else if (level == 2) {
        return 'typeCheck';
    } else if (level == 1) {
        return 'table';
    } else {
        return 'column'
    }
}
