package com.ebay.getway.filter;

import com.alibaba.fastjson.JSONObject;
import com.ebay.getway.handle.ImageCodeHandle;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.superarmyknife.toolbox.Exception.SAKException;
import com.superarmyknife.toolbox.handle.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ImageCodeGatewayFilter  extends ZuulFilter {

    @Autowired
    private ImageCodeHandle imageCodeHandle;


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
        RequestContext ctx = RequestContext.getCurrentContext();
        return imageCodeHandle.shouldCheck(ctx.getRequest());
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        try {
            imageCodeHandle.checkCode(request);
        }catch (SAKException e) {
            log.error("校验失败",e);
            authErrorSession(ctx, e.getMessage());
        }catch (Exception e) {
            log.error("校验失败",e);
            authErrorSession(ctx, "校验失败");
        }

        return null;
    }


    private void authErrorSession(RequestContext requestContext,String message){
        requestContext.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(HttpStatus.PRECONDITION_REQUIRED.value());
        requestContext.setResponseBody(JSONObject.toJSONString(ResponseData.failure(message)));
    }




}
