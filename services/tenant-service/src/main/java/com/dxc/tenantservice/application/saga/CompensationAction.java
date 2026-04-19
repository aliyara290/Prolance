package com.dxc.tenantservice.application.saga;

@FunctionalInterface
public interface CompensationAction {
    void compensate() throws Exception;
}
