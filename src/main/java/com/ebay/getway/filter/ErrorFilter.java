package com.ebay.getway.filter;

import brave.Tracer;
import com.ebay.getway.util.GetwayUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Slf4j
//@Component
public class ErrorFilter extends ZuulFilter {
    @Autowired
    private Tracer tracer;
    @Override
    public String filterType() {
        return "error";
    }
    @Override
    public int filterOrder() {
        return 20;
    }
    @Override
    public boolean shouldFilter() {
        return true;
    }
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable throwable = RequestContext.getCurrentContext().getThrowable();
        log.error("this is a ErrorFilter : {}", throwable.getCause().getMessage());
        ctx.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ctx.set("error.exception", throwable.getCause());
        GetwayUtil.setUnsuccessRequestContext(ctx,throwable.getCause().getMessage(), throwable.getCause().getMessage(), tracer);
        return null;
    }

}
