package com.dtstack.taier.datasource.plugin.nfs.util;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.NfsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.emc.ecs.nfsclient.nfs.NfsSetAttributes;
import com.emc.ecs.nfsclient.nfs.nfs3.Nfs3;
import com.emc.ecs.nfsclient.rpc.CredentialUnix;

/**
 * nfs util
 *
 * @author ：wangchuan
 * date：Created in 下午8:14 2022/3/17
 * company: www.dtstack.com
 */
public class NfsUtil {

    private static final long MODE = 510L;

    public static Nfs3 getClient(ISourceDTO sourceDTO) {
        try {
            NfsSourceDTO nfsSourceDTO = (NfsSourceDTO) sourceDTO;
            NfsSetAttributes nfsSetAttr = new NfsSetAttributes();
            nfsSetAttr.setMode(MODE);
            return new Nfs3(nfsSourceDTO.getServer(), nfsSourceDTO.getPath(), new CredentialUnix(-2, -2, null), 3);
        } catch (Exception e) {
            throw new SourceException(String.format("get nfs client error: %s", e.getMessage()), e);
        }
    }
}
