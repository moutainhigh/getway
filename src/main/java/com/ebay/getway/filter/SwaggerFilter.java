package com.ebay.getway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SwaggerFilter extends ZuulFilter {


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
//        RequestContext ctx = RequestContext.getCurrentContext();
//        if (match(ctx.getRequest().getRequestURI())){
//            // swagger
//            String auth = (String) ctx.getRequest().getSession().getAttribute("Authorization");
//
//        }



        return null;
    }


}
