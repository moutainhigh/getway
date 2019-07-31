package com.ebay.getway.filter;

import com.alibaba.fastjson.JSONObject;
import com.ebay.getway.util.GatewayConstants;
import com.ebay.getway.util.TokenRepository;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.superarmyknife.toolbox.rtndto.GeneratorResult;
import com.superarmyknife.toolbox.web.SAKToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Slf4j
public class TokenFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    private TokenRepository tokenRepository;

    public TokenFilter(){}
    public TokenFilter(TokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
    }

    private static HashMap urlMap = new HashMap();
    static {

        urlMap.put("/pc/user/login","");

    }
    @Override
    public Object run() {


        RequestContext requestContext = RequestContext.getCurrentContext();
        String requestUri = requestContext.getRequest().getRequestURI();
        if (urlMap.containsKey(requestUri)){
            return null;
        }
        HttpServletRequest request = requestContext.getRequest();
        String token = request.getHeader(GatewayConstants.TOKEN_KEY);

        if(StringUtils.isBlank(token)){
            authErrorSession(requestContext,token);
            return null;
        }

        SAKToken SAKToken = null;
        try {
            SAKToken = tokenRepository.getAndRefresh(token);
        } catch (Exception e) {
            log.error("获取token失败 {}",token,e);
            authErrorSession(requestContext,token);
            return null;
        }
        if(SAKToken == null){
            log.info("token is not found: {}",token);
            authErrorSession(requestContext,token);
            return null;
        }

        request.setAttribute(GatewayConstants.TOKEN_KEY,token);
        requestContext.set(GatewayConstants.TOKEN_KEY,token);
        //将用户ID保存到上下文
        requestContext.set(GatewayConstants.USER_ID,SAKToken.getUserResponseDO().getId());
        requestContext.addZuulRequestHeader(GatewayConstants.USER_ID,SAKToken.getUserResponseDO().getId() + "");
        return null;
    }

    private void authErrorSession(RequestContext requestContext,String token){
        requestContext.getResponse().setContentType("text/html;charset=UTF-8");
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(401);
        requestContext.setResponseBody(JSONObject.toJSONString(GeneratorResult.genFailResult("没有权限")));
    }
}
