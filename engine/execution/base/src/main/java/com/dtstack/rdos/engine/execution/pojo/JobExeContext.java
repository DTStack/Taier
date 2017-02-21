package com.dtstack.rdos.engine.execution.pojo;


import com.dtstack.rdos.engine.entrance.sql.operator.Operator;

import java.util.ArrayList;
import java.util.List;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class JobExeContext {

    private List<Operator> operators = new ArrayList<Operator>();

    public void addOperator(Operator operator){
        operators.add(operator);
    }
}
