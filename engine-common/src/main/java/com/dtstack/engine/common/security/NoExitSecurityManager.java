package com.dtstack.engine.common.security;

import java.security.Permission;

public class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
        // allow anything.
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        // allow anything.
    }

    @Override
    public void checkExit(int status) {
        super.checkExit(status);
        if (-2 != status) {
            throw new ExitException(status);
        }
    }
}