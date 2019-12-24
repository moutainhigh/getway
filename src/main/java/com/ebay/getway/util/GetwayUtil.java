package com.ebay.getway.util;

import brave.Tracer;
import com.netflix.zuul.context.RequestContext;
import com.superarmyknife.toolbox.handle.ResponseData;
import com.superarmyknife.toolbox.util.JsonUtil;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

public class GetwayUtil {

    public static void setUnsuccessRequestContext(RequestContext ctx, String errorMessage, String messageContent, Tracer tracer) {
        String body = buildResponseContent(errorMessage,tracer);
        ctx.setResponseBody(body);
        ctx.addZuulResponseHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }


    public static String buildResponseContent( String errorMessage,Tracer tracer){

        ResponseData responseData =  ResponseData.failure(errorMessage);
        return JsonUtil.getJsonString(responseData);
    }
}
