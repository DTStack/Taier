package com.dtstack.rdos.engine.execution.base.operator.batch;

import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.google.code.regexp.Pattern;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
/**
 * Created by sishu.yss on 2017/6/26.
 */
public class BatchCanExecuteOperator implements Operator {

    private String sql;

    private static List<Pattern> cantnotPatterns = Lists.newArrayList();

    private static List<String> cannotExecuteSql = Lists.newArrayList("CREATE\\s+DATABASE","CREATE\\s+SCHEMA",
            "DROP\\s+DATABASE","DROP\\s+SCHEMA","ALTER\\s+SCHEMA","ALTER\\s+DATABASE","CREATE\\s+FUNCTION","DROP\\s+FUNCTION","RELOAD\\s+FUNCTION",
            "CREATE\\s+TEMPORARY\\s+FUNCTION","DROP\\s+TEMPORARY\\s+FUNCTION");


    static{
        for(String cannot:cannotExecuteSql){
            cantnotPatterns.add(Pattern.compile(cannot));
        }
    }

    @Override
    public void createOperator(String sql) throws Exception {
           this.sql = sql;
    }

    @Override
    public boolean verific(String sql) throws Exception {
        if(StringUtils.isBlank(sql))return false;
        for(Pattern cannot:cantnotPatterns){
            if(cannot.matcher(sql).find()){
                return false;
            }
        }
        return true;
    }

    @Override
    public String getSql() {
        return this.sql.trim();
    }

    public static void main(String[] args) throws Exception {

        System.out.println(new BatchCanExecuteOperator().verific("SELECT ... FROM (subquery) name ...;"));

    }
}
