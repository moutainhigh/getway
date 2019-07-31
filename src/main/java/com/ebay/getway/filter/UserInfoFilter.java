package com.ebay.getway.filter;

import com.alibaba.fastjson.JSONObject;
import com.ebay.getway.util.GatewayConstants;
import com.ebay.getway.util.TokenRepository;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.superarmyknife.toolbox.rtndto.GeneratorResult;
import com.superarmyknife.toolbox.web.UserBean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserInfoFilter extends ZuulFilter {


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

    private TokenRepository tokenRepository;
    public  UserInfoFilter(){}
    public UserInfoFilter(TokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Object run(){
        RequestContext requestContext = RequestContext.getCurrentContext();
        String requestUri = requestContext.getRequest().getRequestURI();
        if(requestUri.equals(GatewayConstants.CURRENT_USER_URI)){
            UserBean userInfo = tokenRepository.get(requestContext.get(GatewayConstants.TOKEN_KEY).toString()).getUserResponseDO();
            if(userInfo == null){
                return null;
            }
            //拷贝后再返回，去掉密码等敏感信息
            UserBean userResponseDO = new UserBean();
            userResponseDO.setUserName(userInfo.getUserName());
            userResponseDO.setUserLogin(userInfo.getUserLogin());

            requestContext.getResponse().setContentType("text/html;charset=UTF-8");
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(200);
            requestContext.setResponseBody(JSONObject.toJSONString(GeneratorResult.genSuccessResult(userResponseDO)));
        }
        return null;
    }

}
