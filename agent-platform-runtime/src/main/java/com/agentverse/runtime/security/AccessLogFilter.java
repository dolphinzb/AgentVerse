/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogFilter
implements Filter {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        long startTime = System.currentTimeMillis();
        String method = httpRequest.getMethod();
        String path = httpRequest.getServletPath();
        try {
            chain.doFilter(request, response);
        }
        catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - startTime;
            int status = httpResponse.getStatus();
            log.info("{} {} -> {} ({}ms)", new Object[]{method, path, status, duration});
            throw throwable;
        }
        long duration = System.currentTimeMillis() - startTime;
        int status = httpResponse.getStatus();
        log.info("{} {} -> {} ({}ms)", new Object[]{method, path, status, duration});
    }
}

