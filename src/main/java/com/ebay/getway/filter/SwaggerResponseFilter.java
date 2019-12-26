package com.ebay.getway.filter;

import com.alibaba.fastjson.JSONObject;
import com.ebay.getway.util.GatewayConstants;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.superarmyknife.toolbox.handle.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import java.io.IOException;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;

@Component
@Slf4j
public class SwaggerResponseFilter extends ZuulFilter {


    @Override
    public String filterType() {
        return "post";
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
        RequestContext ctx = RequestContext.getCurrentContext();
        if (StringUtils.isNotBlank(ctx.getRequest().getRequestURI()) && StringUtils.isNotBlank(ctx.getRequest().getHeader("Referer")) &&  "/pc/login".equals(ctx.getRequest().getRequestURI()) && ctx.getRequest().getHeader("Referer").contains("index.html")){

            Pair<String, String>  pair = ctx.getOriginResponseHeaders().stream().filter((a)->GatewayConstants.TOKEN_KEY.equals(a.first())).findFirst().get();
            ctx.getRequest().getSession().setAttribute(GatewayConstants.TOKEN_KEY, pair.second());

        }else if (TokenFilter.match(ctx.getRequest().getRequestURI())) {
            if (401 == ctx.getResponse().getStatus()){
                authErrorSession(ctx);
            }
        }

        log.debug("end path==={}",ctx.getRequest().getRequestURI());
        return null;
    }

    private void authErrorSession(RequestContext requestContext){
        requestContext.getResponse().setContentType(MediaType.TEXT_HTML_VALUE);
        try {
            requestContext.setSendZuulResponse(false);
            requestContext.getResponse().setStatus(301);

            requestContext.setResponseStatusCode(301);
            requestContext.set("forward.to", "/index.html");
            requestContext.set("sendForwardFilter.ran", true);

            RequestContext ctx = RequestContext.getCurrentContext();
            String path = (String) ctx.get(FORWARD_TO_KEY);
            RequestDispatcher dispatcher = ctx.getRequest().getRequestDispatcher(path);
            if (dispatcher != null) {
                    dispatcher.forward(ctx.getRequest(), ctx.getResponse());
                    ctx.getResponse().flushBuffer();

            }
            requestContext.getResponse().sendRedirect("/index.html");

        } catch (Exception e) {
            e.printStackTrace();
        }

//        requestContext.setResponseBody();
    }
}
