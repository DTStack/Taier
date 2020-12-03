package com.dtstack.engine.sql.hive.info;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.hive.HiveSqlBaseTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author chener
 * @Classname HiveTypeTest
 * @Description TODO
 * @Date 2020/12/3 11:55
 * @Created chener@dtstack.com
 */
public class HiveTypeTest extends HiveSqlBaseTest {
    /**
     * hive基本数据类型：
     * TINYINT	1字节整数	45Y
     * SMALLINT	2字节整数	12S
     * INT	4字节整数	10
     * BIGINT	8字节整数	244L
     * FLOAT	4字节单精度浮点数	1.0
     * DOUBLE	8字节双精度浮点数	1.0
     * DECIMAL	任意精度带符号小数	DECIMAL(4, 2)范围：-99.99到99.99
     * BOOLEAN	true/false	TRUE
     * STRING	字符串，长度不定	“a”, ‘b’
     * VARCHAR	字符串，长度不定，有上限	0.12.0版本引入
     * CHAR	字符串，固定长度	“a”, ‘b’
     * BINARY	存储变长的二进制数据
     * TIMESTAMP	时间戳，纳秒精度	122327493795
     * DATE	日期	‘2016-07-03’
     *
     * hive复杂数据类型：
     *ARRAY	存储同类型数据	ARRAY< data_type>
     * MAP	key-value,key必须为原始类型，value可以是任意类型	MAP< primitive_type, data_type>
     * STRUCT	类型可以不同	STRUCT< col_name : data_type [COMMENT col_comment], …>
     * UNION	在有限取值范围内的一个值	UNIONTYPE< data_type, data_type, …>
     */

    /**
     *
     * @throws Exception
     */
    @Test
    public void testHiveType() throws Exception {
        String sql = readStringFromResource("info/hiveType.sql");
        SqlParserImpl hiveSqlParser = getHiveSqlParser();
        ParseResult dev = hiveSqlParser.parseSql(sql, "dev", new HashMap<>());
        Assert.assertEquals(dev.getSqlType(), SqlType.CREATE);
    }
}
