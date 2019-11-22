package com.dtstack.rdos.engine.execution.base.util;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DtStringUtil {

    public static List<String> splitIgnoreQuota(String sqls, char delimiter){
        List<String> tokensList = new ArrayList<>();
        boolean inQuotes = false;
        boolean inSingleQuotes = false;
        StringBuilder b = new StringBuilder();
        char[] chars = sqls.toCharArray();
        int idx = 0;
        for (char c : chars) {
            char flag = 0;
            if (idx>0){
                flag = chars[idx-1];
            }
            if(c == delimiter){
                if (inQuotes) {
                    b.append(c);
                } else if(inSingleQuotes){
                    b.append(c);
                }else {
                    if (StringUtils.isNotBlank(b)){
                        tokensList.add(b.toString());
                        b = new StringBuilder();
                    }
                }
            }else if(c == '\"' && '\\'!=flag){
                inQuotes = !inQuotes;
                b.append(c);
            }else if(c == '\'' && '\\'!=flag && !inQuotes){
                inSingleQuotes = !inSingleQuotes;
                b.append(c);
            }else{
                b.append(c);
            }
            idx++;
        }

        if (StringUtils.isNotBlank(b)){
            tokensList.add(b.toString());
        }

        return tokensList;
    }
}
