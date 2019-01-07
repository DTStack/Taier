package com.dtstack.yarn.am;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.TokenSelector;

import java.util.Collection;

/**
 * Reason:
 * Date: 2019/1/5
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class DTTokenSelector implements TokenSelector<DTTokenIdentifier> {
    @Override
    public Token<DTTokenIdentifier> selectToken(Text service, Collection<Token<? extends TokenIdentifier>> tokens) {
        if (service == null) {
            return null;
        }
        for (Token<? extends TokenIdentifier> token : tokens) {
            if (DTTokenIdentifier.KIND_NAME.equals(token.getKind())
                    && service.equals(token.getService())) {
                return (Token<DTTokenIdentifier>) token;
            }
        }
        return null;
    }
}
