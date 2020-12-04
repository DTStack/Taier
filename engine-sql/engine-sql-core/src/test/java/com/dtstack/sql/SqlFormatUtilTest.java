package com.dtstack.sql;

import org.junit.Test;

/**
 * @author chener
 * @Classname SqlFormatUtilTest
 * @Description sqlFormatUtil test
 * @Date 2020/9/19 15:51
 * @Created chener@dtstack.com
 */
public class SqlFormatUtilTest extends BaseSqlTest{

    @Test
    public void testRemoveCommentByQuotes(){
        String sql = readStringFromResource("SqlWithComment.sql");
        System.out.println(sql);
    }
}
