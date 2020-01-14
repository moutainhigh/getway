package com.ebay.getway.filter;

import com.alibaba.fastjson.JSONObject;
import com.ebay.getway.util.GatewayConstants;
import com.ebay.getway.util.TokenRepository;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.superarmyknife.toolbox.handle.ResponseData;
import com.superarmyknife.toolbox.rtndto.GeneratorResult;
import com.superarmyknife.toolbox.web.SAKToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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

    private static List<Pattern> urlFilters=null;

    public TokenFilter(){

    }
    public TokenFilter(TokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
    }

    private static HashMap urlMap = new HashMap();
    static {

        urlMap.put("/pc/login","1");
        urlFilters=new ArrayList<>();

        urlFilters.add(Pattern.compile(".*?/doc\\.html.*",Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/v2/api-docs.*",Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/v2/api-docs-ext.*",Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-resources.*",Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-ui\\.html.*",Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-resources/configuration/ui.*",Pattern.CASE_INSENSITIVE));
        urlFilters.add(Pattern.compile(".*?/swagger-resources/configuration/security.*",Pattern.CASE_INSENSITIVE));

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

        if (match(requestContext.getRequest().getRequestURI())){
            // swagger
             token = (String) requestContext.getRequest().getSession().getAttribute(GatewayConstants.TOKEN_KEY);

        }
        if(StringUtils.isBlank(token)){
            authErrorSession(requestContext,token);
            return null;
        }

        SAKToken sakToken = null;
        try {
            sakToken = tokenRepository.getAndRefresh(token);
        } catch (Exception e) {
            log.error("获取token失败 {}",token,e);
            authErrorSession(requestContext,token);
            return null;
        }
        if(sakToken == null){
            log.info("token is not found: {}",token);
            authErrorSession(requestContext,token);
            return null;
        }
        log.debug("鉴权结束----");
        request.setAttribute(GatewayConstants.TOKEN_KEY,token);
        requestContext.set(GatewayConstants.TOKEN_KEY,token);
        requestContext.addZuulRequestHeader(GatewayConstants.TOKEN_KEY,sakToken.getId());
        //将用户ID保存到上下文
        requestContext.set(GatewayConstants.USER_ID,sakToken.getUserResponseDO().getId());
        requestContext.addZuulRequestHeader(GatewayConstants.USER_ID,sakToken.getUserResponseDO().getId() + "");
        return null;
    }

    private void authErrorSession(RequestContext requestContext,String token){
        requestContext.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(401);
        requestContext.setResponseBody(JSONObject.toJSONString(ResponseData.failure("没有权限")));
    }



    public static boolean match(String uri){
        boolean match=false;
        if (uri!=null){
            for (Pattern pattern:urlFilters){
                if (pattern.matcher(uri).matches()){
                    match=true;
                    break;
                }
            }
        }
        return match;
    }
}
