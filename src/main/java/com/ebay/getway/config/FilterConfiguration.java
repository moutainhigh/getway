package com.ebay.getway.config;

import com.ebay.getway.filter.AuthFilter;
import com.ebay.getway.filter.TokenFilter;
import com.ebay.getway.filter.UserInfoFilter;
import com.ebay.getway.util.TokenRepository;
import com.superarmyknife.toolbox.web.SAKToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@ConditionalOnProperty("getway.auth.enable")
public class FilterConfiguration {


    @Bean
    public TokenFilter tokenFilter(TokenRepository tokenRepository) {
        return new TokenFilter(tokenRepository);
    }

    @Bean
    public UserInfoFilter userInfoFilter(TokenRepository tokenRepository) {
        return new UserInfoFilter(tokenRepository);
    }


    @Bean
    public TokenRepository tokenRepository(RedisTemplate redisTemplate) {
        return new TokenRepository(redisTemplate);
    }



}
