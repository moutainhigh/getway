package com.ebay.getway.util;

import com.superarmyknife.toolbox.web.SAKToken;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;


public class TokenRepository {

    RedisTemplate<String, SAKToken> redisTemplate;

    public TokenRepository(){}
    public TokenRepository(RedisTemplate<String, SAKToken> redisTemplate){
        this.redisTemplate = redisTemplate;
    }


    public SAKToken get(String token){
        //先判断有没有单点登出过
        if(!redisTemplate.hasKey(GatewayConstants.CAS_TICKET_PREFIX + token)){
            return null;
        }
        return redisTemplate.opsForValue().get(SAKToken.PREFIX + token);
    }

    public SAKToken getAndRefresh(String token){
        SAKToken SAKToken = get(token);
        if(SAKToken == null){
            return null;
        }
        //同时刷新SAKToken和castoken，防止token失效
        redisTemplate.expire(SAKToken.PREFIX + token,SAKToken.getTimeout(), TimeUnit.SECONDS);
        redisTemplate.expire(GatewayConstants.CAS_TICKET_PREFIX + token,SAKToken.getTimeout(), TimeUnit.SECONDS);
        return SAKToken;
    }
}
