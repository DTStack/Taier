package com.dtstack.yarn.am;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.TokenIdentifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Reason:
 * Date: 2019/1/4
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class DTTokenIdentifier extends TokenIdentifier {

    private Text tokenid;
    private Text realUser;
    final static Text KIND_NAME = new Text("test.token");

    public DTTokenIdentifier() {
        this(new Text(), new Text());
    }
    public DTTokenIdentifier(Text tokenid) {
        this(tokenid, new Text());
    }
    public DTTokenIdentifier(Text tokenid, Text realUser) {
        this.tokenid = tokenid == null ? new Text() : tokenid;
        this.realUser = realUser == null ? new Text() : realUser;
    }
    @Override
    public Text getKind() {
        return KIND_NAME;
    }
    @Override
    public UserGroupInformation getUser() {
        if ("".equals(realUser.toString())) {
            return UserGroupInformation.createRemoteUser(tokenid.toString());
        } else {
            UserGroupInformation realUgi = UserGroupInformation
                    .createRemoteUser(realUser.toString());
            return UserGroupInformation
                    .createProxyUser(tokenid.toString(), realUgi);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        tokenid.readFields(in);
        realUser.readFields(in);
    }
    @Override
    public void write(DataOutput out) throws IOException {
        tokenid.write(out);
        realUser.write(out);
    }
}
