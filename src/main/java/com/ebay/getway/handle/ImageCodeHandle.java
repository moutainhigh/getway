package com.ebay.getway.handle;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ebay.getway.config.FilterIgnorePropertiesConfig;
import com.netflix.zuul.context.RequestContext;
import com.superarmyknife.toolbox.Exception.SAKException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.security.sasl.SaslException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;
import java.util.HashSet;
import java.util.Map;

@Service
public class ImageCodeHandle {
    public static final String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY";

    public static HashSet checkCodeSet = new HashSet();

    @Autowired
    private RedisTemplate redisTemplate;

    static {
        checkCodeSet.add("/pc/login");
    }
    @Autowired
    private FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;

    public void checkCode(HttpServletRequest request) {
        String code = request.getParameter("code");
            String randomSn = request.getParameter("randomSn");
        if (StrUtil.isBlank(code)){
            throw new SAKException("验证码不能为空");
        }
        if (StrUtil.isBlank(randomSn)){
            throw new SAKException("验证码流水不能为空");
        }

        String key = DEFAULT_CODE_KEY +randomSn;
        if (!redisTemplate.hasKey(key)) {
            throw new SAKException("无随机数记录");
        }
        Object codeObj = redisTemplate.opsForValue().get(key);
        if (codeObj == null) {
            throw new SAKException("获取到的验证码为null");
        }

        String saveCode = codeObj.toString();

        if (StrUtil.isBlank(saveCode)) {
            redisTemplate.delete(key);
            throw new SAKException("获取到的验证码为空");
        }

        if (!StrUtil.equals(saveCode, code)) {
            redisTemplate.delete(key);
            throw new SAKException("和redis中的验证码不符");
        }

        redisTemplate.delete(key);
    }

    public boolean shouldCheck(HttpServletRequest request){
        if (!checkCodeSet.contains(request.getRequestURI())){
           return false;
        }

        String client = request.getParameter("client_id");
        if (StrUtil.isNotEmpty(client)) {

            if (filterIgnorePropertiesConfig.getClients().contains(client)){
                return false;
            }
        }
        return true;

    }
}
